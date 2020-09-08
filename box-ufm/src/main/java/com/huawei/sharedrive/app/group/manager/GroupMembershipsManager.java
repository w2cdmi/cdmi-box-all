package com.huawei.sharedrive.app.group.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.group.GroupMembershipsInfo;
import com.huawei.sharedrive.app.openapi.domain.group.GroupMembershipsList;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;
import com.huawei.sharedrive.app.openapi.domain.group.RestAddGroupRequest;

@Component
public interface GroupMembershipsManager
{
    /**
     * 添加群组、成员关系 
     * 
     * @param userToken
     * @param member
     * @param groupId
     * @return
     */
    GroupMembershipsInfo addMemberShips(UserToken userToken, RestAddGroupRequest member, Long groupId);
    
    /**
     * 删除群组
     * 
     * @param userToken
     * @param userId
     * @param groupId
     */
    void deleteOne(UserToken userToken, Long userId, Long groupId);
    
    /**
     * 列举成员 支持模糊查询
     * 
     * @param userToken
     * @param order
     * @param length
     * @param offset
     * @param keyword
     * @param groupRole
     * @param groupId
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    GroupMembershipsList listMembers(UserToken userToken, List<GroupOrder> order, Integer length,
        Long offset, String keyword, String groupRole, Long groupId);
    
    /**
     * 修改群组关系
     * 
     * @param userToken
     * @param role
     * @param id
     * @param groupId
     * @return
     */
    GroupMembershipsInfo modifyMemberships(UserToken userToken, String role, Long id, Long groupId);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    void sendEvent(UserToken user, EventType type, INode srcNode, INode destNode,
        UserLogType userLogType, String[] logParams, String keyword,Long groupId, Long long1, String string);
}
