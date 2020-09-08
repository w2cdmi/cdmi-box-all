package pw.cdmi.file.engine.core.thrift.client;

/**
 * 
 * @author s90006125
 * 
 */
public class ThriftClientPoolConfig
{
    private int maxActive = 0;
    
    private int maxIdle = 0;
    
    private int minIdle = 0;
    
    private int maxWait = 0;
    
    private boolean testOnReturn = false;
    
    private boolean testOnBorrow = true;
    
    public int getMaxActive()
    {
        return maxActive;
    }
    
    public void setMaxActive(int maxActive)
    {
        this.maxActive = maxActive;
    }
    
    public int getMaxIdle()
    {
        return maxIdle;
    }
    
    public void setMaxIdle(int maxIdle)
    {
        this.maxIdle = maxIdle;
    }
    
    public int getMinIdle()
    {
        return minIdle;
    }
    
    public void setMinIdle(int minIdle)
    {
        this.minIdle = minIdle;
    }
    
    public int getMaxWait()
    {
        return maxWait;
    }
    
    public void setMaxWait(int maxWait)
    {
        this.maxWait = maxWait;
    }
    
    public boolean isTestOnReturn()
    {
        return testOnReturn;
    }
    
    public void setTestOnReturn(boolean testOnReturn)
    {
        this.testOnReturn = testOnReturn;
    }
    
    public boolean isTestOnBorrow()
    {
        return testOnBorrow;
    }
    
    public void setTestOnBorrow(boolean testOnBorrow)
    {
        this.testOnBorrow = testOnBorrow;
    }
}
