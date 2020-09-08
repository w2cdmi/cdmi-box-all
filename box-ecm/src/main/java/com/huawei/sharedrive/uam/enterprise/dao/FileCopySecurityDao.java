package com.huawei.sharedrive.uam.enterprise.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurity;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface FileCopySecurityDao
{
    void create(FileCopySecurity copySecurity);
    
    void modify(FileCopySecurity copySecurity);
    
    int delete(Long srcSafeRoleId, Long targetSafeRoleId, Long accountId);
    
    List<FileCopySecurity> getByAccountId(long accountId);
    
    List<FileCopySecurity> query(Limit limit, Order order, FileCopySecurity copySecurity);
    
    long getMaxId();
    
    int queryCount(Limit limit, FileCopySecurity copySecurity);
    
    FileCopySecurity get(FileCopySecurity copySecurity);
    
    int getFilterdCount(FileCopySecurity filter);
}
