package com.huawei.sharedrive.app.openapi.restv2.file;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileServiceMulti;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.FileMultiPreUploadRequest;
import com.huawei.sharedrive.app.openapi.domain.node.FileMultiPreUploadResponse;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 文件API Rest接口, 提供批量文件预上传等操作
 *
 * @author t90006461
 * @version V2 CloudStor CSE Service Platform Subproject, 2014-4-30
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v2/files/{ownerId}")
@Api(description = "文件API Rest接口, 提供批量文件预上传等操作")
public class FileMultiApi {
    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private FileServiceMulti fileServiceMulti;
    @Autowired
    private SecurityMatrixService securityMatrixService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    /**
     * 预上传
     *
     * @param request
     * @param ownerId
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/multi", method = RequestMethod.PUT)
    @ApiOperation(value = "预上传")
    @ResponseBody
    public ResponseEntity<?> preUploadFile(@RequestBody FileMultiPreUploadRequest request,
                                           @PathVariable Long ownerId, @RequestHeader("Authorization") String token,
                                           HttpServletRequest requestServlet) throws BaseRunException {
        UserToken userToken = null;
        try {
            // 参数校验
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);
            request.checkParamter();
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            // 安全矩阵校验
            String xUserToken = requestServlet.getHeader("x-usertoken");
            if (StringUtils.isNotEmpty(xUserToken)) {
                UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, headerCustomMap);
                securityMatrixService.checkSecurityMatrix(accessUserToken,
                        ownerId,
                        null,
                        SecurityMethod.FILE_UPLOAD,
                        headerCustomMap);
            } else {
                securityMatrixService.checkSecurityMatrix(userToken,
                        ownerId,
                        null,
                        SecurityMethod.FILE_UPLOAD,
                        headerCustomMap);
            }

            // 空间和文件数校验
            fileBaseService.checkSpaceAndFileCount(ownerId, userToken.getAccountId());

            List<INode> fileNodeList = request.transToINode(ownerId);
            FileMultiPreUploadResponse rsp = fileServiceMulti.preUploadFile(userToken, fileNodeList, request.getTokenTimeout());

            HttpHeaders header = new HttpHeaders();
            header.set("Connection", "close");

            return new ResponseEntity<FileMultiPreUploadResponse>(rsp, header, HttpStatus.OK);
        } catch (RuntimeException t) {
            String parentId = null;
            if (request != null) {
                parentId = String.valueOf(request.getParent());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.UPLOADURL_PROVIDE_ERR,
                    logParams,
                    null);
            throw t;
        }
    }


}
