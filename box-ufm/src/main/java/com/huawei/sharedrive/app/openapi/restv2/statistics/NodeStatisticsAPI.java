package com.huawei.sharedrive.app.openapi.restv2.statistics;

import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.SystemTokenHelper;
import com.huawei.sharedrive.app.openapi.checker.ApplicationChecker;
import com.huawei.sharedrive.app.openapi.checker.RegionChecker;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeCurrentStatisticsResponse;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeHistoryStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeHistoryStatisticsResponse;
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
@RequestMapping(value = "/api/v2/statistics/nodes")
@Api(hidden = true)
public class NodeStatisticsAPI {

    @Autowired
    private AuthAppService authAppService;

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
                                            @RequestBody NodeCurrentStatisticsRequest statisticsRequest,
                                            @RequestHeader("Date") String date) throws ParseException {
        this.checkGetCurrentInfoPara(statisticsRequest);
        NodeCurrentStatisticsResponse result;
        UserToken userToken = getUserToken(authorization);
        try {
            systemTokenHelper.checkSystemToken(authorization, date);
            result = statisticsManager.getCurrentNodeStatistics(statisticsRequest);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_STATISTIC_NODE_CURRENT_ERR,
                    null,
                    null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_STATISTIC_NODE_CURRENT,
                null,
                null);
        return new ResponseEntity<NodeCurrentStatisticsResponse>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/history", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getHistoryInfo(@RequestHeader("Authorization") String authorization,
                                            @RequestBody NodeHistoryStatisticsRequest restStatistiscRequest, @RequestHeader("Date") String date) throws ParseException {
        checkHistroyRequest(restStatistiscRequest);
        NodeHistoryStatisticsResponse result;
        UserToken userToken = getUserToken(authorization);
        try {
            systemTokenHelper.checkSystemToken(authorization, date);
            result = statisticsManager.getHistoryNodeStatistics(restStatistiscRequest);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_STATISTIC_NODE_HISTORY_ERR,
                    null,
                    null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_STATISTIC_NODE_HISTORY,
                null,
                null);
        return new ResponseEntity<NodeHistoryStatisticsResponse>(result, HttpStatus.OK);
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

    private void checkGetCurrentInfoPara(NodeCurrentStatisticsRequest request) {
        request.checkGroupBy();
        ApplicationChecker.checkAppIdAllowEmpty(request.getAppId(), authAppService);
        RegionChecker.checkAppIdAllowEmpty(request.getRegionId(), regionService);
    }

    private void checkHistroyRequest(NodeHistoryStatisticsRequest request) {
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
        ApplicationChecker.checkAppIdAllowEmpty(request.getAppId(), authAppService);
        RegionChecker.checkAppIdAllowEmpty(request.getRegionId(), regionService);
    }

}
