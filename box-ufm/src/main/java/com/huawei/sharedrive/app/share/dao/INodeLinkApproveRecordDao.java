package com.huawei.sharedrive.app.share.dao;

import com.huawei.sharedrive.app.share.domain.INodeLinkApproveRecord;

import java.util.List;

public interface INodeLinkApproveRecordDao {

	void create(INodeLinkApproveRecord approveRecord);

	void deleteByLinkCode(String linkCode);

	List<INodeLinkApproveRecord> listByLinkCode(String linkCode);
}
