package com.huawei.sharedrive.app.mirror.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.ObjectFingerprintIndexDAO;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectFingerprintIndex;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;
import com.huawei.sharedrive.app.mirror.service.MirrorObjectService;

@Service("mirrorObjectManager")
public class MirrorObjectManager
{
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
    @Autowired
    private ObjectFingerprintIndexDAO objectFrIndexDAO;
    
    @Autowired
    private MirrorObjectService mirrorObjectService;
    
    @Autowired
    private ObjectMirrorManager objectMirrorManager;
    
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MirrorObjectManager.class);
    
    public MirrorObject isCopyTaskAlreadyExist(CopyTask copyTask)
    {
        if (copyTask == null)
        {
            return null;
        }
        ObjectReference objectReference = objectReferenceDAO.get(copyTask.getSrcObjectId());
        if (null == objectReference)
        {
            return null;
        }
        List<ObjectFingerprintIndex> lstObjectFingerprintIndex = objectFrIndexDAO.getBySha1(objectReference.getSha1());
        if (null == lstObjectFingerprintIndex || lstObjectFingerprintIndex.isEmpty())
        {
            return null;
        }
        MirrorObject mirrorObject = null;
        for (ObjectFingerprintIndex objectFingerprintIndex : lstObjectFingerprintIndex)
        {
            objectReference = objectReferenceDAO.get(objectFingerprintIndex.getId());
            if (null == objectReference)
            {
                continue;
            }
            if (objectReference.getResourceGroupId() == copyTask.getDestResourceGroupId())
            {
                mirrorObject = new MirrorObject();
                mirrorObject.setSrcObjectId(copyTask.getSrcObjectId());
                mirrorObject.setDestObjectId(objectReference.getId());
                return mirrorObject;
            }
        }
        
        return null;
    }
    
    public void create(MirrorObject mirrorObject, CopyTask copyTask)
    {
        // if(mirrorObject == null || copyTask == null)
        // {
        // return;
        // }
        
        mirrorObject.setOwnedBy(copyTask.getSrcOwnedBy());
        mirrorObject.setCreateAt(new Date());
        mirrorObject.setSrcResourceGroupId(copyTask.getSrcResourceGroupId());
        mirrorObject.setDestResourceGroupId(copyTask.getDestResourceGroupId());
        mirrorObject.setPolicyId(copyTask.getPolicyId());
        mirrorObject.setType(copyTask.getCopyType());
        
        // 需要给镜像文件增加引用计数
        ObjectReference objectReference = objectReferenceDAO.get(mirrorObject.getDestObjectId());
        if (null == objectReference)
        {
            LOGGER.info("get objectReference null by destobjectid:" + mirrorObject.getDestObjectId());
            return;
        }
        
        if (filesInnerManager.increaseObjectRefCount(objectReference))
        {
            try
            {
                mirrorObjectService.insert(mirrorObject);
            }
            catch (Exception e)
            {
                // 新增记录失败，需要回滚增加的对象引用计数
                LOGGER.error("insert mirrorobject error", e);
                filesInnerManager.decreaseRefObjectCount(objectReference);
            }
        }
        else
        {
            LOGGER.error("insert mirrorobject error,ObjectReferrnce refcount is 0,destObjectId :"
                + mirrorObject.getDestObjectId());
        }
        
    }
    
    public MirrorObject getMirrorObject(String srcObjectId, String destObjectId, long ownedBy)
    {
        return mirrorObjectService.getBySrcObjectIdAndDestObjectIdAndOwnedBy(srcObjectId,
            destObjectId,
            ownedBy);
    }
    
    public void insert(MirrorObject mirrorObject)
    {
        mirrorObjectService.insert(mirrorObject);
    }
    
    // 这个暂时没用
    public List<MirrorObject> getBySrcObjectId(String objectId)
    {
        return mirrorObjectService.getBySrcObjectId(objectId);
    }
    
    public List<String> getDestObjectIdBySrcObjectId(String objectId)
    {
        if (objectId == null)
        {
            return null;
        }
        ObjectReference objectReference = objectReferenceDAO.get(objectId);
        List<String> lstDestObjectId = new ArrayList<String>(3);
        if (null == objectReference)
        {
            return null;
        }
        List<ObjectFingerprintIndex> lstObjectFingerprintIndex = objectFrIndexDAO.getBySha1(objectReference.getSha1());
        
        if (lstObjectFingerprintIndex == null || lstObjectFingerprintIndex.isEmpty())
        {
            return null;
        }
        for (ObjectFingerprintIndex objectFingerprintIndex : lstObjectFingerprintIndex)
        {
            if (objectFingerprintIndex.getId().equals(objectId))
            {
                continue;
            }
            lstDestObjectId.add(objectFingerprintIndex.getId());
        }
        return lstDestObjectId;
    }
    
    public void deleteBySrcObjectId(String objectId)
    {
        mirrorObjectService.deleteBySrcObjectId(objectId);
    }
    
    public void deleteBySrcObjectIdAndOwnedBy(String objectId, long ownedBy)
    {
        mirrorObjectService.deleteBySrcObjectIdAndOwnedBy(objectId, ownedBy);
        List<String> lstDestObjectId = getDestObjectIdBySrcObjectId(objectId);
        if (lstDestObjectId == null || lstDestObjectId.isEmpty())
        {
            return;
        }
        // 减少自己文件的镜像文件的引用数
        for (String destObjectId : lstDestObjectId)
        {
            objectMirrorManager.deleteMirrorObject(destObjectId);
        }
    }
    
    public List<MirrorObject> getMirrorObjectByOwnedByAndSrcObjectId(long ownedBy, String objectId)
    {
        LOGGER.info("the ownedBy:" + ownedBy + ";the objectId:" + objectId);
        return mirrorObjectService.getMirrorObjectByOwnedByAndSrcObjectId(ownedBy, objectId);
    }
    
    public void handleDedupMsg(INode srcNode, INode destNode)
    {
        // 两个node的ownedby应该是一样的
        List<MirrorObject> lstMirrorObject = mirrorObjectService.getMirrorObjectByOwnedByAndSrcObjectId(destNode.getOwnedBy(),
            destNode.getObjectId());
        if (null == lstMirrorObject || lstMirrorObject.isEmpty())
        {
            LOGGER.info("the user:" + destNode.getOwnedBy() + " does't has the srcObjectid:"
                + srcNode.getObjectId());
            return;
        }
        
        MirrorObject mirrorObj = null;
        for (MirrorObject mirrorObject : lstMirrorObject)
        {
            mirrorObj = mirrorObjectService.getBySrcObjectIdAndDestObjectIdAndOwnedBy(srcNode.getObjectId(),
                mirrorObject.getDestObjectId(),
                mirrorObject.getOwnedBy());
            if (null != mirrorObj)
            {
                mirrorObjectService.deleteBySrcObjectidAndDestObjectidAndOwnedBy(mirrorObject);
                LOGGER.info("update mirror_object error,becaues the right mirror_object is existed");
            }
            else
            {
                mirrorObjectService.changeUsersSrcObjectId(mirrorObject, srcNode.getObjectId());
            }
            
        }
        
    }
}
