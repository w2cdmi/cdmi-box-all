package com.huawei.sharedrive.uam.weixin.dao.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.dao.WxEnterpriseUserDao;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterpriseUser;
import org.springframework.stereotype.Service;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WxEnterpriseUserDao实现类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxEnterpriseUserDaoImpl extends CacheableSqlMapClientDAO implements WxEnterpriseUserDao {
    @Override
    public void create(WxEnterpriseUser user) {
        sqlMapClientTemplate.insert("WxEnterpriseUser.insert", user);
    }

    @Override
    public void update(WxEnterpriseUser user) {
        sqlMapClientTemplate.update("WxEnterpriseUser.update", user);
    }

    @Override
    public void delete(WxEnterpriseUser user) {
        sqlMapClientTemplate.delete("WxEnterpriseUser.delete", user);
    }

    @Override
    public WxEnterpriseUser get(String corpId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("corpId", corpId);
        map.put("userId", userId);

        return (WxEnterpriseUser)sqlMapClientTemplate.queryForObject("WxEnterpriseUser.get", map);
    }

    @Override
    public List<WxEnterpriseUser> getByCorpId(String corpId) {
        return sqlMapClientTemplate.queryForList("WxEnterpriseUser.getByCorpId", corpId);
    }

    @Override
    public List<WxEnterpriseUser> getByEnterpriseIdAndUserId(long enterpriseId, long enterpriseUserId) {
        Map<String, Long> map = new HashMap<>();
        map.put("boxEnterpriseId", enterpriseId);
        map.put("boxEnterpriseUserId", enterpriseUserId);

        return sqlMapClientTemplate.queryForList("WxEnterpriseUser.getByEnterpriseIdAndUserId", map);
    }

    //批量修改某个企业下的所有部门状态
    public void changeStatus(WxEnterpriseUser wxUser) {
        sqlMapClientTemplate.update("WxEnterpriseUser.changeStatus", wxUser);
    }

    //批量删除某个企业下特定状态的部门
    public void deleteByStatus(String corpId, int status) {
        WxEnterpriseUser dept = new WxEnterpriseUser();
        dept.setCorpId(corpId);
        dept.setStatus(status);

        sqlMapClientTemplate.delete("WxEnterpriseUser.deleteByStatus", dept);
    }
}
