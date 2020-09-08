package com.huawei.sharedrive.app.files.service.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.service.FileService;

public class DedupObjectTask extends Task
{
    private static Logger logger = LoggerFactory.getLogger(DedupObjectTask.class);
    
    private INode fileNode;
    
    private FileService fileService;
    
    private ObjectReference objectReference;
    
    public DedupObjectTask(FileService fileService, INode fileNode, ObjectReference objectReference)
    {
        this.fileService = fileService;
        this.fileNode = INode.valueOf(fileNode);
        this.objectReference = objectReference;
    }
    
    @Override
    public void execute()
    {
        if (null == fileNode)
        {
            logger.warn(" fileNode is null");
            return;
        }
        if (null == objectReference)
        {
            logger.warn("objectReference is null");
            return;
        }
        logger.info(" begin DedupObjectTask:" + objectReference.getId());
        
        try
        {
            if (fileService.dedupObject(fileNode, objectReference))
            {
                // 如果執行了重刪，則刪除老對象
                new AyncDeleteObjectTask(fileService, objectReference).execute();
            }
        }
        catch (BaseRunException e)
        {
            logger.warn(e.getMessage(), e);
        }
        
    }
    
    public INode getFileNode()
    {
        return fileNode;
    }
    
    public FileService getFileService()
    {
        return fileService;
    }
    
    @Override
    public String getName()
    {
        return this.getClass().getName();
    }
    
    public ObjectReference getObjectReference()
    {
        return objectReference;
    }
    
    public void setFileNode(INode fileNode)
    {
        this.fileNode = INode.valueOf(fileNode);
    }
    
    public void setFileService(FileService fileService)
    {
        this.fileService = fileService;
    }
    
    public void setObjectReference(ObjectReference objectReference)
    {
        this.objectReference = objectReference;
    }
}
