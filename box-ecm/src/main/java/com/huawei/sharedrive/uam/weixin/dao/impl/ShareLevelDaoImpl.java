package com.huawei.sharedrive.uam.weixin.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.uam.weixin.dao.ShareLevelDao;
import com.huawei.sharedrive.uam.weixin.domain.ShareLevel;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Repository
public class ShareLevelDaoImpl extends CacheableSqlMapClientDAO implements ShareLevelDao{

	@Override
	public List<ShareLevel> list() {
		// TODO Auto-generated method stub
		return sqlMapClientTemplate.queryForList("ShareLevel.list");
	}

	@Override
	public ShareLevel get(int id) {
		// TODO Auto-generated method stub
		return (ShareLevel) sqlMapClientTemplate.queryForObject("ShareLevel.get",id);
	}

}
