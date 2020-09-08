/**
 * 
 */
package com.huawei.sharedrive.app.files.dao;

import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 文件/文件夹DAO v2版本
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-5-5
 * @see
 * @since
 */
public interface INodeDAOV2 {

	/**
	 * 获取子数量
	 * 
	 * @param INode
	 * @return
	 */
	int getSubINodeCount(INode iNode, boolean withExtraType);

	/**
	 * 根据父节点查询Inode
	 * 
	 * @param filter
	 * @param orderList
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<INode> getINodeByParentAndStatus(INode filter, List<Order> orderList, long offset, int limit,
			boolean withExtraType);
	
	/**
	 * 根据父节点查询所有Inode
	 * 
	 * @param node
	 * @return
	 */
	List<INode> getAllNormalINodeByParent(INode node);

	/**
	 * 获取状态节点元数据
	 * 
	 * @param iNode
	 * @return
	 */
	int getINodeCountByStatus(INode iNode, boolean withExtraType);

	/**
	 * 根据状态查找
	 * 
	 * @param filter
	 * @param orderList
	 * @param limit
	 * @return
	 */
	List<INode> getINodeByStatus(INode filter, List<Order> orderList, long offset, int limit, boolean withExtraType);

	/**
	 * 获取同名节点元数据
	 * 
	 * @param ownerId
	 * @param name
	 * @return
	 */
	int getINodeCountByName(long ownerId, String name, boolean withExtraType, String labelIds, Integer docType);

	/**
	 * 根据名称查询节点
	 * 
	 * @param ownerId
	 * @param name
	 * @param orderList
	 * @param offset
	 * @param limit
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	List<INode> searchByName(long ownerId, String name, List<Order> orderList, long offset, int limit,
			boolean withExtraType, String labelIds, Integer docType);

	/**
	 * 获取状态节点元数据
	 * 
	 * @param iNode
	 * @return
	 */
	int getCountByLinkStatus(long ownerId, String name);

	List<INode> getByLinkStatus(long ownerId, String name, List<Order> orderList, long offset, int limit);

	List<INode> getINodeByStatusInternal(INode filter, List<Order> orderList, Limit limit, boolean withExtraType);

	UserStatisticsInfo getUserInfoById(long ownerId);

	int updateKiaLabel(long ownerId, String objectId, long kiaLabel);

	/**
	 * 列举不包含strResourceGroups 的文件或者 版本节点
	 * 
	 * @param filter
	 * @param limit
	 * @param notInRegionId
	 * @return
	 */
	List<INode> lstContentsNodeFilterRGs(INode filter, Limit limit, String strResourceGroups);

	/**
	 * 
	 * 云盘文件智能分类展示，根据doctype类型判断返回对应内容。
	 * doctype类型请参见pw.cdmi.box.ufm.tools.DocType
	 *
	 * <参数类型> @param paramMap keys:{long ownerId, int doctype, OrderV1 order,
	 * Limit limit} <参数类型> @return <参数类型> @throws Exception
	 *
	 * @return List<INode>
	 */
	List<INode> getINodeByDoctype(INode filter, List<Order> orderList, long offset, int limit);

	List<INode> getINodeByDoctypeName(INode filter,String doctype, List<Order> orderList, long offset, int limit);

	Integer getINodeByDoctypeCount(INode node);

	Integer getINodeByDoctypeNameCount(INode node,String doctype);

	/**
	 * 
	 * 云盘文件智能分类对应的表进行割接。将DocType=5对应的类型修改DocType=x
	 *
	 * <参数类型> @param paramMap keys:{long owner_id,int doctype} <参数类型> @return
	 * <参数类型> @throws Exception
	 *
	 * @return List<INode>
	 */
	void updateINodeDoctype(Map<String, Map<String, Object>> paramMap) throws Exception;

	List<INode> getSubReciveINode(INode filter, List<Order> orderList, long offset, int limit, boolean withExtraType);

	int getSubReciveINodeCount(INode iNode, boolean withExtraType);

	INode getSubFolderByName(INode parantNode, String name);

	int getSubINodeCount(INode parentNode, ListFolderRequest listFolderRequest);

	List<INode> getINodeByParentAndStatus(INode parentNode, ListFolderRequest listFolderRequest);

	List<INode> getSubNode(INode parentNode, ListFolderRequest listFolderRequest);

}
