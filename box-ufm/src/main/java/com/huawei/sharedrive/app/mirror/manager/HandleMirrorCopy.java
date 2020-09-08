package com.huawei.sharedrive.app.mirror.manager;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.exception.DbCommitException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;
import com.huawei.sharedrive.app.mirror.domain.ObjectMirrorShip;
import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskExeResult;

import pw.cdmi.core.utils.MethodLogAble;

@Service("handleMirrorCopy")
public class HandleMirrorCopy implements BaseHandleCopyTask
{
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired
    private MirrorObjectManager mirrorObjectManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HandleMirrorCopy.class);
    
    @Autowired
    private DCManager dcManager;
    
    /**
     * 判断需要复制的对象在目的数据中心是否存在 判断对象是否存在，主要看是否存在镜像关系或者是否存在相同的SHA1值。 算法思想
     * 1:首先在目标存储区域中，列举相同数据指纹对象 2:如果存在相同数据的对象，有以下几种情况
     * 2-1:对象镜像关系中和相同数据对象，存在相同存储区域，这部分只需要增加对象的引用计数，修改镜像关系的类型
     * 2-2:对象镜像关系中和相同数据对象,不存在相同存储，这部分只需要增加对象的引用计数，新建镜像关系的类型 3:建立镜像关系后，需要根据不同复制任务类型处理梳理
     * 
     * @param task
     * @return
     */
    public boolean handleDataIsExistingBeforeCopy(CopyTask task)
    {
        
        if (null == task)
        {
            return false;
        }
        
        MirrorObject mirrorObject = mirrorObjectManager.isCopyTaskAlreadyExist(task);
        if (null != mirrorObject)
        {
            MirrorObject checkMirrorObject = mirrorObjectManager.getMirrorObject(mirrorObject.getSrcObjectId(),
                mirrorObject.getDestObjectId(),
                task.getSrcOwnedBy());
            if (null != checkMirrorObject)
            {
                LOGGER.info("this date is already in mirrorobject table");
                return true;
            }
            //
            LOGGER.info("the objectid:" + task.getSrcObjectId() + " in resourceGroup:"
                + task.getSrcResourceGroupId() + " has its mirror in resourceGroup:"
                + task.getDestResourceGroupId());
            try
            {
                mirrorObjectManager.create(mirrorObject, task);
            }
            catch (Exception e)
            {
                LOGGER.error("insert data error", e);
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 判断是否已经存在复制关系是一致，主要看parentId和当前ID是否一致，目标的对象的区域和资源组是否一致。
     * 
     * @param lstObjectMirrorShip
     * @param task
     * @return
     */
    public ObjectMirrorShip findSameObjectMirrorShip(List<ObjectMirrorShip> lstObjectMirrorShip, CopyTask task)
    {
        if (null == lstObjectMirrorShip || lstObjectMirrorShip.isEmpty() || null == task)
        {
            LOGGER.info("not find same objectmirrorship");
            return null;
        }
        
        for (ObjectMirrorShip ship : lstObjectMirrorShip)
        {
            if (ship.getParentObjectId().equalsIgnoreCase(task.getSrcObjectId()))
            {
                if (CopyPolicySiteInfo.DEFAULT_VALUE == task.getDestResourceGroupId())
                {
                    // 判断存储区域
                    ResourceGroup group = dcManager.getCacheResourceGroup(ship.getResourceGroupId());
                    if (null != group && group.getRegionId() == task.getDestRegionId())
                    {
                        LOGGER.info("find same objectmirrorship and objectid is:" + ship.getObjectId()
                            + " and ResoureceGroupId is " + ship.getResourceGroupId());
                        return ship;
                    }
                    
                }
                else if (ship.getResourceGroupId() == task.getDestResourceGroupId())
                {
                    LOGGER.info("find same objectmirrorship and objectid is:" + ship.getObjectId()
                        + " and ResoureceGroupId is " + ship.getResourceGroupId());
                    return ship;
                }
            }
        }
        LOGGER.info("not find same objectmirrorship");
        return null;
    }
    
    private void copyKiaLabel(ObjectReference obj, CopyTask task)
    {
        try
        {
            ObjectReference srcObj = filesInnerManager.getObjectReference(task.getSrcObjectId());
            if (null == srcObj)
            {
                LOGGER.error("get objectreference error");
                return;
            }
            obj.setSecurityLabel(srcObj.getSecurityLabel());
            obj.setSecurityVersion(srcObj.getSecurityVersion());
            if (!filesInnerManager.updateKiaLabel(obj))
            {
                LOGGER.error("update objectRef KiaLabel error, objectid is:" + obj.getId());
            }
        }
        catch (Exception e)
        {
            LOGGER.error("copyKiaLabel get some error", e);
        }
        
    }
    
    /**
     * 处理镜像复制上报结果
     * 
     * @param task
     * @param result
     */
    @MethodLogAble
    public void handleCopyResult(CopyTask task, CopyTaskExeResult result)
    {
        INode node = MirrorCommonStatic.getDestNode(task);
        if (node == null)
        {
            LOGGER.warn("node is null");
            return;
        }
        node.setBlockMD5(result.dataBlockMd5);
        node.setMd5(result.md5);
        
        ObjectReference obj = null;
        try
        {
            // 创建对象
            obj = filesInnerManager.createObject(node);
            
            copyKiaLabel(obj, task);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            handleMirrorCopyForObjectConflict(task, result);
            return;
        }
        
        try
        {
            createMirrorObject(task);
        }
        catch (Exception e)
        {
            // 数据回滚；
            LOGGER.error(e.getMessage(), e);
            filesInnerManager.decreaseRefObjectCount(obj);
        }
        
    }
    
    private void createMirrorObject(CopyTask task)
    {
        MirrorObject mirrorObject = new MirrorObject();
        mirrorObject.setOwnedBy(task.getSrcOwnedBy());
        mirrorObject.setSrcObjectId(task.getSrcObjectId());
        mirrorObject.setDestObjectId(task.getDestObjectId());
        mirrorObject.setSrcResourceGroupId(task.getSrcResourceGroupId());
        mirrorObject.setDestResourceGroupId(task.getDestResourceGroupId());
        mirrorObject.setType(task.getCopyType());
        mirrorObject.setCreateAt(new Date());
        mirrorObject.setPolicyId(task.getPolicyId());
        
        mirrorObjectManager.insert(mirrorObject);
    }
    
    /**
     * 处理因为OBJECT冲突的场景
     * 
     * @param task
     * @param result
     */
    private void handleMirrorCopyForObjectConflict(CopyTask task, CopyTaskExeResult result)
    {
        
        ObjectReference obj = filesInnerManager.getObjectReference(result.getDestObjectId());
        
        MirrorObject mirrorObject = mirrorObjectManager.getMirrorObject(task.getSrcObjectId(),
            result.getDestObjectId(),
            task.getSrcOwnedBy());
        
        if (null != obj && null == mirrorObject)
        {
            // 增加了引用计数
            if (filesInnerManager.increaseObjectRefCount(obj))
            {
                try
                {
                    // 创建对象关系
                    createMirrorObject(task);
                }
                catch (Exception e1)
                {
                    // 数据回滚；
                    LOGGER.error(e1.getMessage(), e1);
                    filesInnerManager.decreaseRefObjectCount(obj);
                }
            }
            
            return;
        }
        else if (null != obj && null != mirrorObject)
        {
            LOGGER.info("Duplicate report,src object: " + result.getSrcObjectId() + ",dest object"
                + result.getDestObjectId());
            return;
        }
        else
        {
            LOGGER.error("report failed,src object: " + result.getSrcObjectId() + ",dest object"
                + result.getDestObjectId());
            throw new DbCommitException();
        }
    }
    
}
