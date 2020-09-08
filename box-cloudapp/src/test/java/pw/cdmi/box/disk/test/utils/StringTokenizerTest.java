package pw.cdmi.box.disk.test.utils;

import org.junit.Test;

import pw.cdmi.box.disk.utils.StringTokenizerUtils;

public class StringTokenizerTest {
	
	@Test
	public void test(){
		String key = "wuzhiyuan\nabc;ddd,ccc；asd\n ;中文";
		System.out.println(StringTokenizerUtils.wsString(key));
	}
}
