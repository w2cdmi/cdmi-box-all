package com.huawei.sharedrive.app.user.domain;

import com.huawei.sharedrive.app.utils.GroupInfoCommon;

public class GroupInfo extends GroupInfoCommon
{
    private static final long serialVersionUID = 280682481604220153L;
    
    /** 群组角色 0：表示拥有者，1：表示管理员，2表示普通成员 */
    private byte groupRole;

	public byte getGroupRole() {
		return groupRole;
	}

	public void setGroupRole(byte groupRole) {
		this.groupRole = groupRole;
	}
}
