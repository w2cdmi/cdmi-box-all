package com.huawei.sharedrive.isystem.mirror.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;

import pw.cdmi.box.dao.BaseDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 
 * @author c00287749
 * 
 */
public interface CopyPolicyDAO extends BaseDAO<CopyPolicy, Integer>
{
    
    CopyPolicy get(CopyPolicy policy);
    
    @Override
    CopyPolicy get(Integer id);
    
    List<CopyPolicy> getByApp(String appId);
    
    List<CopyPolicy> listCopyPolicy();
    
    List<CopyPolicy> listCopyPolicy(Order order, Limit limit);
    
    int getNextAvailableId();
    
}
