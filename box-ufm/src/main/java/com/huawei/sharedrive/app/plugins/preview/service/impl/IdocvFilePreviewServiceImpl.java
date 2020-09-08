package com.huawei.sharedrive.app.plugins.preview.service.impl;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.plugins.preview.service.FilePreviewService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service("idocvFilePreviewService")
@Lazy
public class IdocvFilePreviewServiceImpl implements FilePreviewService {
    private static final Logger logger = LoggerFactory.getLogger(IdocvFilePreviewServiceImpl.class);

    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;

    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private INodeACLService iNodeACLService;

    @Value("${preview.server.idocv.token}")
    private String token = "filepro-token";

    @Value("${preview.server.idocv.url}")
    private String previewUrl = "https://view.filepro.cn";

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    @Override
    public String getPreviewUrl(UserToken userToken, INode node) {
        if(!isSupported(node)) {
            throw new ForbiddenException("Not Supported File Type, only support doc/docx, xls/xlsx, ppt/pptx, txt, pdf files.");
        }
        long userId = iNodeACLService.vaildINodeOperACL(userToken, node, AuthorityMethod.GET_PREVIEW.name());
        //获取文件的md5
        String md5 = null;
        ObjectReference object = objectReferenceDAO.get(node.getObjectId());
        if(object != null) {
            md5 = object.getSha1();
            logger.error("Object Reference: " + md5);
        }
        //获取文件的下载地址
        DataAccessURLInfo dataAccessUrlInfo = fileBaseService.getDownURLByNearAccess(userToken.getLoginRegion(), userId, node);
        String downloadUlr = dataAccessUrlInfo.getDownloadUrl();

        try {
            downloadUlr = URLEncoder.encode(downloadUlr, "utf-8");
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        }

        //构造预览服务器需要的链接, md5用于判断预览文件是否已经生成, downloadUrl用于预览文件不存在时，下载文件。
        if(md5 != null) {
            return previewUrl + "/view/url?token=" + token + "&md5=" + md5 + "&url=" + downloadUlr;
        }

        return previewUrl + "/view/url?token=" + token + "&url=" + downloadUlr;
    }

    @Override
    public void startConvertTask(INode node) {
        if(!isSupported(node)) {
            return;
        }

        UserToken token = new UserToken();
        token.setCloudUserId(node.getOwnedBy());
        userTokenHelper.assembleUserToken(node.getOwnedBy(), token);

        //模拟用户点击预览按钮动作，解决预览服务器生成预览文件
        String previewUlr = getPreviewUrl(token, node);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpClient.execute(new HttpGet(previewUlr));
            if(response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                logger.warn("Failed to auto convert preview file. ownerId={}, nodeId={}, fileName={}, statusCode={}", node.getOwnedBy(), node.getId(), node.getName(), response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            logger.warn("Failed to auto convert preview file. ownerId={}, nodeId={}, fileName={}, error={}", node.getOwnedBy(), node.getId(), node.getName(), e.getMessage());
            logger.warn("Failed to auto convert preview file.", e);
        }
    }

    public void deleteByObjectId(String sourceObjectId) {
        //获取md5

        //todo: 通知预览服务器删除预览文件
    }

    boolean isSupported(INode node) {
        if(node == null || node.getName() == null) {
            return false;
        }

        String fileName = node.getName().toLowerCase();
        if(fileName.endsWith(".doc") || fileName.endsWith(".docx") ||
                fileName.endsWith(".xls") || fileName.endsWith(".xlsx") ||
                fileName.endsWith(".ppt") || fileName.endsWith(".pptx") ||
                fileName.endsWith(".txt") || fileName.endsWith(".pdf")) {
            return true;
        }

        return false;
    }
}
