package com.huawei.sharedrive.app.share.service;

import java.util.List;

import com.huawei.sharedrive.app.share.domain.LinkApproveUser;

public interface LinkApproveUserService {
	
	void create(LinkApproveUser linkApproveUser);
	
	List<LinkApproveUser> list(LinkApproveUser filter);

}
