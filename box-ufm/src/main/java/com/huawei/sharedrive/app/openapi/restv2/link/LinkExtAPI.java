package com.huawei.sharedrive.app.openapi.restv2.link;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.link.RestLinkExtRequest;
import com.huawei.sharedrive.app.openapi.domain.node.DownloadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.PreviewResponse;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import com.huawei.sharedrive.app.plugins.preview.service.impl.ImageFilePreviewServiceImpl;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityScanManager;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.domain.LinkAccessCodeMode;
import com.huawei.sharedrive.app.share.service.LinkService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.common.util.signature.SignatureUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 文件夹提取接口
 *
 * @author pWX231110
 */
@Controller
@RequestMapping(value = "/api/v2/f/")
@Api(value = "文件夹提取接口" ,description = "文件夹提取接口")
public class LinkExtAPI {
    private static Logger logger = LoggerFactory.getLogger(LinkExtAPI.class);

    private static final String DIRECT_THUMBNIAL = "thumbnail";

    private static final String DIRECT_ORIGINAL = "original";

    @Autowired
    private INodeACLService iNodeACLService;

    @Autowired
    private SecurityMatrixService securityMatrixService;

    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private SecurityScanManager securityScanManager;
    
    @Autowired
	private FilePreviewManager filePreviewManager;

    @Autowired
 	private ImageFilePreviewServiceImpl imageFilePreviewServiceImpl;

    @Autowired
    private FileService fileService;

    private static final boolean LINK_EXT_SECURITY_FLAG = Boolean.parseBoolean(PropertiesUtils.getProperty("link.ext.security.flag", "true"));

    private static final boolean IGNORE_SCAN_RESULT = Boolean.parseBoolean(PropertiesUtils.getProperty("security.scan.ignore.result", "true"));

    /**
     * 直链
     * 
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "{linkCode}", method = RequestMethod.GET)
    @ApiOperation(value = "直链" ,notes = "直链服务")
    public ResponseEntity<String> directLink(@PathVariable String linkCode) throws BaseRunException {
        checkEnableDirectLink();
        INodeLink nodeLink = getNodeLink(linkCode);

        INode node = fileBaseService.getAndCheckNode(nodeLink.getOwnedBy(), nodeLink.getiNodeId(), INode.TYPE_FILE);

        userTokenHelper.checkUserStatus(null, node.getOwnedBy());

        checkLinkNodeOperACL(linkCode, node);

        // 安全扫描
        sendDownloadScanTask(node);

        DataAccessURLInfo accessUrlInfo;
        UserToken usertoken = new UserToken();
        usertoken.setLoginName(linkCode);
        try {
            accessUrlInfo = fileBaseService.getINodeInfoDownURL(nodeLink.getOwnedBy(), node);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(usertoken, EventType.OTHERS, null, null, UserLogType.GET_DIRECT_LINK_ERR, null, null);
            throw e;
        }
        fileBaseService.sendINodeEvent(usertoken, EventType.OTHERS, null, null, UserLogType.GET_DIRECT_LINK_ERR, null, null);
        HttpHeaders header = new HttpHeaders();
        header.set("Connection", "close");
        String url = accessUrlInfo.getDownloadUrl();
        header.set("Location", url);
        return new ResponseEntity<String>(null, header, HttpStatus.TEMPORARY_REDIRECT);
    }

    /**
     * 获取下载URL
     */
    @RequestMapping(value = "{linkCode}/url", method = RequestMethod.GET)
    public ResponseEntity<DownloadResponse> getDownloadUrl(@PathVariable String linkCode, @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        checkEnableDirectLink();

        //todo: 此处直接从使用明文的Authorization头部，传输时没有对AccessCode进行加密，后续实现加密传输
        UserToken userToken = getLinkToken(token);

        INodeLink nodeLink = getNodeLink(linkCode, userToken.getPlainAccessCode());
        INode node = fileBaseService.getAndCheckNode(nodeLink.getOwnedBy(), nodeLink.getiNodeId(), INode.TYPE_FILE);
        userTokenHelper.checkUserStatus(null, node.getOwnedBy());

        //
        checkLinkNodeOperACL(linkCode, node);

        DataAccessURLInfo accessUrlInfo;
        UserToken usertoken = new UserToken();
        usertoken.setLoginName(linkCode);
        try {
            accessUrlInfo = fileBaseService.getINodeInfoDownURL(nodeLink.getOwnedBy(), node);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(usertoken, EventType.OTHERS, null, null, UserLogType.GET_DIRECT_LINK_ERR, null, null);
            throw e;
        }
        fileBaseService.sendINodeEvent(usertoken, EventType.OTHERS, null, null, UserLogType.GET_DIRECT_LINK_ERR, null, null);

        return new ResponseEntity<>(new DownloadResponse(accessUrlInfo.getDownloadUrl()), HttpStatus.OK);
    }


