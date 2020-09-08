package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import junit.framework.Assert;
import pw.cdmi.common.log.syslog.exception.SyslogException;

import com.google.common.net.InetAddresses;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestNodeACLInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberList;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceModifyRequest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

public class TeamSpaceUtil
{
    private static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";
    
    public static RestTeamSpaceInfo getTeamSpaceResult(HttpURLConnection openurl) throws Exception
    {
        InputStream stream = null;
        BufferedReader in = null;
        StringBuilder sb = null;
        String userInfoString = null;
        
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            sb = new StringBuilder();
            String re = null;
            while ((re = in.readLine()) != null)
            {
                sb.append(re);
            }
            userInfoString = sb.toString();
        }
        catch (Exception e)
        {
            stream = openurl.getErrorStream();
            if (null == stream)
            {
                System.err.println("There is not any return data.");
                e.printStackTrace();
            }
            else
            {
                in = new BufferedReader(new InputStreamReader(stream));
                System.out.println("Error return data is------------------------------------------");
                System.err.println(in.readLine());
            }
        }
        finally
        {
            openurl.disconnect();
        }
        System.out.println(userInfoString);
        RestTeamSpaceInfo result = JSonUtils.stringToObject(userInfoString, RestTeamSpaceInfo.class);
        return result;
    }
    
    public static RestTeamMemberInfo getTeamMemberResult(HttpURLConnection openurl) throws Exception
    {
        InputStream stream = null;
        BufferedReader in = null;
        StringBuilder sb = null;
        String userInfoString = null;
        
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            sb = new StringBuilder();
            String re = null;
            while ((re = in.readLine()) != null)
            {
                sb.append(re);
            }
            userInfoString = sb.toString();
        }
        catch (Exception e)
        {
            stream = openurl.getErrorStream();
            if (null == stream)
            {
                System.err.println("There is not any return data.");
                e.printStackTrace();
            }
            else
            {
                in = new BufferedReader(new InputStreamReader(stream));
                System.out.println("Error return data is------------------------------------------");
                System.err.println(in.readLine());
            }
        }
        finally
        {
            openurl.disconnect();
        }
        System.out.println(userInfoString);
        RestTeamMemberInfo result = JSonUtils.stringToObject(userInfoString, RestTeamMemberInfo.class);
        return result;
    }
    
    public static RestTeamMemberList getTeamMemberListResult(HttpURLConnection openurl) throws Exception
    {
        InputStream stream = null;
        BufferedReader in = null;
        StringBuilder sb = null;
        String userInfoString = null;
        
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            sb = new StringBuilder();
            String re = null;
            while ((re = in.readLine()) != null)
            {
                sb.append(re);
            }
            userInfoString = sb.toString();
        }
        catch (Exception e)
        {
            stream = openurl.getErrorStream();
            if (null == stream)
            {
                System.err.println("There is not any return data.");
                e.printStackTrace();
            }
            else
            {
                in = new BufferedReader(new InputStreamReader(stream));
                System.out.println("Error return data is------------------------------------------");
                System.err.println(in.readLine());
            }
        }
        finally
        {
            openurl.disconnect();
        }
        System.out.println(userInfoString);
        RestTeamMemberList result = JSonUtils.stringToObject(userInfoString, RestTeamMemberList.class);
        return result;
    }
    
    public static RestNodeACLInfo getACLResult(HttpURLConnection openurl) throws Exception
    {
        InputStream stream = null;
        BufferedReader in = null;
        StringBuilder sb = null;
        String userInfoString = null;
        
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            sb = new StringBuilder();
            String re = null;
            while ((re = in.readLine()) != null)
            {
                sb.append(re);
            }
            userInfoString = sb.toString();
        }
        catch (Exception e)
        {
            stream = openurl.getErrorStream();
            if (null == stream)
            {
                System.err.println("There is not any return data.");
                e.printStackTrace();
            }
            else
            {
                in = new BufferedReader(new InputStreamReader(stream));
                System.out.println("Error return data is------------------------------------------");
                System.err.println(in.readLine());
            }
        }
        finally
        {
            openurl.disconnect();
        }
        System.out.println(userInfoString);
        RestNodeACLInfo result = JSonUtils.stringToObject(userInfoString, RestNodeACLInfo.class);
        return result;
    }
    
    public static void checkTeamSpaceResult(HttpURLConnection openurl, String bodyStr) throws Exception
    {
        RestTeamSpaceModifyRequest info = JSonUtils.stringToObject(bodyStr, RestTeamSpaceModifyRequest.class);
        InputStream stream = null;
        BufferedReader in = null;
        StringBuilder sb = null;
        String userInfoString = null;
        
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            sb = new StringBuilder();
            String re = null;
            while ((re = in.readLine()) != null)
            {
                sb.append(re);
            }
            userInfoString = sb.toString();
        }
        catch (Exception e)
        {
            stream = openurl.getErrorStream();
            if (null == stream)
            {
                System.err.println("There is not any return data.");
                e.printStackTrace();
            }
            else
            {
                in = new BufferedReader(new InputStreamReader(stream));
                System.out.println("Error return data is------------------------------------------");
                System.err.println(in.readLine());
            }
        }
        finally
        {
            openurl.disconnect();
        }
        System.out.println(userInfoString);
        RestTeamSpaceInfo result = JSonUtils.stringToObject(userInfoString, RestTeamSpaceInfo.class);
        Assert.assertEquals(result.getName(), info.getName());
        Assert.assertEquals(result.getDescription(), info.getDescription());
        // Assert.assertEquals((int)result.getSpaceQuota(), (int)info.getSpaceQuota());
        Assert.assertEquals((int) result.getStatus(), (int) info.getStatus());
    }
    
    public static Long getOtherTeamSpaceId(String testKey) throws Exception
    {
        if (StringUtils.isBlank(testKey))
        {
            testKey = "normalstatus";
        }
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken2());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, testKey);
        openurl.getOutputStream().write(bodyStr.getBytes());
        
        return getTeamSpaceResult(openurl).getId();
    }
    
    @Test
    public void test()
    {
        DatagramSocket socket = null;
        try
        {
            socket = new DatagramSocket();
        }
        catch (SocketException se)
        {
            throw new SyslogException(se);
        }
        
        InetAddress hostAddress =InetAddresses.forString("100");
        
        DatagramPacket packet = new DatagramPacket("ssss".getBytes(), "ssss".getBytes().length, hostAddress,
            10000);
        
        int attempts = 0;
        int retryConf = 3;
        while (attempts != -1 && attempts < retryConf)
        {
            attempts = doWrite(socket, packet, attempts, retryConf);
        }
    }
    
    private int doWrite(DatagramSocket socket, DatagramPacket packet, int attempts, int retryConf)
    {
        int attempted = attempts;
        try
        {
            socket.send(packet);
            attempted = -1;
            
        }
        catch (IOException ioe)
        {
            if (attempted == retryConf)
            {
                throw new SyslogException(ioe);
            }
        }
        return attempted;
    }
}
