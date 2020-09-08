package com.huawei.sharedrive.uam.user.dao;

import java.util.List;

import com.huawei.sharedrive.uam.user.domain.Tag;

import pw.cdmi.box.domain.Limit;

public interface TagDAO
{
    List<Tag> getTagList(Limit limit);
    
    int getTagListCount();
    
    void insert(Tag tag);
    
    void delete(String uuid);
    
    Tag getTagByid(String tagId);
    
    Tag getTagByTag(String tag);
}