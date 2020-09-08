package com.huawei.sharedrive.uam.weixin.dao.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.dao.WxDepartmentDao;
import com.huawei.sharedrive.uam.weixin.domain.WxDepartment;
import org.springframework.stereotype.Service;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WxDepartmentDao实现类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/

@Service
public class WxDepartmentDaoImpl extends CacheableSqlMapClientDAO implements WxDepartmentDao {
    @Override
    public void create(WxDepartment department) {
        sqlMapClientTemplate.insert("WxDepartment.insert", department);
    }

    @Override
    public void update(WxDepartment department) {
        sqlMapClientTemplate.update("WxDepartment.update", department);
    }

    @Override
    public void delete(WxDepartment department) {
        sqlMapClientTemplate.delete("WxDepartment.delete", department);
    }

    @Override
    public WxDepartment get(String corpId, Integer deptId) {
        Map<String, String> map = new HashMap<>();
        map.put("corpId", corpId);
        map.put("id", String.valueOf(deptId));

        return (WxDepartment)sqlMapClientTemplate.queryForObject("WxDepartment.get", map);
    }

    @Override
    public List<WxDepartment> listByCorpId(String corpId) {
        return sqlMapClientTemplate.queryForList("WxDepartment.listByCorpId", corpId);
    }

    //批量修改某个企业下的所有部门状态
    public void changeState(WxDepartment dept) {
        sqlMapClientTemplate.update("WxDepartment.changeState", dept);
    }

    //批量删除某个企业下特定状态的部门
    public void deleteByState(String corpId, int state) {
        WxDepartment dept = new WxDepartment();
        dept.setCorpId(corpId);
        dept.setState(state);

        sqlMapClientTemplate.delete("WxDepartment.deleteByState", dept);
    }
}
