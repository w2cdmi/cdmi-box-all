package com.huawei.sharedrive.uam.security.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.security.dao.SecretSatffDAO;
import com.huawei.sharedrive.uam.security.domain.SecretStaff;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("secretSatffDAO")
@SuppressWarnings("deprecation")
public class SecretSatffDAOImpl extends AbstractDAOImpl implements SecretSatffDAO {

	@Override
	public List<SecretStaff> getByAccountId(long accountId) {
		// TODO Auto-generated method stub
		return (List<SecretStaff>) sqlMapClientTemplate.queryForList("SecretStaff.getByAccountId",accountId);
	}
	@Override
	public SecretStaff getByAccountIdAndSecretLevel(SecretStaff secretStaff) {
		// TODO Auto-generated method stub
		return (SecretStaff) sqlMapClientTemplate.queryForObject("SecretStaff.getByAccountIdAndSecretLevel",secretStaff);
	}

	@Override
	public void create(SecretStaff secretStaff) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("SecretStaff.create",secretStaff);
		
	}
	
	@Override
	public void update(SecretStaff secretStaff) {
		// TODO Auto-generated method stub
		
		sqlMapClientTemplate.update("SecretStaff.update",secretStaff);
		
	}


}
