package com.huawei.sharedrive.app.files.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.batchparam.BatchParams;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.exception.NodeException;
import com.huawei.sharedrive.app.files.synchronous.INodeRowHandle;
import com.huawei.sharedrive.app.plugins.preview.util.PreviewFileUtil;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.ufm.tools.DoctypeManager;
import pw.cdmi.box.ufm.tools.Doctype.Doctypes;
import pw.cdmi.core.utils.HashTool;

@Service("iNodeDAO")
@SuppressWarnings({ "deprecation", "unchecked" })
public class INodeDAOImpl extends AbstractDAOImpl implements INodeDAO {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(INodeDAOImpl.class);

	public static final int TABLE_COUNT = 500;

	private static final long BASE_NODE_ID = 0;

	private static final long BASE_SYNCVER_NUM = 0;

	@Autowired
	private PreviewFileUtil previewFileUtil;

	@Autowired
	private DoctypeManager doctypeManager;

	@Override
	public List<INode> batchQueryNormalByParentAndStatus(Long ownerId, List<INode> parentList) {

		int listSize = parentList.size();
		if (listSize == 0) {
			return Collections.EMPTY_LIST;
		}
		int batchQueryItems = BatchParams.getBatchQueryItems();
		if (listSize <= batchQueryItems) {
			return batchQueryNormalByParentOnce(ownerId, parentList);
		}
		List<INode> tempParentList;
		int start = 0;
		int end = 0;
		List<INode> result = new ArrayList<INode>(100);
		while (start < listSize) {
			if (listSize - start > batchQueryItems) {
				end = start + batchQueryItems;
			} else {
				end = listSize;
			}
			tempParentList = parentList.subList(start, end);
			start = end;
			result.addAll(batchQueryNormalByParentOnce(ownerId, tempParentList));
		}
		return result;
	}

	@Override
	public int batchUpdateStatusByParentList(INode inode, List<Long> parentList) {
		if (parentList.isEmpty()) {
			return 0;
		}
		Map<String, Object> map = new HashMap<String, Object>(2);
		inode.setTableSuffix(getTableSuffix(inode));
		map.put("filter", inode);
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Long parentId : parentList) {
			sb.append(parentId).append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(')');
		map.put("parentListString", sb.toString());
		int res = sqlMapClientTemplate.update("INode.updateStatusByParentList", map);
		return res;
	}

	@Override
	public boolean checkINodeExist(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		Long id = (Long) sqlMapClientTemplate.queryForObject("INode.check", iNode);

		return null != id;
	}

