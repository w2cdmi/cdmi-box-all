package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

public class NodeACLDeleteTest extends BaseAPITest
{
    private static final String ACL_ADD_FILE = "testData/teamspace/addacl.txt";
    
    private boolean showResult = true;
    
    private Long teamspaceId = null;
    
    /**
     * 正常的删除
     * 
     * @throws Exception
     */
    @Test
    public void deleteACLNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceResult() + "/" + getACLId();
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    /**
     * 不存在的ownerId
     * 
     * @throws Exception
     */
    @Test
    public void deleteACLNoSuchOwnerid() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + 90000000 + "/" + getACLId();
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    /**
     * 不存在的acl
     * 
     * @throws Exception
     */
    @Test
    public void deleteACLNoSuchACL() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + getTeamSpaceResult() + "/" + 900000;
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_ACL, showResult);
    }
    
    /**
     * 不存在的acl
     * 
     * @throws Exception
     */
    @Test
    public void deleteACLNoSuchACL1() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + MyTestUtils.getTestCloudUserId1() + "/" + 1;
        HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "DELETE");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(false);
        openurl.connect();
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_ACL, showResult);
    }
    
    
    /**
     * 无效的ownerId
     * 
     * @throws Exception
     */
    @Test
    public void deleteACLInvalidOwner() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + -100000 + "/" + getACLId();
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.BAD_REQUEST, showResult);
    }
    
    /**
     * 无权限的ownerId
     * 
     * @throws Exception
     */
    @Test
    public void deleteACLOtherOwner() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + userId2 + "/" + getACLId();
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    /**
     * 创建一个文件夹A 然后对文件夹A添加访问控制，
     * 再删除文件夹A，
     * 并在回收站清除文件夹A，
     * 然后再去删除文件夹A的访问控制
     * @throws Exception 
     */
    @Test
    public void testFolderDeleteAndDeleteACL() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ACL", 0L);
        String bodyAdd = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "addAclReplace");
        bodyAdd = bodyAdd.replaceAll("#ownerId#", userId1 +"");
        bodyAdd = bodyAdd.replaceAll("#nodeId#", folderInfo.getId().toString());
        bodyAdd = bodyAdd.replaceAll("#id#", userId2 +"");
        bodyAdd = bodyAdd.replaceAll("#type#", "\"user\"");
        bodyAdd = bodyAdd.replaceAll("#role#", "\"editor\" ");
        System.out.println(bodyAdd);
        long addId = getACLId(bodyAdd,MyTestUtils.getTestUserToken2());
        System.out.println("ID:"+addId);
        
        deleteNode(userId1, folderInfo.getId());
        String trashUrl = MyTestUtils.SERVER_URL_UFM_V2 + "trash/" + userId1 + "/" + folderInfo.getId();
        HttpURLConnection connection = getConnection(trashUrl, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
        System.out.println("回收站清除该节点。");
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl/" + userId2 + "/" + addId;
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    private long getACLId(String body,String token) throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            token,
            body);
        return TeamSpaceUtil.getACLResult(openurl).getId();
    }
    
    private long getACLId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "acl";
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(ACL_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            replaceOwnerId(bodyStr, getTeamSpaceResult()));
        return TeamSpaceUtil.getACLResult(openurl).getId();
    }
    
    private String replaceOwnerId(String bodyString, Long ownerid)
    {
        bodyString = bodyString.replaceAll("#teamSpaceId#", ownerid.toString());
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
