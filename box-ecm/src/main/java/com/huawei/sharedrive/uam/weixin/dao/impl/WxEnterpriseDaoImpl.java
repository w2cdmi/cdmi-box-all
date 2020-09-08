package com.huawei.sharedrive.uam.weixin.dao.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.dao.WxEnterpriseDao;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import org.springframework.stereotype.Service;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import java.util.HashMap;
import java.util.Map;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WxEnterpriseDao实现类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxEnterpriseDaoImpl extends CacheableSqlMapClientDAO implements WxEnterpriseDao {
    @Override
    public void create(WxEnterprise enterprise) {
        sqlMapClientTemplate.insert("WxEnterprise.insert", enterprise);
    }

    @Override
    public void update(WxEnterprise enterprise) {
        sqlMapClientTemplate.update("WxEnterprise.update", enterprise);
    }

    @Override
    public void updateState(WxEnterprise enterprise) {
        sqlMapClientTemplate.update("WxEnterprise.updateState", enterprise);
    }

    @Override
    public WxEnterprise get(String id) {
        return (WxEnterprise)sqlMapClientTemplate.queryForObject("WxEnterprise.get", id);
    }
}
