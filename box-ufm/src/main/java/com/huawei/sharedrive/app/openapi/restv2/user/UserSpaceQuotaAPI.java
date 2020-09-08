/**
 *
 */
package com.huawei.sharedrive.app.openapi.restv2.user;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.user.RestUpdateAccountSpaceQuotaRequest;
import com.huawei.sharedrive.app.openapi.domain.user.RestUpdateUserSpaceQuotaRequest;
import com.huawei.sharedrive.app.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/v2/users")
@Api(tags = {"用户账号容量配额接口"})
public class UserSpaceQuotaAPI {
    private static final Logger logger = LoggerFactory.getLogger(UserSpaceQuotaAPI.class);

    /**
     * 邮箱正则表达式
     */
    public final static String EMAIL_RULE = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    private static final byte DEFAULT_REGION = -1;

    private static final long FAIL_CREATE_USER = 0;

    private static final int USER_NAME_LENGTH = 127;

    private static final int USER_EMAIL_LENGTH = 255;

    private static final int LOGIN_NAME_LENGTH = 127;
    @Autowired
    private UserService userService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    /**
     * 调整用戶账号的空间配额
     */
    @RequestMapping(value = "/accountSpaceQuota", method = RequestMethod.PUT)
    @ApiOperation(value = "更新用户信息")
    @ResponseBody
    public ResponseEntity<String> updateAccountSpaceQuota(@RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date,
                                                                    @RequestBody RestUpdateAccountSpaceQuotaRequest restRequest) throws BaseRunException {
        try {
            // 参数校验
            if(restRequest.getAccountId() < 1 || restRequest.getFrom() < 1 || restRequest.getTo() < 1) {
                logger.warn("Invalid Parameter of update account default space quota: accountId={}, from={}, to={}", restRequest.getAccountId(), restRequest.getFrom(), restRequest.getTo());
                throw new InvalidParamException("Invalid Parameter.");
            }

            userTokenHelper.checkAppAndAccountToken(authorization, date);

            userService.compareAndSwapSpaceQuotaByAccountId(restRequest.getAccountId(), restRequest.getFrom(), restRequest.getTo());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException t) {
            logger.warn("Failed to update account default space quota: accountId={}, from={}, to={}, error={}", restRequest.getAccountId(), restRequest.getFrom(), restRequest.getTo(), t.getMessage());
            throw t;
        }
    }

    /**
     * 修改用戶账号的空间配额
     */
    @RequestMapping(value = "/userSpaceQuota", method = RequestMethod.PUT)
    @ApiOperation(value = "更新用户信息")
    @ResponseBody
    public ResponseEntity<String> updateUserSpaceQuota(@RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date,
                                                                    @RequestBody RestUpdateUserSpaceQuotaRequest restRequest) throws BaseRunException {
        try {
            // 参数校验
            if(restRequest.getAccountId() < 1 || restRequest.getUserIdList() == null || restRequest.getUserIdList().isEmpty() || restRequest.getSpaceQuota() < 1) {
                logger.warn("Invalid Parameter of update account default space quota: accountId={}, userIds={}, quota={}", restRequest.getAccountId(), restRequest.getUserIdList(), restRequest.getSpaceQuota());
                throw new InvalidParamException("Invalid Parameter.");
            }

            userTokenHelper.checkAppAndAccountToken(authorization, date);

            userService.updateSpaceQuotaByAccountIdAndUserIds(restRequest.getAccountId(), restRequest.getUserIdList(), restRequest.getSpaceQuota());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException t) {
            logger.warn("Failed to update account default space quota: accountId={}, userIds={}, quota={}, error={}", restRequest.getAccountId(), restRequest.getUserIdList(), restRequest.getSpaceQuota(), t.getMessage());
            throw t;
        }
    }
}
