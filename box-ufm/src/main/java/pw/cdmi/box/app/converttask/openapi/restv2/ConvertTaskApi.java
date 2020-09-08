package pw.cdmi.box.app.converttask.openapi.restv2;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.exception.NoSuchParentException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.rest.FilesCommonApi;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.app.converttask.domain.ConvertInfo;
import pw.cdmi.box.app.converttask.openapi.domain.DeleteConvertRequest;
import pw.cdmi.box.app.converttask.openapi.domain.RetryConvertRequest;
import pw.cdmi.box.app.converttask.service.ConvertTaskService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;


/**
 * 文件夹API Rest接口, 提供当前转换进度的查询?历史转换记录查询和清除
 *
 * @author
 * @version CloudStor CSE Service Platform Subproject, 2016-6-22
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v2/convertTask")
public class ConvertTaskApi extends FilesCommonApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertTaskApi.class);
    @Autowired
    private ConvertTaskService convertTaskService;
    @Autowired
    private UserTokenHelper userTokenHelper;
    @Autowired
    private FileBaseService fileBaseService;

    @RequestMapping(value = "/doing/{ownerId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ConvertInfo> queryDoingTask(
        @PathVariable
    Long ownerId, @RequestHeader("Authorization")
    String token, @RequestHeader("spaceType")
    String spaceType, @RequestHeader("curpage")
    String curpage, @RequestHeader("size")
    String size, HttpServletRequest request) throws BaseRunException {
        LOGGER.info("ufm->queryDoingTask==>ownerId:" + ownerId +
            "  spaceType:" + spaceType + "  token:" + token);

        UserToken userToken = null;

        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);

            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token,
                    headerCustomMap);

            // 用户状?校?
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            ConvertInfo convertInfo = convertTaskService.getDoingConvertTask(userToken,
                    ownerId, spaceType, curpage, size);

            return new ResponseEntity<ConvertInfo>(convertInfo, HttpStatus.OK);
        } catch (NoSuchItemsException e) {
            throw new NoSuchParentException(e);
        } catch (BaseRunException t) {
            String[] logParams = new String[] { String.valueOf(ownerId) };
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null,
                null, UserLogType.GET_DOINGTASK_ERR, logParams, "");

            throw t;
        }
    }

    @RequestMapping(value = "/done/{ownerId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ConvertInfo> queryDoneTask(
        @PathVariable
    Long ownerId, @RequestHeader("Authorization")
    String token, @RequestHeader("spaceType")
    String spaceType, @RequestHeader("curpage")
    String curpage, @RequestHeader("size")
    String size, HttpServletRequest request) throws BaseRunException {
        LOGGER.info("ufm->queryDoneTask==>ownerId:" + ownerId + "  spaceType:" +
            spaceType + "  token:" + token);

        UserToken userToken = null;

        try {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId);

            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token,
                    headerCustomMap);

            // 用户状?校?
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            ConvertInfo convertInfo = convertTaskService.getDoneConvertTask(userToken,
                    ownerId, spaceType, curpage, size);

            return new ResponseEntity<ConvertInfo>(convertInfo, HttpStatus.OK);
        } catch (NoSuchItemsException e) {
            throw new NoSuchParentException(e);
        } catch (BaseRunException t) {
            String[] logParams = new String[] { String.valueOf(ownerId) };
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null,
                null, UserLogType.GET_DONETASK_ERR, logParams, "");

            throw t;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteDoneTask(
        @RequestBody
    DeleteConvertRequest request, @RequestHeader("Authorization")
    String token, @RequestHeader("spaceType")
    String spaceType, HttpServletRequest httpRequest) throws BaseRunException {
        LOGGER.info("ufm->deleteDoneTask==>ownerId:" + request.getOwnerId() +
            "  spaceType:" + spaceType + "  token:" + token);
        LOGGER.info("ufm->deleteDoneTask==>taskids:" +
            Arrays.toString(request.getTaskids()));

        UserToken userToken = null;

        try {
            FilesCommonUtils.checkNonNegativeIntegers(request.getOwnerId());

            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(httpRequest);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token,
                    headerCustomMap);

            // 用户状?校?
            userTokenHelper.checkUserStatus(userToken.getAppId(),
                request.getOwnerId());

            convertTaskService.deleteDoneConvertTask(userToken,
                request.getTaskids(), spaceType, request.getOwnerId());

            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NoSuchItemsException e) {
            throw new NoSuchParentException(e);
        } catch (BaseRunException t) {
            String[] logParams = new String[] { "null" };
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null,
                null, UserLogType.DELETE_DONETASK_ERR, logParams, "");

            throw t;
        }
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> retryDoing(@RequestBody RetryConvertRequest request, @RequestHeader("Authorization")
            String token, @PathVariable String taskId, @RequestHeader("spaceType") String spaceType, HttpServletRequest httpRequest) throws BaseRunException {
        LOGGER.info("ufm->retryDoing==>ownerId:" + request.getOwnerId() + "  spaceType:" + spaceType + "  token:" + token);
        LOGGER.info("ufm->retryDoing==>taskId==================:" + taskId);

        UserToken userToken = null;

        try {
            FilesCommonUtils.checkNonNegativeIntegers(request.getOwnerId());

            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(httpRequest);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状?校?
            userTokenHelper.checkUserStatus(userToken.getAppId(), request.getOwnerId());

            convertTaskService.retryDoing(userToken, taskId, spaceType, request.getOwnerId());

            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NoSuchItemsException e) {
            throw new NoSuchParentException(e);
        } catch (BaseRunException t) {
            String[] logParams = new String[]{String.valueOf(taskId)};
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.RETRY_DONETASK_ERR, logParams, "");

            throw t;
        }
    }
}
