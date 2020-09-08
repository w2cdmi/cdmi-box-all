package com.huawei.sharedrive.app.files.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.ObjectSecretLevelDAO;
import com.huawei.sharedrive.app.files.domain.ObjectSecretLevel;
import com.huawei.sharedrive.app.files.service.ObjectSecretLevelService;
@Service
public class objectSecretLevelServiceImpl implements ObjectSecretLevelService{

	@Autowired
	private ObjectSecretLevelDAO objectSecretLevelDAO;
	
	@Override
	public void create(ObjectSecretLevel objectSecretLevel) {
		// TODO Auto-generated method stub
		objectSecretLevelDAO.create(objectSecretLevel);
	}

	@Override
	public ObjectSecretLevel getByAccountId(String sha1, int regionId, long accountId) {
		// TODO Auto-generated method stub
		return objectSecretLevelDAO.getByAccountId(sha1, regionId, accountId);
	}

	@Override
	public void updateSecretLevel(String sha1, Long accountId, int regionId,byte secretLevel) {
		// TODO Auto-generated method stub
		objectSecretLevelDAO.createOrUpdate(sha1,accountId,regionId,secretLevel);
	}
	

}
