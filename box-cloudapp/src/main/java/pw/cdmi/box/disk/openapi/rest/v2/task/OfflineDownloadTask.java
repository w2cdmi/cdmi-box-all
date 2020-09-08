
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.box.disk.openapi.rest.v2.task;

import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pw.cdmi.box.disk.client.domain.node.FilePreUploadRequest;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.httpclient.rest.response.FilePreUploadResponse;
import pw.cdmi.box.disk.utils.BasicConstants;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/************************************************************
 * @Description:
 * <pre>提供离线下载功能，客户端提交下载请求后，由服务器后台下载，并上传到指定的目录</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-wxwork-mobile Component. 2018/3/27
 ************************************************************/
@Component
@Scope("prototype")
public class OfflineDownloadTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(OfflineDownloadTask.class);
    @Autowired
    private RestClient ufmClientService;

    private String token;
    private String url; //下载地址
    private long ownerId; //拥有者
    private long parentId;//目录
    private String fileName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        String pathPre = PropertiesUtils.getProperty("upload.photo.path");
        if (pathPre == null) {
            logger.error("not set photo temp path");
            return;
        }
        String filePath = pathPre + getFileName() ;
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            logger.info("The parent path doesn't exist. create it. path={}", file.getParent());
            file.getParentFile().mkdirs();
        }

        logger.info("Begin to donwload file. file={}, url={}", filePath, url);
        try (
                InputStream in = new URL(url).openStream();
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        ) {
            IOUtils.copy(in, out);
        } catch (Exception e) {
            logger.error("Can't download the file. url={}", url);
            logger.error("Failed to download file.", e);
        }

        String preFileUrl = null;
        String uri = BasicConstants.RESOURCE_FILE + '/' + ownerId;
        Map<String, String> headerMap = new HashMap<>(1);
        headerMap.put("Authorization", token);
        FilePreUploadRequest uploadRequest = new FilePreUploadRequest(fileName, parentId, file.length());
        TextResponse response;
        int status = 0;

        int i = 1;
        while (i < 100) {
            response = ufmClientService.performJsonPutTextResponse(uri, headerMap, uploadRequest);
            status = response.getStatusCode();
            if (status == HttpStatus.OK.value()) {
                String content = response.getResponseBody();
                preFileUrl = JsonUtils.stringToObject(content, FilePreUploadResponse.class).getUploadUrl() + "?objectLength=" + file.length();
                String uploadResultStr = uploadFile(preFileUrl, filePath);
                JSONObject uploadResult = JSONObject.fromObject(uploadResultStr);
                Object code = uploadResult.get("code");
                if ("OK".equals(code)) {
                    deleteFile(filePath);
                } else {
                    logger.warn("Failed to upload the file to oss, ownerId={}, parentId={}, filename={}, code={}", ownerId, parentId, fileName, code);
                }
                break;
            } else if (status == HttpStatus.CONFLICT.value()) {
                //只有在文件名冲突时才会不断尝试重传
                String newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, fileName, i);
                uploadRequest.setName(newName);
                i++;
            } else {
                logger.warn("Failed to upload the file to oss, ownerId={}, parentId={}, filename={}, statusCode={}", ownerId, parentId, fileName, status);
                break;
            }
        }

        //CONFLICT，上传失败，已经超过尝试次数
        if (status == HttpStatus.CONFLICT.value()) {
            logger.warn("Failed to upload the file to oss, file conflicted. ownerId={}, parentId={}, filename={}", ownerId, parentId, fileName);
        }
    }



    /**
     * 将本地文件通过dss上传到存储中
     *
     * @param preUrl
     *            预上传地址
     * @param uploadFilePath
     *            上传文件路径
     */
    @SuppressWarnings("finally")
    public String uploadFile(String preUrl, String uploadFilePath) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        DataOutputStream ds = null;
        String tempLine = null;

        try {
            // 统一资源
            URL url = new URL(preUrl);
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpURLConnection.setDoInput(true);
            // 设置是否向httpUrlConnection输出
            httpURLConnection.setDoOutput(true);
            // Post 请求不能使用缓存
            httpURLConnection.setUseCaches(false);
            // 设定请求的方法，POST
            httpURLConnection.setRequestMethod("POST");
            // 设置字符编码连接参数
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 设置请求内容类型
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // 设置DataOutputStream
            ds = new DataOutputStream(httpURLConnection.getOutputStream());
            String filename = uploadFilePath.substring(uploadFilePath.lastIndexOf("//") + 1);
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\";filename=\"" + filename + "\"" + end);
            ds.writeBytes(end);

            try (
                    FileInputStream fStream = new FileInputStream(uploadFilePath);
            ) {
                IOUtils.copy(fStream, ds);
            } catch (Exception e) {
                logger.warn("Failed to write file content to HTTP connection.  ownerId={}, parentId={}, filename={}, error={}", ownerId, parentId, fileName, e.getMessage());
            }

            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            ds.flush();
            ds.close();
            if (httpURLConnection.getResponseCode() >= 300) {
                return "exception";
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));) {
                    while ((tempLine = reader.readLine()) != null) {
                        builder.append(tempLine);
                        builder.append("\n");
                    }
                }

                return builder.toString();
            }
        } catch (Exception e) {
            logger.warn("Failed to write upload file.  ownerId={}, parentId={}, filename={}, error={}", ownerId, parentId, fileName, e.getMessage());
        }

        return "exception";
    }


    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public void deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                logger.info("delete temp photo success, photoPath : " + fileName);
            } else {
                logger.info("delete temp photo fail,  photoPath : " + fileName);
            }
        }
    }
}
