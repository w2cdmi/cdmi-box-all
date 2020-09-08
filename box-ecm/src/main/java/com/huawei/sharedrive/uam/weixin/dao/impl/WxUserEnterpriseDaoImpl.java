package com.huawei.sharedrive.uam.weixin.dao.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.weixin.dao.WxUserEnterpriseDao;
import com.huawei.sharedrive.uam.weixin.domain.WxUserEnterprise;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WxUserEnterpriseDao实现类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxUserEnterpriseDaoImpl extends CacheableSqlMapClientDAO implements WxUserEnterpriseDao {
    @Override
    public void create(WxUserEnterprise user) {
        sqlMapClientTemplate.insert("WxUserEnterprise.insert", user);
    }

    @Override
    public void delete(WxUserEnterprise user) {
        sqlMapClientTemplate.delete("WxUserEnterprise.delete", user);
    }

    @Override
    public void deleteByUnionId(String unionId) {
        sqlMapClientTemplate.delete("WxUserEnterprise.deleteByUnionId", unionId);
    }

    @Override
    public WxUserEnterprise getByUnionIdAndEnterpriseId(String unionId, long enterpriseId) {
        Map<String, Object> map = new HashMap<>();
        map.put("unionId", unionId);
        map.put("enterpriseId", enterpriseId);

        return (WxUserEnterprise)sqlMapClientTemplate.queryForObject("WxUserEnterprise.getByUnionIdAndEnterpriseId", map);
    }

    @Override
    public List<WxUserEnterprise> listByUnionId(String unionId) {
        return sqlMapClientTemplate.queryForList("WxUserEnterprise.listByUnionId", unionId);
    }

	@Override
	public WxUserEnterprise getByEnterpriseUser(long enterpriseUserId, long enterpriseId) {
		Map<String, Object> prameter=new HashMap<>();
		prameter.put("enterpriseUserId", enterpriseUserId);
		prameter.put("enterpriseId", enterpriseId);
		return (WxUserEnterprise) sqlMapClientTemplate.queryForObject("WxUserEnterprise.getByEnterpriseUser",prameter);
	}

    @Override
    public void deleteByEnterpriseUserIdAndEnterpriseId(long employeeid, long enterpriseId) {
        Map<String, Object> prameter=new HashMap<>();
        prameter.put("enterpriseUserId", employeeid);
        prameter.put("enterpriseId", enterpriseId);
        sqlMapClientTemplate.delete("WxUserEnterprise.deleteEnterpriseUser",prameter);
    }

    @Override
	public void update(WxUserEnterprise user) {
		sqlMapClientTemplate.update("WxUserEnterprise.update", user);
	}
}
