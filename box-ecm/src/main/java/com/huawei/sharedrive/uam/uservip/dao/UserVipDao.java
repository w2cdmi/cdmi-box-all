package com.huawei.sharedrive.uam.uservip.dao;

import java.util.List;

import com.huawei.sharedrive.uam.uservip.domain.UserVip;

public interface UserVipDao {
	
	void create(UserVip userVip);

	void update(UserVip userVip);
	
	void delete(UserVip userVip);

	UserVip get(UserVip userVip);

	List<UserVip> listAll();
	
}
