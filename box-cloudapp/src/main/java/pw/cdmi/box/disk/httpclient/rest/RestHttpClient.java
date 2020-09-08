package pw.cdmi.box.disk.httpclient.rest;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RestHttpClient
{
    private static Logger logger = LoggerFactory.getLogger(RestHttpClient.class);
    
    private static volatile CloseableHttpClient instance = null;
    
    private RestHttpClient()
    {
        
    }
    
    public static synchronized CloseableHttpClient getInstance(HttpClientConfiguration config)
    {
        if (instance == null)
        {
            configureHttpClient(config);
        }
        return instance;
    }
    
    private static void configureHttpClient(HttpClientConfiguration config)
    {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getMaxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(config.getPerRouteMaxConnections());
        
        instance = HttpClients.custom().setConnectionManager(connectionManager).build();
        logger.info("Init HttpClientConnection with Max " + config.getMaxTotalConnections() + " MaxRoute :"
            + config.getPerRouteMaxConnections());
    }
    
}
