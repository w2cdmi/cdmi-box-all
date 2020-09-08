package pw.cdmi.file.engine.manage.datacenter.statistics;

public final class ConcDownloadStatistician
{
    private ConcDownloadStatistician()
    {
        
    }
    
    private static int current = 0;
    
    private static int max = 0;
    
    public static void startDownload()
    {
        synchronized (ConcDownloadStatistician.class)
        {
            ConcDownloadStatistician.current++;
            if (ConcDownloadStatistician.current > ConcDownloadStatistician.max)
            {
                ConcDownloadStatistician.max = ConcDownloadStatistician.current;
            }
        }
    }
    
    public static void endDownload()
    {
        synchronized (ConcDownloadStatistician.class)
        {
            ConcDownloadStatistician.current--;
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
