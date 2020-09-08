/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.weixin.dao.WxUserDao;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.service.WxUserService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.uam.domain.AuthApp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>部门员工管理</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Service
public class WxUserServiceImpl implements WxUserService {
    private static final Logger logger = LoggerFactory.getLogger(WxUserServiceImpl.class);
    
	@Resource
	private RestClient ufmClientService;
    
    @Autowired
    private WxUserDao wxUserDao;
    
    @Autowired
    private AuthAppService authAppService;

    @Override
    public void create(WxUser wxUser) {
        if(wxUser.getCreatedAt() == null) {
            wxUser.setCreatedAt(new Date());
        }
        if(wxUser.getModifiedAt() == null) {
            wxUser.setModifiedAt(new Date());
        }
        
        wxUser.setType((byte)0);	//首次创建都默认为普通账号

        wxUserDao.create(wxUser);
    }

    @Override
    public void update(WxUser wxUser) {
        if(wxUser.getModifiedAt() == null) {
            wxUser.setModifiedAt(new Date());
        }

        wxUserDao.update(wxUser);
    }
    
    @Override
	public void updateWxUserAccount(WxUser wxUser) {
		// TODO Auto-generated method stub
		UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
		AuthApp authApp = authAppService.getDefaultWebApp();
		userHttpClient.updateWxUserAccount(wxUser, authApp);
	}

    @Override
    public void delete(String unionId) {
        //删除微信用户信息
        wxUserDao.deleteByUnionId(unionId);
    }

    @Override
    public WxUser getByOpenId(String openId) {
        return wxUserDao.getByOpenId(openId);
    }

    public WxUser getByUnionId(String unionId) {
        return wxUserDao.getByUnionId(unionId);
    }

	@Override
	public WxUser createWxUserAccount(WxUser wxUser) {
		// TODO Auto-generated method stub
		UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
		AuthApp  authApp = authAppService.getDefaultWebApp();
		long cloudUserId = userHttpClient.createWxUserAccount(wxUser,authApp);
		return wxUser;
	
	}

	@Override
	public WxUser getCloudUserId(Long cloudUserId) {
		// TODO Auto-generated method stub
		return wxUserDao.getCloudUserId(cloudUserId);
	}
	
	
	@Override
	public void updateCountInvitByMe(WxUser wxUser) {
		// TODO Auto-generated method stub
		wxUserDao.updateCountInvitByMe(wxUser);
	}
	
	@Override
	public void updateCountTodayInvitByMe(WxUser wxUser) {
		// TODO Auto-generated method stub
		wxUserDao.updateCountTodayInvitByMe(wxUser);
	}
	
	@Override
	public List<WxUser> listByInviterId(String inviterId, List<Order> orderList, Limit limit) {
		// TODO Auto-generated method stub
		return wxUserDao.listByInviterId(inviterId, orderList, limit);
	}

	@Override
	public void cleanCountTodayInvitByMe() {
		// TODO Auto-generated method stub
		wxUserDao.cleanCountTodayInvitByMe();
	}

	@Override
	public void updateCountTotalProfits(WxUser inviter) {
		// TODO Auto-generated method stub
		wxUserDao.updateCountTotalProfits(inviter);
	}

	@Override
	public void updateCountTodayProfits(WxUser inviter) {
		// TODO Auto-generated method stub
		wxUserDao.updateCountTodayProfits(inviter);
	}
}