	@Override
	public void copyTempINodeTable(INode srcNode, Long destTableSuffix) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("srcTableSuffix", getTableSuffix(srcNode));
		map.put("ownedBy", srcNode.getOwnedBy());
		map.put("destTableSuffix", destTableSuffix);
		sqlMapClientTemplate.update("INode.copyTempInode", map);
	}

	@Override
	public void copyTempINodeTableNoBackup(INode srcNode, Long destTableSuffix) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("srcTableSuffix", getTableSuffix(srcNode));
		map.put("ownedBy", srcNode.getOwnedBy());
		map.put("destTableSuffix", destTableSuffix);
		sqlMapClientTemplate.update("INode.copyTempInodeNoBackup", map);
	}

	@Override
	public void create(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		// 对于所有新建的inode,当是文件的时候都要对doctype进行赋值
		if (iNode.getType()>0 && -1 != iNode.getName().lastIndexOf(".")) {
			try {
				iNode.setDoctype(doctypeManager.contains(iNode.getName().substring(iNode.getName().lastIndexOf(".") + 1)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			iNode.setDoctype(Doctypes.other.getValue());
		}
		sqlMapClientTemplate.insert("INode.insert", iNode);
	}

	@Override
	public int decreaseFileVersionNum(long ownedBy, long fileId) {
		INode node = new INode();
		node.setOwnedBy(ownedBy);
		node.setId(fileId);
		node.setTableSuffix(getTableSuffix(ownedBy));
		return sqlMapClientTemplate.update("INode.decreaseFileVersionNum", node);
	}

	@Override
	public int delete(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.delete("INode.delete", iNode);
	}

	@Override
	public int deleteNodeByObjectAndCheckStatus(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.delete("INode.deleteObjectNodeCheckStatus", iNode);
	}

	@Override
	public void dropTempINodeTable(long ownerId, Long destTableSuffix) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("destTableSuffix", destTableSuffix);
		map.put("ownedBy", ownerId);
		sqlMapClientTemplate.update("INode.dropTempInode", map);
	}

	@Override
	public INode get(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		INode node = (INode) sqlMapClientTemplate.queryForObject("INode.get", iNode);
		if (node != null) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return node;
	}

	@Override
	public INode get(long ownerId, long inodeId) {
		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setId(inodeId);
		iNode.setTableSuffix(getTableSuffix(iNode));
		INode node = (INode) sqlMapClientTemplate.queryForObject("INode.get", iNode);
		if (node != null) {
			node.setPreviewable(previewFileUtil.isPreviewable(node));
		}
		return node;
	}

	@Override
	public INodeRowHandle getAllINodeMetadatas(long ownerId, final INodeRowHandle rowHandle) {
		INode node = new INode();
		node.setTableSuffix(getTableSuffix(ownerId));
		node.setOwnedBy(ownerId);

		sqlMapClientTemplate.queryWithRowHandler("INode.getAllINodeMetadatas", node, rowHandle);
		rowHandle.getFolderLst().clear();
		return rowHandle;
	}

	@Override
	public INodeRowHandle getChangeMetadatas(long ownerId, Date modifiedAt, final INodeRowHandle rowHandle) {
		INode node = new INode();
		node.setOwnedBy(ownerId);
		node.setTableSuffix(getTableSuffix(node));
		node.setModifiedAt(modifiedAt);
		node.setStatus(INode.STATUS_NORMAL);
		// 不列表版本内容
		node.setType(INode.TYPE_VERSION);

		// 不列表全盘备份元数据
		node.setSyncStatus(INode.SYNC_STATUS_BACKUP);

		sqlMapClientTemplate.queryWithRowHandler("INode.getChangeNodesAfterTime", node, rowHandle);
		rowHandle.getFolderLst().clear();
		return rowHandle;
	}

	@Override
	public List<INode> getChangeNodesAfterTime(long ownerId, Date modifiedAt) {
		INode node = new INode();
		node.setOwnedBy(ownerId);
		node.setTableSuffix(getTableSuffix(node));
		node.setModifiedAt(modifiedAt);
		node.setStatus(INode.STATUS_NORMAL);
		// 不列表版本内容
		node.setType(INode.TYPE_VERSION);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getChangeNodesAfterTime", node);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public INodeRowHandle getDeltaINodeMetadatas(long ownerId, long beginSyncVersion, long endSyncVersion,
			final INodeRowHandle rowHandle) {
		INode parentNode = new INode();
		parentNode.setOwnedBy(ownerId);
		parentNode.setTableSuffix(getTableSuffix(parentNode));
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", parentNode);
		map.put("beginSyncVersion", beginSyncVersion);
		map.put("endSyncVersion", endSyncVersion);
		map.put("excludeSyncStatus", INode.SYNC_STATUS_BACKUP);
		map.put("excludeEmailSyncStatus", INode.SYNC_STATUS_EMAIL);

		sqlMapClientTemplate.queryWithRowHandler("INode.geSyncNodeBySyncVer", map, rowHandle);
		rowHandle.getFolderLst().clear();

		return rowHandle;

	}

	@Override
	public List<INode> getEarliestVersions(Long ownerId, Long nodeId, int limit) {

		INode filter = new INode();
		filter.setParentId(nodeId);
		filter.setOwnedBy(ownerId);
		filter.setTableSuffix(getTableSuffix(filter));
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", filter);
		map.put("limit", limit);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getEarliestVersions", map);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public List<INode> getFileByNameAndParentId(String name, long parentId, long ownerId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("name", name);
		map.put("parentId", parentId);
		map.put("ownedBy", ownerId);
		map.put("tableSuffix", getTableSuffix(ownerId));
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getByNameAndParentId", map);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public INodeRowHandle getFolderMetadatas(INode filter, final INodeRowHandle rowHandle) {
		filter.setTableSuffix(getTableSuffix(filter));
		// 不列表全盘备份元数据
		filter.setSyncStatus(INode.SYNC_STATUS_BACKUP);

		sqlMapClientTemplate.queryWithRowHandler("INode.getFolderMetadatas", filter, rowHandle);

		return rowHandle;
	}

	@Override
	public INode getINodeByName(long ownerId, long parentNodeId, String name) {

		INode node = new INode();
		node.setOwnedBy(ownerId);
		node.setParentId(parentNodeId);
		node.setName(name);
		node.setTableSuffix(getTableSuffix(node));
		INode tmp = (INode) sqlMapClientTemplate.queryForObject("INode.getNodeByName", node);
		if (tmp != null) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return tmp;
	}

	@Override
	public List<INode> getINodeByObjectId(long ownerId, String objectId) {
		INode inode = new INode();
		inode.setOwnedBy(ownerId);
		inode.setObjectId(objectId);
		inode.setTableSuffix(getTableSuffix(inode));
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getObject", inode);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public List<INode> getINodeByParent(INode filter, OrderV1 order, Limit limit) {
		List<INode> list = getINodeByParentInternal(filter, order, limit);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public List<INode> getINodeByParentAndStatus(INode filter, OrderV1 order, Limit limit) {
		filter.setTableSuffix(getTableSuffix(filter));

		Map<String, Object> map = new HashMap<String, Object>(3);
		// 解决中文名称排序问题
		if (order != null && "name".equals(order.getField())) {
			order.setField("convert(name using gb2312) ");
		}
		map.put("limit", limit);
		map.put("filter", filter);
		map.put("order", order);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getbyparentandstatus", map);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public List<INode> getINodeByParentAndType(INode filter, Limit limit) {
		filter.setTableSuffix(getTableSuffix(filter));
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", filter);
		map.put("limit", limit);
		return sqlMapClientTemplate.queryForList("INode.getByParentAndType", map);
	}

	@Override
	public List<INode> getINodeByParentInternal(INode filter, OrderV1 order, Limit limit) {
		filter.setTableSuffix(getTableSuffix(filter));
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", filter);
		map.put("order", order);
		map.put("limit", limit);
		return sqlMapClientTemplate.queryForList("INode.getbyparent", map);
	}

	@Override
	public List<INode> getINodeByStatus(INode filter, OrderV1 order, Limit limit) {
		filter.setTableSuffix(getTableSuffix(filter));
		Map<String, Object> map = new HashMap<String, Object>(3);
		// 解决中文名称排序问题
		if (order != null && "name".equals(order.getField())) {
			order.setField("convert(name using gb2312) ");
		}
		map.put("filter", filter);
		map.put("order", order);
		map.put("limit", limit);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getbystatus", map);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public List<INode> getINodeBySyncVersion(long ownerId, long beginSyncVersion, long endSyncVersion) {
		INode parentNode = new INode();
		parentNode.setOwnedBy(ownerId);
		parentNode.setTableSuffix(getTableSuffix(parentNode));
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", parentNode);
		map.put("beginSyncVersion", beginSyncVersion);
		map.put("endSyncVersion", endSyncVersion);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.geSyncNodeBySyncVer", map);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;

	}

	@Override
	public int getINodeCountByName(long ownerId, String name) {
		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setName(name);
		// 列举文件或者文件夹，不列举版本
		iNode.setType(INode.TYPE_VERSION);
		// 处于正常状态
		iNode.setStatus(INode.STATUS_NORMAL);
		iNode.setTableSuffix(getTableSuffix(iNode));
		return (Integer) sqlMapClientTemplate.queryForObject("INode.getCountByName", iNode);
	}

	@Override
	public int getINodeCountByStatus(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getCountByStatus", iNode);

		return num;
	}

	@Override
	public int getINodeCountByStatusIgnoreVersion(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getCountByStatusIgnoreVersion", iNode);

		return num;
	}

	@Override
	public List<INode> getINodeFilterd(INode filter, OrderV1 order, Limit limit) {
		filter.setTableSuffix(getTableSuffix(filter));

		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", filter);
		map.put("order", order);
		map.put("limit", limit);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getFilterd", map);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public int getINodeTotal(INode filter) {
		filter.setTableSuffix(getTableSuffix(filter));
		return (int) sqlMapClientTemplate.queryForObject("INode.getSubINodeAndSelfTotal", filter);
	}

	@Override
	public int getINodeTotalForUpdate(INode filter) {
		filter.setTableSuffix(getTableSuffix(filter));
		return (int) sqlMapClientTemplate.queryForObject("INode.getSubINodeAndSelfTotalForUpdate", filter);
	}

	@Override
	public long getMaxINodeId(long ownedBy) {
		INode node = new INode();
		node.setOwnedBy(ownedBy);
		node.setTableSuffix(getTableSuffix(node));
		Object maxINodeId = sqlMapClientTemplate.queryForObject("INode.getMaxINodeId", node);
		if (maxINodeId == null) {
			return BASE_NODE_ID;
		}
		return (Long) maxINodeId;
	}

	@Override
	public long getMaxSyncVersion(long ownedBy) {
		INode node = new INode();
		node.setOwnedBy(ownedBy);
		node.setTableSuffix(getTableSuffix(node));
		Object maxSyncVersion = sqlMapClientTemplate.queryForObject("INode.getMaxSyncVersion", node);
		if (maxSyncVersion == null) {
			return BASE_SYNCVER_NUM;
		}
		return (Long) maxSyncVersion;
	}

	@Override
	public List<INode> getNodeByName(long ownerId, long parentId, String name) {
		INode inode = new INode();
		inode.setOwnedBy(ownerId);
		inode.setParentId(parentId);
		inode.setName(name);
		inode.setStatus(INode.STATUS_NORMAL);
		inode.setTableSuffix(getTableSuffix(inode));
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getByName", inode);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public List<INode> getSubINodeAndSelf(INode filter, OrderV1 order, Limit limit) {
		List<INode> list = null;
		
		try{
			filter.setTableSuffix(getTableSuffix(filter));

			Map<String, Object> map = new HashMap<String, Object>(3);
			map.put("filter", filter);
			map.put("order", order);
			map.put("limit", limit);
			list = sqlMapClientTemplate.queryForList("INode.getSubINodeAndSelf", map);
			
			for (INode tmp : list) {
				tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
			}
			return list;
		}catch(Exception e){
			LOGGER.error("[INodeDaoImpl] getSubINodeAndSelf error:" + e.getMessage());
		}
		
		return list;
	}

	@Override
	public List<INode> getSubINodeByTypeAndStatus(INode parentNode) {
		parentNode.setTableSuffix(getTableSuffix(parentNode));
		List<INode> list = sqlMapClientTemplate.queryForList("INode.getSubNodebyType", parentNode);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public int getSubINodeCount(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		Integer num = (Integer) sqlMapClientTemplate.queryForObject("INode.getSubCount", iNode);

		return num;
	}

	@Override
	public INodeRowHandle getTempINodes(long ownerId, Long destTableSuffix, Limit limit,
			final INodeRowHandle rowHandle) {
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("destTableSuffix", destTableSuffix);
		map.put("ownedBy", ownerId);
		map.put("datalen", limit.getLength());
		map.put("offset", limit.getOffset());

		sqlMapClientTemplate.queryWithRowHandler("INode.getTempInode", map, rowHandle);
		rowHandle.getFolderLst().clear();

		return rowHandle;
	}

	@Override
	public long getTrashTotalSize(INode iNode) {
		INode tmpNode = INode.valueOf(iNode);
		tmpNode.setTableSuffix(getTableSuffix(tmpNode));
		tmpNode.setType(INode.TYPE_FILE);
		tmpNode.setStatus(INode.STATUS_TRASH);
		long size1 = (long) sqlMapClientTemplate.queryForObject("INode.getTrashTotalSize", tmpNode);

		tmpNode.setStatus(INode.STATUS_TRASH_DELETE);

		Object result2 = sqlMapClientTemplate.queryForObject("INode.getTrashTotalSize", tmpNode);

		if (result2 != null) {
			return size1 + (long) result2;
		}
		return size1;
	}

	@Override
	public long getUserTotalFiles(Long ownerId) {
		INode node = new INode();
		node.setOwnedBy(ownerId);
		node.setTableSuffix(getTableSuffix(node));
		Object fileCount = sqlMapClientTemplate.queryForObject("INode.getUserTotalFiles", node);
		return fileCount != null ? (long) fileCount : 0L;
	}

	@Override
	public long getUserTotalSpace(long ownerId) {
		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setTableSuffix(getTableSuffix(iNode));
		iNode.setType(INode.TYPE_FOLDER);
		iNode.setStatus(INode.STATUS_DELETE);
		Object totalSize = sqlMapClientTemplate.queryForObject("INode.getUserTotalSpace", iNode);

		if (totalSize != null) {
			return (long) totalSize;
		}
		return 0L;
	}

	@Override
	public List<INode> listFileAndVersions(int dbNum, int tableNum, Limit limit) {
		INode filter = new INode();
		filter.setTableSuffix(tableNum);
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", filter);
		map.put("limit", limit);
		map.put("partitionNum", dbNum);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.listFileAndVersions", map);
		return list;
	}

	@Override
	public List<INode> lstContentNode(int userdbNumber, int tableNumber, Limit limit) {
		INode iNode = new INode();
		iNode.setTableSuffix(tableNumber);
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", iNode);
		map.put("limit", limit);
		map.put("partitionNum", userdbNumber);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.lstContentNode", map);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public List<INode> lstDeleteNode(int userdbNumber, int tableNumber, Date lastModified, Limit limit) {
		INode iNode = new INode();
		iNode.setStatus(INode.STATUS_DELETE);
		iNode.setTableSuffix(tableNumber);
		iNode.setModifiedAt(lastModified);
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", iNode);
		map.put("limit", limit);
		map.put("partitionNum", userdbNumber);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.lstDeleteNode", map);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public List<INode> lstFileAndVersionNode(long userId, Limit limit) {
		INode iNode = new INode();
		iNode.setTableSuffix(getTableSuffix(userId));
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("filter", iNode);
		map.put("limit", limit);

		return sqlMapClientTemplate.queryForList("INode.lstFileAndVersionNode", map);
	}

	@Override
	public Map<Long, Long> lstFilesNumAndSizesByResourceGroup(int userdbNumber, int tableNumber, int resourceGroupId) {
		INode iNode = new INode();
		iNode.setTableSuffix(tableNumber);
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", iNode);
		map.put("resourceGroupId", resourceGroupId);
		map.put("partitionNum", userdbNumber);
		return sqlMapClientTemplate.queryForMap("INode.statisticByResourceGroupId", map, "key", "value");
	}

	@Override
	public int reallyDeleteFolderNode(int userdbNumber, int tableNumber, Date lastModified) {
		INode iNode = new INode();
		iNode.setStatus(INode.STATUS_DELETE);
		iNode.setTableSuffix(tableNumber);
		iNode.setModifiedAt(lastModified);
		iNode.setType(INode.TYPE_FOLDER);
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", iNode);
		map.put("partitionNum", userdbNumber);
		return sqlMapClientTemplate.delete("INode.deleteFolderNodeCheckStatus", map);
	}

	@Override
	public int replaceObjectForINode(INode node, ObjectReference objRf) {
		node.setTableSuffix(getTableSuffix(node));
		node.setResourceGroupId(objRf.getResourceGroupId());
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("filter", node);
		map.put("newObjectId", objRf.getId());
		return sqlMapClientTemplate.update("INode.replaceObjectForINode", map);
	}

	@Override
	public List<INode> searchNodeByName(long ownerId, String name, OrderV1 order, Limit limit) {
		INode iNode = new INode();
		iNode.setOwnedBy(ownerId);
		iNode.setName(name);

		// 查询文件或者文件夹，不是版本类型
		iNode.setType(INode.TYPE_VERSION);
		iNode.setStatus(INode.STATUS_NORMAL);
		iNode.setTableSuffix(getTableSuffix(iNode));

		Map<String, Object> map = new HashMap<String, Object>(1);
		// 解决中文名称排序问题
		if (order != null && "name".equals(order.getField())) {
			order.setField("convert(name using gb2312) ");
		}
		map.put("filter", iNode);
		map.put("order", order);
		map.put("limit", limit);
		List<INode> list = sqlMapClientTemplate.queryForList("INode.searchNodeByName", map);
		for (INode tmp : list) {
			tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
		}
		return list;
	}

	@Override
	public int update(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		 // 对于所有新建的inode,当是文件的时候都要对doctype进行赋值
        if ( -1 != iNode.getName().lastIndexOf("."))
        {
             try
             {
                  iNode.setDoctype(doctypeManager.contains(iNode.getName().substring(iNode.getName().lastIndexOf(".") + 1)));
             }
             catch (Exception e)
             {
                  e.printStackTrace();
             }
        }  
        // 当没有对应匹配的
        else 
        {
             iNode.setDoctype(Doctypes.other.getValue());
        }
		return sqlMapClientTemplate.update("INode.update", iNode);
	}

	@Override
	public int updateForRename(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		// 对于所有新建的inode,当是文件的时候都要对doctype进行赋值
        if ( -1 != iNode.getName().lastIndexOf("."))
        {
             try
             {
                  iNode.setDoctype(doctypeManager.contains(iNode.getName().substring(iNode.getName().lastIndexOf(".") + 1)));
             }
             catch (Exception e)
             {
                  e.printStackTrace();
             }
        }  
        // 当没有对应匹配的
        else 
        {
             iNode.setDoctype(Doctypes.other.getValue());
        }
		return sqlMapClientTemplate.update("INode.updateForRename", iNode);
	}

	@Override
	public int updateNameAndSyncVersion(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateNameAndSyncVersion", iNode);
	}

	@Override
	public int updateVersionNum(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateVersionNum", iNode);
	}

	@Override
	public int updateForRestore(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateForRestore", iNode);
	}

	@Override
	public int updateForMove(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateForMove", iNode);
	}

	@Override
	public int updateAllINodeStatus(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateAllStatus", iNode);
	}

	@Override
	public int updateAllNodesStatusToDelete(Long ownerId) {
		INode node = new INode();
		node.setOwnedBy(ownerId);
		return sqlMapClientTemplate.update("INode.updateAllNodesStatusToDelete", node);
	}

	@Override
	public int updateByOriginalId(INode node, long originalId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		node.setTableSuffix(getTableSuffix(node.getOwnedBy()));
		map.put("filter", node);
		map.put("originalId", originalId);
		return sqlMapClientTemplate.update("INode.updateByOriginalId", map);
	}

	@Override
	public void updateForUploadFile(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		sqlMapClientTemplate.update("INode.updateForUploadFile", iNode);
	}

	// @Override
	// public int updateINodeByObjectId(INode iNode)
	// {
	// iNode.setTableSuffix(getTableSuffix(iNode));
	// return sqlMapClientTemplate.update("INode.updateByObjectId", iNode);
	// }

	@Override
	public int updateINodeLinkCode(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		int num = sqlMapClientTemplate.update("INode.updateLinkCode", iNode);
		return num;
	}

	@Override
	public int updateINodeShareStatus(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		int num = sqlMapClientTemplate.update("INode.updateShareStatus", iNode);
		return num;
	}

	@Override
	public int updateINodeStatus(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateStatus", iNode);
	}

	@Override
	public int updateINodeStatusToDelete(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateStatusToDelete", iNode);
	}

	@Override
	public int updateINodeSyncVersion(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		int num = sqlMapClientTemplate.update("INode.updateSyncVersion", iNode);
		if (1 != num) {
			throw new NodeException("update");
		}

		return 0;
	}

	@Override
	public int updateObjectForDedup(String newObjId, int newRGId, String oldObjId, long ownerId) {
		INode node = new INode();
		node.setTableSuffix(getTableSuffix(ownerId));
		node.setOwnedBy(ownerId);
		node.setObjectId(newObjId);
		node.setResourceGroupId(newRGId);
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", node);
		map.put("oldObjectId", oldObjId);
		return sqlMapClientTemplate.update("INode.updateObjectForDedup", map);
	}

	@Override
	public int updateObjectForMerge(long size, String sha1, String objId, long ownerId) {
		INode node = new INode();
		node.setTableSuffix(getTableSuffix(ownerId));
		node.setOwnedBy(ownerId);
		node.setObjectId(objId);
		node.setSha1(sha1);
		node.setSize(size);
		return sqlMapClientTemplate.update("INode.updateObjectForMerge", node);
	}

	@Override
	public void updateObjectModifiedAt(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		sqlMapClientTemplate.update("INode.updateObjectModifiedAt", iNode);
	}

	@Override
	public int updateStatusByParent(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateStatusByParent", iNode);
	}

	@Override
	public int updateStatusToRealDelete(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateStatusToRealDelete", iNode);
	}

	@Override
	public int updateSubINodeStatus(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateSubINodeStatus", iNode);
	}

	@Override
	public int updateSubINodeStatusByParent(INode iNode) {
		iNode.setTableSuffix(getTableSuffix(iNode));
		return sqlMapClientTemplate.update("INode.updateSubStatusByparent", iNode);
	}

	@Override
	public int updateTrashAllNodesStatus(INode parentNode, Long id) {
		parentNode.setTableSuffix(getTableSuffix(parentNode));
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("filter", parentNode);
		map.put("status1", INode.STATUS_TRASH);
		if (null != id) {
			map.put("id", id);
		}
		map.put("status2", INode.STATUS_TRASH_DELETE);
		return sqlMapClientTemplate.update("INode.updateTarshItemsStatus", map);
	}

	private List<INode> batchQueryNormalByParentOnce(Long ownerId, List<INode> parentList) {
		if (parentList.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		Map<String, Object> map = new HashMap<String, Object>(2);
		INode tempNode = new INode();
		tempNode.setOwnedBy(ownerId);
		tempNode.setTableSuffix(getTableSuffix(ownerId));
		map.put("filter", tempNode);
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (INode parentNode : parentList) {
			sb.append(parentNode.getId()).append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(')');
		map.put("parentListString", sb.toString());
		return sqlMapClientTemplate.queryForList("INode.getByParentList", map);
	}

	private int getTableSuffix(INode iNode) {
		long ownerId = iNode.getOwnedBy();
		if (ownerId <= 0) {
			throw new InvalidParamException("illegal owner id " + ownerId);
		}
		return getTableSuffix(ownerId);
	}

	private int getTableSuffix(long ownerId) {
		int table = (int) (HashTool.apply(String.valueOf(ownerId)) % TABLE_COUNT);
		return table;
	}

	@Override
	public List<INode> getChiledrenNodes(INode iNode) {
		// TODO Auto-generated method stub
		iNode.setTableSuffix(getTableSuffix(iNode));
		List<INode> nodes = (List<INode>) sqlMapClientTemplate.queryForList("INode.getChildren", iNode);
		// if (node != null)
		// {
		// node.setPreviewable(previewFileUtil.isPreviewable(node));
		// }
		return nodes;
	}


}
