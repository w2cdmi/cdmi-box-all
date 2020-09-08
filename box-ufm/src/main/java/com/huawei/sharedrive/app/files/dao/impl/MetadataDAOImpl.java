package com.huawei.sharedrive.app.files.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.MetadataDAO;
import com.huawei.sharedrive.app.files.domain.INode;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.core.utils.HashTool;

@Service
public class MetadataDAOImpl extends AbstractDAOImpl implements MetadataDAO
{

    @SuppressWarnings("deprecation")
    @Override
    public void exportINodesToLocal(INode srcNode, String filename)
    {
      Map<String, Object> map = new HashMap<String, Object>(3);
      map.put("srcTableSuffix", getTableSuffix(srcNode.getOwnedBy()));
      map.put("ownedBy", srcNode.getOwnedBy());
      map.put("localFile", filename);
      sqlMapClientTemplate.queryForObject("INode.exportToLocal", map);
    }
    
    public static final int TABLE_COUNT = 500;
    
    private int getTableSuffix(long ownerId)
    {
        int table = (int) (HashTool.apply(String.valueOf(ownerId)) % TABLE_COUNT);
        return table;
    }
    
}
