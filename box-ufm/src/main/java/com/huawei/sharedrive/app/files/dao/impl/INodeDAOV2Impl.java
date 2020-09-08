package com.huawei.sharedrive.app.files.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;
import com.huawei.sharedrive.app.plugins.preview.util.PreviewFileUtil;
import com.huawei.sharedrive.app.share.dao.ShareDAO;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;
import com.huawei.sharedrive.app.utils.OrderCommon;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.ufm.tools.Doctype.Doctypes;
import pw.cdmi.box.ufm.tools.DoctypeManager;
import pw.cdmi.core.utils.HashTool;

/**
 * @author q90003805
 * 
 */
@Service("iNodeDAOV2")
@SuppressWarnings("deprecation")
public class INodeDAOV2Impl extends AbstractDAOImpl implements INodeDAOV2 {

	private static Logger logger = LoggerFactory.getLogger(INodeDAOV2Impl.class);

	private static final int TABLE_COUNT = 500;

	@Autowired
	private PreviewFileUtil previewFileUtil;
	
	 
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private ShareDAO shareDAO;

	@Override
	public int getSubINodeCount(INode iNode, boolean withExtraType) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("filter", iNode);
		map.put("withExtraType", withExtraType);
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getSubCountV2", map);
		return num;
	}
	
	@Override
	public int getSubReciveINodeCount(INode iNode, boolean withExtraType) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("filter", iNode);
		map.put("withExtraType", withExtraType);
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getByCreatedCountV2", map);;
		return num;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<INode> getSubReciveINode(INode filter, List<Order> orderList, long offset, int limit,
			boolean withExtraType) {
		filter.setTableSuffix(getTableSuffix(filter));

		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("offset", offset);
		map.put("limit", limit);
		map.put("filter", filter);
		map.put("withExtraType", withExtraType);
		if (CollectionUtils.isNotEmpty(orderList)) {
			map.put("orderBy", getOrderByStr(orderList));
		}
		List<INode> list;
		list = sqlMapClientTemplate.queryForList("INode.getByCreatedV2", map);	
		for (INode node : list) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<INode> getINodeByParentAndStatus(INode filter, List<Order> orderList, long offset, int limit,
			boolean withExtraType) {
		filter.setTableSuffix(getTableSuffix(filter));

		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("offset", offset);
		map.put("limit", limit);
		map.put("filter", filter);
		map.put("withExtraType", withExtraType);
		if (CollectionUtils.isNotEmpty(orderList)) {
			map.put("orderBy", getOrderByStr(orderList));
		}
		List<INode> list;
		list = sqlMapClientTemplate.queryForList("INode.getByParentAndStatusV2", map);	
		for (INode node : list) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<INode> getAllNormalINodeByParent(INode node) {
		node.setStatus(INode.STATUS_NORMAL);
		node.setTableSuffix(getTableSuffix(node));
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("offset",0);
		map.put("limit", 100000);
		map.put("filter", node);
		map.put("withExtraType", true);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getByParentAndStatusV2", map);
		return list;
	}

	@Override
	public int getINodeCountByStatus(INode iNode, boolean withExtraType) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", iNode);
		map.put("withExtraType", withExtraType);
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getCountByStatusV2", map);;
		return num;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<INode> getINodeByStatus(INode filter, List<Order> orderList, long offset, int limit,
			boolean withExtraType) {
		filter.setTableSuffix(getTableSuffix(filter));

		Map<String, Object> map = new HashMap<String, Object>(4);
		// 解决中文名称排序问题
		if (CollectionUtils.isNotEmpty(orderList)) {
			map.put("orderBy", getOrderByStr(orderList));
		}
		map.put("filter", filter);
		map.put("limit", limit);
		map.put("offset", offset);
		map.put("withExtraType", withExtraType);
		
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getbystatusV2", map);
		for (INode node : list) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return list;
	}

	@Override
	public int getINodeCountByName(long ownerId, String name, boolean withExtraType, 
	    String labelIds, Integer docType) {
		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setName(name);
		// 列举文件或者文件夹，不列举版本
		iNode.setType(INode.TYPE_VERSION);
		// 处于正常状态
		iNode.setStatus(INode.STATUS_NORMAL);
		iNode.setTableSuffix(getTableSuffix(iNode));
		if (docType != null){
            iNode.setDoctype(docType);
        }
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("filter", iNode);
		map.put("withExtraType", withExtraType);
		map.put("labelIds", labelIds);
		
		
		return (Integer) sqlMapClientTemplate.queryForObject("INode.getCountByNameV2", map);
	}

	@SuppressWarnings({ "PMD.ExcessiveParameterList", "unchecked" })
	@Override
	public List<INode> searchByName(long ownerId, String name, List<Order> orderList, long offset, int limit,
			boolean withExtraType, String labelIds, Integer docType) {
		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setName(name);

		// 查询文件或者文件夹，非版本类型
		iNode.setType(INode.TYPE_VERSION);
		iNode.setStatus(INode.STATUS_NORMAL);
		iNode.setTableSuffix(getTableSuffix(iNode));
		if (docType != null){
		    iNode.setDoctype(docType);
		}
		

		Map<String, Object> map = new HashMap<String, Object>(4);
		if (CollectionUtils.isNotEmpty(orderList)) {
			map.put("orderBy", getOrderByStr(orderList));
		}
		map.put("filter", iNode);
		map.put("limit", limit);
		map.put("offset", offset);
		map.put("withExtraType", withExtraType);
		map.put("labelIds", labelIds);
		
		List<INode> list = sqlMapClientTemplate.queryForList("INode.searchNodeByNameV2", map);
		for (INode node : list) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return list;
	}

	private String getOrderByStr(List<Order> orderList) {
		return OrderCommon.getOrderByStr(orderList);
	}
	
	private String getOrderByStr(List<Order> orderList,String suffix) {
		return OrderCommon.getOrderByStr(orderList);
	}

	private int getTableSuffix(INode iNode) {
		long ownerId = iNode.getOwnedBy();
		if (ownerId <= 0) {
			throw new IllegalArgumentException("illegal owner id " + ownerId);
		}
		return getTableSuffix(ownerId);
	}

	public static int getTableSuffix(long ownerId) {
		int table = (int) (HashTool.apply(String.valueOf(ownerId)) % TABLE_COUNT);
		return table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<INode> getByLinkStatus(long ownerId, String name, List<Order> orderList, long offset, int limit) {
		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setName(name);
		iNode.setTableSuffix(getTableSuffix(iNode));

		Map<String, Object> map = new HashMap<String, Object>(3);
		// 解决中文名称排序问题
		if (CollectionUtils.isNotEmpty(orderList)) {
			map.put("orderBy", getOrderByStr(orderList));
		}
		Limit limit2 = new Limit(offset, limit);

		map.put("filter", iNode);
		map.put("limit", limit2);
		map.put("offset", offset);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getByLinkStatus", map);
		for (INode node : list) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return list;
	}

	@Override
	public int getCountByLinkStatus(long ownerId, String name) {
		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setName(name);
		iNode.setTableSuffix(getTableSuffix(iNode));
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getCountByLinkStatus", iNode);

		return num;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<INode> getINodeByStatusInternal(INode filter, List<Order> orderList, Limit limit,
			boolean withExtraType) {
		filter.setTableSuffix(getTableSuffix(filter));
		Map<String, Object> map = new HashMap<String, Object>(3);
		// 解决中文名称排序问题
		if (CollectionUtils.isNotEmpty(orderList)) {
			map.put("orderBy", getOrderByStr(orderList));
		}
		map.put("filter", filter);
		map.put("limit", limit);
		map.put("withExtraType", withExtraType);
		return sqlMapClientTemplate.queryForList("INode.getbystatusV2", map);
	}

	@Override
	public UserStatisticsInfo getUserInfoById(long ownerId) {
		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setTableSuffix(getTableSuffix(iNode));
		iNode.setType(INode.TYPE_FOLDER);
		iNode.setStatus(INode.STATUS_DELETE);
		UserStatisticsInfo userSpaceInfo = (UserStatisticsInfo) sqlMapClientTemplate
				.queryForObject("INode.getUserInfoById", iNode);
		if (userSpaceInfo.getSpaceUsed() == null) {
			userSpaceInfo.setSpaceUsed(0L);
		}
		if (userSpaceInfo.getFileCount() == null) {
			userSpaceInfo.setFileCount(0L);
		}
		return (UserStatisticsInfo) userSpaceInfo;
	}

	@Override
	public int updateKiaLabel(long ownerId, String objectId, long kiaLabel) {

		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setObjectId(objectId);
		iNode.setKiaLabel(kiaLabel);
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateKiaLabel", iNode);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<INode> lstContentsNodeFilterRGs(INode filter, Limit limit, String strResourceGroups) {
		filter.setTableSuffix(getTableSuffix(filter));
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", filter);
		map.put("limit", limit);
		map.put("strResourceGroups", strResourceGroups);
		return sqlMapClientTemplate.queryForList("INode.lstContentsNodeByRegionId", map);
	}

	/**
	 * 云盘文件智能分类展示，根据doctype类型判断返回总记录 <参数类型> @param INode <参数类型> @return
	 * <参数类型> @throws Exception
	 */
	@Override
	public Integer getINodeByDoctypeCount(INode node) {
		node.setTableSuffix(getTableSuffix(node));
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("filter", node);
		map.put("labelIds", node.getFilelabelIds());
		
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getINodeByDoctypeCount", map);
		logger.info("---selete-c- info-o,f,s,t,d--" + node.getOwnedBy() + "," + node.getTableSuffix() + ","
				+ node.getStatus() + ", " + node.getType() + ", " + node.getDoctype());
		logger.info("INodeDAOV2Impl.getINodeByDoctypeCount[Suffix,num]: " + node.getTableSuffix() + "," + num);
		return num;
	}

	@Override
	public Integer getINodeByDoctypeNameCount(INode node,String doctype) {
		node.setTableSuffix(getTableSuffix(node));
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("filter", node);
		map.put("doctype", doctype);
		map.put("labelIds", node.getFilelabelIds());
		
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getINodeByDoctypeNameCount", map);
		return num;
	}

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
	@Override
	public List<INode> getINodeByDoctype(INode filter, List<Order> orderList, long offset, int limit) {
		filter.setTableSuffix(getTableSuffix(filter));
		logger.info("---selete-l- info-o,f,s,t,d--" + filter.getOwnedBy() + "," + filter.getTableSuffix() + ","
				+ filter.getStatus() + ", " + filter.getType() + ", " + filter.getDoctype());

		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("offset", offset);
		map.put("limit", limit);
		map.put("filter", filter);
		map.put("labelIds", filter.getFilelabelIds());
		if (CollectionUtils.isNotEmpty(orderList)) {
			map.put("orderBy", getOrderByStr(orderList));
		}
		@SuppressWarnings("unchecked")
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getINodeByDoctype", map);
		logger.info("INodeDAOV2Impl.getINodeByDoctype[Suffix,size]: " + filter.getTableSuffix() + "," + list.size());
		for (INode node : list) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return list;
	}

	@Override
	public List<INode> getINodeByDoctypeName(INode filter,String doctype, List<Order> orderList, long offset, int limit) {
		filter.setTableSuffix(getTableSuffix(filter));
		logger.info("---selete-l- info-o,f,s,t,d--" + filter.getOwnedBy() + "," + filter.getTableSuffix() + ","
				+ filter.getStatus() + ", " + filter.getType() + ", " + filter.getDoctype());

		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("offset", offset);
		map.put("limit", limit);
		map.put("filter", filter);
		map.put("doctype", doctype);
		map.put("labelIds", filter.getFilelabelIds());
		
		if (CollectionUtils.isNotEmpty(orderList)) {
			map.put("orderBy", getOrderByStr(orderList));
		}
		@SuppressWarnings("unchecked")
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getINodeByDoctypeName", map);
		logger.info("INodeDAOV2Impl.getINodeByDoctype[Suffix,size]: " + filter.getTableSuffix() + "," + list.size());
		for (INode node : list) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return list;
	}

	@Autowired
	DoctypeManager doctypeManager;

	/**
	 * 
	 * 云盘文件智能分类对应的表进行割接。将DocType=5对应的类型修改DocType=x
	 *
	 * <参数类型> @param paramMap 可接受参数 keys:{int doctype,} <参数类型> @return
	 * <参数类型> @throws Exception
	 *
	 * @return List<INode>
	 */
	@Override
	public void updateINodeDoctype(Map<String, Map<String, Object>> paramMap) throws Exception {
		try {
			logger.info("INodeDAOV2Impl.updateINodeDoctype[paramMap.keySet.size] " + paramMap.keySet().size());
			// 处理拥有都下的所有类型
			Object onwedBy = paramMap.get("onwedBy");
			logger.info("INodeDAOV2Impl.updateINodeDoctype[onwedBy] " + onwedBy);
			if (null != onwedBy) {
				Map<String, Map<String, Object>> docTypeAll = doctypeManager.getDoctypeAll();
				if (docTypeAll.containsKey(Doctypes.other.name())) {
					docTypeAll.remove(Doctypes.other.name());
				}
				for (String key : docTypeAll.keySet()) {
					Map<String, Object> map = new HashMap<String, Object>();
					int doctypeValue = Integer.valueOf(docTypeAll.get(key).get(Doctypes.doctype.name()).toString());
					map.put(key, doctypeValue); // doctyp = ?
					map.put("ownedBy", onwedBy);
					map.put("regFileNameSuffix", doctypeManager.getRegdoctypeValue(doctypeValue));

					INode iNode = new INode();
					iNode.setOwnedBy((long) onwedBy);
					map.put("tableSuffix", getTableSuffix(iNode));
					sqlMapClientTemplate.update("INode.updateINodeDocType", map);
					logger.info("INodeDAOV2Impl.updateINodeDoctype owner inode_" + getTableSuffix(iNode)
							+ " update success ");
				}
			}
			// 处理确定类型
			else {
				@SuppressWarnings("unchecked")
				List<String> schemaNameLst = sqlMapClientTemplate.queryForList("INode.getINodeTables");
				logger.info("INodeDAOV2Impl.updateINodeDoctype[schemaNameLst.size] " + schemaNameLst.size());
				for (String schemaName : schemaNameLst) {

					Map<String, Object> snMap = new HashMap<String, Object>(2);
					snMap.put("schemaName", schemaName);
					@SuppressWarnings("unchecked")
					List<String> tableNameLst = sqlMapClientTemplate.queryForList("INode.getINodeTableName", snMap);
					logger.info("INodeDAOV2Impl.updateINodeDoctype[tableNameLst.size] " + tableNameLst.size());

					for (String tableName : tableNameLst) {
						for (String key : paramMap.keySet()) {

							int doctype = Integer.valueOf(paramMap.get(key).get(Doctypes.doctype.name()).toString());

							Map<String, Object> map = new HashMap<String, Object>(3);
							map.put("tableName", tableName);
							map.put(Doctypes.doctype.name(), doctype);
							// extensions结果参见Doctype类
							String extensions = "\\." + paramMap.get(key).get(Doctypes.extensions.name()).toString()
									.replaceAll(",", "|\\\\.") + "$";
							map.put("regFileNameSuffix", extensions);
							// System.out.println("---"+map);
							sqlMapClientTemplate.update("INode.updateINodeDocType", map);
							// logger.info("INodeDAOV2Impl.updateINodeDoctype "
							// + tableName + " doctype's " +
							// Doctypes.doctype.name() + " update success ");
						}
					}
					logger.info("INodeDAOV2Impl.updateINodeDoctype[update success] ");
				}
			}
		} catch (Exception e) {
			logger.error("ufm-INodeDAOV2Impl.updateINodeDoctype error ", e);
			throw e;
		}
	}

	@Override
	public INode getSubFolderByName(INode parantNode, String name) {
		// TODO Auto-generated method stub
		
		Map<String, Object> map = new HashMap<String, Object>(4);
		parantNode.setTableSuffix(getTableSuffix(parantNode));
		map.put("filter", parantNode);
		map.put("name", name);
		INode subInode = (INode) sqlMapClientTemplate.queryForObject("INode.getSubFolderByName",map);
		return subInode;
	}

	@Override
	public int getSubINodeCount(INode parentNode, ListFolderRequest listFolderRequest) {
		// TODO Auto-generated method stub
		parentNode.setTableSuffix(getTableSuffix(parentNode));
		parentNode.setStatus(INode.STATUS_NORMAL);
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("filter", parentNode);
		if(listFolderRequest.getCreatedBy()!=null){
			map.put("createdBy", listFolderRequest.getCreatedBy());
		}
		map.put("withExtraType", listFolderRequest.getWithExtraType());
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getSubCountV2", map);
		return num;
	}

	@Override
	public List<INode> getINodeByParentAndStatus(INode parentNode, ListFolderRequest listFolderRequest) {
		// TODO Auto-generated method stub
		parentNode.setTableSuffix(getTableSuffix(parentNode));

		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("offset", listFolderRequest.getOffset());
		map.put("limit", listFolderRequest.getLimit());
		map.put("filter", parentNode);
		map.put("withExtraType", listFolderRequest.getWithExtraType());
		if (CollectionUtils.isNotEmpty(listFolderRequest.getOrder())) {
			map.put("orderBy", getOrderByStr(listFolderRequest.getOrder()));
		}
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getByParentAndStatusV2", map);	
		for (INode node : list) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return list;
	}

	@Override
	public List<INode> getSubNode(INode parentNode, ListFolderRequest listFolderRequest) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		parentNode.setTableSuffix(getTableSuffix(parentNode));
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("offset", listFolderRequest.getOffset());
		map.put("limit", listFolderRequest.getLimit());
		map.put("filter", parentNode);
		if(listFolderRequest.getCreatedBy()!=null){
			map.put("createdBy", listFolderRequest.getCreatedBy());
		}
		map.put("withExtraType", listFolderRequest.getWithExtraType());
		if (CollectionUtils.isNotEmpty(listFolderRequest.getOrder())) {
			map.put("orderBy", getOrderByStr(listFolderRequest.getOrder(),"t1"));
		}
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getSubNodeV2", map);	
		for (INode node : list) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
			
		}
		return list;
	
	}

}
