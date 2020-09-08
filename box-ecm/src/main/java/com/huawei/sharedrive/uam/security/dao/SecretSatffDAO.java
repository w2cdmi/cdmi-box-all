package com.huawei.sharedrive.uam.security.dao;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.SecretStaff;

public interface SecretSatffDAO {

	List<SecretStaff> getByAccountId(long accountId);

	SecretStaff getByAccountIdAndSecretLevel(SecretStaff secretStaff);
	
	void create(SecretStaff secretStaff);

	void update(SecretStaff secretStaff);


}
