package com.huawei.sharedrive.app.openapi.restv2.docType;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.doctype.RestCreateDocTypeRequest;
import com.huawei.sharedrive.app.openapi.domain.doctype.RestDocTypeList;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import com.huawei.sharedrive.thrift.app2dc.FileObjectThriftService.AsyncProcessor.updateFileObject;

import pw.cdmi.box.ufm.tools.domain.DocUserConfig;
import pw.cdmi.box.ufm.tools.service.DocTypeService;

@Controller
@RequestMapping(value = "/api/v2/doctype")
public class DoctypeApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(DoctypeApi.class);

	@Autowired
	private DocTypeService docTypeService;

	@Autowired
	private UserTokenHelper userTokenHelper;

	@RequestMapping(value = "/find/owner/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<RestDocTypeList> findDocTypesByOwner(@PathVariable("userId") Long userId,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		UserToken userToken = null;
		try {
			FilesCommonUtils.checkNonNegativeIntegers(userId);
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			// Token 验证
			userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

			// 用户状态校验
			userTokenHelper.checkUserStatus(userToken.getAppId(), userId);
			List<DocUserConfig> docUserConfigs = docTypeService.getDocUserConfigsByOwnerId(userId);
			RestDocTypeList doctypeInfo = new RestDocTypeList();
			doctypeInfo.setDocUserConfigs(docUserConfigs);
			return new ResponseEntity<RestDocTypeList>(doctypeInfo, HttpStatus.OK);
		} catch (BaseRunException t) {
			LOGGER.error("findDocTypesByOwner get doctype fail:" + t.getMessage());
			throw t;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/find/id/{id}", method = RequestMethod.GET)
	public ResponseEntity<DocUserConfig> findDoctypeById(@PathVariable long id,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		try {
			FilesCommonUtils.checkNonNegativeIntegers(id);
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);

			userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

			DocUserConfig docUserConfig = docTypeService.getDocUserConfigById(id);

			return new ResponseEntity<DocUserConfig>(docUserConfig, HttpStatus.OK);

		} catch (BaseRunException t) {
			LOGGER.error("Find doctype by id faile." + t.getMessage());
			throw t;
		}

	}

	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<String> updateDoctypeUser(@RequestBody RestCreateDocTypeRequest restCreateDocTypeRequest) {
		try {
			DocUserConfig docUserConfig = transDocUserConfig(restCreateDocTypeRequest);
			docTypeService.updateDocUserType(docUserConfig);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<String> createDoctypeUser(@RequestBody RestCreateDocTypeRequest restCreateDocTypeRequest,
			@RequestHeader("Authorization") String authorization) {

		try {
			DocUserConfig docUserConfig = transDocUserConfig(restCreateDocTypeRequest);
			long count = docTypeService.getCountByOwnerId(docUserConfig.getUserId());

			if (count >= 5) {

				return new ResponseEntity<String>("user doctype>5", HttpStatus.BAD_REQUEST);
			}
			docTypeService.insertUserDoctype(docUserConfig);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (BaseRunException t) {
			LOGGER.error("create doctype faile." + t.getMessage());
			throw t;
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/remove/id/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeDoctypeUserById(@PathVariable("id") long id,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		try {

			docTypeService.delUserDocTypeById(id);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (BaseRunException e) {
			LOGGER.error("remove doctype by id failed." + e.getMessage());
			throw e;
		}

	}

	@ResponseBody
	@RequestMapping(value = "/remove/userId/{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeDoctypeUserByUserId(@PathVariable("userId") long userId,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		try {
			docTypeService.delUserDocTypeByOwner(userId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (BaseRunException e) {
			LOGGER.error("remove doctype by userId failed." + e.getMessage());
			throw e;
		}
	}

	private DocUserConfig transDocUserConfig(RestCreateDocTypeRequest restCreateDocTypeRequest) {
		DocUserConfig docUserConfig = new DocUserConfig();
		docUserConfig.setId(restCreateDocTypeRequest.getId());
		docUserConfig.setName(restCreateDocTypeRequest.getName());
		docUserConfig.setValue(restCreateDocTypeRequest.getValue());
		docUserConfig.setUserId(restCreateDocTypeRequest.getUserId());
		docUserConfig.setAppId(restCreateDocTypeRequest.getAppId());
		return docUserConfig;
	}
}
