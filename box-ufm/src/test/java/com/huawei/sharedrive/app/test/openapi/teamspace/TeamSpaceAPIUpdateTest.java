package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceAPIUpdateTest extends BaseAPITest
{
    private Long teamSpaceId = null;
    
    private static final String SPACE_MODIFY_FILE = "testData/teamspace/modifyspace.txt";
    
    private boolean showResult = true;
    
    @Test
    public void testAppModifyTeamSapceNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "normal");
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        TeamSpaceUtil.checkTeamSpaceResult(openurl, bodyStr);
    }
    
    @Test
    public void testModifyTeamSpaceNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "normal");
        String dateStr = MyTestUtils.getDateString();
//        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        TeamSpaceUtil.checkTeamSpaceResult(openurl, bodyStr);
    }
    
    
    @Test
    public void testModifyTeamSpaceOnlystatus() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "onlystatus");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceAbnormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);

        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "abnormal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        TeamSpaceUtil.checkTeamSpaceResult(openurl, bodyStr);
//        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceNoSuchSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + 90000;
        System.out.println("url is " + urlString);
        
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_TEAMSPACE, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceRepeat() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "repeat");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceNoname() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "noName");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceEmptyname() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "emptyName");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceNoDescp() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "nodescp");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceEmptyDescp() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "emptydescp");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceNoSize() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "nosize");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceNoStatus() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "nostatus");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceInvalidSize() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "invalidsize");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        
    }
    
    @Test
    public void testModifyTeamSpaceZeroSize() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "zerosize");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        
    }
    
    @Test
    public void testModifyTeamSpaceInvalidStatus() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "invalidstatus");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        
    }
    
    @Test
    public void testModifyTeamSpaceLongName() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "longname");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceLongNameLt255() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "longname1");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
        
    }
    
    @Test
    public void testModifyTeamSpaceNoBody() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "nobody");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
        
    }
    
    @Test
    public void testModifyTeamSpaceSmallSize() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "smallsize");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testModifyTeamSpaceNoRight() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + createUserTeamSpace2().getId();
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getTestUserToken1(), bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testAdminModifyTeamSpace()
    {
        
    }
    
    private Long getTeamSpaceId() throws Exception
    {
        if (teamSpaceId != null)
        {
            return teamSpaceId;
        }
        teamSpaceId = createUserTeamSpace1().getId();
        return teamSpaceId;
    }
}
