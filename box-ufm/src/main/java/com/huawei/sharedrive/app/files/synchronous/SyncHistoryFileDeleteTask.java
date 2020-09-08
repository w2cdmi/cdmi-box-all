package com.huawei.sharedrive.app.files.synchronous;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("syncHistoryFileDeleteTask")
public class SyncHistoryFileDeleteTask extends QuartzJobTask implements Runnable
{
    private static final long DAY_TO_MS = 1 * 24L * 60 * 60 * 1000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncHistoryFileDeleteTask.class);
    
    private static final long MAX_ALIVE = 70 * 60 *1000;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            LOGGER.info("Start Delete SyncHistoryFiles.");
            doDelete(this.getParameter());
            deleteTempExpiredFile();
            LOGGER.info("End Delete SyncHistoryFiles.");
        }
        catch (Exception e)
        {
            LOGGER.error("Delete SyncHistoryFiles failed.", e);
            record.setSuccess(false);
            record.setOutput(e.getMessage());
        }
    }
    
    /**
    * 供业务调用，固定删除一天以前的历史文件
    * 
    */
   @Override
   public void run()
   {
    try
    {
        deleteSyncHistoryFiles(1);
    }
    catch (InterruptedException e)
    {
        LOGGER.warn(e.getMessage());
    }
   }
    
    
    private void deleteExpiredFile(File tempFile)
    {
       if(tempFile.isDirectory())
       {
           return;
       }
       String[] fileNameArray = tempFile.getName().split("_");
       if(fileNameArray.length != 2)
       {
           return;
       }
       long createdTime = 0L;
       try
       {
           createdTime = Long.parseLong(fileNameArray[1]);
           if(System.currentTimeMillis() - createdTime > MAX_ALIVE)
           {
               try
               {
                   FileUtils.forceDelete(tempFile);
               }
               catch(Exception e)
               {
                   LOGGER.warn("Can not delete the file " + tempFile.getName());
               }
           }
       }
       catch(NumberFormatException e)
       {
           return;
       }
        
    }

    
    /**
     * 异步删除文件
     * 
     * @param fileToDeleteArray
     */
    private void deleteFile(List<File> fileToDeleteArray)
    {
        for (File file : fileToDeleteArray)
        {
            /**
             * 不存在继续
             */
            if (!file.exists())
            {
                continue;
            }
            
            if (!file.delete())
            {
                continue;
            }
        }
        
    }
    
    private void deleteSyncFilesByRecursive(int fileReserveDays, long currentTimeMillis, File dFile)
        throws InterruptedException
    {
        File[] subFiles = dFile.listFiles();
        
        if (subFiles == null)
        {
            return;
        }
        
        String fileName = null;
        Long timeMillis = null;
        int count = 0;
        List<File> fileToDeleteArray = new ArrayList<File>(1000);
        
        for (File file : subFiles)
        {
            if (!file.exists())
            {
                continue;
            }
            if (file.isDirectory())
            {
                deleteSyncFilesByRecursive(fileReserveDays, currentTimeMillis, file);
            }
            else
            {
                fileName = file.getName();
                timeMillis = SyncFileUtils.getTimeMillisFromFileName(fileName);
                
                if (timeMillis + fileReserveDays * DAY_TO_MS < currentTimeMillis)
                {
                    fileToDeleteArray.add(file);
                    count++;
                }
                
                if (count >= 1000)
                {
                    deleteFile(fileToDeleteArray);
                    // 重新计数并释放 CPU
                    count = 0;
                    fileToDeleteArray.clear();
                    Thread.sleep(0);
                }
            }
        }
        
        deleteFile(fileToDeleteArray);
    }
    
    private void deleteSyncHistoryFiles(int days) throws InterruptedException
    {
        long currentTimeMillis = System.currentTimeMillis();
        
        StringBuffer pathBuf = new StringBuffer(SynConstants.SYNC_METADATA_TEMP_FILE_PATH);
        pathBuf.append('/');
        
        File dFile = new File(pathBuf.toString());
        if (!dFile.exists() || !dFile.isDirectory())
        {
            return;
        }
        deleteSyncFilesByRecursive(days, currentTimeMillis, dFile);
    }
    
    /**
     * 异步删除文件
     * 
     * @param fileToDeleteArray
     */
    private void deleteTempExpiredFile()
    {
        LOGGER.info("begin to delete expired file");
        try
        {
            String dbFilePath = getLocalFilePath();
            File rootFolder = new File(dbFilePath);
            if(!rootFolder.exists() || rootFolder.isFile())
            {
                LOGGER.warn("Is not existed directory for key db.file.path");
                return;
            }
            File[] files = rootFolder.listFiles();
            if(null == files)
            {
                return;
            }
            for(File tempFile: files)
            {
                deleteExpiredFile(tempFile);
            }
        }
        catch(Exception e)
        {
            LOGGER.warn("", e);
        }
        finally
        {
            LOGGER.info("end to delete expired file");
        }
    }
    
    private void doDelete(String days) throws InterruptedException
    {
        int fileReserveDays = Integer.parseInt(days);
        
        long currentTimeMillis = System.currentTimeMillis();
        File dFile = null;
        
        StringBuffer pathBuf = new StringBuffer(SynConstants.SYNC_METADATA_TEMP_FILE_PATH);
        pathBuf.append('/');
        
        dFile = new File(pathBuf.toString());
        if (!dFile.exists() || !dFile.isDirectory())
        {
            return;
        }
        deleteSyncFilesByRecursive(fileReserveDays, currentTimeMillis, dFile);
    }

        private String getLocalFilePath()
        {
            return PropertiesUtils.getProperty("filebridge.localFile", "/opt/tomcat_ufm/temp/",  PropertiesUtils.BundleName.BRIDGE);
        }
    
}
