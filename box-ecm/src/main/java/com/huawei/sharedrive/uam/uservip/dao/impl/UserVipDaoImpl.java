package com.huawei.sharedrive.uam.uservip.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.uam.uservip.dao.UserVipDao;
import com.huawei.sharedrive.uam.uservip.domain.UserVip;

@Repository
public class UserVipDaoImpl implements UserVipDao{
	
	@Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;

	@Override
	public void create(UserVip userVip) {
		sqlMapClientTemplate.insert("UserVip.insert",userVip);
	}

	@Override
	public void update(UserVip userVip) {
		sqlMapClientTemplate.update("UserVip.update",userVip);
	}

	@Override
	public UserVip get(UserVip userVip) {
		return (UserVip) sqlMapClientTemplate.queryForObject("UserVip.get",userVip);
	}

	@Override
	public List<UserVip> listAll() {
		return sqlMapClientTemplate.queryForList("UserVip.listAll");
	}

	@Override
	public void delete(UserVip userVip) {
		sqlMapClientTemplate.delete("UserVip.delete",userVip);
	}

}
