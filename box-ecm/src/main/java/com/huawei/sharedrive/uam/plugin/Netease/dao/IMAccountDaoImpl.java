package com.huawei.sharedrive.uam.plugin.Netease.dao;

import org.springframework.stereotype.Repository;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import com.huawei.sharedrive.uam.plugin.Netease.domain.IMAccount;

@SuppressWarnings("deprecation")
@Repository
public class IMAccountDaoImpl extends CacheableSqlMapClientDAO implements IMAccountDao {

	@Override
	public void insert(IMAccount imAccount) {
		sqlMapClientTemplate.insert("IMAccount.insert",imAccount);
	}

	@Override
	public IMAccount get(IMAccount imAccount) {
		return (IMAccount) sqlMapClientTemplate.queryForObject("IMAccount.get",imAccount);
	}

	@Override
	public IMAccount getByIMaccid(IMAccount imAccount) {
		return (IMAccount) sqlMapClientTemplate.queryForObject("IMAccount.getByIMaccid",imAccount);
	}

}
