package pw.cdmi.file.engine.core.thrift;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.hw.TSSLTransportFactory;
import org.apache.thrift.transport.hw.TSSLTransportFactory.TSSLTransportParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.common.job.ThreadPool;
import pw.cdmi.core.utils.EDToolsEnhance;

public class DSSLocalThriftServer implements Runnable
{

    private static Logger logger = LoggerFactory.getLogger(DSSLocalThriftServer.class);
    
    private int minWorkerThreads = 1;
    
    private int maxWorkerThreads = 10;
    
    private int maxSelectorThreads = 8;
    
    private String bindAddr;
    
    private int port;
    
    private int clientTimeout = 40000;
    
    private boolean useSSL = true;
    
    private String keyStorePath;
    
    private String keyStorePass;
    
    private String keyStorePassKey;
    
    private String trustStorePath;
    
    private String trustStorePass;
    
    private String trustStorePassKey;
    
    private String[] cipherSuites;
    
    private String[] enabledProtocols;
    
    private String name;
    
    private Map<String, TProcessor> processorMap = new HashMap<String, TProcessor>(10);
    
    private TServer server;
    
    public void destroy()
    {
        if (server != null)
        {
            server.stop();
        }
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setBindAddr(String bindAddr)
    {
        this.bindAddr = bindAddr;
    }
    
    public void setUseSSL(boolean useSSL)
    {
        this.useSSL = useSSL;
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
    
    /**
     * @param port the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setClientTimeout(int clientTimeout)
    {
        this.clientTimeout = clientTimeout;
    }
    
    public void setMinWorkerThreads(int minWorkerThreads)
    {
        this.minWorkerThreads = minWorkerThreads;
    }
    
    public void setMaxWorkerThreads(int maxWorkerThreads)
    {
        this.maxWorkerThreads = maxWorkerThreads;
    }
    
    public void setMaxSelectorThreads(int maxSelectorThreads)
    {
        this.maxSelectorThreads = maxSelectorThreads;
    }
    
    public void setKeyStorePath(String keyStorePath)
    {
        this.keyStorePath = keyStorePath;
    }
    
    public void setKeyStorePass(String keyStorePass)
    {
        this.keyStorePass = keyStorePass;
    }
    
    public void setKeyStorePassKey(String keyStorePassKey)
    {
        this.keyStorePassKey = keyStorePassKey;
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
    
    /**
     * @param processorMap the processorMap to set
     */
    public void setProcessorMap(Map<String, TProcessor> processorMap)
    {
        this.processorMap = processorMap;
    }
    
    @Override
    public void run()
    {
        TMultiplexedProcessor processor = new TMultiplexedProcessor();
        for (Map.Entry<String, TProcessor> entry : processorMap.entrySet())
        {
            logger.info("Thrift server >>>>>> " + name + " regist " + entry.getKey());
            processor.registerProcessor(entry.getKey(), entry.getValue());
        }
        if (useSSL)
        {
            initSSL(processor);
        }
        else
        {
            initNoSSL(processor);
        }
        logger.info("Thrift server >>>>>> " + name + " started");
        server.serve();
    }
    
    private void initSSL(TMultiplexedProcessor processor)
    {
        try
        {
            TServerSocket serverSocket = createSSLServerSocket();
            org.apache.thrift.server.TThreadPoolServer.Args args = new org.apache.thrift.server.TThreadPoolServer.Args(
                serverSocket);
            args.processor(processor);
            args.minWorkerThreads(minWorkerThreads);
            args.maxWorkerThreads(maxWorkerThreads);
            args.transportFactory(new TFramedTransport.Factory(1024 * 1024 * 1024));
            server = new TThreadPoolServer(args);
        }
        catch (TTransportException e)
        {
            throw new IllegalStateException("error occur when create server socket", e);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("error occur when create server socket", e);
        }
    }
    
    private void initNoSSL(TMultiplexedProcessor processor)
    {
        try
        {
            TNonblockingServerSocket serverSocket = createNoSSLServerSocket();
            org.apache.thrift.server.TThreadedSelectorServer.Args args = new org.apache.thrift.server.TThreadedSelectorServer.Args(serverSocket);
            args.processor(processor);
            args.selectorThreads(maxSelectorThreads);
            args.workerThreads(maxWorkerThreads);
            args.maxReadBufferBytes = 1024*1024;//设置最大缓存为1M，防止非法的请求（如HTTP请求）连接到该端口后，会导致读内存分配过大，出现OOM。
            args.transportFactory(new TFramedTransport.Factory(1024 * 1024 * 1024));
            server = new TThreadedSelectorServer(args);
        }
        catch (TTransportException e)
        {
            throw new IllegalStateException("error occur when create server socket", e);
        }
        
    }
    
    private void setCertParams(TSSLTransportParameters params) throws IOException
    {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader == null)
        {
            throw new IllegalStateException("can not get class loader for " + this.getClass().getName());
        }
        setKeyStore(params, classLoader);
        setTrustStore(params, classLoader);
    }
    
    private void setKeyStore(TSSLTransportParameters params, ClassLoader classLoader) throws IOException
    {
        if (StringUtils.isBlank(keyStorePath))
        {
            throw new IllegalArgumentException("keyStorePath is null");
        }
        URL url = classLoader.getResource(keyStorePath);
        if (url == null)
        {
            throw new IOException("No keyStore exist for " + keyStorePath);
        }
        if (StringUtils.isBlank(keyStorePass))
        {
            throw new IllegalArgumentException("keyStorePass is null");
        }
        if (StringUtils.isBlank(keyStorePassKey))
        {
            throw new IllegalArgumentException("keyStorePassKey is null");
        }
        String realKeyStorePass = EDToolsEnhance.decode(keyStorePass, keyStorePassKey);
        params.setKeyStore(url.getFile(), realKeyStorePass);
    }
    
    private void setTrustStore(TSSLTransportParameters params, ClassLoader classLoader) throws IOException
    {
        if (StringUtils.isBlank(trustStorePath))
        {
            throw new IllegalArgumentException("trustStorePath is null");
        }
        URL url = classLoader.getResource(trustStorePath);
        if (url == null)
        {
            throw new IOException("No trustStore exist for " + trustStorePath);
        }
        if (StringUtils.isBlank(trustStorePass))
        {
            throw new IllegalArgumentException("trustStorePass is null");
        }
        if (StringUtils.isBlank(trustStorePassKey))
        {
            throw new IllegalArgumentException("trustStorePassKey is null");
        }
        String realTrustStorePass = EDToolsEnhance.decode(trustStorePass, trustStorePassKey);
        params.setTrustStore(url.getFile(), realTrustStorePass);
    }
    
    private TServerSocket createSSLServerSocket() throws TTransportException, IOException
    {
        TSSLTransportParameters params = null;
        if (cipherSuites == null || cipherSuites.length == 0)
        {
            params = new TSSLTransportParameters();
        }
        else
        {
            params = new TSSLTransportParameters("TLS", cipherSuites);
        }
        params.setEnabledProtocols(enabledProtocols);
        params.requireClientAuth(true);
        setCertParams(params);
        if (StringUtils.isBlank(this.bindAddr))
        {
            return TSSLTransportFactory.getServerSocket(getPort(), clientTimeout, null, params);
        }
        return TSSLTransportFactory.getServerSocket(getPort(),
            clientTimeout,
            InetAddress.getByName(bindAddr),
            params);
    }
    
    private TNonblockingServerSocket createNoSSLServerSocket() throws TTransportException
    {
        if (StringUtils.isBlank(this.bindAddr))
        {
            return new TNonblockingServerSocket(getPort(), clientTimeout);
        }
        return new TNonblockingServerSocket(new InetSocketAddress(bindAddr, getPort()), clientTimeout);
    }
    
    public void start()
    {
        /*if (StringUtils.isBlank(name))
        {
            new Thread(this).start();
        }
        else
        {
            new Thread(this, name).start();
        }*/
        ThreadPool.execute(this);
    }
}
