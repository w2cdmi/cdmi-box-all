package pw.cdmi.file.engine.manage.datacenter.statistics;

public final class ConcUploadStatistician
{
    private ConcUploadStatistician()
    {
        
    }
    
    private static int current = 0;
    
    private static int max = 0;
    
    public static void startUpload()
    {
        synchronized (ConcUploadStatistician.class)
        {
            ConcUploadStatistician.current++;
            if (ConcUploadStatistician.current > ConcUploadStatistician.max)
            {
                ConcUploadStatistician.max = ConcUploadStatistician.current;
            }
        }
    }
    
    public static void endUpload()
    {
        synchronized (ConcUploadStatistician.class)
        {
            ConcUploadStatistician.current--;
        }
    }
    
    public static int getCurrent()
    {
        return current;
    }
    
    public static int getMax()
    {
        return max;
    }
    
    public static int getAndResetMax()
    {
        int tempMax = max;
        max = current;
        return tempMax;
    }
    
}
