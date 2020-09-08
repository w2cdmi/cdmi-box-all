package com.huawei.sharedrive.uam.weixin.dao.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.dao.WxWorkCorpAppDao;
import com.huawei.sharedrive.uam.weixin.domain.WxWorkCorpApp;
import org.springframework.stereotype.Service;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WxWorkCorpAppDao实现类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxWorkCorpAppDaoImpl extends CacheableSqlMapClientDAO implements WxWorkCorpAppDao {
    @Override
    public void create(WxWorkCorpApp wxWorkCorpApp) {
        sqlMapClientTemplate.insert("WxWorkCorpApp.insert", wxWorkCorpApp);
    }

    @Override
    public void update(WxWorkCorpApp wxWorkCorpApp) {
        sqlMapClientTemplate.update("WxWorkCorpApp.update", wxWorkCorpApp);
    }

    @Override
    public void delete(WxWorkCorpApp wxWorkCorpApp) {
        sqlMapClientTemplate.delete("WxWorkCorpApp.delete", wxWorkCorpApp);
    }

    @Override
    public void deleteByCorpIdAndSuiteId(String corpId, String suiteId) {
        Map<String, Object> map = new HashMap<>();
        map.put("corpId", corpId);
        map.put("suiteId", suiteId);

        sqlMapClientTemplate.delete("WxWorkCorpApp.deleteByCorpIdAndSuiteId", map);
    }

    @Override
    public WxWorkCorpApp get(String corpId, String suiteId, Integer appId) {
        Map<String, Object> map = new HashMap<>();
        map.put("corpId", corpId);
        map.put("suiteId", suiteId);
        map.put("appId", appId);

        return (WxWorkCorpApp)sqlMapClientTemplate.queryForObject("WxWorkCorpApp.getByCorpIdAndSuiteIdAndAppId", map);
    }

    @Override
    public WxWorkCorpApp getByCorpIdAndAgentId(String corpId, Integer agentId) {
        Map<String, Object> map = new HashMap<>();
        map.put("corpId", corpId);
        map.put("agentId", agentId);

        return (WxWorkCorpApp)sqlMapClientTemplate.queryForObject("WxWorkCorpApp.getByCorpIdAndAgentId", map);
    }

    @Override
    public List<WxWorkCorpApp> getByCorpId(String corpId) {
        return sqlMapClientTemplate.queryForList("WxWorkCorpApp.getByCorpId", corpId);
    }

    @Override
    public WxWorkCorpApp getByCorpIdAndSuiteId(String corpId, String suiteId) {
        Map<String, Object> map = new HashMap<>();
        map.put("corpId", corpId);
        map.put("suiteId", suiteId);

        return (WxWorkCorpApp)sqlMapClientTemplate.queryForObject("WxWorkCorpApp.getByCorpIdAndSuiteId", map);
    }
}
