
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.sql;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/************************************************************
 * @Description:
 * <pre> SQL常用测试类 </pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2018/5/11
 ************************************************************/
@RunWith(JUnit4.class)
public class SqlTest {

    @Test
    public void updateEnterpriseAccountMaxSpace() {
        int m =  1048576;
        long g = 1073741824;
        for(int i = 1; i < 210; i++) {
            int table = (int) (HashTool.apply(String.valueOf(i)) % 100);

            System.out.println("UPDATE `enterprise_account` e SET `maxSpace` = " + g + " + " + m + " * (SELECT count(*) FROM user_account_" + table  + " u WHERE u.accountId = " + i + ") WHERE `maxSpace` = 999999999999 and e.accountId = " + i + ";");
        }
    }
}
