package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceAPIGetInfoTest extends BaseAPITest
{
    private static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";

    private boolean showResult = true;
    @Test
    public void testGetTeamSpaceNormal() throws Exception
    {
        long teamspaceId = getTeamSpaceResult("normalstatus");
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamspaceId;
//        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET, MyTestUtils.getAppAuthorization(dateStr), dateStr, null);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testGetTeamSpaceNoRight() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + createUserTeamSpace2().getId();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testGetTeamSpaceStatusAbnormal() throws Exception
    {
        long teamspaceId = getTeamSpaceResult("abnormalstatus");
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamspaceId;
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testGetTeamSpaceNoSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/9000";
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_TEAMSPACE, showResult);
    }
    
    private Long getTeamSpaceResult(String testKey) throws Exception
    {
        if (StringUtils.isBlank(testKey))
        {
            testKey = "normalstatus";
        }
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, testKey);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        return TeamSpaceUtil.getTeamSpaceResult(openurl).getId();
    }
    
}
