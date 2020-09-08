package com.huawei.sharedrive.app.share.dao.impl;

import com.huawei.sharedrive.app.share.dao.INodeLinkApproveRecordDao;
import com.huawei.sharedrive.app.share.domain.INodeLinkApproveRecord;
import org.springframework.stereotype.Component;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import java.util.List;

@Component("iNodeLinkApproveRecordDao")
public class INodeLinkApproveRecordDaoImpl extends CacheableSqlMapClientDAO implements INodeLinkApproveRecordDao {
	@Override
	public void create(INodeLinkApproveRecord linkApprove) {
		sqlMapClientTemplate.insert("INodeLinkApproveRecord.insert",linkApprove);
	}

	@Override
	public void deleteByLinkCode(String linkCode) {
		sqlMapClientTemplate.delete("INodeLinkApproveRecord.deleteByLinkCode", linkCode);
	}

	@Override
	public List<INodeLinkApproveRecord> listByLinkCode(String linkCode) {
		return sqlMapClientTemplate.queryForList("INodeLinkApproveRecord.listByLinkCode", linkCode);
	}
}
