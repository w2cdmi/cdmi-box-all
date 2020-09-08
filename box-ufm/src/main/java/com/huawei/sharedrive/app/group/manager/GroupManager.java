package com.huawei.sharedrive.app.group.manager;

import java.util.List;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.group.GroupList;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;
import com.huawei.sharedrive.app.openapi.domain.group.GroupUserList;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupModifyRequest;

public interface GroupManager
{
    /**
     * 创建群组
     * 
     * @param userToken
     * @param group
     * @return
     */
    Group createGroup(UserToken userToken, Group group, Long ownedBy);
    
    /**
     * 删除群组
     * 
     * @param userToken
     * @param id
     */
    void deleteGroup(UserToken userToken, Long id);
    
    /**
     * 获取群组信息
     * 
     * @param userToken
     * @param id
     * @return
     */
    Group getGroupInfo(UserToken userToken, Long id);
    
    /**
     * 列举群组 支持模糊查询
     * 
     * @param orders
     * @param limit
     * @param offset
     * @param keyword
     * @param userToken
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    GroupList getGroupList(List<GroupOrder> orders, Integer limit, Long offset, String keyword, String type,
        UserToken userToken);
    
    /**
     * 列举群组用户
     * 
     * @param orders
     * @param limit
     * @param offset
     * @param userToken
     * @param userId
     * @param keyword
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    GroupUserList getUserGroupList(List<GroupOrder> orders, Integer limit, Long offset, UserToken userToken,
        String keyword, String type, String isListRole);
    
    /**
     * 修改群组
     * 
     * @param userToken
     * @param group
     * @param restGroupRequest
     * @return
     */
    Group modifyGroup(UserToken userToken, Group group, RestGroupModifyRequest restGroupRequest);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    void sendEvent(UserToken user, EventType type, INode srcNode, INode destNode, UserLogType userLogType,
        String[] logParams, String keyword, Long groupId);

	/**
	 * 按名称查询群组
	 * @param userToken
	 * @param name
	 * @return
	 */
	Group getGroupByName(UserToken userToken, String name);
}
