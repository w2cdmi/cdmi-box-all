package com.huawei.sharedrive.app.share.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.share.dao.LinkApproveUserDao;
import com.huawei.sharedrive.app.share.domain.LinkApproveUser;
import com.huawei.sharedrive.app.share.service.LinkApproveUserService;

@Service
public class LinkApproveUserServiceImpl implements LinkApproveUserService{
	
	@Autowired
	private LinkApproveUserDao  linkApproveUserDao;

	@Override
	public void create(LinkApproveUser linkApproveUser) {
		// TODO Auto-generated method stub

		LinkApproveUser filter=new LinkApproveUser();
		filter.setCloudUserId(linkApproveUser.getCloudUserId());
		filter.setLinkCode(linkApproveUser.getLinkCode());
		List<LinkApproveUser> list=this.list(filter);
		if(list.size()!=0){
			LinkApproveUser dbLinkApproveUser=list.get(0);
			if(dbLinkApproveUser.getType()==LinkApproveUser.TYPE_ASSISTANT){
                if(linkApproveUser.getType()==LinkApproveUser.TYPE_MASTER){
					dbLinkApproveUser.setType(LinkApproveUser.TYPE_MASTER);
					linkApproveUserDao.updateType(dbLinkApproveUser);
				}
			}
		}else{
			linkApproveUserDao.create(linkApproveUser);
		}



		
	}

	@Override
	public List<LinkApproveUser> list(LinkApproveUser filter) {
		// TODO Auto-generated method stub
		return linkApproveUserDao.list(filter);
	}

}
