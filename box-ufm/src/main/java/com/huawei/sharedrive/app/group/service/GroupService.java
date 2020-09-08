package com.huawei.sharedrive.app.group.service;

import java.util.List;

import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;

import pw.cdmi.box.domain.Limit;

public interface GroupService
{
    
    /**
     * 创建群组
     * 
     * @param userToken
     * @param group
     * @return
     */
    Group createGroup(UserToken userToken, Group group);
    
    /**
     * 通过群组ID 删除群组
     * 
     * @param id
     * @return
     */
    int delete(Long id);
    
    /**
     * 通过群组ID 和企业Id 获取群组
     * 
     * @param userToken
     * @param id
     * @return
     */
    Group get(Long id,Long accountId);
    
    /**
     * 通过群组ID 获取群组
     * 
     * @param userToken
     * @param id
     * @return
     */
    Group get(Long id);
    
    /**
     * 通过群组名称 应用ID 获取群组总数
     * 
     * @param group
     * @return
     */
    Integer getCount(Group group);
    
    /**
     * 列举群组
     * 
     * @param orders
     * @param limit
     * @param group
     * @return
     */
    List<Group> getGroupsList(List<GroupOrder> orders, Limit limit, Group group);
    
    /**
     * 更新群组
     * 
     * @param userToken
     * @param group
     */
    void modifyGroup(UserToken userToken, Group group);

	/**
	 * 按名称查找群组
	 * @param name
	 * @return
	 */
	Group getByName(String name,long accountId);
}
