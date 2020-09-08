package pw.cdmi.box.disk.test.service;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.httpclient.exception.ClientException;
import pw.cdmi.box.disk.httpclient.rest.request.UserRequest;
import pw.cdmi.box.disk.test.AbstractSpringTest;
import pw.cdmi.box.disk.user.service.UserLoginService;

@Component
public class UserLoginServiceTest extends AbstractSpringTest {
	private static Log logger = LogFactory.getLog(UserLoginServiceTest.class);

	@Resource
	private UserLoginService userLoginServiceImpl;

	@Test
	public void testLoginPlat() throws ClientException {
		UserRequest requestBody = new UserRequest();
		requestBody.setLoginName("w00186884");
		requestBody.setPassword("22312wh@)!$");
		requestBody.setDeviceType(2);
		requestBody.setDeviceAddress("127.0.0.1");
		requestBody.setDeviceAgent("web");
		requestBody.setDeviceName("Shareweb");
		requestBody.setDeviceOS("Java");
		requestBody.setDeviceSn("123");
		// response = new UserHttpClient().loginPlatform(requestBody);

		// Assert.notNull(response);
	}

	@Test
	public void testLoginPlat2() throws ClientException {
		if (response != null) {
			logger.info("the user already login,the token is :" + response.getToken());
		} else {
			UserRequest requestBody = new UserRequest();
			requestBody.setLoginName("w00186884");
			requestBody.setPassword("22312wh@)!$");
			requestBody.setDeviceType(2);
			requestBody.setDeviceAddress("127.0.0.1");
			requestBody.setDeviceAgent("web");
			requestBody.setDeviceName("Shareweb");
			requestBody.setDeviceOS("Java");
			requestBody.setDeviceSn("123");
			// response = new UserHttpClient().loginPlatform(requestBody);

			// Assert.notNull(response);
		}
	}
}
