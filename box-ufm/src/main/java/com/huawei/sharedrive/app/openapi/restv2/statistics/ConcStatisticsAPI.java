package com.huawei.sharedrive.app.openapi.restv2.statistics;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.SystemTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.statistics.concurrence.ConcStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.concurrence.ConcStatisticsResponse;
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
@RequestMapping(value = "/api/v2/statistics/concurrence")
@Api(hidden = true)
public class ConcStatisticsAPI {

    @Autowired
    private StatisticsManager statisticsManager;

    @Autowired
    private SystemTokenHelper systemTokenHelper;

    @Autowired
    private FileBaseService fileBaseService;

    @RequestMapping(value = "/history", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getHistoryInfo(@RequestHeader("Authorization") String authorization,
                                            @RequestBody ConcStatisticsRequest restStatistiscRequest, @RequestHeader("Date") String date)
            throws ParseException {
        checkHistroyRequest(restStatistiscRequest);
        systemTokenHelper.checkSystemToken(authorization, date);
        ConcStatisticsResponse result = null;
        UserToken userToken = getUserToken(authorization);
        try {
            result = statisticsManager.getHistoryConcStatistics(restStatistiscRequest);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_SYSTEM_CONCURRENCE_HISTORY_ERR,
                    null,
                    null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_SYSTEM_CONCURRENCE_HISTORY,
                null,
                null);
        return new ResponseEntity<ConcStatisticsResponse>(result, HttpStatus.OK);
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

    private void checkHistroyRequest(ConcStatisticsRequest request) {
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
    }

}
