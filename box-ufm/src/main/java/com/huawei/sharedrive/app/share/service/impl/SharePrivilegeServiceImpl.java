/**
 * 
 */
package com.huawei.sharedrive.app.share.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.share.service.SharePrivilegeService;
import com.huawei.sharedrive.app.user.domain.User;

/**
 * @author l90003768
 * 
 */
@Component
public class SharePrivilegeServiceImpl implements SharePrivilegeService {

	@Autowired
	private INodeACLService iNodeACLService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.huawei.sharedrive.app.share.service.SharePrivilegeService#
	 * checkPrivilege(com .huawei.sharedrive.app.oauth2.domain.UserToken, long,
	 * long)
	 */
	@Override
	public void checkPrivilege(UserToken curUser, long ownerId, long nodeId) throws BaseRunException {
		if (ownerId == curUser.getCloudUserId()) {
			return;
		}
		String enterpriseId = "";
		if (curUser.getAccountVistor() != null) {
			enterpriseId = String.valueOf(curUser.getAccountVistor().getEnterpriseId());
		}
		INode node = new INode();
		node.setOwnedBy(ownerId);
		node.setId(nodeId);
		ACL acl = iNodeACLService.getACLForAccessUser(curUser.getId(), INodeACL.TYPE_USER, node, enterpriseId,curUser);

		if (acl == null || !acl.isAuthor()) {
			throw new ForbiddenException();
		}

	}

}
