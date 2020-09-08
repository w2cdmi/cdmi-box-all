package com.huawei.sharedrive.uam.weixin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.openapi.domain.user.PageRequestUserProfits;
import com.huawei.sharedrive.uam.weixin.dao.UserProfitDetailDao;
import com.huawei.sharedrive.uam.weixin.domain.UserProfitDetail;
import com.huawei.sharedrive.uam.weixin.service.UserProfitDetailService;

@Service
public class UserProfitDetailServiceImpl implements UserProfitDetailService{
	
	@Autowired
	private UserProfitDetailDao userProfitDetailDao;

	@Override
	public List<UserProfitDetail> listByTypeAndStatus(byte type,byte status) {
		// TODO Auto-generated method stub
		return userProfitDetailDao.listByTypeAndStatus(type,status);
	}

	@Override
	public void create(UserProfitDetail transferMoney2UserDetail) {
		// TODO Auto-generated method stub
		userProfitDetailDao.create(transferMoney2UserDetail);
		
	}

	@Override
	public List<UserProfitDetail> list(UserProfitDetail filter,PageRequestUserProfits requestUserProfits) {
		// TODO Auto-generated method stub
		return userProfitDetailDao.list(filter,requestUserProfits);
	}

	@Override
	public void updateStatus(UserProfitDetail userProfitDetail) {
		// TODO Auto-generated method stub
		userProfitDetailDao.updateStatus(userProfitDetail);
	}

}
