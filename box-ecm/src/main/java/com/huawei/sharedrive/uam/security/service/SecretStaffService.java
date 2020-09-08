package com.huawei.sharedrive.uam.security.service;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.SecretStaff;

public interface SecretStaffService {

	List<SecretStaff> getByAccountId(long accountId);

	void create(SecretStaff secretStaff);

}
