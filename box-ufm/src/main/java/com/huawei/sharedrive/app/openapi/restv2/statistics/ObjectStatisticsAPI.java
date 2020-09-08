package com.huawei.sharedrive.app.openapi.restv2.statistics;

import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.SystemTokenHelper;
import com.huawei.sharedrive.app.openapi.checker.RegionChecker;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectCurrentStatisticsResponse;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectHistoryStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectHistoryStatisticsResponse;
import com.huawei.sharedrive.app.statistics.manager.StatisticsManager;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;

@Controller
@RequestMapping(value = "/api/v2/statistics/objects")
@Api(hidden = true)
public class ObjectStatisticsAPI {

    @Autowired
    private RegionService regionService;

    @Autowired
    private StatisticsManager statisticsManager;

    @Autowired
    private SystemTokenHelper systemTokenHelper;

    @Autowired
    private FileBaseService fileBaseService;

    @RequestMapping(value = "/current", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getCurrentInfo(@RequestHeader("Authorization") String authorization,
                                            @RequestBody ObjectCurrentStatisticsRequest statisticsRequest, @RequestHeader("Date") String date)
            throws ParseException {
        this.checkGetCurrentInfoPara(statisticsRequest);
        ObjectCurrentStatisticsResponse result;
        UserToken userToken = getUserToken(authorization);
        try {
            systemTokenHelper.checkSystemToken(authorization, date);

            result = statisticsManager.getCurrentObjectStatistics(statisticsRequest);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_STATISTIC_OBJECT_CURRENT_ERR,
                    null,
                    null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_STATISTIC_OBJECT_CURRENT,
                null,
                null);
        return new ResponseEntity<ObjectCurrentStatisticsResponse>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/history", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getHistoryInfo(@RequestHeader("Authorization") String authorization,
                                            @RequestBody ObjectHistoryStatisticsRequest restStatistiscRequest, @RequestHeader("Date") String date)
            throws ParseException {
        checkHistroyRequest(restStatistiscRequest);
        ObjectHistoryStatisticsResponse result;
        UserToken userToken = getUserToken(authorization);
        try {
            systemTokenHelper.checkSystemToken(authorization, date);
            result = statisticsManager.getHistoryObjectStatistics(restStatistiscRequest);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_STATISTIC_OBJECT_HISTORY_ERR,
                    null,
                    null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_STATISTIC_OBJECT_HISTORY,
                null,
                null);
        return new ResponseEntity<ObjectHistoryStatisticsResponse>(result, HttpStatus.OK);
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

    private void checkGetCurrentInfoPara(ObjectCurrentStatisticsRequest request) {
        request.checkGroupBy();
        RegionChecker.checkAppIdAllowEmpty(request.getRegionId(), regionService);
    }

    private void checkHistroyRequest(ObjectHistoryStatisticsRequest request) {
        if (null == request.getBeginTime()) {
            throw new InvalidParamException("null beginTime");
        }
        if (null == request.getEndTime()) {
            request.setEndTime(new Date().getTime());
        }
        if (request.getBeginTime() > request.getEndTime()) {
            throw new InvalidParamException("beginTime larger than endTime");
        }
        request.checkInterval();
        RegionChecker.checkAppIdAllowEmpty(request.getRegionId(), regionService);
    }

}
