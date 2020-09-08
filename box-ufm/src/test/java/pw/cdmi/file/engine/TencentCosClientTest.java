
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.engine;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import pw.cdmi.core.utils.EDToolsEnhance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;

/************************************************************
 * @Description:
 * <pre> 测试类 </pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/23
 ************************************************************/
@RunWith(JUnit4.class)
public class TencentCosClientTest {
    String appId = "1255692311";
    private String secretId = "AKIDNdibIRhuysa8UdsIoBh8Oo8GF3fML1ok";
    private String secretKey = "xjJhvys474RmvsdCZkkYHqFTUaigV8Nd";
    private String endpoint = "filepro-cos-bucket-1255692311.cos.ap-chengdu.myqcloud.com:80:80:AKID09B9zlQSfvJdrwySkBYyCMZv480jFriP:d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzM5Mzk0NTM4MzQ0MTQ1Mzk0MTMxMzM0NTMwMzkzNjM4NDEzNzMwNDMzMDM5MzAzOTQxNDEzODMzMzUzMzM2MzA0NTQxNDYzOTQzMzY0MTM4MzYzNzQ1MzczOTQzMzYzNzQ1MzUzNjQxNDUzMDMzMzY0MTQyMzYzODQyNDMzNTM3NDUzMzQyMzgzMDMwMzIzNzMxNDY0MjQ1NDMzMzQ0MzY0MjQ1NDYzNTMxNDU0NDMxMzMzNjMyMzk0NTQ2NDEzMTs7MzUzMDMwMzAzMDtDOUM1NzkyQkY1Q0ZCMTg4QTVCOTZCMEI3Njk3NjkwNDswNDFENkRGNzUyNTBDQzNGOw:d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzM2MzI0NDQ0Mzk0NjQzMzk0MTM1MzMzNDM0MzM0MjM4NDQzNzMyMzE0MzQ2NDQ0MjM0NDMzMDM2MzEzOTMwMzkzNDMzNDE0NTQ0NDU0NDQ0MzQzNTQxNDIzOTM1NDIzMTQzNDMzNzMzMzM0MjMzNDEzMTM2MzM0MTQzNDYzNjQ2MzQzNTMyMzQ0MzQ1Mzg0NDQ0Mzk0NjQzNDI0MjQxNDEzMDM5MzI0MjQ1NDMzNDM2MzMzNjM2MzI0MjQ2NDUzNTszMTM1MzAzMTM1MzczNDMyMzgzMzM5MzQzMjszNTMwMzAzMDMwOzA4Q0U4MDNFMjA2ODZFNjdBM0NEOUEzRTM1NjQ2MDZGOw";
    String bucketName = "filepro-cos-bucket-" + appId;

    COSClient cosClient;

    @Before
    public void init() {
        if(cosClient == null) {
            // 1 初始化用户身份信息(appid, secretId, secretKey)
            COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
            // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
            ClientConfig clientConfig = new ClientConfig(new Region("ap-chengdu"));
            // 3 生成cos客户端
            cosClient = new COSClient(cred, clientConfig);
        }
    }

    @After
    public void destroy() {
        if(cosClient != null) {
            cosClient.shutdown();
            cosClient = null;
        }
    }

    @Test
    public void test() {
        String url = testPresignedUrl();
        System.out.println(url);
    }

    public String testPresignedUrl() {
        // 设置bucket名, bucket名需包含appid
        String key = "1516889148832_958c9c55ea56c500f5ba8dd8a647708d";
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
        // 设置签名过期时间(可选), 最大允许设置签名一个月有效, 若未进行设置, 则默认使用ClientConfig中的签名过期时间(5分钟)
        // 这里设置签名过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000);
        req.setExpiration(expirationDate);

        URL url = cosClient.generatePresignedUrl(req);
        return url.toString();
    }

    @Test
    public void testUploadFile() {
        String fileName = "华一云网企业文件宝用户快速使用1.docx";
//        String fileName = "JavaEE开发的颠覆者：Spring Boot实战 中文PDF完整版.rar";
//        String fileName = "开.rar";
        String filePath = "F:\\private\\Chat\\WxWork\\File\\2018-04\\" + fileName;
        String key = "00";
        File file = new File(filePath);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        //使用S3FSObject中的文件长度，不能使用inputStream.available().
        objectMetadata.setContentLength(file.length());


        try {
            //设置文件名元数据，以使用云存储直接下载时，也能获取到正确的文件名
            objectMetadata.setContentDisposition("attachment;filename*=utf-8''" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));

            PutObjectRequest pubobjRequest = new PutObjectRequest(bucketName, "/" + key, new FileInputStream(file), objectMetadata);
            cosClient.putObject(pubobjRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testParsePath() {
        String path = "[tencent_cloud_cos][V2_HWY][9c19b7b6-ba9b-28b5-440c-c085266933d7][filepro-cos-bucket-1255692311][1517925188938_1549b041fda38df3886af1329498c295]";
    }

    @Test
    public void testEncode() {
        String[] split = endpoint.split(":");
        try {
            String decode = EDToolsEnhance.decode(split[4], split[5]);

            System.out.println(decode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
