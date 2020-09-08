package com.huawei.sharedrive.app.share.dao;

import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.share.domain.INodeLinkApprove;

public interface INodeLinkApproveDao {

	void create(INodeLinkApprove linkApprove);

	void update(INodeLinkApprove linkApprove);

	List<INodeLinkApprove> listLinkApprove(long ownerId, Byte status,long accountId);

	void updateStatus(INodeLinkApprove linkApprove);

	INodeLinkApprove getApproveByLinkCode(String linkCode);

	void deleteByLinkCode(String linkCode);

	List<INodeLinkApprove> listLinkApprove(INodeLinkApprove linkApprove, Map<String, Object> filter);

	int listCount(INodeLinkApprove linkApprove, Map<String, Object> filter);

	void deleteByNodeId(long cloudUserId, long inodeId);

	void updateLinkStatus(long cloudUserId, long inodeId);

	void updateStatusByDuplicateTo(INodeLinkApprove linkApprove);

	List<INodeLinkApprove> listAllLinkApprove(INodeLinkApprove linkApprove, Map<String, Object> filter);

	int listAllCount(INodeLinkApprove linkApprove, Map<String, Object> filter);

	List<INodeLinkApprove> listUserApprove(INodeLinkApprove linkApprove, Map<String, Object> filter);

	int listCountUserApprove(INodeLinkApprove linkApprove, Map<String, Object> filter);

}
