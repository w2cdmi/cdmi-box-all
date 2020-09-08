package com.huawei.sharedrive.app.test.openapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BaseConnection
{
    
    public static HttpURLConnection getURLConnection(String urlStr, String method)
    {
        
        if (urlStr == null || urlStr.equals(""))
        {
            return null;
        }
        
        trustAllHosts();
        
        URL url;
        try
        {
            url = new URL(urlStr);
            if (isHttps(urlStr))
            {
                HttpsURLConnection urlConn1 = (HttpsURLConnection) url.openConnection();
                
                urlConn1.setHostnameVerifier(new TrustAnyHostnameVerifier());
                urlConn1.setRequestMethod(method);
                return urlConn1;
            }
            else
            {
                HttpURLConnection urlConn2 = (HttpURLConnection) url.openConnection();
                urlConn2.setRequestMethod(method);
                return urlConn2;
            }
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private static void trustAllHosts()
    {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
        {
            public java.security.cert.X509Certificate[] getAcceptedIssuers()
            {
                return new java.security.cert.X509Certificate[]{};
            }
            
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException
            {
                // TODO Auto-generated method stub
                
            }
        }};
        
        try
        {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private static boolean isHttps(String urlStr)
    {
        if (urlStr.startsWith("https"))
        {
            return true;
        }
        return false;
    }
    
    public static void printBody(HttpURLConnection conn) throws IOException
    {
        System.out.println(conn.getResponseCode());
        
        if (conn.getResponseCode() / 100 == 2)
        {
            printBody(conn, false);
        }
        else
        {
            printBody(conn, true);
        }
    }
    
    public static void writeFile(HttpURLConnection conn, File f) throws IOException
    {
        System.out.println(conn.getResponseCode());
        
        if (conn.getResponseCode() / 100 == 2)
        {
            printfile(conn, f);
        }
        else
        {
            printBody(conn, true);
        }
    }
    
    public static void printfile(HttpURLConnection conn, File f) throws IOException
    {
        InputStream in = null;
        
        in = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(in);
        OutputStreamWriter osr = new OutputStreamWriter(new FileOutputStream(f));
        BufferedReader br = new BufferedReader(isr);
        String temp = null;
        while ((temp = br.readLine()) != null)
        {
            osr.write(temp);
        }
        isr.close();
        osr.close();
        
    }
    
    public static void printBody(HttpURLConnection conn, boolean isError) throws IOException
    {
        InputStream in = null;
        if (isError)
        {
            in = conn.getErrorStream();
            ;
        }
        else
        {
            in = conn.getInputStream();
        }
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);
        String temp = null;
        StringBuffer sb = new StringBuffer("");
        while ((temp = br.readLine()) != null)
        {
            sb.append(temp);
        }
        
        System.out.println("Body: " + sb.toString());
    }
    
    static class TrustAnyHostnameVerifier implements HostnameVerifier
    {
        public boolean verify(String hostname, SSLSession session)
        {
            // 直接返回true
            return true;
        }
    }
}
