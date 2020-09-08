package com.huawei.sharedrive.app.share.dao;


import java.util.List;
import com.huawei.sharedrive.app.share.domain.LinkApproveUser;

public interface LinkApproveUserDao {

	void create(LinkApproveUser linkApproveUser);
	
	List<LinkApproveUser> list(LinkApproveUser filter);

	void updateType(LinkApproveUser dbLinkApproveUser);
}
