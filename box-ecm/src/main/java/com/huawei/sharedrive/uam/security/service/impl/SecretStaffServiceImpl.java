package com.huawei.sharedrive.uam.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.security.dao.SecretSatffDAO;
import com.huawei.sharedrive.uam.security.domain.SecretStaff;
import com.huawei.sharedrive.uam.security.service.SecretStaffService;

@Service
public class SecretStaffServiceImpl implements SecretStaffService{
	
	@Autowired SecretSatffDAO secretSatffDAO;

	@Override
	public List<SecretStaff> getByAccountId(long accountId) {
		// TODO Auto-generated method stub
		return secretSatffDAO.getByAccountId(accountId);
	}

	@Override
	public void create(SecretStaff secretStaff) {
		// TODO Auto-generated method stub
		if(secretSatffDAO.getByAccountIdAndSecretLevel(secretStaff)!=null){
			secretSatffDAO.update(secretStaff);
		}else{
			secretSatffDAO.create(secretStaff);
		}
		
	}

}
