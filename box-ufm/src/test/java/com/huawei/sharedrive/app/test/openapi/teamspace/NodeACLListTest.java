package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class NodeACLListTest extends BaseAPITest
{
    private static final String ACL_ADD_FILE = "testData/teamspace/addacl.txt";
    
    private static final String ACL_LIST_FILE = "testData/teamspace/listacl.txt";
    
    private boolean showResult = true;
    
    private Long teamspaceId = null;
    
    private Long aclId = null;
    
    /**
     * 正常列举acl
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsNormal() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    
    /**
     * 无效的nodeid
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsInvalidNodeId() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "invalidnodeId");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 无效的limit
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsInvalidLimit() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceId();
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "invalidlimit");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 无效的offset
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsInvalidOffset() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceId();
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "invalidoffset");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 无nodeid
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsNoNodeId() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceId();
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "nonodeId");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    /**
     * 无limit
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsNoLimit() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceId();
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "nolimit");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    /**
     * 无offet
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsNoOffset() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceId();
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "nolimit");
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, getTeamSpaceId()).getBytes());
        
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    /**
     * 不存在的node
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsNoSuchNode() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceId();
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "nosuchnode");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    /**
     * 不存在的ownerId
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsNoSuchOwner() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + 900000;
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    /**
     * 无效的ownerId
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsInvalidOwner() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + -20000;
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 无权限的ownerId
     * 
     * @throws Exception
     */
    @Test
    public void listNodeACLsOtherOwner() throws Exception
    {
        addACL();
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + userId2;
        String bodyStr = MyFileUtils.getDataFromFile(ACL_LIST_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    private String replaceOwnerId(String bodyString, Long ownerid)
    {
        bodyString = bodyString.replaceAll("#teamSpaceId#", ownerid.toString());
        return bodyString;
    }
    
    private Long getTeamSpaceId() throws Exception
    {
        if (teamspaceId != null)
        {
            return teamspaceId;
        }
        
        teamspaceId = createUserTeamSpace1().getId();
        return teamspaceId;
    }
    
    private void addACL() throws Exception
    {
        if (aclId != null)
        {
            return;
        }
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceId()));
        aclId = TeamSpaceUtil.getACLResult(openurl).getId();
    }
    
    private String replaceUserId(String bodyString, Long userId)
    {
        bodyString = bodyString.replaceAll("#userId#", userId.toString());
        return bodyString;
    }
}
