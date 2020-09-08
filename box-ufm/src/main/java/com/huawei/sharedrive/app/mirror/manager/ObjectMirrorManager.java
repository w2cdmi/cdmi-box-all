package com.huawei.sharedrive.app.mirror.manager;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;

@Service("objectMirrorManager")
public class ObjectMirrorManager
{
    /**
     * 删除对象的镜像及镜像关系
     * @param objectId
     */
    
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired
    private MirrorObjectManager mirrorObjectManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMirrorManager.class);
    
    /**
     * 删除对象的mirror
     * @param objectId
     */
    public void clearMirrorObject(String objectId)
    {
        List<String> lstDestObjectId = mirrorObjectManager.getDestObjectIdBySrcObjectId(objectId);
        
        if(lstDestObjectId == null || lstDestObjectId.isEmpty())
        {
            return;
        }
        
        for(String destObjectId : lstDestObjectId)
        {
            deleteMirrorObject(destObjectId);
        }
        
//        mirrorObjectManager.deleteBySrcObjectId(objectId);
    }

    /**
     * 删除镜像
     * @param mirror
     */
    public void deleteMirrorObject(String destObjectId)
    {
        try
        {
            ObjectReference objRf = new ObjectReference();
            objRf.setId(destObjectId);
            filesInnerManager.decreaseRefObjectCount(objRf);
        }catch(Exception e)
        {
            LOGGER.warn(e.getMessage(),e);
        }
        
    }
    
    public void createMirrorObject(CopyTask task,INode iNode, ObjectReference objRef,ObjectReference oldObjRef)
    {
        
        //该函数createObjectMirrorShip是不是只会在数据迁移使用？
        MirrorObject mirrorObject = new MirrorObject();
        mirrorObject.setOwnedBy(iNode.getOwnedBy());
        mirrorObject.setSrcObjectId(objRef.getId());
        mirrorObject.setDestObjectId(oldObjRef.getId());
        mirrorObject.setSrcResourceGroupId(objRef.getResourceGroupId());
        mirrorObject.setDestResourceGroupId(oldObjRef.getResourceGroupId());
        mirrorObject.setType(task.getCopyType());
        mirrorObject.setCreateAt(new Date());
        mirrorObject.setPolicyId(task.getPolicyId());
        
        mirrorObjectManager.insert(mirrorObject);
    }

    
}
