package com.huawei.sharedrive.uam.test;

import org.junit.Test;

import pw.cdmi.core.utils.IpUtils;

public class IpUtilsTest {

	@Test
	public void test() {
		System.out.println(IpUtils.toLong("10.169.1.1"));
		System.out.println(IpUtils.toLong("10.169.255.254"));
		
		System.out.println(IpUtils.isIPv4LiteralAddress("10.1.1.1"));
	}

}
