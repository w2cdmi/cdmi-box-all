package com.huawei.sharedrive.app.mirror.dao;

import java.util.List;

import com.huawei.sharedrive.app.mirror.domain.MirrorObject;

public interface MirrorObjectDAO
{
    List<MirrorObject> getByOwnedByAndSrcObjectId(MirrorObject mirrorObject);
    
    List<MirrorObject> getBySrcObjectId(String objectId);
    
    void insert(MirrorObject mirrorObject);
    
    int deleteBySrcObjectidAndDestObjectidAndOwnedBy(MirrorObject mirrorObject);
    
    int deleteBySrcObjectId(String objectId);
    
    MirrorObject getBySrcObjectIdAndDestObjectIdAndOwnedBy(MirrorObject mirrorObject);
    
    List<MirrorObject> getMirrorObjectByOwnedByAndSrcObjectId(long ownedBy, String objectId);
    
    int deleteBySrcObjectIdAndOwnedBy(String objectId, long ownedBy);
    
    int changeUsersSrcObjectId(MirrorObject mirrorObject,String newObjectId);
    
    List<MirrorObject> getBySrcObjectIds(String objectId,long ownedBy);
}
