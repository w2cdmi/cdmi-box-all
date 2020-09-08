package com.huawei.sharedrive.app.files.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.ObjectFingerprintIndexDAO;
import com.huawei.sharedrive.app.files.dao.ObjectSecretLevelDAO;
import com.huawei.sharedrive.app.files.domain.ObjectFingerprintIndex;
import com.huawei.sharedrive.app.files.domain.ObjectSecretLevel;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.core.utils.HashTool;

@Service("objectSecretLevelDAO")
@SuppressWarnings("deprecation")
public class ObjectSecretLevelDAOImpl extends AbstractDAOImpl implements ObjectSecretLevelDAO
{
    private static final int TABLE_COUNT = 100;
    
    @Override
    public void create(ObjectSecretLevel sbjectSecretLevel)
    {
    	sbjectSecretLevel.setTableSuffix(getTableSuffix(sbjectSecretLevel));
        sqlMapClientTemplate.update("ObjectSecretLevel.insert", sbjectSecretLevel);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ObjectSecretLevel getByAccountId(String sha1, int regionId,long accountId)
    {
    	if(sha1.equals("")){
    		return null;
    	}
    	ObjectSecretLevel objectSecretLevel = new ObjectSecretLevel();
    	objectSecretLevel.setSha1(sha1);
    	objectSecretLevel.setRegionId(regionId);
    	objectSecretLevel.setAccountId(accountId);
    	objectSecretLevel.setTableSuffix(getTableSuffix(objectSecretLevel));
        return (ObjectSecretLevel) sqlMapClientTemplate.queryForObject("ObjectSecretLevel.getbyAccountId", objectSecretLevel);
    }
    
    private int getTableSuffix(ObjectSecretLevel sbjectSecretLevel)
    {
        String sha1 = sbjectSecretLevel.getSha1();
        if (StringUtils.isBlank(sha1))
        {
            throw new IllegalArgumentException("illegal sbjectSecretLevel sha1 " + sha1);
        }
        
        int database = (int) (HashTool.applySuffux(sha1) % TABLE_COUNT);
        return database;
    }


	@Override
	public void createOrUpdate(String sha1, Long accountId, int regionId, byte secretLevel) {
		// TODO Auto-generated method stub
		ObjectSecretLevel objectSecretLevel = new ObjectSecretLevel();
    	objectSecretLevel.setSha1(sha1);
    	objectSecretLevel.setRegionId(regionId);
    	objectSecretLevel.setAccountId(accountId);
    	objectSecretLevel.setSecretLevel(secretLevel);
    	objectSecretLevel.setTableSuffix(getTableSuffix(objectSecretLevel));
    	ObjectSecretLevel dbObject=(ObjectSecretLevel) sqlMapClientTemplate.queryForObject("ObjectSecretLevel.getbyAccountId",objectSecretLevel);
    	if(dbObject==null){
    		this.create(objectSecretLevel);
    	}else{
    		sqlMapClientTemplate.update("ObjectSecretLevel.update",objectSecretLevel);
    	}
    	
	}
	
    
}
