package com.huawei.sharedrive.uam.uservip.dao.impl;

import com.huawei.sharedrive.uam.product.domain.Product;
import com.huawei.sharedrive.uam.uservip.dao.EnterpriseVipDao;
import com.huawei.sharedrive.uam.uservip.domain.EnterpriseVip;
import org.springframework.stereotype.Repository;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EnterpriseVipDaoImpl extends CacheableSqlMapClientDAO implements EnterpriseVipDao {

    @Override
    public void create(EnterpriseVip enterpriseVip) {
        sqlMapClientTemplate.insert("EnterpriseVip.create", enterpriseVip);
    }

    @Override
    public void update(EnterpriseVip enterpriseVip) {
        sqlMapClientTemplate.update("EnterpriseVip.update", enterpriseVip);
    }

    @Override
    public EnterpriseVip get(EnterpriseVip enterpriseVip) {
        return (EnterpriseVip) sqlMapClientTemplate.queryForObject("EnterpriseVip.get", enterpriseVip);
    }

    @Override
    public List<EnterpriseVip> listAll() {
        return sqlMapClientTemplate.queryForList("EnterpriseVip.listAll");
    }

    @Override
    public Product getProductByEnterpriseAccountId(long accountId) {
        Map<String, Object> map = new HashMap<>();
        map.put("enterpriseAccountId", accountId);
        map.put("expireDate", new Date());

        return (Product) sqlMapClientTemplate.queryForObject("EnterpriseVip.getProductByEnterpriseAccountId", map);
    }
}
