package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;

public class UserAPIUpdateUserTest extends BaseAPITest
{
    /**
     * 格式化时间
     * 
     * @param pattern string类型
     * @param d 日期
     * @param timeZone Sting类型
     * @return sDate 格式化后的时间
     */
    public static String dataToString(String pattern, Date d, String timeZone)
    {
        DateFormat dFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        if (timeZone != null && timeZone.length() > 0)
        {
            Calendar calendar = Calendar.getInstance(new SimpleTimeZone(0, timeZone));
            dFormat.setCalendar(calendar);
        }
        String sDate = dFormat.format(d);
        return sDate;
    }
    
    @Test
    public void testUpdateStatus() throws Exception
    {
        String url = buildUrl(userId1);
        String body = generalBody();
        Date date = new Date();
        String dateStr = dataToString(DateUtils.RFC822_DATE_FORMAT, date, null);
        String authorization = MyTestUtils.getAppAuthorization(dateStr);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, authorization,dateStr, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    private String buildUrl(long ownerId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "users/" + ownerId;
    }
    
    private String generalBody()
    {
        RestUserCreateRequest user = new RestUserCreateRequest();
        Byte disable = 0;
        user.setStatus(disable);
        return JsonUtils.toJson(user);
    }
}
