/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service;

import com.huawei.sharedrive.uam.weixin.domain.WxDepartment;

import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>管理微信企业中的部门数据</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
public interface WxDepartmentService {
    WxDepartment get(String corpId, int deptId);
    void create(WxDepartment dept);
    void update(WxDepartment dept);
    void delete(WxDepartment dept);
    void delete(String corpId, int deptId);
    List<WxDepartment> listByCorpId(String corpId);

    //批量修改某个企业下的所有部门状态
    void changeState(String corpId, int state);

    //批量删除某个企业下特定状态的部门
    void deleteByState(String corpId, int state);
}
