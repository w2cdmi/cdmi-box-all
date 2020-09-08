package com.huawei.sharedrive.app.mirror.service;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;

public interface MirrorObjectService
{
    List<MirrorObject> getBySrcObjectId(String objectId);
    
    void insertNewOne(MirrorObject mirrorObject,CopyTask task);
    
    void insertNewOne(CopyTask task);
    
    void insert(MirrorObject mirrorObject);
    
    int deleteMirrorObjectBySrcObjectIdAndOwnedBy(ObjectReference objectReference);
    
    int deleteBySrcObjectidAndDestObjectidAndOwnedBy(MirrorObject mirrorObject);
    
    int deleteBySrcObjectId(String objectId);
    
    MirrorObject getBySrcObjectIdAndDestObjectIdAndOwnedBy(String srcObjectId, String destObjectId, long ownedBy);
    
    List<MirrorObject> getMirrorObjectByOwnedByAndSrcObjectId(long ownedBy,String objectId);
    
    int deleteBySrcObjectIdAndOwnedBy(String objectId, long ownedBy);
    
    int changeUsersSrcObjectId(MirrorObject mirrorObject,String newObjectId);
}
