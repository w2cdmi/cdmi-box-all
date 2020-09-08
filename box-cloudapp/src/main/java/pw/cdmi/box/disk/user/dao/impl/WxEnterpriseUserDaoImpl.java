package pw.cdmi.box.disk.user.dao.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;


import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.user.dao.WxEnterpriseUserDao;
import pw.cdmi.box.disk.user.domain.WxEnterpriseUser;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WxEnterpriseUserDao实现类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxEnterpriseUserDaoImpl extends CacheableSqlMapClientDAO implements WxEnterpriseUserDao {
    @Override
    public WxEnterpriseUser get(String corpId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("corpId", corpId);
        map.put("userId", userId);

        return (WxEnterpriseUser)sqlMapClientTemplate.queryForObject("WxEnterpriseUser.get", map);
    }
}
