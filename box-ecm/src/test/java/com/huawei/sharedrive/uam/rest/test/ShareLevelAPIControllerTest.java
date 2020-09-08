package com.huawei.sharedrive.uam.rest.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.uam.rest.test.util.Constants;

public class ShareLevelAPIControllerTest {


	@Test
	public void testListShareLevel() {

		String urlString = Constants.SERVER_RUL + "/api/v2/shareLevel/items";
		String result = "";
		BufferedReader reader = null;
		try {
			URL url = null;
			url = new URL(urlString);
			HttpURLConnection openurl = null;
			openurl = (HttpURLConnection) url.openConnection();
			openurl.setRequestProperty("x-device-type", "ios");
			openurl.setRequestProperty("x-device-sn", "111");
			openurl.setRequestProperty("x-device-os", "MIUI 2.3.5");
			openurl.setRequestProperty("x-device-name", "xiaomi-one");
			openurl.setRequestProperty("x-client-version", "V1.1");
			openurl.setRequestMethod("GET");
			// openurl.setRequestProperty("Content-type", "application/json");
			openurl.setDoInput(true);
			openurl.setDoOutput(true);
			openurl.connect();
			// 往服务器里面发送数据

			if (openurl.getResponseCode() == 200) {
				reader = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
				result = reader.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(result);
	}
	
	
	@Test
	public void testGetShareLevel() {

		String urlString = Constants.SERVER_RUL + "/api/v2/shareLevel/2";
		String result = "";
		BufferedReader reader = null;
		try {
			URL url = null;
			url = new URL(urlString);
			HttpURLConnection openurl = null;
			openurl = (HttpURLConnection) url.openConnection();
			openurl.setRequestProperty("x-device-type", "ios");
			openurl.setRequestProperty("x-device-sn", "111");
			openurl.setRequestProperty("x-device-os", "MIUI 2.3.5");
			openurl.setRequestProperty("x-device-name", "xiaomi-one");
			openurl.setRequestProperty("x-client-version", "V1.1");
			openurl.setRequestMethod("GET");
			// openurl.setRequestProperty("Content-type", "application/json");
			openurl.setDoInput(true);
			openurl.setDoOutput(true);
			openurl.connect();
			// 往服务器里面发送数据

			if (openurl.getResponseCode() == 200) {
				reader = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
				result = reader.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(result);
	}

}
