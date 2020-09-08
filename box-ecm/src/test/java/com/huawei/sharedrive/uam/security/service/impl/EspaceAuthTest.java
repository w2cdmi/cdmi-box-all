/**
 * 
 */
package com.huawei.sharedrive.uam.security.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.huawei.sharedrive.uam.openapi.domain.RestUserLoginCreateRequest;
import com.huawei.sharedrive.uam.test.AbstractSpringTest;

import pw.cdmi.core.restrpc.RestClient;

/**
 * @author w00186884
 * 
 */
public class EspaceAuthTest extends AbstractSpringTest {
	/**
	 * RestClient-uam.
	 */
	@Resource
	private RestClient uamClientService;

	@Test
	public void testEspaceAuth() {
		RestUserLoginCreateRequest request = new RestUserLoginCreateRequest();
		request.setAppId("espace");
		request.setLoginName("w00186884");
		request.setPassword("22312wh@)!##");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("x-device-type", "android");
		headers.put("x-device-sn", "123456");
		headers.put("x-device-os", "android");
		headers.put("x-device-name", "神机");
		headers.put("x-client-version", "V1.0");
		headers.put("x-request-ip", "10.169.37.104");

		uamClientService.performJsonPostTextResponse("/api/v2/token", headers, request);
	}
}
