package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceAPIDeleteTest extends BaseAPITest
{
    private static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";

    private boolean showResult = true;
    
    @Test
    public void testDeleteTeamSpaceNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "normalstatus");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + TeamSpaceUtil.getTeamSpaceResult(openurl).getId();
        HttpURLConnection openurl11 = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assert200(openurl11, showResult);
    }
    
    @Test
    public void testDeleteTeamSpaceStatusAbnormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "abnormalstatus");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + TeamSpaceUtil.getTeamSpaceResult(openurl).getId();
        HttpURLConnection openurl11 = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assert200(openurl11, showResult);
    }
    
    @Test
    public void testDeleteTeamSpaceNoSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/9000";
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_TEAMSPACE, showResult);
    }
    
    @Test
    public void testDeleteTeamSpaceNoRight() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + createUserTeamSpace2().getId();
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
}
