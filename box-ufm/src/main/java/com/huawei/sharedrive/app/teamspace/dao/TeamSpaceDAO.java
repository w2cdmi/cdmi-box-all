package com.huawei.sharedrive.app.teamspace.dao;

import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 
 * @author c00110381
 * 
 */
public interface TeamSpaceDAO
{
    
    /**
     * 变更团队空间拥有者
     * 
     * @param teamspace
     * @return
     */
    int changeOwner(TeamSpace teamspace);
    
    /**
     * 创建团队空间
     * 
     * @param teamSpace
     */
    void create(TeamSpace teamSpace);
    
    /**
     * 删除团队空间
     * 
     * @param teamSpaceId
     * @return
     */
    int delete(long teamSpaceId);
    
    /**
     * 获取团队空间信息
     * 
     * @param teamSpaceId
     * @return
     */
    TeamSpace get(long teamSpaceId);
    
    /**
     * 获取团队空间总数
     * 
     * @param filter
     * @return
     */
    int getTeamSpaceCount(TeamSpace filter);
    
    /**
     * 列举团队空间
     * 
     * @param orderList
     * @param limit
     * @param filter
     * @return
     */
    List<TeamSpace> listTeamSpaces(List<Order> orderList, Limit limit, TeamSpace filter);
    
    /**
     * 更新团队空间信息
     * 
     * @param teamspace
     * @return
     */
    int update(TeamSpace teamspace);
    
    /**
     * 更新团队空间状态(暂时未提供)
     * 
     * @param teamSpaceId
     * @param status
     * @return
     */
    int updateStatus(long teamSpaceId, int status);
    
    /**
     * 更新上传文件发送消息配置参数
     * 
     * @param teamSpaceId
     * @param uploadNotice
     * @return
     */
    int updateUploadNotice(long teamSpaceId, byte uploadNotice);

	/**
	 * 按团队空间名称查询
	 * @param name
	 * @return
	 */
	TeamSpace getByName(String name,long accountId);

	TeamSpace getByType(long cloudUserId, int type);

}
