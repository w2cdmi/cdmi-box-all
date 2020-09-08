package com.huawei.sharedrive.app.share.service;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.link.RestLinkApproveList;
import com.huawei.sharedrive.app.openapi.domain.share.RestLinkApproveDetail;
import com.huawei.sharedrive.app.share.domain.INodeLinkApprove;
import com.huawei.sharedrive.app.share.domain.INodeLinkApproveRecord;

public interface INodeLinkApproveService {

	void create(INodeLinkApprove linkApprove);

	RestLinkApproveList listLinkApprove(INodeLinkApprove linkApprove, long offset, int limit, String orderField, String order, Byte type);

	void update(INodeLinkApprove iNodeLinkApprove);

	void updateStatus(INodeLinkApprove linkApprove);
	
	/*void updateLinkStatus(INodeLinkApprove linkApprove);*/

	INodeLinkApprove getApproveByLinkCode(String linkCode);

	void deleteByLinkCode(String linkCode);

	RestLinkApproveDetail getApproveDetailByLinkCode(String linkCode);

	void deleteByNodeId(UserToken userInfo, INode iNode);

	RestLinkApproveList listAllLinkApprove(INodeLinkApprove linkApprove, long offset, int limit, String orderField,
			String order);

}
