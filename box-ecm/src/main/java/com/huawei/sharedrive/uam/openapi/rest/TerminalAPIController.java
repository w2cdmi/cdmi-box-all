package com.huawei.sharedrive.uam.openapi.rest;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackCreateRequest;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackDetail;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestFeedBackSubInfo;
import com.huawei.sharedrive.uam.feedback.domain.RestUserFeedBackListRequest;
import com.huawei.sharedrive.uam.feedback.domain.RestUserFeedBackListResponse;
import com.huawei.sharedrive.uam.feedback.manager.UserFeedBackManager;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.ListTerminalRequest;
import com.huawei.sharedrive.uam.openapi.domain.ListTerminalResonse;
import com.huawei.sharedrive.uam.openapi.domain.terminal.UpdateTerminalRequest;
import com.huawei.sharedrive.uam.terminal.manager.TerminalCheckManager;
import com.huawei.sharedrive.uam.terminal.manager.TerminalManager;

import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/api/v2/terminal")
public class TerminalAPIController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(TerminalAPIController.class);
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private TerminalManager terminalManager;
    
    @Autowired
    private TerminalCheckManager terminalCheckManager;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private UserFeedBackManager feedBackManager;
    
    @RequestMapping(value = "/items", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ListTerminalResonse> listTerminal(
        @RequestBody(required = false) ListTerminalRequest listTerminaRequest,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(token);
        UserLog userLog = UserLogType.getUserLog(userToken);
        ListTerminalResonse listTerminalResonse = null;
        try
        {
            long cloudUserId = userToken.getCloudUserId();
            if (listTerminaRequest != null)
            {
                terminalCheckManager.checkRequestParameter(listTerminaRequest);
            }
            else
            {
                listTerminaRequest = new ListTerminalRequest();
            }
            
            int limit = listTerminaRequest.getLimit();
            long offset = listTerminaRequest.getOffset();
            
            listTerminalResonse = terminalManager.listTerminal(cloudUserId, offset, limit);
            LOGGER.debug(ToStringBuilder.reflectionToString(listTerminalResonse));
        }
        catch (InvalidParameterException e)
        {
            // TODO Auto-generated catch block
            userLogService.saveUserLog(userLog, UserLogType.KEY_SYSTEM_CONFIG_ERR, null);
            throw e;
        }
        userLogService.saveUserLog(userLog, UserLogType.KEY_SYSTEM_CONFIG, null);
        return new ResponseEntity<ListTerminalResonse>(listTerminalResonse, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/status", method = RequestMethod.PUT)
    public ResponseEntity<String> updateTerminalStatus(@RequestHeader("Authorization") String token,
        @RequestBody UpdateTerminalRequest request) throws BaseRunException
    {
        UserLog userLog = null;
        try
        {
            UserToken userToken = userTokenHelper.checkTokenAndGetUser(token);
            userLog = UserLogType.getUserLog(userToken);
            long cloudUserId = userToken.getCloudUserId();
            request.checkParam();
            terminalManager.updateClientTerminalStatus(cloudUserId,
                request.getDeviceSN(),
                request.getStatus());
        }
        catch (RuntimeException e)
        {
            userLogService.saveUserLog(userLog, UserLogType.KEY_TERMINAL_UPDATE_STATUS_ERR, null);
            throw e;
        }
        userLogService.saveUserLog(userLog, UserLogType.KEY_TERMINAL_UPDATE_STATUS, null);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
	 * 
	 * @param userId
	 * @return
	 * @throws BaseRunException
	 * @throws IOException
	 */
	@RequestMapping(value = "/list", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<RestUserFeedBackListResponse> listUserFeedBack(
			@RequestBody(required = false) RestUserFeedBackListRequest condition,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		
		
		RestUserFeedBackListResponse result = feedBackManager.queryRestUserFeedBackList(condition);

		LOGGER.debug(ToStringBuilder.reflectionToString(result));

		return new ResponseEntity<RestUserFeedBackListResponse>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/count", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Integer> countUserFeedBack(
			@RequestBody(required = false) RestUserFeedBackListRequest condition,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		

		int result = feedBackManager.countRestFeedBack(condition);

		LOGGER.debug(ToStringBuilder.reflectionToString(result));

		return new ResponseEntity<Integer>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/add", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<String> addUserFeedBack(
			@RequestBody(required = true) RestFeedBackCreateRequest restFeedBackCreateRequest,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		
		LOGGER.debug("uam addUserFeedback start");
		
		UserToken userToken = userTokenHelper.checkTokenAndGetUser(token);

		EnterpriseUser enterpriseUser = feedBackManager.getByIdAndEnterprise(userToken.getId(), userToken.getEnterpriseId());
		
        
		if(null!=enterpriseUser){
			
			restFeedBackCreateRequest.setCustomerName(enterpriseUser.getName()+"/"+enterpriseUser.getAlias());
			restFeedBackCreateRequest.setCustomerEmail(enterpriseUser.getEmail());
		}
		try {
			feedBackManager.addUserFeedBack(restFeedBackCreateRequest);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
		}
		
		LOGGER.debug("uam addUserFeedback success");
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/addFeedBackSub", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<String> addUserFeedBackSub(@RequestBody(required = true) RestFeedBackSubInfo restFeedBackInfo,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {

		feedBackManager.addNewFeedBackSub(restFeedBackInfo);

		return new ResponseEntity<String>(HttpStatus.OK);
	}
	

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<String> updateUserFeedBack(@RequestBody(required = true) RestFeedBackInfo restFeedBackInfo,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		
		feedBackManager.updateUserFeedBack(restFeedBackInfo);

		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/updateFeedBack", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<String> updateUserFeedBack(@RequestBody(required = true) RestFeedBackDetail feedBackDetail,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		
		feedBackManager.updateTeedBackTime(feedBackDetail);;

		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/findFeedBackSubs", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<List<RestFeedBackSubInfo>> findFeedBackSubs(
			@RequestBody(required = true) long problemID,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {

		List<RestFeedBackSubInfo> feedBackSubList = feedBackManager.getFeedBackSubList(problemID);

		LOGGER.debug(ToStringBuilder.reflectionToString(feedBackSubList));

		return new ResponseEntity<List<RestFeedBackSubInfo>>(feedBackSubList, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/findFeedBack", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<RestFeedBackInfo> findFeedBack(
			@RequestBody(required = true) long problemID,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {

		RestFeedBackInfo feedBackInfo = feedBackManager.getFeedBackByID(problemID);

		LOGGER.debug(ToStringBuilder.reflectionToString(feedBackInfo));

		return new ResponseEntity<RestFeedBackInfo>(feedBackInfo, HttpStatus.OK);
	}


	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> deleteUserFeedBack(@RequestParam("problemID") long problemID,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {

		feedBackManager.logicDeleteUserFeedBack(problemID);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteSub", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> deleteUserFeedBackSub(@RequestParam("problemID") long problemID,
			@RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {

		feedBackManager.deleteUserFeedBackSub(problemID);

		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	/**
	 * ���ύ Date�������ݰ� <������ϸ����>
	 * 
	 * @param binder
	 * @see [�ࡢ��#��������#��Ա]
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
}
