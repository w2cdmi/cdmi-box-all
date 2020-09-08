/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import com.huawei.sharedrive.uam.weixin.dao.WxWorkCorpAppDao;
import com.huawei.sharedrive.uam.weixin.domain.WxWorkCorpApp;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>部门</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
@Service
public class WxWorkCorpAppServiceImpl implements WxWorkCorpAppService {
    private static final Logger logger = LoggerFactory.getLogger(WxWorkCorpAppServiceImpl.class);

    @Autowired
    private WxWorkCorpAppDao wxWorkCorpAppDao;

    @Override
    public void create(WxWorkCorpApp wxWorkCorpApp) {
        if(wxWorkCorpApp.getCreatedAt() == null) {
            wxWorkCorpApp.setCreatedAt(new Date());
        }
        if(wxWorkCorpApp.getModifiedAt() == null) {
            wxWorkCorpApp.setModifiedAt(new Date());
        }

        wxWorkCorpAppDao.create(wxWorkCorpApp);
    }

    @Override
    public void update(WxWorkCorpApp wxWorkCorpApp) {
        if(wxWorkCorpApp.getModifiedAt() == null) {
            wxWorkCorpApp.setModifiedAt(new Date());
        }

        wxWorkCorpAppDao.update(wxWorkCorpApp);
    }

    @Override
    public void delete(WxWorkCorpApp wxWorkCorpApp) {
        wxWorkCorpAppDao.delete(wxWorkCorpApp);
    }

    @Override
    public void deleteByCorpIdAndSuiteId(String corpId, String suiteId) {
        wxWorkCorpAppDao.deleteByCorpIdAndSuiteId(corpId, suiteId);
    }

    @Override
    public WxWorkCorpApp get(String corpId, String suiteId, int appId) {
        return wxWorkCorpAppDao.get(corpId, suiteId, appId);
    }

    @Override
    public WxWorkCorpApp getByCorpIdAndAgentId(String corpId, int agentId) {
        return wxWorkCorpAppDao.getByCorpIdAndAgentId(corpId, agentId);
    }

    @Override
    public List<WxWorkCorpApp> getByCorpId(String corpId) {
        return wxWorkCorpAppDao.getByCorpId(corpId);
    }

    @Override
    public WxWorkCorpApp getByCorpIdAndSuiteId(String corpId, String suiteId) {
        return wxWorkCorpAppDao.getByCorpIdAndSuiteId(corpId, suiteId);
    }
}
