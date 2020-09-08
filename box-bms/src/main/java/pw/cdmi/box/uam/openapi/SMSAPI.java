package pw.cdmi.box.uam.openapi;

import java.security.InvalidParameterException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.system.service.SMSService;
import pw.cdmi.core.utils.JsonUtils;

@Controller
@RequestMapping(value="/api/v2/sms")
public class SMSAPI {
	@Autowired
	private SMSService smsService;
	
	@RequestMapping(value="sendIdentifyCode",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> sendIdentifyCode(@RequestBody String string){
		Map<String, Object> stringToMap = JsonUtils.stringToMap(string);
		String contactPhone =(String) stringToMap.get("contactPhone");
		String identifyCode =(String) stringToMap.get("identifyCode");
		if (StringUtils.isBlank(contactPhone)||StringUtils.isBlank(identifyCode)) {
			throw new InvalidParameterException();
		}
		smsService.sendSMS("a4a89f483a334227abc033ef509a27db",contactPhone, null, null,identifyCode);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
