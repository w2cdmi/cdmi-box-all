package com.huawei.sharedrive.uam.uservip.service;

import java.util.List;

import com.huawei.sharedrive.uam.uservip.domain.UserVip;

public interface UserVipService {
	
	void create(UserVip userVip);

	void update(UserVip userVip);
	
	void delete(UserVip userVip);

	UserVip get(UserVip userVip);

	List<UserVip> listAll();
	
}
