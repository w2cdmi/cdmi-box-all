package com.huawei.sharedrive.uam.user.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.user.dao.TagDAO;
import com.huawei.sharedrive.uam.user.domain.Tag;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;

@Service("tagDAO")
@SuppressWarnings("deprecation")
public class TagDAOImpl extends AbstractDAOImpl implements TagDAO
{
    public int getTagListCount()
    {
        int listCount = (Integer) sqlMapClientTemplate.queryForObject("Tag.getCount");
        return listCount;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Tag> getTagList(Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("limit", limit);
        List<Tag> list = (List<Tag>) sqlMapClientTemplate.queryForList("Tag.get", map);
        return list;
    }
    
    @Override
    public void insert(Tag tag)
    {
        String uuid = UUID.randomUUID().toString();
        tag.setId(uuid);
        sqlMapClientTemplate.insert("Tag.insert", tag);
    }
    
    @Override
    public Tag getTagByid(String tagId)
    {
        return (Tag) sqlMapClientTemplate.queryForObject("Tag.getTagByid", tagId);
    }
    
    @Override
    public Tag getTagByTag(String tag)
    {
        return (Tag) sqlMapClientTemplate.queryForObject("Tag.getTagByTag", tag);
    }
    
    @Override
    public void delete(String uuid)
    {
        sqlMapClientTemplate.delete("Tag.delete", uuid);
    }
}
