package com.huawei.sharedrive.uam.enterprise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterprise.dao.FileCopySecurityDao;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurity;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service
@SuppressWarnings("deprecation")
public class FileCopySecurityDaoImpl extends CacheableSqlMapClientDAO implements FileCopySecurityDao
{
    private static final long DEFAULT_ID = 0;
    
    @Override
    public void create(FileCopySecurity copySecurity)
    {
        sqlMapClientTemplate.insert("FileCopySecurity.insert", copySecurity);
    }
    
    @Override
    public void modify(FileCopySecurity copySecurity)
    {
        sqlMapClientTemplate.update("FileCopySecurity.update", copySecurity);
    }
    
    @Override
    public int delete(Long srcSafeRoleId, Long targetSafeRoleId, Long accountId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("srcSafeRoleId", srcSafeRoleId);
        map.put("targetSafeRoleId", targetSafeRoleId);
        map.put("accountId", accountId);
        return sqlMapClientTemplate.delete("FileCopySecurity.delete", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<FileCopySecurity> query(Limit limit, Order order, FileCopySecurity copySecurity)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", copySecurity);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("FileCopySecurity.getFilter", map);
    }
    
    @Override
    public int queryCount(Limit limit, FileCopySecurity copySecurity)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", copySecurity);
        map.put("limit", limit);
        return (int) sqlMapClientTemplate.queryForObject("FileCopySecurity.getFilterCount", map);
    }
    
    @Override
    public long getMaxId()
    {
        Long maxId = (Long) sqlMapClientTemplate.queryForObject("FileCopySecurity.getMaxId");
        if (maxId == null)
        {
            maxId = DEFAULT_ID;
        }
        return maxId;
    }
    
    @Override
    public FileCopySecurity get(FileCopySecurity copySecurity)
    {
        return (FileCopySecurity) sqlMapClientTemplate.queryForObject("FileCopySecurity.getObject",
            copySecurity);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<FileCopySecurity> getByAccountId(long accountId)
    {
        return sqlMapClientTemplate.queryForList("FileCopySecurity.getByAccountId", accountId);
    }
    
    @Override
    public int getFilterdCount(FileCopySecurity filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("FileCopySecurity.getFilterdCount", map);
    }
}
