package com.huawei.sharedrive.app.log.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.utils.file.TempFileUtils;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.JsonUtils;

/**
 * @author l90003768
 * 
 */
public final class LoggerCacheWriter
{
    private LoggerCacheWriter()
    {
        
    }
    
    /**
     * 最大文件数
     */
    static final int MAX_FILE_COUNT = 5000;
    
    final static String SUFFIX = ".log";
    
    private static int curWriterFile = 0;
    
    /**
     * 当前日志文件的最早写入时间
     */
    private static long beginTime = 0;
    
    private static int curWriterLine = 0;
    
    static final String FILENAME = "cacheLog";
    
    private static Logger logger = LoggerFactory.getLogger(LoggerCacheWriter.class);
    
    public static final int SIZE_PER_FILE = 1000;
    
    private static BufferedWriter writer;
    
    private static final int MAX_QUEUE_SIZE = 10000;
    
    private static WriteLogThread wThread = new WriteLogThread();
    
    private static LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(MAX_QUEUE_SIZE);
    
    static
    {
        initSignal();
        eventQueue.clear();
        wThread.start();
    }
    
    /**
     * 超时时间
     */
    public static final long TIMEOUT = 2 * 60 * 1000;
    
    public static void pushLog(Event event)
    {
        eventQueue.add(event);
    }
    
    private static class WriteLogThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                writeLog(eventQueue.take());
                
            }
            catch (Exception e)
            {
                logger.warn(e.getMessage(), e);
            }
        }
    }
    
    public static void writeLog(Event event) throws IOException
    {
        BufferedWriter curWriter = null;
        boolean needCloseWriter = false;
        int thisWriteLine = curWriterLine;
        try
        {
            if (thisWriteLine > SIZE_PER_FILE || System.currentTimeMillis() - beginTime > TIMEOUT)
            {
                switchNextWriter();
            }
            else if (thisWriteLine == SIZE_PER_FILE)
            {
                needCloseWriter = true;
            }
            UserLog userLog = event.convertToUserLog();
            String jsonData = JsonUtils.toJson(userLog);
            jsonData = URLEncoder.encode(jsonData, "utf-8");
            curWriter = getWriter();
            curWriter.write(jsonData + END);
            curWriter.newLine();
            curWriter.flush();
            curWriterLine++;
        }
        finally
        {
            if (needCloseWriter)
            {
                IOUtils.closeQuietly(curWriter);
            }
        }
    }
    
    /**
     * @throws IOException
     */
    private static void switchNextWriter() throws IOException
    {
        curWriterLine = 1;
        curWriterFile++;
        if (curWriterFile == MAX_FILE_COUNT)
        {
            curWriterFile = 0;
        }
        resetWriter();
    }
    
    static final String END = "@@@END***";
    
    public static int getCurrentWriteFile()
    {
        return curWriterFile;
    }
    
    private static File getTempWriteFile()
    {
        return new File(TempFileUtils.getTempPath() + FILENAME + curWriterFile + SUFFIX);
    }
    
    private static BufferedWriter getWriter() throws IOException
    {
        if (null == writer)
        {
            synchronized (LoggerCacheWriter.class)
            {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getTempWriteFile(),
                    true), "utf8"));
                beginTime = System.currentTimeMillis();
                writer.write(BEGIN_TIME + beginTime);
                writer.newLine();
                writer.flush();
            }
        }
        return writer;
    }
    
    /**
     * 初始化起始位置
     */
    private static void initSignal()
    {
        File rootFile = new File(TempFileUtils.getTempPath());
        if (rootFile.isFile())
        {
            curWriterFile = 0;
            return;
        }
        File[] subFiles = rootFile.listFiles();
        
        if (null == subFiles || subFiles.length == 0)
        {
            curWriterFile = 0;
            return;
        }
        File tempFile = null;
        long lastModified = Long.MIN_VALUE;
        for (File tFile : subFiles)
        {
            if (tFile.isDirectory())
            {
                continue;
            }
            if (!tFile.getName().startsWith(FILENAME))
            {
                continue;
            }
            if (tFile.lastModified() > lastModified)
            {
                lastModified = tFile.lastModified();
                tempFile = tFile;
            }
        }
        if (null == tempFile)
        {
            curWriterFile = 0;
            return;
        }
        String number = tempFile.getName()
            .replaceAll(LoggerCacheWriter.FILENAME, "")
            .replaceAll(LoggerCacheWriter.SUFFIX, "");
        try
        {
            curWriterFile = Integer.parseInt(number);
            curWriterFile++;
            beginTime = System.currentTimeMillis();
        }
        catch (NumberFormatException e)
        {
            logger.error("", e);
            curWriterFile = 0;
        }
    }
    
    static final String BEGIN_TIME = "[beginTime]";
    
    public static void closeWriter(int readerFile)
    {
        if (readerFile == curWriterFile)
        {
            IOUtils.closeQuietly(writer);
        }
    }
    
    private static void resetWriter() throws IOException
    {
        synchronized (LoggerCacheWriter.class)
        {
            if (System.currentTimeMillis() - beginTime > TIMEOUT)
            {
                IOUtils.closeQuietly(writer);
            }
            writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(getTempWriteFile(), true), "utf8"));
            beginTime = System.currentTimeMillis();
            writer.write(BEGIN_TIME + beginTime);
            writer.newLine();
            writer.flush();
        }
    }
}
