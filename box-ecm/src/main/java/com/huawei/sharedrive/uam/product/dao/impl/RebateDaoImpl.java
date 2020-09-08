package com.huawei.sharedrive.uam.product.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.uam.product.dao.RebateDao;
import com.huawei.sharedrive.uam.product.domain.Rebate;

@Repository
public class RebateDaoImpl implements RebateDao{
	
	@Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;

	@Override
	public Rebate getRebate(Rebate rebate) {
		return (Rebate) sqlMapClientTemplate.queryForObject("Rebate.getRebate", rebate);
	}

	@Override
	public List<Rebate> getByProductId(long productId) {
		return sqlMapClientTemplate.queryForList("Rebate.getByProductId", productId);
	}

}
