package com.huawei.sharedrive.isystem.dns.dao;

import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.isystem.dns.domain.UasNode;

import pw.cdmi.box.dao.BaseDAO;

public interface UasNodeDao extends BaseDAO<UasNode, String>
{
    Map<String, UasNode> getUasNodeMap();
    
    List<UasNode> getUasNodeList();
}
