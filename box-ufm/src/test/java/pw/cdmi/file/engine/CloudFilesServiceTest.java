
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.engine;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/************************************************************
 * @Description:
 * <pre> 云存储测试基类，保存一些通用的操作 </pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/
public class CloudFilesServiceTest {

    private static String fileName = null;
    @BeforeClass
    public static void init() {
        fileName = "test_" + System.currentTimeMillis() + ".txt";
        File file = new File("./" + fileName);

        System.out.println("Create the test file: " + fileName);

        if(file.exists()) {
            Assert.fail("There is a same file exist: " + file);
        }

        try {
            file.createNewFile();

            try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file))) {
                writer.write("" + System.currentTimeMillis());
                writer.flush();
            }
        } catch (Exception e) {
            Assert.fail("Failed to prepare the test file: " + fileName);
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void clean() {
        File file = new File("./" + fileName);
        if(file.exists()) {
            file.delete();
        }
    }
}
