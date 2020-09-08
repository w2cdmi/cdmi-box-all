package com.huawei.sharedrive.app.openapi.restv2.file;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;
import com.huawei.sharedrive.app.filelabel.domain.LatestViewFileLabel;
import com.huawei.sharedrive.app.filelabel.dto.*;
import com.huawei.sharedrive.app.filelabel.exception.FileLabelException;
import com.huawei.sharedrive.app.filelabel.service.IFileLabelService;
import com.huawei.sharedrive.app.filelabel.util.FileLabelContants;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.doctype.RestDocTypeList;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.ufm.tools.domain.DocUserConfig;
import pw.cdmi.box.ufm.tools.service.DocTypeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 
 * Desc  : 文件标签API
 * Author: 77235 
 * Date	 : 2016年11月29日
 */
@Controller
@RequestMapping("/api/v2/fl/{ownerId}")
@Api(description = "文件标签API接口")
public class FileLabelApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileLabelApi.class);

    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private IFileLabelService fileLabelService;
    
    @Autowired
    private DocTypeService docTypeService;
    
    /**
     * 标签绑定
     * @param token
     * @param ownerId
     * @param fileLabelDto
     * @param request
     */
    @RequestMapping(value = "/bindFilelabel", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "标签绑定")
    @ResponseBody
    public ResponseEntity<EntityFilelabelResponseDto> bindFileLabel(
        @RequestHeader("Authorization") String token, @PathVariable Long ownerId,
        @RequestBody FileLabelRequestDto fileLabelRequest, HttpServletRequest request)
        throws BaseRunException {
        EntityFilelabelResponseDto respDto = new EntityFilelabelResponseDto();
        
        try {
            
            UserToken userToken = checkUserToken(request, token, ownerId, fileLabelRequest.getNodeId());
            if (StringUtils.isEmpty(fileLabelRequest.getLabelName())) {
                FileLabelException.throwFilelabelException(null, FileLabelContants.FL_EXCEPTION_LACK_LABELNAME);
            }
            
            fileLabelRequest.setBindUserId(userToken.getId());
            fileLabelRequest.setEnterpriseId(userToken.getAccountId());
            
            long fileLabelId = fileLabelService.bindFileLabel(fileLabelRequest);
            respDto.setStatus(HttpStatus.CREATED);
            respDto.setFilelabelInfo(new BaseFileLabelInfo(fileLabelId, fileLabelRequest.getLabelName()));
        } catch (FileLabelException fe) {
			LOGGER.error("[FileLabelApi] bindFileLabel error:" + fe.getMessage(), fe);

            if (StringUtils.isNotEmpty(fe.getErrorCode())) {
                respDto.setErrorCode(fe.getErrorCode());
            } else {
                respDto.setErrorCode(FileLabelContants.FL_EXCEPTION_UNKNOW);
            }
        } catch (Exception e) {
			LOGGER.error("[FileLabelApi] bindFileLabel error:" + e.getMessage(), e);

            respDto.setErrorCode(FileLabelContants.FL_EXCEPTION_UNKNOW);
        }
        
        return new ResponseEntity<EntityFilelabelResponseDto>(respDto, HttpStatus.OK);
    }
    
    /**
     * 解除标签绑定
     * @param token
     * @param ownerId
     * @param fileLabelDto
     * @param request {nodeId}/{labelId}
     */
    @RequestMapping(value = "/unbindFilelabel", method = RequestMethod.POST)
    @ApiOperation(value = "解除标签绑定")
    @ResponseBody
    public ResponseEntity<BaseFilelabelResponseDto> unbindFileLabel(
        @RequestHeader("Authorization") String token, @PathVariable Long ownerId,
        @RequestBody FileLabelRequestDto fileLabelRequest, HttpServletRequest request)
        throws BaseRunException {
        BaseFilelabelResponseDto respDto = new BaseFilelabelResponseDto();
        
        try {
            if (fileLabelRequest.getNodeId() <= 0) {
                FileLabelException.throwFilelabelException(null,
                    FileLabelContants.FL_EXCEPTION_INVALID_LABELID);
            }
            
            UserToken userToken = checkUserToken(request, token, ownerId, fileLabelRequest.getLabelId());
            fileLabelRequest.setBindUserId(userToken.getId());
            fileLabelRequest.setEnterpriseId(userToken.getAccountId());
            
            fileLabelService.unbindFileLabel(fileLabelRequest);
            respDto.setStatus(HttpStatus.OK);
        } catch (FileLabelException fe) {
			LOGGER.error("[FileLabelApi] unbindFileLabel error:" + fe.getMessage(), fe);

            if (StringUtils.isNotEmpty(fe.getErrorCode())) {
                respDto.setErrorCode(fe.getErrorCode());
            } else {
                respDto.setErrorCode(FileLabelContants.FL_EXCEPTION_UNKNOW);
            }
        } catch (Exception e) {
        	LOGGER.error("[FileLabelApi] unbindFileLabel error:" + e.getMessage(), e);
        	
            respDto.setErrorCode(FileLabelContants.FL_EXCEPTION_UNKNOW);
        }
        
        return new ResponseEntity<BaseFilelabelResponseDto>(respDto, HttpStatus.OK);
    }

    /**
     * 列举企业标签信息
     * @param token
     * @param ownerId
     * @param reqPage
     * @param pageSize
     * @param request
     * @return 
     */
    @RequestMapping(value = "/listFilelabels/{labelType}/{reqPage}/{pageSize}", method = RequestMethod.POST)
    @ApiOperation(value = "列举企业标签信息")
    @ResponseBody
    public ResponseEntity<ListFilelabelResponseDto> listEnterpriseFileLabels(
        @RequestHeader("Authorization") String token, @PathVariable("ownerId") long ownerId,
        @PathVariable int reqPage, @PathVariable int pageSize, @PathVariable("labelType") int labelType,
        @RequestParam(required = false, defaultValue = "") String filelabelName, HttpServletRequest request) {
        ListFilelabelResponseDto respDto = new ListFilelabelResponseDto();
        
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            FileLabelQueryCondition condition = new FileLabelQueryCondition(reqPage, pageSize);
            condition.setCreateBy(userToken.getId())
                .setOwnerId(ownerId)
                .setLabelType(labelType)
                .setEnterpriseId(userToken.getAccountId())
                .setLabelName(filelabelName);
            
            List<BaseFileLabelInfo> retList = fileLabelService.retrivalFilelabelsByEnterprise(condition);
            
            respDto.setTotalCount(condition.getTotalCount());
            respDto.setCurrPage(condition.getCurrPage());
            respDto.setTotalPageSize(condition.getTotalPage());
            respDto.setPageSize(condition.getPageSize());
            respDto.setFileLabelList(retList);
            respDto.setStatus(HttpStatus.OK);
        } catch (FileLabelException fe) {
			LOGGER.error("[FileLabelApi] listEnterpriseFileLabels error:" + fe.getMessage(), fe);

            if (StringUtils.isNotEmpty(fe.getErrorCode())) {
                respDto.setErrorCode(fe.getErrorCode());
            } else {
                respDto.setErrorCode(FileLabelContants.FL_EXCEPTION_UNKNOW);
            }
        } catch (Exception e) {
			LOGGER.error("[FileLabelApi] listEnterpriseFileLabels error:" + e.getMessage(), e);

            respDto.setErrorCode(FileLabelContants.FL_EXCEPTION_UNKNOW);
        }
        
        return new ResponseEntity<ListFilelabelResponseDto>(respDto, HttpStatus.OK);
    }

    /**
     * 查询通用文档类型信息
     * @param userId
     * @param token
     * @param request
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/doctype", method = RequestMethod.GET)
    @ApiOperation(value = "查询通用文档类型信息")
    @ResponseBody
    public ResponseEntity<RestDocTypeList> listDocTypesForOwner(@PathVariable("ownerId") long ownerId,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        ResponseEntity<RestDocTypeList> retEntity = null;
        RestDocTypeList retDto = new RestDocTypeList();
        
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
            List<DocUserConfig> docUserConfigs = docTypeService.getDocUserConfigsByOwnerId(ownerId);
            for (DocUserConfig docConf : docUserConfigs) {
                docConf.setValue("");
            }
            
            retDto.setDocUserConfigs(docUserConfigs);
            retEntity = new ResponseEntity<RestDocTypeList>(retDto, HttpStatus.OK);
            
        } catch (BaseRunException t) {
			LOGGER.error("[FileLabelApi] listDocTypesForOwner error:" + t.getMessage(), t);

            retEntity = new ResponseEntity<RestDocTypeList>(retDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return retEntity;
    }

    /**
     * 用户最近使用的5次标签信息
     * @param token
     * @param request
     * @return
     */
    @RequestMapping(value = "/userLatestViewedLabels", method = RequestMethod.POST)
    @ApiOperation(value = "用户最近使用的5次标签信息")
    @ResponseBody
    public ResponseEntity<ListFilelabelResponseDto> listUserLatestviewdFileLabels(
        @RequestHeader("Authorization") String token, HttpServletRequest request) {
        ListFilelabelResponseDto respDto = new ListFilelabelResponseDto();
        
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            List<LatestViewFileLabel> latestlabels = fileLabelService
                .retrivalUserLatestVistedLabels(userToken.getAccountId(), userToken.getId());
            respDto.setFileLabelList(latestlabels);
            respDto.setStatus(HttpStatus.OK);
        } catch (FileLabelException fe) {
			LOGGER.error("[FileLabelApi] listUserLatestviewdFileLabels error:" + fe.getMessage(), fe);

            if (StringUtils.isNotEmpty(fe.getErrorCode())) {
                respDto.setErrorCode(fe.getErrorCode());
            } else {
                respDto.setErrorCode(FileLabelContants.FL_EXCEPTION_UNKNOW);
            }
        } catch (Exception e) {
			LOGGER.error("[FileLabelApi] listUserLatestviewdFileLabels error:" + e.getMessage(), e);

            respDto.setErrorCode(FileLabelContants.FL_EXCEPTION_UNKNOW);
        }
        
        return new ResponseEntity<ListFilelabelResponseDto>(respDto, HttpStatus.OK);
        
    }
    /**
     * 根据用户token获取用户信息
     * @param request
     * @param token
     * @param ownerId
     * @param nodeId
     * @return
     */
    private UserToken checkUserToken(HttpServletRequest request, String token, Long ownerId, Long nodeId) {
        UserToken userToken = null;
        
        FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
        } catch (Exception e) {
            FileLabelException.throwFilelabelException(e,
                FileLabelContants.FL_EXCEPTION_INVALID_USER);
        }
        
        try {
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
        } catch (Exception e) {
            FileLabelException.throwFilelabelException(e,
                FileLabelContants.FL_EXCEPTION_INVALID_USER_STATUS);
        }
        
        return userToken;
    }
}
