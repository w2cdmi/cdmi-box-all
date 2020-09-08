package com.huawei.sharedrive.app.share.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.huawei.sharedrive.app.share.dao.LinkApproveUserDao;
import com.huawei.sharedrive.app.share.domain.LinkApproveUser;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Repository
public class LinkApproveUserDaoImpl extends CacheableSqlMapClientDAO implements LinkApproveUserDao{

	@Override
	public void create(LinkApproveUser linkApproveUser) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("LinkApproveUser.create", linkApproveUser);
	}

	@Override
	public List<LinkApproveUser> list(LinkApproveUser filter) {
		// TODO Auto-generated method stub
		return sqlMapClientTemplate.queryForList("LinkApproveUser.list",filter);
	}

	@Override
	public void updateType(LinkApproveUser dbLinkApproveUser) {
		sqlMapClientTemplate.update("LinkApproveUser.updateType",dbLinkApproveUser);
	}

}
