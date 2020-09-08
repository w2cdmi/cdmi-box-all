package com.huawei.sharedrive.uam.enterprise.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfigExt;
import pw.cdmi.common.domain.AccountConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccountConfigAttribute;
import com.huawei.sharedrive.uam.enterprise.manager.AccountConfigManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.openapi.domain.account.AccountAttribute;

@Controller
@RequestMapping(value = "/enterprise/config")
public class AccountConfigController extends AbstractCommonController {

	private final static Logger LOGGER = LoggerFactory.getLogger(AccountConfigController.class);

	@Autowired
	private AccountConfigManager accountConfigManager;

	@Autowired
	private AdminLogManager adminLogManager;

	@RequestMapping(value = "basic/{appId}", method = RequestMethod.GET)
	public ResponseEntity<?> functionParameter(HttpServletRequest request,
			@PathVariable(value = "appId") String appId, String token) throws IOException {
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		long accountId = getAccoutId(appId);
		List<AccountConfig> accountConfigList;
		try {
			accountConfigList=accountConfigManager.getAll(accountId);
		} catch (RuntimeException e) {
			LOGGER.error("create list config fail", e);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_SPACE_ADD_ERROR,
					new String[] { getEnterpriseName(), appId });
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<AccountConfig>>(accountConfigList,HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "basic/{appId}", method = RequestMethod.POST)
	public ResponseEntity<String> modifyBasic(HttpServletRequest request,
			@PathVariable(value = "appId") String appId, String token) throws IOException {
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		long accountId = getAccoutId(appId);
		try {
			String key = request.getParameter("name");
			String value= request.getParameter("value");
			accountConfigManager.createOrUpdate(accountId, key,value);
		} catch (RuntimeException e) {
			LOGGER.error("create functionParameter config fail", e);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_SPACE_ADD_ERROR,
					new String[] { getEnterpriseName(), appId });
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "modifyBasicArray/{appId}", method = RequestMethod.POST)
	public ResponseEntity<String> modifyBasicArray(HttpServletRequest request,
			@PathVariable(value = "appId") String appId, String token) throws IOException {
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		long accountId = getAccoutId(appId);
		try {
			String  jarry = request.getParameter("data");
			String  regexp = "\'";
			jarry=jarry.replaceAll(regexp, "\"");
			ObjectMapper mapper = new ObjectMapper();
			ArrayType arrayType = mapper.getTypeFactory().constructArrayType(AccountConfig.class);
			AccountConfig[] accountConfigs = mapper.readValue(jarry, arrayType);
			for(int i=0;i<accountConfigs.length;i++){
				AccountConfig accountConfig=accountConfigs[i];
				accountConfigManager.createOrUpdate(accountId, accountConfig.getName(),accountConfig.getValue());
			}
			
		} catch (RuntimeException e) {
			LOGGER.error("create functionParameter config fail", e);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_SPACE_ADD_ERROR,
					new String[] { getEnterpriseName(), appId });
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}


}
