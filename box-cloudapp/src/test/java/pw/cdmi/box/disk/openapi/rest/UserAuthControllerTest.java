package pw.cdmi.box.disk.openapi.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import pw.cdmi.box.disk.httpclient.exception.ClientException;
import pw.cdmi.box.disk.httpclient.rest.request.RestUserLoginCreateRequest;
import pw.cdmi.box.disk.test.AbstractSpringTest;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

@Component
public class UserAuthControllerTest extends AbstractSpringTest {

	/**
	 * RestClient-ufm.
	 */
	@Resource
	private RestClient ufmClientService;
	/**
	 * RestClient-uam.
	 */
	@Resource
	private RestClient uamClientService;

	@Test
	public void userLogin() throws ClientException {
		if (loginUser != null) {
			Assert.isTrue(true);
		} else {
			/*
			 * String deviceTypeStr = request.getHeader("x-device-type"); String
			 * deviceSN = request.getHeader("x-device-sn"); String deviceOS =
			 * request.getHeader("x-device-os"); String deviceName =
			 * request.getHeader("x-device-name"); String deviceAgent =
			 * request.getHeader("x-client-version"); String requestIp =
			 * request.getHeader("x-request-ip"); private String appId; private
			 * String loginName; private String password;
			 */

			Map<String, String> header = new HashMap<String,String>();
			header.put("x-device-type", "pc");
			header.put("x-device-sn", "123456");
			header.put("x-device-os", "Window 7");
			header.put("x-device-name", "PC");
			header.put("x-client-version", "V1.2.3.1702");
			header.put("x-request-ip", "10.169.37.104");

			RestUserLoginCreateRequest request = new RestUserLoginCreateRequest();
			request.setAppId("OneBox");
			request.setLoginName("w00186884");
			request.setPassword("22312wh@)!$");
			TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/token", header, request);

			Assert.notNull(response);
		}
	}
}
