package com.huawei.sharedrive.uam.weixin.dao;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.domain.WxDepartment;

import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WxDepartmentDao</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public interface WxDepartmentDao {
    void create(WxDepartment department);
    void update(WxDepartment department);
    void delete(WxDepartment department);

    WxDepartment get(String corpId, Integer deptId);
    List<WxDepartment> listByCorpId(String corpId);

    //批量修改某个企业下的所有部门状态
    void changeState(WxDepartment department);

    //批量删除某个企业下特定状态的部门
    void deleteByState(String corpId, int state);
}
