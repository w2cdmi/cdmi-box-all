package pw.cdmi.box.disk.user.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.disk.user.domain.RestTempAccountRequest;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

@Controller
@RequestMapping(value = "/tempAccount")
public class TempAccountController {
	
	private static Logger logger = LoggerFactory.getLogger(TempAccountController.class);
	
	@Resource
	private RestClient uamClientService;
	
	@Autowired
	private UserTokenManager userTokenManager;
	
	/**
     * 对临时账号升级为vip账号
     * 
     */
    @RequestMapping(value = "/up2VIP", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> tempAccount2VIP(@RequestBody RestTempAccountRequest restTempAccountRequest){
    	
    	User user = (User) SecurityUtils.getSubject().getPrincipal();
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", userTokenManager.getToken());
		Map<String, Long> requestBody = new HashMap<String, Long>(1);
		requestBody.put("orderId", Long.valueOf(restTempAccountRequest.getOrderId()));
		TextResponse restResponse = uamClientService.performJsonPostTextResponse("/api/v2/tempAccount", headerMap, requestBody);
    	
    	if(restResponse.getStatusCode() == 200){
    		logger.info("user up2vip success, name: " + user.getName());
    		return new ResponseEntity<>(HttpStatus.OK);
    	}else{
    		logger.error("user up2vip fial, name: " + user.getName() + "orderId:" + restTempAccountRequest.getOrderId());
    		return new ResponseEntity<>(restResponse.getResponseBody(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
