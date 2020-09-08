/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.file.engine.object.exception.ObjectNotFoundException;
import pw.cdmi.file.engine.realtime.exception.RealTimeCopyTaskError;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.util.RealTimeCopyTaskTool;


@SuppressWarnings("deprecation")
public class RealTimeCopyHttpClient
{
    private static final class IgnoreValidateX509TrustManager implements X509TrustManager
    {
        
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
            // do nothing;
        }
        
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
            // do nothing;
            
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            // do nothing;
            return new X509Certificate[0];
        }
        
    }
    
    public static final String HTTP_ACCEPT = "Accept";
       
    private ThreadSafeClientConnManager connectionManager = null;
    
    private IdleConnectionMonitorThread idleConnectionMonitorThread;
    
    private static final HttpParams PARAMS = new BasicHttpParams();
    
    private List<String> routeHosts = new ArrayList<String>(20);
    
    private List<Integer> httpPorts = new ArrayList<Integer>(5);
    
    private List<Integer> httpsPorts = new ArrayList<Integer>(5);
    
    private String[] cipherSuites;
    
    private String[] enabledProtocols;
    
    private boolean validateServerCert = true;
    
    private String trustStorePath;
    
    private String trustStorePass;
    
    private String trustStorePassKey;
    
    /**
     * 最大连接数
     */
    private final static int MAX_TOTAL_CONNECTIONS = 200;
    
    /**
     * 默认每个路由最大连接数
     */
    private final static int DEFAULT_MAX_ROUTE_CONNECTIONS = 40;
    
    /**
     * 每个路由最大连接数
     */
    private final static int MAX_ROUTE_CONNECTIONS = 100;
    
    /**
     * 连接超时时间
     */
    private final static int CONNECT_TIMEOUT = 60000;
    
    /**
     * 读取超时时间
     */
    private final static int READ_TIMEOUT = 60000;
    
    /**
     * socket内部缓冲区缓存数据的大小
     */
    private final static int SOCKET_BUFFER_SIZE = 8192;
    
    static
    {
        HttpConnectionParams.setConnectionTimeout(PARAMS, CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(PARAMS, READ_TIMEOUT);
        HttpConnectionParams.setStaleCheckingEnabled(PARAMS, false);
        HttpConnectionParams.setSocketBufferSize(PARAMS, SOCKET_BUFFER_SIZE);
        HttpClientParams.setRedirecting(PARAMS, false);
        HttpProtocolParams.setUserAgent(PARAMS, "CopyTask/1.0 Onebox 2.0");
    }
    
    @PostConstruct
    public void init() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException,
        CertificateException, IOException
    {
        
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        if (CollectionUtils.isEmpty(httpPorts))
        {
            httpPorts.add(80);
            
        }
        if (CollectionUtils.isEmpty(httpsPorts))
        {
            httpsPorts.add(443);
        }
        
        Scheme scheme = null;
        
        SSLSocketFactory socketFactory = null;
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, initTrustManagers(), null);
        for (Integer httpsPort : httpsPorts)
        {
            socketFactory = new SSLSocketFactory(sslContext, enabledProtocols, cipherSuites,
                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            scheme = new Scheme("https", httpsPort.intValue(), socketFactory);
            schemeRegistry.register(scheme);
        }

        connectionManager = new ThreadSafeClientConnManager(schemeRegistry);
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_ROUTE_CONNECTIONS);
        
        String split = ":";
        HttpHost host = null;
        HttpRoute route = null;
        for (String routeHost : routeHosts)
        {
            if (StringUtils.isBlank(routeHost))
            {
                continue;
            }
            
            if (routeHost.indexOf(split) < 0)
            {
                host = new HttpHost(routeHost);
            }
            else
            {
                host = new HttpHost(routeHost.split(split)[0], Integer.parseInt(routeHost.split(split)[1]));
            }
            
            route = new HttpRoute(host);
            connectionManager.setMaxForRoute(route, MAX_ROUTE_CONNECTIONS);
        }
        idleConnectionMonitorThread = new IdleConnectionMonitorThread(connectionManager);
        idleConnectionMonitorThread.start();
        RealTimePrinter.info(RealTimeCopyTaskTool.LOG_TAG+"CopyHttpClient inited");
    }
    
    private TrustManager[] initTrustManagers()
        throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
    {
        if (!validateServerCert)
        {
            return new TrustManager[]{new IgnoreValidateX509TrustManager()};
        }
        ClassLoader classLoader = getClassLoader();
        if (StringUtils.isBlank(trustStorePath))
        {
            throw new IllegalArgumentException("trustStorePath is null");
        }
        URL url = classLoader.getResource(trustStorePath);
        if (url == null)
        {
            throw new IllegalArgumentException("trust store " + trustStorePath + " is not exist");
        }
        String realTrustStorePass = getRealTrustStorePass();
        KeyStore trustStore = getKeyStore(url, realTrustStorePass);
        final TrustManagerFactory tmfactory = TrustManagerFactory
            .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(trustStore);
        return tmfactory.getTrustManagers();
    }
    
    private ClassLoader getClassLoader()
    {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader == null)
        {
            throw new IllegalStateException("can not get class loader for " + this.getClass().getName());
        }
        return classLoader;
    }
    
    private String getRealTrustStorePass()
    {
        if (StringUtils.isBlank(trustStorePass))
        {
            throw new IllegalArgumentException("trustStorePass is null");
        }
        if (StringUtils.isBlank(trustStorePassKey))
        {
            throw new IllegalArgumentException("trustStorePassKey is null");
        }
        return EDToolsEnhance.decode(trustStorePass, trustStorePassKey);
    }
    
    private KeyStore getKeyStore(URL url, String password)
        throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
    {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream in = null;
        try
        {
            in = url.openStream();
            keyStore.load(in, password.toCharArray());
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
        return keyStore;
    }
    
    private HttpClient getClient()
    {
        return new DefaultHttpClient(connectionManager, PARAMS);
    }
    
    @PreDestroy
    public void destroy()
    {
        idleConnectionMonitorThread.shutdown();
    }
    
    private static final class IdleConnectionMonitorThread extends Thread
    {
        
        private final ClientConnectionManager connMgr;
        
        private volatile boolean isShutdown;
        
        private final Object lock = new Object();
        
        IdleConnectionMonitorThread(ClientConnectionManager connMgr)
        {
            super();
            this.connMgr = connMgr;
        }
        
        @Override
        public void run()
        {
            try
            {
                closeIdleConnections();
            }
            catch (InterruptedException ex)
            {
                RealTimePrinter.error(RealTimeCopyTaskTool.LOG_TAG+"InterruptedException.");
            }
        }
        
        private void closeIdleConnections() throws InterruptedException
        {
            while (!isShutdown)
            {
                synchronized (lock)
                {
                    if (isShutdown)
                    {
                        break;
                    }
                    lock.wait(10000);
                    connMgr.closeExpiredConnections();
                    connMgr.closeIdleConnections(60, TimeUnit.SECONDS);
                }
            }
        }
        
        public void shutdown()
        {
            isShutdown = true;
            synchronized (lock)
            {
                lock.notifyAll();
            }
        }
    }
    
    public InputStream download(String requestUri, Map<String, String> headers)
        throws ServiceException, ObjectNotFoundException
    {
        HttpGet request = new HttpGet(requestUri);
        if (headers != null)
        {
            for (Entry<String, String> entry : headers.entrySet())
            {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
        String errorBody = null;
        InputStream input = null;
        StatusLine statusLine = null;
        try
        {
            HttpResponse response = this.getClient().execute(request);
            statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            input = response.getEntity().getContent();
            
            
            
            if (statusCode == RealTimeCopyTaskError.SOURCE_OBJECT_NOT_FOUND.getErrorCode())
            {
                errorBody = this.getErrorBody(response);
                
                throw new ObjectNotFoundException("Object Not exists,statusCode:" + statusCode, null);
            }
            else if (statusCode >= 300)
            {
                errorBody = this.getErrorBody(response);
                
                throw new ServiceException("Encountered a Internal Server errors, aborting request.",
                    statusCode);
            }
            return input;
        }
        catch (ObjectNotFoundException e)
        {
            RealTimePrinter.error(RealTimeCopyTaskTool.LOG_TAG+"ObjectNotFoundException:" + errorBody);
            
            IOUtils.closeQuietly(input);
            
            request.abort();
            
            throw e;
        }
        catch (Exception e)
        {
            String log = "error occur when download,status :"
                + (statusLine == null ? "Unkown" : statusLine.getStatusCode());
                
            RealTimePrinter.error(RealTimeCopyTaskTool.LOG_TAG+log + errorBody, e);
            
            IOUtils.closeQuietly(input);
            
            request.abort();
            
            throw new ServiceException(log, e);
        }
    }
    
    private String getErrorBody(HttpResponse response) throws IOException
    {
        HttpEntity body = response.getEntity();
        String errorBody = "";
        if (body != null)
        {
            errorBody = EntityUtils.toString(body);
        }
        return errorBody;
    }
    
    public List<String> getRouteHosts()
    {
        return routeHosts;
    }
    
    public void setRouteHosts(List<String> routeHosts)
    {
        this.routeHosts = routeHosts;
    }
    
    public List<Integer> getHttpPorts()
    {
        return httpPorts;
    }
    
    public void setHttpPorts(List<Integer> httpPorts)
    {
        this.httpPorts = httpPorts;
    }
    
    public List<Integer> getHttpsPorts()
    {
        return httpsPorts;
    }
    
    public void setHttpsPorts(List<Integer> httpsPorts)
    {
        this.httpsPorts = httpsPorts;
    }
    
    public void setCipherSuites(String[] cipherSuites)
    {
        if (cipherSuites == null)
        {
            this.cipherSuites = null;
        }
        else
        {
            this.cipherSuites = cipherSuites.clone();
        }
    }
    
    public void setEnabledProtocols(String[] enabledProtocols)
    {
        if (enabledProtocols == null)
        {
            this.enabledProtocols = null;
        }
        else
        {
            this.enabledProtocols = enabledProtocols.clone();
        }
    }
    
    public void setValidateServerCert(boolean validateServerCert)
    {
        this.validateServerCert = validateServerCert;
    }
    
    public void setTrustStorePath(String trustStorePath)
    {
        this.trustStorePath = trustStorePath;
    }
    
    public void setTrustStorePass(String trustStorePass)
    {
        this.trustStorePass = trustStorePass;
    }
    
    public void setTrustStorePassKey(String trustStorePassKey)
    {
        this.trustStorePassKey = trustStorePassKey;
    }
}
