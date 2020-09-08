/**
 * 
 */
package com.huawei.sharedrive.uam.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.user.dao.TagDAO;
import com.huawei.sharedrive.uam.user.domain.Tag;
import com.huawei.sharedrive.uam.user.service.TagService;
import com.huawei.sharedrive.uam.user.service.UserTagService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;

@Component
public class TagServiceImpl implements TagService
{
    
    @Autowired
    private TagDAO tagDAO;
    
    @Autowired
    private UserTagService userTagService;
    
    @Override
    public Page<Tag> getTagList(PageRequest pageRequest)
    {
        int total = tagDAO.getTagListCount();
        List<Tag> list = tagDAO.getTagList(pageRequest.getLimit());
        Page<Tag> page = new PageImpl<Tag>(list, pageRequest, total);
        return page;
    }
    
    @Override
    public void insert(Tag tag)
    {
        tagDAO.insert(tag);
    }
    
    @Override
    public Tag getTagByid(String tagId)
    {
        return tagDAO.getTagByid(tagId);
    }
    
    @Override
    public Tag getTagByTag(String tag)
    {
        return (Tag) tagDAO.getTagByTag(tag);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(String uuid)
    {
        userTagService.delete(uuid);
        tagDAO.delete(uuid);
    }
    
}
