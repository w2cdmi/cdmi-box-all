package com.huawei.sharedrive.uam.security.dao;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.ProxyNetwork;

import pw.cdmi.box.dao.BaseDAO;

public interface ProxyNetworkDAO extends BaseDAO<ProxyNetwork, String>
{
    void delete(Integer networkType);
    
    List<ProxyNetwork> getTypeList(Integer networkType);
    
    List<ProxyNetwork> getAll();
    
    long getNextAvailableId();
}