    /**
	 * 获取文件预览地址
	 *
	 * @param ownerId 所属owner
	 * @param fileId 文件唯一标识
	 * @param token token
	 * @param request Http请求对象
	 * @return
	 */
	@RequestMapping(value = "/{linkCode}/preview", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<PreviewResponse> preview(@PathVariable String linkCode, @RequestHeader("Authorization") String token, HttpServletRequest request) {

		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		UserToken userToken = getLinkToken(token);
		INodeLink nodeLink = getNodeLink(linkCode, userToken.getPlainAccessCode());
		INode node = fileBaseService.getAndCheckNode(nodeLink.getOwnedBy(), nodeLink.getiNodeId(), INode.TYPE_FILE);
		userTokenHelper.checkUserStatus(null, node.getOwnedBy());
		long ownerId = node.getOwnedBy();
		long fileId = node.getId();

		try {

			String xUserToken = request.getHeader("x-usertoken");
			if (StringUtils.isNotEmpty(xUserToken)) {
				UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, headerCustomMap);
				securityMatrixService.checkSecurityMatrix(accessUserToken, ownerId, fileId, SecurityMethod.FILE_PREVIEW, headerCustomMap);
			} else {
				securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_PREVIEW, headerCustomMap);
			}
			String previewUrl = filePreviewManager.getPreviewUrl(userToken, node);
			return new ResponseEntity<>(new PreviewResponse(previewUrl), HttpStatus.OK);
		} catch (RuntimeException t) {
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_DIRECT_LINK_ERR, null, null);
			throw t;
		}
	}


    public UserToken getLinkToken(String authorization) throws BaseRunException {
        UserToken userToken;
        if (!authorization.startsWith(UserTokenHelper.LINK_PREFIX)) {
            throw new AuthFailedException("Bad link authorization: " + authorization);
        }
        String[] strArr = authorization.split(",");
        if (strArr.length != 2 && strArr.length != 3) {
            throw new AuthFailedException("Bad link authorization: " + authorization);
        }

        userToken = new UserToken();
        userToken.setLinkCode(strArr[1]);

        if(strArr.length == 3) {
            userToken.setPlainAccessCode(strArr[2]);
        }

        return userToken;
    }

    /**
     * 直链
     *
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "{linkCode}/thumbnail", method = RequestMethod.GET)
    @ApiOperation(value = "直链" ,notes = "直链服务")
    public ResponseEntity<String> directLinkThumbnail(@PathVariable String linkCode,
                                                      HttpServletRequest request) throws BaseRunException {
        checkEnableDirectLink();
        INodeLink nodeLink = getNodeLink(linkCode);

        INode node = fileBaseService.getAndCheckNode(nodeLink.getOwnedBy(), nodeLink.getiNodeId(), INode.TYPE_FILE);

        // 检查文件所在用户空间的状态
        userTokenHelper.checkUserStatus(null, node.getOwnedBy());

        checkLinkNodeOperACL(linkCode, node);

        // 安全扫描
        sendDownloadScanTask(node);
        UserToken usertoken = new UserToken();
        usertoken.setLoginName(linkCode);
        usertoken.setDeviceAddress(request.getRemoteAddr());
        DataAccessURLInfo accessUrlInfo;
        try {

            accessUrlInfo = fileBaseService.getINodeInfoDownURL(nodeLink.getOwnedBy(), node);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(usertoken, EventType.OTHERS, null, null, UserLogType.GET_DIRECT_LINK_ERR, null, null);
            throw e;
        }
        fileBaseService.sendINodeEvent(usertoken, EventType.OTHERS, null, null, UserLogType.GET_DIRECT_LINK, null, null);
        HttpHeaders header = new HttpHeaders();
        header.set("Connection", "close");
        String url = accessUrlInfo.getDownloadUrl();
        if (request.getParameter("minHeight") != null && request.getParameter("minWidth") != null) {
            String height = request.getParameter("minHeight");
            String width = request.getParameter("minWidth");
            checkNumber(height, width);
            url = url + '/' + DIRECT_THUMBNIAL + "?minHeight=" + height + "&minWidth=" + width;
        } else if (request.getParameter("height") != null && request.getParameter("width") != null) {
            String height = request.getParameter("height");
            String width = request.getParameter("width");
            checkNumber(height, width);
            url = url + '/' + DIRECT_THUMBNIAL + "?minHeight=" + height + "&minWidth=" + width;
        } else {
            throw new InvalidParamException("height or width is null");
        }
        header.set("Location", url);
        return new ResponseEntity<String>(null, header, HttpStatus.TEMPORARY_REDIRECT);
    }

    @RequestMapping(value = "{linkCode}", method = RequestMethod.POST)
    public ResponseEntity<String> distillLinkAccessCode(@PathVariable String linkCode,
                                                        @RequestBody RestLinkExtRequest linkRequest, @RequestHeader("Authorization") String token,
                                                        HttpServletRequest request) throws BaseRunException {
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
        // TODO:检查用户空间状态

        // 用户状态校验
        userTokenHelper.checkUserStatus(null, userToken.getCloudUserId());
        userToken.setLinkCode(linkCode);
        // 根据linkCode获取对应链接
        INodeLink nodeLink = linkService.getLinkForDirect(userToken, linkCode);
        if (null == nodeLink) {
            throw new NoSuchLinkException("The link file is not null");
        }
        if (nodeLink.getStatus() == LinkAccessCodeMode.TYPE_MAIL_VALUE
                || nodeLink.getStatus() == LinkAccessCodeMode.TYPE_PHONE_VALUE) {
            throw new ForbiddenException(
                    "The link with accesscode can not been accessed directly. Status is " + nodeLink.getStatus());
        }
        if (LINK_EXT_SECURITY_FLAG) {
            securityMatrixService.checkSecurityMatrix(userToken, nodeLink.getOwnedBy(), nodeLink.getiNodeId(), SecurityMethod.FILE_DOWNLOAD, headerCustomMap);
        }
        String dateStr = request.getHeader("date");
        checkLink(dateStr, linkRequest.getPlainAccessCode(), nodeLink);
        INode node = fileBaseService.getAndCheckNode(nodeLink.getOwnedBy(), nodeLink.getiNodeId(), INode.TYPE_FILE);
        String[] description = {String.valueOf(linkCode)};
        HttpHeaders header = new HttpHeaders();
        header.set("Connection", "close");
        String downloadUrl;
        try {
            downloadUrl = fileService.getFileDownloadUrl(userToken, node, header);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_FILE_DOWNLOAD_URL_ERR, description, null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_FILE_DOWNLOAD_URL, description, null);
        if (linkRequest.getType() == null) {
            linkRequest.setType(DIRECT_ORIGINAL);
        } else if (!DIRECT_THUMBNIAL.equals(linkRequest.getType())
                && !DIRECT_ORIGINAL.equals(linkRequest.getType())) {
            throw new InvalidParamException("no contain type");
        }

        if (DIRECT_THUMBNIAL.equals(linkRequest.getType())) {
            downloadUrl = downloadUrl + '/' + DIRECT_THUMBNIAL + "?minHeight=" + linkRequest.getHeight() + "&minWidth=" + linkRequest.getWidth();
        }

        header.set("Location", downloadUrl);
        return new ResponseEntity<>(null, header, HttpStatus.TEMPORARY_REDIRECT);
    }

    private void checkLinkNodeOperACL(String linkCode, INode node) {
        UserToken user = new UserToken();
        user.setLinkCode(linkCode);
        iNodeACLService.vaildINodeOperACL(user, node, AuthorityMethod.GET_OBJECT.name());
    }

    /**
     *
     */
    private void checkEnableDirectLink() {
        if (!enableDirectAccess()) {
            throw new ForbiddenException("Can not access the link directly.");
        }
    }

    private void checkLink(String dateStr, String sign, INodeLink link) throws ForbiddenException,
            AuthFailedException {
        if (StringUtils.isEmpty(link.getPlainAccessCode())) {
            return;
        }
        String dbCalRes = SignatureUtils.getSignature(link.getPlainAccessCode(), dateStr);
        if (!StringUtils.equals(dbCalRes, sign)) {
            throw new ForbiddenException("Error signature");
        }
    }

    private void checkNumber(String height, String width) {
        try {
            if (Integer.parseInt(height) <= 0) {
                throw new InvalidParamException("height is " + height + ", width is " + width);
            }
            if (Integer.parseInt(width) <= 0) {
                throw new InvalidParamException("height is " + height + ", width is " + width);
            }
        } catch (InvalidParamException e) {
            throw new InvalidParamException("height is " + height + ", width is " + width, e);
        }
    }

    private Boolean enableDirectAccess() {
        return "true".equals(PropertiesUtils.getProperty("direct.link.enable", "true"));
    }

    /**
     * @param linkCode
     * @return
     */
    private INodeLink getNodeLink(String linkCode) {
        return getNodeLink(linkCode, null);
    }

    private INodeLink getNodeLink(String linkCode, String accessCode) {
        INodeLink nodeLink = null;
        try {
            nodeLink = linkService.getLinkByLinkCode(linkCode);
        } catch (NoSuchLinkException e) {
            throw new NoSuchLinkException("The nodelink  not exist, linkCode:" + linkCode, e);
        }

/*
        if (nodeLink.getStatus() == LinkAccessCodeMode.TYPE_MAIL_VALUE || nodeLink.getStatus() == LinkAccessCodeMode.TYPE_PHONE_VALUE) {
            throw new ForbiddenException("The link with accesscode can not been accessed directly. Status is " + nodeLink.getStatus());
        }
*/
        if (StringUtils.isNotEmpty(nodeLink.getPlainAccessCode()) && !nodeLink.getPlainAccessCode().equals(accessCode)) {
            logger.warn("Get Link Info failed, the link access is wrong. expected:{}, actually:{}", nodeLink.getPlainAccessCode(), accessCode);
            throw new ForbiddenException("The link access code is wrong. " + accessCode);
        }

        return nodeLink;
    }

    private void sendDownloadScanTask(INode node) {
        int secLabel = securityScanManager.sendScanTask(node, SecurityScanTask.PRIORITY_HIGH);
        SecurityStatus status = SecurityStatus.getSecurityStatus(secLabel);
        if (status == null || IGNORE_SCAN_RESULT) {
            return;
        }
        switch (status) {
            case KIA_UMCOMPLETED:
                throw new FileScanningException("File is not ready");
            case KIA_COMPLETED_INSECURE:
                throw new ScannedForbiddenException("This file is not allowed to be downloaded");
            default:
                break;
        }
    }


    boolean isImageSupported(INode node) {
        if(node == null || node.getName() == null) {
            return false;
        }

        String fileName = node.getName().toLowerCase();
        if(fileName.endsWith(".bmp") || fileName.endsWith(".gif") ||
                fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".jif ") ) {
            return true;
        }

        return false;
    }
}
