package pw.cdmi.box.disk.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.util.Assert;

import pw.cdmi.box.disk.client.domain.user.RestUserloginRequest;
import pw.cdmi.box.disk.client.domain.user.RestUserloginRsp;
import pw.cdmi.box.disk.httpclient.exception.ClientException;
import pw.cdmi.box.disk.httpclient.rest.UserTestHttpClient;
import pw.cdmi.box.disk.httpclient.rest.response.UserResponse;
import pw.cdmi.core.restrpc.RestClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext-test*.xml" })
@TransactionConfiguration(defaultRollback = false)
public abstract class AbstractSpringTest extends AbstractJUnit4SpringContextTests {

	protected static UserResponse response;

	protected static RestUserloginRsp loginUser;
	
	@Resource
	private RestClient  uamClientService;

	@Before
	public void userLogin() throws ClientException {
		if (loginUser != null) {
			Assert.isTrue(true);
			return;
		}
		RestUserloginRequest request = new RestUserloginRequest();
		request.setAppName("");
		request.setDeviceAddress("10.66.192.152");
		request.setDeviceAgent("API");
		request.setDeviceName("HttpClient");
		request.setDeviceOS("Win");
		request.setDeviceSn("1234566");
		request.setDeviceType(1);
		request.setLoginName("w00186884");
		request.setPassword("22312wh@)!$");

		loginUser = new UserTestHttpClient().executePut("http://10.169.37.104:8080/enterprise/api/v1/auth/login", request,
				RestUserloginRsp.class);

		Assert.notNull(loginUser);
		Assert.notNull(loginUser.getToken());
		Assert.notNull(loginUser.getUserId());
	}

	protected void setSimpleProperties(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		for (Field field : fields) {
			String methodName = "set" + StringUtils.capitalize(field.getName());
			try {
				Method method = o.getClass().getMethod(methodName, field.getType());
				Random r = new Random();
				int value = r.nextInt(100);
				if ("String".equals(field.getType().getSimpleName())) {
					method.invoke(o, String.valueOf(value / 10));
				} else if ("Long".equals(field.getType().getSimpleName()) && !("id".equals(field.getName()))) {
					method.invoke(o, Long.valueOf(value));
				} else if ("Integer".equals(field.getType().getSimpleName()) && !("id".equals(field.getName()))) {
					method.invoke(o, Integer.valueOf(value));
				} else if ("Calendar".equals(field.getType().getSimpleName())) {
					method.invoke(o, Calendar.getInstance());
				}
			} catch (SecurityException e) {
				logger.warn("SecurityException: " + methodName + " method");
			} catch (NoSuchMethodException e) {
				logger.warn("NoSuchMethodException: " + methodName + " method");
			} catch (IllegalArgumentException e) {
				logger.warn("IllegalArgumentException: invoke method " + methodName + " ");
			} catch (IllegalAccessException e) {
				logger.warn("IllegalAccessException: invoke method " + methodName + " ");
			} catch (InvocationTargetException e) {
				logger.warn("InvocationTargetException: invoke method " + methodName + " ");
			}
		}
	}

}
