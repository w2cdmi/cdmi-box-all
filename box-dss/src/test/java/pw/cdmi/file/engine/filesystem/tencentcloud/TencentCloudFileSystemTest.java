package pw.cdmi.file.engine.filesystem.tencentcloud;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.file.engine.filesystem.aws.S3FSObject;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.io.MD5DigestInputStream;
import pw.cdmi.file.engine.filesystem.model.FSAccessPath;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.cloud.S3BucketInfo;
import pw.cdmi.file.engine.filesystem.model.cloud.S3Userinfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by quxiangqian on 2018/1/11.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath*:context/applicationContext.xml"})
public class TencentCloudFileSystemTest {

    @Autowired
    private TencentCloudFileSystem tencentCloudFileSystem=new TencentCloudFileSystem();
    @Test
    public void transToFSObject(){
       S3FSObject s3Object= tencentCloudFileSystem.transToFSObject("[aliyun_oss][V2_HWY][cae5f863-17b1-3c83-4bbd-8184b7d517ee][storbox-4c1784737782b0de6bb694c986795ccf][1500967873432_092c959e7862ee8894e6a2af36f4072a]");
       System.out.println(s3Object);

    }

    private FSEndpoint getEndpoint(){
        FSEndpoint fsEndpoint=new FSEndpoint();
        fsEndpoint.setMaxUtilization(0.0F);
        fsEndpoint.setRetrieval(0.0F);
        //String dns="filepro-cos-bucket-1255692311.coscd.myqcloud.com";
        String dns="filepro-cos-bucket-1255692311.cos.ap-chengdu.myqcloud.com";
        String secretKey="BE5jBRely9YsAWkq7IAsPlx691KZfZdc";
        String accessKey="AKID09B9zlQSfvJdrwySkBYyCMZv480jFriP";

        Map<String, String> map = EDToolsEnhance.encode(secretKey);

        String encryptedSecretKey = (String) map.get("encryptedContent");
        String encryptKey = (String) map.get("encryptedKey");
        FSAccessPath accessPath=new FSAccessPath("filepro-cos-bucket-1255692311");
        accessPath.setEndpointID("4c9da8b3-e06e-bd8f-4723-031cbb73c18c");
        accessPath.setAvailable(true);
        accessPath.setWriteAble(true);
        accessPath.setId("48be8930-1b20-1f24-6c8a-81c38ecf5701");
        //fsEndpoint.addAccessPaths(accessPath);
        List<FSAccessPath> li=new ArrayList<>();
        li.add(accessPath);
        fsEndpoint.setId("4c9da8b3-e06e-bd8f-4723-031cbb73c18c");

        fsEndpoint.setAccessPaths(li);
        StringBuilder newEndpoint = new StringBuilder(dns);
        newEndpoint.append(":").append(80);
        newEndpoint.append(":").append(80);

        newEndpoint.append(":").append(accessKey);
        newEndpoint.append(":").append(secretKey);
        newEndpoint.append(":").append(encryptedSecretKey);
        newEndpoint.append(":").append(encryptKey);
        fsEndpoint.setEndpoint(newEndpoint.toString());
        return fsEndpoint;
    }

    @Test
    public void transToFSObject1(){


//        FileObject fileObject=new FileObject();
//        fileObject.setObjectID("4c9da8b3-e06e-bd8f-4723-031cbb73c18c");
//        S3FSObject s3Object= tencentCloudFileSystem.transToFSObject(fsEndpoint,fileObject);
//        System.out.println(s3Object);

    }

    //测试通过
    @Test
    public void Put(){
//         final FSEndpoint fsEndpoint=getEndpoint();
       // COSClientInfo cosClientInfo =new COSClientInfo(fsEndpoint);
        //System.out.println(cosClientInfo);
        S3BucketInfo s3BucketInfo = new S3BucketInfo();
        S3Userinfo userinfo = new S3Userinfo();
        userinfo.setSk("BE5jBRely9YsAWkq7IAsPlx691KZfZdc");
        s3BucketInfo.setUserInfo(userinfo);

        File file = new File("E:/tmp/test_length.jpeg");
        System.out.println("Put File: " + file.getName());
        S3FSObject s3Object = new S3FSObject(getEndpoint(), file.getName(), s3BucketInfo);
        s3Object.setLength(file.length());

        System.out.println("File Length: " + s3Object.getLength());
        try {
            InputStream inputStream = new FileInputStream(file);
            MD5DigestInputStream md5DigestInputStream = new MD5DigestInputStream(inputStream);
            byte[] buffer = transToCacheByteBuffer(md5DigestInputStream);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
            byteArrayInputStream.mark(buffer.length);
            inputStream = byteArrayInputStream;

            tencentCloudFileSystem.doPut(s3Object, inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private byte[] transToCacheByteBuffer(InputStream inputStream) throws FSException {
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            String message = "Trans To Byte Array Failed.";
            throw new FSException(message, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    //测试通过
    @Test
    public void CheckFileExist(){
//         final FSEndpoint fsEndpoint=getEndpoint();
        // COSClientInfo cosClientInfo =new COSClientInfo(fsEndpoint);
        //System.out.println(cosClientInfo);
        S3BucketInfo s3BucketInfo=new S3BucketInfo();
        S3Userinfo userinfo=new S3Userinfo();
        userinfo.setSk("BE5jBRely9YsAWkq7IAsPlx691KZfZdc");
        s3BucketInfo.setUserInfo(userinfo);
        S3FSObject s3Object=new S3FSObject(getEndpoint(),"11111.doc",s3BucketInfo);
        System.out.println(tencentCloudFileSystem.checkObjectExist(s3Object));

    }

    //测试通过
    @Test
    public void doGet()
    {
        S3BucketInfo s3BucketInfo=new S3BucketInfo();
        S3Userinfo userinfo=new S3Userinfo();
        userinfo.setSk("BE5jBRely9YsAWkq7IAsPlx691KZfZdc");
        s3BucketInfo.setUserInfo(userinfo);
        S3FSObject s3Object=new S3FSObject(getEndpoint(),"11111.doc",s3BucketInfo);
        System.out.println(tencentCloudFileSystem.doGetObject(s3Object,0l,0l));
    }

    //测试通过
    @Test
    public void doCopy()
    {
        S3BucketInfo s3BucketInfo=new S3BucketInfo();
        S3Userinfo userinfo=new S3Userinfo();
        userinfo.setSk("BE5jBRely9YsAWkq7IAsPlx691KZfZdc");
        s3BucketInfo.setUserInfo(userinfo);
        S3FSObject s3Object=new S3FSObject(getEndpoint(),"11111.doc",s3BucketInfo);
        S3FSObject s3Object1=new S3FSObject(getEndpoint(),"22222.doc",s3BucketInfo);
        tencentCloudFileSystem.doCopy(s3Object,s3Object1);
        //System.out.println(tencentCloudFileSystem.doGetObject(s3Object,0l,0l));
    }
}
