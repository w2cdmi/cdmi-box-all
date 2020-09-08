package com.huawei.sharedrive.app.openapi.restv2.statistics;

import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.SystemTokenHelper;
import com.huawei.sharedrive.app.openapi.checker.ApplicationChecker;
import com.huawei.sharedrive.app.openapi.checker.RegionChecker;
import com.huawei.sharedrive.app.openapi.domain.statistics.*;
import com.huawei.sharedrive.app.statistics.manager.StatisticsManager;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/v2/statistics/users")
@Api(hidden = true)
public class UserStatisticsAPI {

    @Autowired
    private StatisticsManager statisticsManager;

    @Autowired
    private SystemTokenHelper systemTokenHelper;

    @Autowired
    private FileBaseService fileBaseService;

    @RequestMapping(value = "/current", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getCurrentInfo(@RequestHeader("Authorization") String authorization,
                                            @RequestBody RestUserCurrentStatisticsRequest restStatistiscRequest,
                                            @RequestHeader("Date") String date) {
        checkCurrentRequest(restStatistiscRequest);
        UserCurrentStatisticsList statistics;
        UserToken userToken = getUserToken(authorization);
        try {
            systemTokenHelper.checkSystemToken(authorization, date);

            statistics = statisticsManager.getUserCurrentStatistics(restStatistiscRequest);
        } catch (BaseRunException e) {
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_STATISTIC_USER_CURRENT_ERR,
                    null,
                    null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_STATISTIC_USER_CURRENT,
                null,
                null);
        return new ResponseEntity<UserCurrentStatisticsList>(statistics, HttpStatus.OK);
    }

    @RequestMapping(value = "/history", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getHistoryInfo(@RequestHeader("Authorization") String authorization,
                                            @RequestBody RestUserHistoryStatisticsRequest restStatistiscRequest,
                                            @RequestHeader("Date") String date) {
        checkHistoryRequest(restStatistiscRequest);
        UserHistoryStatisticsList statistics;
        UserToken userToken = getUserToken(authorization);
        try {
            systemTokenHelper.checkSystemToken(authorization, date);

            statistics = statisticsManager.getUserHistoryStatistics(restStatistiscRequest);
        } catch (BaseRunException e) {
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_STATISTIC_USER_HISTORY_ERR,
                    null,
                    null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_STATISTIC_USER_HISTORY_ERR,
                null,
                null);
        return new ResponseEntity<UserHistoryStatisticsList>(statistics, HttpStatus.OK);
    }

    @RequestMapping(value = "/cluster", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getCluster(@RequestHeader("Authorization") String authorization,
                                        @RequestBody RestUserClusterStatisticsRequest restStatistiscRequest,
                                        @RequestHeader("Date") String date) {
        UserClusterStatisticsList statistics;
        UserToken userToken = getUserToken(authorization);
        try {
            systemTokenHelper.checkSystemToken(authorization, date);
            restStatistiscRequest.checkParameter();
            statistics = statisticsManager.getUserClusterStatistics(restStatistiscRequest);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_STATISTIC_CLUSTER_ERR,
                    null,
                    null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_STATISTIC_CLUSTER,
                null,
                null);
        return new ResponseEntity<UserClusterStatisticsList>(statistics, HttpStatus.OK);
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

    private void checkCurrentRequest(RestUserCurrentStatisticsRequest request) {
        request.checkParameter();
        ApplicationChecker.checkAppIdAllowEmpty(request.getAppId(), authAppService);
        RegionChecker.checkAppIdAllowEmpty(request.getRegionId(), regionService);
    }

    private void checkHistoryRequest(RestUserHistoryStatisticsRequest request) {
        request.checkParameter();
        ApplicationChecker.checkAppIdAllowEmpty(request.getAppId(), authAppService);
        RegionChecker.checkAppIdAllowEmpty(request.getRegionId(), regionService);
    }

    @Autowired
    private RegionService regionService;

    @Autowired
    private AuthAppService authAppService;

}
