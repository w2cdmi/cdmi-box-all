package com.huawei.sharedrive.uam.weixin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.weixin.dao.ShareLevelDao;
import com.huawei.sharedrive.uam.weixin.domain.ShareLevel;
import com.huawei.sharedrive.uam.weixin.service.ShareLevelService;

@Service
public class ShareLevelServiceImpl implements ShareLevelService{
	
	@Autowired
	private ShareLevelDao shareLevelDao;

	@Override
	public List<ShareLevel> list() {
		// TODO Auto-generated method stub
		return shareLevelDao.list();
	}

	@Override
	public ShareLevel get(int id) {
		// TODO Auto-generated method stub
		return shareLevelDao.get(id);
	}

}
