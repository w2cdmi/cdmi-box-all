package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceAPIChangeOwnerTest extends BaseAPITest
{
    private static final String TEAMSPACE_CHANGE_OWNER_FILE = "testData/teamspace/changeowner.txt";
    
    private boolean showResult = true;
    
    @Test
    public void testChangeOwnerNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult() + "/changeowner";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_CHANGE_OWNER_FILE, "normal");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, MyTestUtils.getAppAuthorization(dateStr), dateStr, replaceUserId(bodyStr, userId2));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    
    private String replaceUserId(String bodyString, Long userId)
    {
        bodyString = bodyString.replaceAll("#userId#", userId.toString());
        return bodyString;
    }
    
    private Long getTeamSpaceResult() throws Exception
    {
        return createUserTeamSpace1().getId();
    }
    
}
