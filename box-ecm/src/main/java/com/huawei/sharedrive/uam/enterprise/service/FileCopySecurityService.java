package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;

import com.huawei.sharedrive.uam.enterprise.domain.FileCopyDelRequest;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurity;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface FileCopySecurityService
{
    void create(FileCopySecurity copySecurity);
    
    void modify(FileCopySecurity copySecurity);
    
    FileCopySecurity get(FileCopySecurity copySecurity);
    
    List<FileCopySecurity> getByAccountId(long accountId);
    
    int delete(FileCopyDelRequest fileCopyDelRequest, Long accountId);
    
    List<FileCopySecurity> query(Limit limit, Order order, FileCopySecurity copySecurity);
    
    int queryCount(Limit limit, FileCopySecurity copySecurity);
}
