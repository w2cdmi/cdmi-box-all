package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.RecentBrowse;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

public interface RecentBrowseService {
	
	void create(RecentBrowse recentBrowse);
	void createByNode(UserToken user, INode node);
	void delete(INode file);
	void deleteRecentByUserId(long userId, long ownerId, long nodeId);

}
