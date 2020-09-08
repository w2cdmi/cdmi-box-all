package com.huawei.sharedrive.uam.weixin.service;

import java.util.List;

import com.huawei.sharedrive.uam.weixin.domain.ShareLevel;

public interface ShareLevelService {
	
	List<ShareLevel> list();
	
	ShareLevel get(int id);

}
