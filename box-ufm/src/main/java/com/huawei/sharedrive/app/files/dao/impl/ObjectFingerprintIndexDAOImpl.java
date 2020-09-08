package com.huawei.sharedrive.app.files.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.ObjectFingerprintIndexDAO;
import com.huawei.sharedrive.app.files.domain.ObjectFingerprintIndex;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.core.utils.HashTool;

@Service("objectFingerprintIndexDAO")
@SuppressWarnings("deprecation")
public class ObjectFingerprintIndexDAOImpl extends AbstractDAOImpl implements ObjectFingerprintIndexDAO
{
    private static final int TABLE_COUNT = 500;
    
    @Override
    public void create(ObjectFingerprintIndex objFpIndex)
    {
        objFpIndex.setTableSuffix(getTableSuffix(objFpIndex));
        sqlMapClientTemplate.update("ObjectFingerprintIndex.insert", objFpIndex);
    }
    
    @Override
    public int delete(ObjectFingerprintIndex objFpIndex)
    {
        objFpIndex.setTableSuffix(getTableSuffix(objFpIndex));
        return sqlMapClientTemplate.delete("ObjectFingerprintIndex.delete", objFpIndex);
    }
    
    @Override
    public int deleteBySha1(String sha1)
    {
        ObjectFingerprintIndex objFpIndex = new ObjectFingerprintIndex();
        objFpIndex.setSha1(sha1);
        objFpIndex.setTableSuffix(getTableSuffix(objFpIndex));
        return sqlMapClientTemplate.delete("ObjectFingerprintIndex.deletebysha1", objFpIndex);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectFingerprintIndex> getBySha1(String sha1)
    {
        ObjectFingerprintIndex objFpIndex = new ObjectFingerprintIndex();
        objFpIndex.setSha1(sha1);
        objFpIndex.setTableSuffix(getTableSuffix(objFpIndex));
        List<ObjectFingerprintIndex> listObjectFingerprintIndex = sqlMapClientTemplate.queryForList("ObjectFingerprintIndex.getbysha1",
            objFpIndex);
        return listObjectFingerprintIndex;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectFingerprintIndex> getBySha1AndRegionID(String sha1, int regionId)
    {
        ObjectFingerprintIndex objFpIndex = new ObjectFingerprintIndex();
        objFpIndex.setSha1(sha1);
        objFpIndex.setRegionId(regionId);
        objFpIndex.setTableSuffix(getTableSuffix(objFpIndex));
        return sqlMapClientTemplate.queryForList("ObjectFingerprintIndex.getbysha1Andregionid", objFpIndex);
    }
    
    private int getTableSuffix(ObjectFingerprintIndex objFpIndex)
    {
        String sha1 = objFpIndex.getSha1();
        if (StringUtils.isBlank(sha1))
        {
            throw new IllegalArgumentException("illegal ObjectFingerprintIndex sha1 " + sha1);
        }
        
        int database = (int) (HashTool.applySuffux(sha1) % TABLE_COUNT);
        return database;
    }
    
}
