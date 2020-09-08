package com.huawei.sharedrive.app.files.synchronous;

import java.io.File;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.files.domain.INode;

import pw.cdmi.core.utils.HashTool;

public final class SyncFileUtils
{
    private SyncFileUtils()
    {
        
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncFileUtils.class);
    
    /**
     * 异步删除文件
     * 
     * @param filePath
     */
    public static void deleteFile(String filePath)
    {
        /**
         * 不存在就退出
         */
        File dFile = new File(filePath);
        if (!dFile.exists())
        {
            return;
        }
        
        if (!dFile.delete())
        {
            LOGGER.error("file remove failed,filePath:" + filePath + ";and async delete it");
        }
        
    }
    
    public static String buildSyncPath(String fileRootDir, long ownerId, long reSyncVersion, INode syncFolder)
        throws InternalServerErrorException
    {
        // 构成方式不能随便修改，否则影响SyncHistoryFileDeleteTask.java
        
        StringBuffer pathBuf = new StringBuffer(fileRootDir);
        pathBuf.append('/').append(HashTool.apply(String.valueOf(ownerId)) % SynConstants.SUB_FOLDER_MAXNUM);
        pathBuf.append('/').append(ownerId).append('/');
        
        File folder = new File(pathBuf.toString());
        if (!folder.exists())
        {
            if (!folder.mkdirs())
            {
                throw new InternalServerErrorException("folder.mkdirs fail");
            }
        }
        StringBuffer fileBuf = new StringBuffer(pathBuf);
        File file = null;
        int random = 0;
        SecureRandom randomGen = new SecureRandom();
        while (true)
        {
            if (null != syncFolder)
            {
                fileBuf.append(syncFolder.getId());
            }
            fileBuf.append(SynConstants.SYNCFILE_SPLIT).append(System.currentTimeMillis());
            random = randomGen.nextInt(10000);
            fileBuf.append(SynConstants.RANDOM_SPLIT).append(random);
            file = new File(fileBuf.toString());
            if (!file.exists())
            {
                break;
            }
            
            fileBuf = new StringBuffer(pathBuf);
        }
        
        return fileBuf.toString();
    }
    
    public static String buildSyncPath(long ownerId, long nodeId)
        throws InternalServerErrorException
    {
        // 构成方式不能随便修改，否则影响SyncHistoryFileDeleteTask.java
        
        StringBuffer pathBuf = new StringBuffer(SynConstants.SYNC_METADATA_TEMP_FILE_PATH);
        pathBuf.append('/').append(HashTool.apply(String.valueOf(ownerId)) % SynConstants.SUB_FOLDER_MAXNUM);
        pathBuf.append('/').append(ownerId).append('/');
        
        File folder = new File(pathBuf.toString());
        if (!folder.exists())
        {
            if (!folder.mkdirs())
            {
                throw new InternalServerErrorException("folder.mkdirs fail");
            }
        }
        StringBuffer fileBuf = new StringBuffer(pathBuf);
        File file = null;
        int random = 0;
        SecureRandom randomGen = new SecureRandom(); 
        while (true)
        {
            fileBuf.append(nodeId);
            fileBuf.append(SynConstants.SYNCFILE_SPLIT).append(System.currentTimeMillis());
            random = randomGen.nextInt(10000);
            fileBuf.append(SynConstants.RANDOM_SPLIT).append(random);
            file = new File(fileBuf.toString());
            if (!file.exists())
            {
                break;
            }
            fileBuf = new StringBuffer(pathBuf);
        }
        
        return fileBuf.toString();
    }
    
    public static long getTimeMillisFromFileName(String fileName)
    {
        int begainIndex = 0;
        int endndex = 0;
        begainIndex = fileName.indexOf(SynConstants.SYNCFILE_SPLIT) + 1;
        endndex = fileName.lastIndexOf(SynConstants.RANDOM_SPLIT);
        long timeMillis = Long.parseLong(fileName.substring(begainIndex, endndex));
        return timeMillis;
    }
    
}
