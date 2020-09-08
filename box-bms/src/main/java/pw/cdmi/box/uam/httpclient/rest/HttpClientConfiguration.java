package pw.cdmi.box.uam.httpclient.rest;

public class HttpClientConfiguration
{
    /**
     * The amount of time to wait (in milliseconds) for data to be transfered over an
     * established, open connection before the connection is timed out. A value of 0 means
     * infinity, and is not recommended.
     */
    private int socketTimeout = 100 * 1000;
    
    /**
     * The amount of time to wait (in milliseconds) when initially establishing a
     * connection before giving up and timing out. A value of 0 means infinity, and is not
     * recommended.
     */
    private int connectionTimeout = 10 * 1000;
    
    /** The maximum number of open HTTP connections. */
    private int maxTotalConnections = 10000;
    
    private int perRouteMaxConnections = 400;
    
    public int getSocketTimeout()
    {
        return socketTimeout;
    }
    
    public void setSocketTimeout(int socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }
    
    public int getConnectionTimeout()
    {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }
    
    public int getMaxTotalConnections()
    {
        return maxTotalConnections;
    }
    
    public void setMaxTotalConnections(int maxTotalConnections)
    {
        this.maxTotalConnections = maxTotalConnections;
    }
    
    public int getPerRouteMaxConnections()
    {
        return perRouteMaxConnections;
    }
    
    public void setPerRouteMaxConnections(int perRouteMaxConnections)
    {
        this.perRouteMaxConnections = perRouteMaxConnections;
    }
    
}
