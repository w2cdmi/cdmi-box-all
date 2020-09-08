package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class NodeACLUpdateTest extends BaseAPITest
{
    private static final String ACL_ADD_FILE = "testData/teamspace/addacl.txt";
    
    private static final String ACL_MODIFY_FILE = "testData/teamspace/modifyacl.txt";
    
    private boolean showResult = true;
    
    private Long teamspaceId = null;
    
    private Long aclId = null;
    
    
    /**
     * 正常更新测试
     * 
     * @throws Exception
     */
    @Test
    public void modifyACLNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceResult() + "/" + getACLId();
        String bodyStr = MyFileUtils.getDataFromFile(ACL_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_PUT,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    
    /**
     * 重复更新
     * 
     * @throws Exception
     */
    @Test
    public void modifyACLRepeat() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceResult() + "/" + getACLId();
        
        String bodyStr = MyFileUtils.getDataFromFile(ACL_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_PUT,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    
    /**
     * 无role
     * 
     * @throws Exception
     */
    @Test
    public void modifyACLNoRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceResult() + "/" + getACLId();
       
        String bodyStr = MyFileUtils.getDataFromFile(ACL_MODIFY_FILE, "norole");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_PUT,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 无效role
     * 
     * @throws Exception
     */
    @Test
    public void modifyACLInvalidRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceResult() + "/" + getACLId();
        
        String bodyStr = MyFileUtils.getDataFromFile(ACL_MODIFY_FILE, "invalidrole");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_PUT,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_RESOURCE_ROLE, showResult);
    }
    
    
    /**
     * 不存在的ownerId
     * 
     * @throws Exception
     */
    @Test
    public void modifyACLNoSuchOwnerid() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + 90000000 + "/" + getACLId();
        
        String bodyStr = MyFileUtils.getDataFromFile(ACL_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_PUT,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    /**
     * 不存在的acl
     * 
     * @throws Exception
     */
    @Test
    public void modifyACLNoSuchACL() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceResult() + "/" + 900000;
        
        String bodyStr = MyFileUtils.getDataFromFile(ACL_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_PUT,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_ACL, showResult);
    }
    
    
    /**
     * 无效的ownerId
     * 
     * @throws Exception
     */
    @Test
    public void modifyACLInvalidOwner() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + -100000 + "/" + getACLId();
       
        String bodyStr = MyFileUtils.getDataFromFile(ACL_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_PUT,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.BAD_REQUEST, showResult);
    }
    
    
    /**
     * 其他的ownerId
     * 
     * @throws Exception
     */
    @Test
    public void modifyACLOtherOwner() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + userId2 + "/" + getACLId();
       
        String bodyStr = MyFileUtils.getDataFromFile(ACL_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_PUT,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult())); 
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    private long getACLId() throws Exception
    {
        if (aclId != null)
        {
            return aclId;
        }
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        aclId = TeamSpaceUtil.getACLResult(openurl).getId();
        return aclId;
    }
    
    private String replaceOwnerId(String bodyString, Long ownerid)
    {
        bodyString = bodyString.replaceAll("#teamSpaceId#", ownerid.toString());
        System.out.println(bodyString);
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
    
    private String replaceUserId(String bodyString, Long userId)
    {
        bodyString = bodyString.replaceAll("#userId#", userId.toString());
        return bodyString;
    }
}
