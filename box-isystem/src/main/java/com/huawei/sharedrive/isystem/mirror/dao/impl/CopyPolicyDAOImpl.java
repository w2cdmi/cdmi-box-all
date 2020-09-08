package com.huawei.sharedrive.isystem.mirror.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.isystem.mirror.dao.CopyPolicyDAO;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicySiteInfo;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 
 * @author c00287749
 * 
 */
@Component
public class CopyPolicyDAOImpl extends AbstractDAOImpl implements CopyPolicyDAO
{
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(CopyPolicy policy)
    {
        for (CopyPolicySiteInfo siteInfo : policy.getLstCopyPolicyDataSiteInfo())
        {
            siteInfo.setId(getSiteInfoAvailableId());
            siteInfo.setPolicyId(policy.getId());
            sqlMapClientTemplate.insert("CopyPolicySiteInfo.insert", siteInfo);
        }
        
        sqlMapClientTemplate.insert("CopyPolicy.insert", policy);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void delete(Integer id)
    {
        sqlMapClientTemplate.delete("CopyPolicy.delete", id);
        sqlMapClientTemplate.delete("CopyPolicySiteInfo.deleteByPolicy", id);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public CopyPolicy get(Integer id)
    {
        CopyPolicy policy = (CopyPolicy) sqlMapClientTemplate.queryForObject("CopyPolicy.get", id);
        if (null != policy)
        {
            List<CopyPolicySiteInfo> lst = sqlMapClientTemplate.queryForList("CopyPolicySiteInfo.getByPolicy",
                id);
            if (null != lst && !lst.isEmpty())
            {
                policy.setLstCopyPolicyDataSiteInfo(lst);
            }
            
        }
        return policy;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void update(CopyPolicy policy)
    {
        if (null != policy.getLstCopyPolicyDataSiteInfo() && !policy.getLstCopyPolicyDataSiteInfo().isEmpty())
        {
            sqlMapClientTemplate.delete("CopyPolicySiteInfo.deleteByPolicy", policy.getId());
            
            for (CopyPolicySiteInfo siteInfo : policy.getLstCopyPolicyDataSiteInfo())
            {
                siteInfo.setPolicyId(policy.getId());
                siteInfo.setId(getSiteInfoAvailableId());
                sqlMapClientTemplate.insert("CopyPolicySiteInfo.insert", siteInfo);
            }
        }
        sqlMapClientTemplate.update("CopyPolicy.update", policy);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public CopyPolicy get(CopyPolicy policy)
    {
        return (CopyPolicy) sqlMapClientTemplate.queryForObject("CopyPolicy.get", policy);
    }
    
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<CopyPolicy> listCopyPolicy()
    {
        
        List<CopyPolicy> lstPolicy = sqlMapClientTemplate.queryForList("CopyPolicy.getAll");
        if (null == lstPolicy || lstPolicy.isEmpty())
        {
            return null;
        }
        
        List<CopyPolicy> retPolicy = new ArrayList<CopyPolicy>(10);
        List<CopyPolicySiteInfo> lst = null;
        for (CopyPolicy policy : lstPolicy)
        {
            lst = sqlMapClientTemplate.queryForList("CopyPolicySiteInfo.getByPolicy", policy.getId());
            if (null != lst && !lst.isEmpty())
            {
                policy.setLstCopyPolicyDataSiteInfo(lst);
            }
            retPolicy.add(policy);
        }
        
        return retPolicy;
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<CopyPolicy> getByApp(String appId)
    {
        CopyPolicy copyPolicy = new CopyPolicy();
        copyPolicy.setAppId(appId);
        List<CopyPolicy> lstPolicy = sqlMapClientTemplate.queryForList("CopyPolicy.getByApp", copyPolicy);
        if (null != lstPolicy && !lstPolicy.isEmpty())
        {
            List<CopyPolicy> relstPolicy = new ArrayList<CopyPolicy>(10);
            List<CopyPolicySiteInfo> lst = null;
            for (CopyPolicy policy : lstPolicy)
            {
                
                lst = sqlMapClientTemplate.queryForList("CopyPolicySiteInfo.getByPolicy", policy.getId());
                if (null != lst && !lst.isEmpty())
                {
                    policy.setLstCopyPolicyDataSiteInfo(lst);
                }
                relstPolicy.add(policy);
            }
            
            return relstPolicy;
        }
        return null;
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<CopyPolicy> listCopyPolicy(Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(10);
        map.put("limit", limit);
        map.put("order", order);
        List<CopyPolicy> lstPolicy = sqlMapClientTemplate.queryForList("CopyPolicy.getAllByPage", map);
        if (null != lstPolicy && !lstPolicy.isEmpty())
        {
            List<CopyPolicy> relstPolicy = new ArrayList<CopyPolicy>(10);
            List<CopyPolicySiteInfo> lst = null;
            for (CopyPolicy policy : lstPolicy)
            {
                
                lst = sqlMapClientTemplate.queryForList("CopyPolicySiteInfo.getByPolicy", policy.getId());
                if (null != lst && !lst.isEmpty())
                {
                    policy.setLstCopyPolicyDataSiteInfo(lst);
                }
                relstPolicy.add(policy);
            }
            
            return relstPolicy;
        }
        
        return null;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getNextAvailableId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "copyPolicyId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        Long id = (Long) map.get("returnid");
        return id.intValue();
    }
    
    @SuppressWarnings("deprecation")
    private int getSiteInfoAvailableId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "copyPolicySiteInfoId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        Long id = (Long) map.get("returnid");
        return id.intValue();
    }
    
}
