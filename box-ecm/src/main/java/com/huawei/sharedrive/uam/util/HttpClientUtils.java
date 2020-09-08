/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import pw.cdmi.core.utils.JsonUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>对HTTP访问进行简单的封装</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/28
 ************************************************************/
public class HttpClientUtils {
    public static String httpPostWithNameValuePair(String url, Map<String, String> map) {
        try {
            PostMethod post = new PostMethod(url);

            if(map != null && !map.isEmpty()) {
                NameValuePair[] pairs = new NameValuePair[map.size()];

                int i = 0;
                for(String key : map.keySet()) {
                    pairs[i++] = new NameValuePair(key, map.get(key));
                }

                post.setRequestBody(pairs);
            }

            HttpClient client = new HttpClient();
            client.executeMethod(post);

            return post.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String httpPostWithJsonBody(String url, Object o) {
        try {
            PostMethod post = new PostMethod(url);

            if(o != null) {
                String body = JsonUtils.toJson(o);
                post.setRequestEntity(new StringRequestEntity(body, null, "utf-8"));
            }

            HttpClient client = new HttpClient();
            client.executeMethod(post);

            return post.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String httpGet(String url) {
        try {
            HttpMethod post = new GetMethod(url);

            org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
            client.executeMethod(post);

            return post.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //下载byte[]数据
    public static byte[] downloadAsStream(String urlStr) {
        InputStream in = null;
        try {
            URL url = new URL(urlStr);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setHostnameVerifier(new HostnameVerifier(){
                                public boolean verify(String string,SSLSession ssls) {
                    return true;
                }
            });
            in = urlConnection.getInputStream();
            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }

        return null;
    }
}
