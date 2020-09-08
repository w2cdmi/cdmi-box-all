package com.huawei.sharedrive.uam.plugin.Netease.dao;

import com.huawei.sharedrive.uam.plugin.Netease.domain.IMAccount;

public interface IMAccountDao {
	
	void insert(IMAccount imAccount);

	IMAccount get(IMAccount imAccount);
	
	IMAccount getByIMaccid(IMAccount imAccount);

}
