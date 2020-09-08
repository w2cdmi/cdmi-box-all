package com.huawei.sharedrive.app.group.dao;

import java.util.List;

import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;

import pw.cdmi.box.domain.Limit;

public interface GroupDAO
{
    /**
     * 创建群组
     * 
     * @param group
     */
    void create(Group group);
    
    
    /**
     * 通过ID 删除群组
     * 
     * @param id
     * @return
     */
    int delete(long id);
    
    /**
     * 通过群组ID 获取群组
     * 
     * @param id
     * @return
     */
    Group get(long id);
    
    /**
     * 通过群组ID和企业id 获取群组
     * 
     * @param id
     * @return
     */
    Group get(long id,long accountId);
    
    
    /**
     * 获取群组总数 支持模糊查询
     * 
     * @param groupFilter
     * @return
     */
    int getCount(Group groupFilter);
    
    /**
     * 列举群组 支持模糊查询
     * 
     * @param orders
     * @param limit
     * @param group
     * @return
     */
    List<Group> getGroupsList(List<GroupOrder> orders, Limit limit, Group group);
    
    long getMaxGroupId();
    
    /**
     * 更新群组
     * 
     * @param group
     * @return
     */
    int update(Group group);
    
	/**
	 * 按名称查找群组
	 * @param name
	 * @return
	 */
	Group getByName(String name,long accountId);
 
}
