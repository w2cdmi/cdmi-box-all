package com.huawei.sharedrive.app.openapi.restv2.statistics;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.AuthFailedException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestStatisticsRequest;
import com.huawei.sharedrive.app.statistics.domain.AppStatisticsInfo;
import com.huawei.sharedrive.app.statistics.service.StatisticsService;
import com.huawei.sharedrive.app.user.domain.User;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/v2/statistics")
@Api(hidden = true)
public class StatisticsAPI {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private FileBaseService fileBaseService;

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getStaticsInfo(@RequestHeader("Authorization") String authorization,
                                            @RequestBody RestStatisticsRequest restStatistiscRequest, @RequestHeader("Date") String date) {
        restStatistiscRequest.checkParameter();
        User user = new User();
        AppStatisticsInfo statistics;
        UserToken userToken = getUserToken(authorization);
        try {
            if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
                    || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX)) {
                Account account = userTokenHelper.checkAccountToken(authorization, date);
                user.setAccountId(account.getId());
                user.setAppId(account.getAppId());
                userToken.setAppId(account.getAppId());
                userToken.setId(account.getId());
            } else {
                throw new AuthFailedException("Bad app authorization: " + authorization);
            }

            statistics = statisticsService.getStatisticsInfo(restStatistiscRequest, user);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_STATISTIC_INFO_ERR,
                    null,
                    null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_STATISTIC_INFO,
                null,
                null);
        return new ResponseEntity<AppStatisticsInfo>(statistics, HttpStatus.OK);
    }

    private UserToken getUserToken(String authorization) {
        UserToken userToken = new UserToken();
        if (authorization == null) {
            userToken.setLoginName("");
            return userToken;
        }
        String[] akArray = authorization.split(",");
        userToken.setLoginName(akArray.length < 2 ? authorization : akArray[1]);
        return userToken;
    }
}
