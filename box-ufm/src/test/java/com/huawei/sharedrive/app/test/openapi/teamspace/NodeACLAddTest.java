package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * @author t00159390
 * 
 */
public class NodeACLAddTest extends BaseAPITest
{
    private static final String ACL_ADD_FILE = "testData/teamspace/addacl.txt";
    
    private boolean showResult = true;
    
    private Long teamspaceId = null;
    
    /**
     * 正常添加acl
     * 
     * @throws Exception
     */
    @Test
    public void addACLNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assert201(openurl, showResult);
    }
    
	   
    
    /**
     * 正常添加acl
     * 
     * @throws Exception
     */
    @Test
    public void addACLNormalOneNode() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normalNode");
        bodyStr = replaceUserId(bodyStr, MyTestUtils.getTestCloudUserId2());
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, getTeamSpaceResult()).getBytes());
        
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    /**
     * 正常添加自己资源的acl
     * 
     * @throws Exception
     */
    @Test
    public void addACLNormalMyself() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken2());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, MyTestUtils.getTestCloudUserId2());
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId2()).getBytes());
        
        MyResponseUtils.assert201(openurl, showResult);
    }
	
    /**
     * 重复添加，返回409
     * 
     * @throws Exception
     */
    @Test
    public void addACLRepeat() throws Exception
    {
        long teamId = getTeamSpaceResult();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, teamId));
        
        MyResponseUtils.assert201(openurl, showResult);
        
        bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl11 = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, teamId));
        MyResponseUtils.assertReturnCode(openurl11, ErrorCode.ACL_CONFLICT, showResult);
    }
    
    /**
     * 添加team acl
     * 
     * @throws Exception
     */
    @Test
    public void addACLNormalTeam() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normalTeam");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    /**
     * 添加team acl, 无userid
     * 
     * @throws Exception
     */
    @Test
    public void addACLNormalTeamNoUserId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normalTeamNoUserId");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    /**
     * team 和 ownerid不对应
     * 
     * @throws Exception
     */
    @Test
    public void addACLNormalTeamInvalid() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
       
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normalTeam");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, userId1));
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 添加其他用户
     * 
     * @throws Exception
     */
    @Test
    public void addACLNormalOtherUser() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
       
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, userId1));
        
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    
    /**
     * 重复添加team
     * 
     * @throws Exception
     */
    @Test
    public void addACLTeamRepeat() throws Exception
    {
        long teamId = getTeamSpaceResult();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normalTeam");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, teamId));
        MyResponseUtils.assert201(openurl, showResult);
        
        bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normalTeam");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl11 = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, teamId));
        
        MyResponseUtils.assertReturnCode(openurl11, ErrorCode.ACL_CONFLICT, showResult);
    }
    
    /**
     * 同时添加user 和team
     * 
     * @throws Exception
     */
    @Test
    public void addACLTeamUser() throws Exception
    {
        long teamId = getTeamSpaceResult();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, teamId));
        MyResponseUtils.assert201(openurl, showResult);
        
        bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normalTeam");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl11 = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, teamId));
        MyResponseUtils.assert201(openurl11, showResult);
    }
    
    /**
     * add System acl
     * 
     * @throws Exception
     */
    @Test
    public void addACLNormalSystem() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normalSystem");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    /**
     * 添加 public acl
     * 
     * @throws Exception
     */
    @Test
    public void addACLNormalPublic() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normalPublic");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    /**
     * 不带资源的请求
     * 
     * @throws Exception
     */
    @Test
    public void addACLNoResource() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "noresource");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求中无ownerId
     * 
     * @throws Exception
     */
    @Test
    public void addACLNoOwnerId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "noownerid");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求中无nodeId
     * 
     * @throws Exception
     */
    @Test
    public void addACLNoNodeId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "nonodeid");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, userId1));
        
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    /**
     * 请求中无user
     * 
     * @throws Exception
     */
    @Test
    public void addACLNoUser() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "nouser");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求中无userId
     * 
     * @throws Exception
     */
    @Test
    public void addACLNoUserId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "nouserid");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求中无userType
     * 
     * @throws Exception
     */
    @Test
    public void addACLNoUserType() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "nousertype");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求中无role
     * 
     * @throws Exception
     */
    @Test
    public void addACLNoRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "norole");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求无效ownerId
     * 
     * @throws Exception
     */
    @Test
    public void addACLInvalidOwnerId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "invalidownerid");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求无效nodeId
     * 
     * @throws Exception
     */
    @Test
    public void addACLInvalidNodeId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "invalidnodeid");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求无效userId
     * 
     * @throws Exception
     */
    @Test
    public void addACLInvalidUserId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "invaliduserid");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求无效userType
     * 
     * @throws Exception
     */
    @Test
    public void addACLInvalidUserType() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "invalidusertype");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 请求无效role
     * 
     * @throws Exception
     */
    @Test
    public void addACLInvalidRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "invalidrole");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_RESOURCE_ROLE, showResult);
    }
    
    /**
     * 请求不存在user
     * 
     * @throws Exception
     */
    @Test
    public void addACLNosuchUser() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "nosuchuser");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    private String replaceOwnerId(String bodyString, Long ownerid)
    {
        bodyString = bodyString.replaceAll("#teamSpaceId#", ownerid.toString());
        System.out.println(bodyString);
        return bodyString;
    }
    
    private String replaceUserId(String bodyString, Long userId)
    {
        bodyString = bodyString.replaceAll("#userId#", userId.toString());
        return bodyString;
    }
    
    private Long getTeamSpaceResult() throws Exception
    {
        if (teamspaceId != null)
        {
            return teamspaceId;
        }
        
        teamspaceId = createUserTeamSpace1().getId();
        return teamspaceId;
    }
    
}
