package com.huawei.sharedrive.app.log.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.utils.file.TempFileUtils;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.JsonUtils;

/**
 * 缓存日志读取类
 * 
 * @author l90003768
 * 
 */
public final class LoggerCacheReader
{
    private LoggerCacheReader()
    {
        
    }
    
    private static int curReaderFile = 0;
    
    private static Logger logger = LoggerFactory.getLogger(LoggerCacheReader.class);
    
    private static final int SIZE_PER_FILE = 1000;
    
    private final static long TIMEOUT = 22 * 6 * 1000;
    
    static
    {
        initSignal();
    }
    
    /**
     * 存在新的日志文件
     * 
     * @return
     */
    public static boolean findNewLoggerFile()
    {
        File rootFile = new File(TempFileUtils.getTempPath());
        File[] children = rootFile.listFiles();
        
        if (children == null)
        {
            return false;
        }
        for (File child : children)
        {
            if (child.getName().endsWith(LoggerCacheWriter.SUFFIX))
            {
                return true;
            }
        }
        return false;
    }
    
    public static List<UserLog> readEventList() throws IOException, LoggerReadingException
    {
        LinkedList<UserLog> eventList = new LinkedList<UserLog>();
        UserLog tempEvt = null;
        String tempData = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        FileInputStream fs = null;
        InputStreamReader isr = null;
        try
        {
            fs = new FileInputStream(getTempFile());
            isr = new InputStreamReader(fs, "utf8");
            reader = new BufferedReader(isr);
            for (int i = 0; i < SIZE_PER_FILE * 2; i++)
            {
                tempData = reader.readLine();
                if (tempData == null || "".equals(tempData))
                {
                    break;
                }
                // 第一行数据为该日志的写入时间，忽略
                if (i == 0)
                {
                    checkIsReading(tempData);
                    continue;
                }
                sb.append(tempData);
                if (tempData.endsWith(LoggerCacheWriter.END))
                {
                    try
                    {
                        tempEvt = convertIntoEvent(sb);
                        eventList.add(tempEvt);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        logger.warn("Can not parse into eventList, ignore the file data for file: "
                            + curReaderFile, e);
                    }
                    sb = new StringBuilder();
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(reader);
        }
        return eventList;
    }
    
    /**
     * 切换到下一个日志文件
     * 
     * @param removeFile 删除本日志文件
     */
    @SuppressWarnings("static-access")
    public static void switchToNextReadFile(boolean removeFile)
    {
        File file = getTempFile();
        int previewsReaderFile = curReaderFile;
        synchronized (LoggerCacheReader.class)
        {
            curReaderFile++;
            if (curReaderFile == LoggerCacheWriter.MAX_FILE_COUNT)
            {
                curReaderFile = 0;
            }
        }
        if (!removeFile)
        {
            return;
        }
        logger.warn("Delete file " + curReaderFile);
        for (int i = 0; i < 3; i++)
        {
            try
            {
                LoggerCacheWriter.closeWriter(previewsReaderFile);
                FileUtils.forceDelete(file);
                break;
            }
            catch (IOException e)
            {
                logger.warn("Fail to delete file " + curReaderFile);
                try
                {
                    Thread.currentThread().sleep(100);
                }
                catch (InterruptedException e1)
                {
                    logger.warn("", e1);
                }
            }
        }
        
    }
    
    /**
     * @param tempData
     * @throws Exception
     */
    private static void checkIsReading(String tempData) throws LoggerReadingException
    {
        if (curReaderFile == LoggerCacheWriter.getCurrentWriteFile())
        {
            long beginTime = getBeginTime(tempData);
            if (System.currentTimeMillis() - beginTime < TIMEOUT)
            {
                throw new LoggerReadingException("The file is reading, can not be the read file: "
                    + curReaderFile);
            }
        }
    }
    
    private static UserLog convertIntoEvent(StringBuilder sb) throws UnsupportedEncodingException
    {
        String value = sb.substring(0, sb.length() - LoggerCacheWriter.END.length());
        value = URLDecoder.decode(value, "utf-8");
        return JsonUtils.stringToObject(value, UserLog.class);
    }
    
    /**
     * 获取该日志缓存文件的生成时间
     * 
     * @param tempData
     * @return
     */
    private static long getBeginTime(String tempData)
    {
        if (tempData == null)
        {
            throw new InnerException("参数为空");
        }
        try
        {
            return Long.parseLong(tempData.substring(tempData.lastIndexOf("]") + 1));
        }
        catch (NumberFormatException e)
        {
            logger.error("Wrong data is" + tempData, e);
            return 0;
        }
    }
    
    private static File getTempFile()
    {
        synchronized (LoggerCacheReader.class)
        {
            return new File(TempFileUtils.getTempPath() + LoggerCacheWriter.FILENAME
                + curReaderFile + LoggerCacheWriter.SUFFIX);
        }
    }
    
    /**
     * 初始化起始位置
     */
    private static synchronized void initSignal()
    {
        File rootFile = new File(TempFileUtils.getTempPath());
        if (rootFile.isFile())
        {
            curReaderFile = 0;
            return;
        }
        
        File[] children = rootFile.listFiles();
        if (null == children)
        {
            curReaderFile = 0;
            return;
        }
        if (children.length == 0)
        {
            curReaderFile = 0;
        }
        File tempFile = null;
        long lastModified = Long.MAX_VALUE;
        for (File tFile : children)
        {
            if (tFile.isDirectory())
            {
                continue;
            }
            if (!tFile.getName().startsWith(LoggerCacheWriter.FILENAME))
            {
                continue;
            }
            if (tFile.lastModified() < lastModified)
            {
                lastModified = tFile.lastModified();
                tempFile = tFile;
            }
        }
        if (tempFile == null)
        {
            curReaderFile = 0;
            return;
        }
        String number = tempFile.getName()
            .replaceAll(LoggerCacheWriter.FILENAME, "")
            .replaceAll(LoggerCacheWriter.SUFFIX, "");
        try
        {
            curReaderFile = Integer.parseInt(number);
        }
        catch (NumberFormatException e)
        {
            logger.error("", e);
            curReaderFile = 0;
        }
    }
    
}
