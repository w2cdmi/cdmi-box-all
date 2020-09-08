/**
 * 
 */
package com.huawei.sharedrive.uam.user.service;

import com.huawei.sharedrive.uam.user.domain.Tag;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface TagService
{
    Page<Tag> getTagList(PageRequest pageRequest);
    
    void insert(Tag tag);
    
    void delete(String uuid);
    
    Tag getTagByid(String tagId);
    
    Tag getTagByTag(String tag);
}
