package com.huawei.sharedrive.app.mirror.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.mirror.dao.MirrorObjectDAO;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;
import com.huawei.sharedrive.app.mirror.service.MirrorObjectService;

@Service("mirrorObjectService")
public class MirrorObjectServiceImpl implements MirrorObjectService
{
    @Autowired
    private MirrorObjectDAO mirrorObjectDAO;
    
    @Override
    public List<MirrorObject> getBySrcObjectId(String objectId)
    {
        return mirrorObjectDAO.getBySrcObjectId(objectId);
    }
    
    @Override
    public void insertNewOne(MirrorObject mirrorObject, CopyTask task)
    {
        mirrorObject.setOwnedBy(task.getSrcOwnedBy());
        mirrorObjectDAO.insert(mirrorObject);
    }
    
    @Override
    public void insertNewOne(CopyTask task)
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
        mirrorObjectDAO.insert(mirrorObject);
    }
    
    @Override
    public void insert(MirrorObject mirrorObject)
    {
        mirrorObjectDAO.insert(mirrorObject);
    }
    
    // 暂时无引用
    @Override
    public int deleteMirrorObjectBySrcObjectIdAndOwnedBy(ObjectReference objectReference)
    {
        return 0;
    }
    
    @Override
    public int deleteBySrcObjectidAndDestObjectidAndOwnedBy(MirrorObject mirrorObject)
    {
        return mirrorObjectDAO.deleteBySrcObjectidAndDestObjectidAndOwnedBy(mirrorObject);
    }
    
    @Override
    public int deleteBySrcObjectId(String objectId)
    {
        
        return mirrorObjectDAO.deleteBySrcObjectId(objectId);
    }
    
    @Override
    public MirrorObject getBySrcObjectIdAndDestObjectIdAndOwnedBy(String srcObjectId, String destObjectId,
        long ownedBy)
    {
        MirrorObject mirrorObject = new MirrorObject();
        mirrorObject.setSrcObjectId(srcObjectId);
        mirrorObject.setDestObjectId(destObjectId);
        mirrorObject.setOwnedBy(ownedBy);
        
        return mirrorObjectDAO.getBySrcObjectIdAndDestObjectIdAndOwnedBy(mirrorObject);
    }
    
    @Override
    public List<MirrorObject> getMirrorObjectByOwnedByAndSrcObjectId(long ownedBy, String objectId)
    {
        return mirrorObjectDAO.getMirrorObjectByOwnedByAndSrcObjectId(ownedBy, objectId);
    }

    @Override
    public int deleteBySrcObjectIdAndOwnedBy(String objectId, long ownedBy)
    {
        
        return mirrorObjectDAO.deleteBySrcObjectIdAndOwnedBy(objectId,ownedBy);
    }

    @Override
    public int changeUsersSrcObjectId(MirrorObject mirrorObject, String newObjectId)
    {
        return mirrorObjectDAO.changeUsersSrcObjectId(mirrorObject, newObjectId);
    }
    
}
