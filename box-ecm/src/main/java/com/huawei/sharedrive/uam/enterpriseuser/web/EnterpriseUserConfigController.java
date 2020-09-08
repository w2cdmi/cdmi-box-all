package com.huawei.sharedrive.uam.enterpriseuser.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserConfigManager;

/*@Controller
@RequestMapping(value = "/enterprise/user/config")*/
public class EnterpriseUserConfigController {

	 @Autowired
	 private EnterpriseUserConfigManager userConfigManager;
	 
	  @RequestMapping(value = "personal/{accountId}/{userId}", method = RequestMethod.POST)
	    public ResponseEntity<String> personal(HttpServletRequest request,@PathVariable(value = "accountId") String accountId,
	    		@PathVariable(value = "userId") String userId, String token) throws IOException{
		  
		  return null;
	  }
}
