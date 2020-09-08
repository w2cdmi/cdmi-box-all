package com.huawei.sharedrive.app.openapi.restv2.teamspace;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchReginException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.teamspace.*;
import com.huawei.sharedrive.app.teamspace.domain.*;
import com.huawei.sharedrive.app.teamspace.manager.TeamspaceManager;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.domain.Limit;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 团队空间对外接口
 *
 * @author t00159390
 */
@Controller
@RequestMapping(value = "/api/v2/teamspaces")
@Api(description = "团队空间操作接口")
public class TeamSpaceApi extends TeamSpaceCommonApi {
    @Autowired
    private TeamSpaceService teamSpaceService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private FileBaseService fileBaseService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private TeamspaceManager teamspaceManager;

    @Autowired
    private UserService userService;

    /**
     * 创建团队空间：名称，描述，配额，状态
     *
     * @param createRequest
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "创建团队空间", notes = "创建团队空间：名称，描述，配额，状态")
    @ResponseBody
    public ResponseEntity<?> createTeamSpace(@RequestBody RestTeamSpaceCreateRequest createRequest, @RequestHeader("Authorization") String authorization, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
        UserToken userInfo = null;
        try {
            createRequest.checkParameter();

            if (authorization.startsWith(UserTokenHelper.APP_PREFIX) || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX)) {
                Account account = userTokenHelper.checkAccountToken(authorization, date);
                String[] str = authorization.split(",");
                String ak = str[1];
                userInfo = new UserToken();
                userInfo.setLoginName(ak);
                userInfo.setAccountId(account.getId());
                userInfo.setAppId(account.getAppId());
                userInfo.setId(User.APP_USER_ID);
                userInfo.setAccountVistor(account);

                //请求中携带了所有者信息, 在此替换，保持后续流程逻辑不变
                if(createRequest.getOwnerBy() != null) {
                    User user = userService.get(createRequest.getOwnerBy());
                    if (user != null) {
                        userInfo.setId(user.getId());
                        userInfo.setName(user.getName());
                    }
                }
            } else {
                userInfo = userTokenHelper.checkTokenAndGetUserForV2(authorization, null);
            }

            // 校验regionId
            checkRegionValid(createRequest.getRegionId());
            // 为了提供较少的补丁文件，regionId放入userInfo中传入
            userInfo.setRegionId(createRequest.getRegionId() == null ? userInfo.getRegionId() : createRequest.getRegionId());

            TeamSpace ts = teamspaceManager.createTeamspace(createRequest, userInfo);

            return new ResponseEntity<>(new RestTeamSpaceInfo(ts), HttpStatus.CREATED);
        } catch (RuntimeException t) {
            String keyword = StringUtils.trimToEmpty(createRequest.getName());
            fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.CREATE_TEAMSPACE_ERR, null, keyword);
            throw t;
        }
    }

	/**
	 * 刪除团队空间
	 * 
	 * @param teamId
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/{teamId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "刪除团队空间")
    @ResponseBody
	public ResponseEntity<?> deleteTeamSpace(@PathVariable Long teamId, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date, HttpServletRequest request)
			throws BaseRunException {
		UserToken user = null;
		try {
			FilesCommonUtils.checkNonNegativeIntegers(teamId);
			user = userTokenHelper.getUserToken(token, date);

			teamspaceManager.deleteTeamspaceById(teamId, user);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (RuntimeException t) {
			if (user != null) {
				String[] logParams = new String[] { "null" };
				fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.DELETE_TEAMSPACE_ERR,
						logParams, "");
			}
			throw t;
		}
	}

	@RequestMapping(value = "/{teamSpaceId}/attributes", method = RequestMethod.GET)
    @ApiOperation(value = "获取团队空间属性")
    @ResponseBody
	public ResponseEntity<GetTeamSpaceAttrResponse> getAttributes(@PathVariable Long teamSpaceId,
			@RequestHeader("Authorization") String token,
			@RequestHeader(value = "Date", required = false) String date, @RequestParam(required = false) String name)
			throws BaseRunException {
		if (StringUtils.isNotBlank(name)) {
			TeamSpaceAttributeEnum attribute = TeamSpaceAttributeEnum.getTeamSpaceConfig(name);
			if (attribute == null) {
				throw new InvalidParamException("Invalid attribute " + name);
			}
		}

		UserToken userToken = userTokenHelper.getUserToken(token, date);

		String[] description = { String.valueOf(teamSpaceId) };
		List<TeamSpaceAttribute> attrList = null;
		String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}
		try {
			attrList = teamSpaceService.getTeamSpaceAttrs(userToken, name, teamSpaceId, enterpriseId);
		} catch (RuntimeException e) {
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null,
					UserLogType.GET_TEAMSPACE_ATTRIBUTE_ERR, description, null);
			throw e;
		}
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_TEAMSPACE_ATTRIBUTE,
				description, null);
		GetTeamSpaceAttrResponse response = new GetTeamSpaceAttrResponse();
		response.setAttributes(attrList);
		return new ResponseEntity<GetTeamSpaceAttrResponse>(response, HttpStatus.OK);
	}

	/**
	 * 获取团队空间信息
	 * 
	 * @param teamId
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/{teamId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取团队空间信息")
    @ResponseBody
	public ResponseEntity<?> getTeamSpaceInfo(@PathVariable Long teamId,
			@RequestHeader("Authorization") String token,
			@RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		UserToken user = null;
		TeamSpace ts = null;
		try {
			user = userTokenHelper.getUserToken(token, date);

			ts = teamSpaceService.getTeamSpaceInfo(user, teamId);
			return new ResponseEntity<RestTeamSpaceInfo>(new RestTeamSpaceInfo(ts), HttpStatus.OK);
		} catch (RuntimeException t) {
			String[] logParams = new String[] { String.valueOf(teamId), String.valueOf(INode.FILES_ROOT) };
			fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.GET_TEAMSPACE_ERR, logParams,
					null);
			throw t;
		}
	}

    /**
     * 获取所有的空间列表
     *
     * @param listRequest
     * @param authorization
     * @param date
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/all", method = RequestMethod.POST)
    @ApiOperation(value = "获取所有的空间列表")
    @ResponseBody
    public ResponseEntity<?> listaAllTeamSpaces(@RequestBody ListAllTeamSpaceRequest listRequest,
                                                @RequestHeader("Authorization") String authorization,
                                                @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {

        String appId = null;
        long accountId = 0;
        UserToken userToken = new UserToken();
        if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
                || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX)) {
            Account account = userTokenHelper.checkAccountToken(authorization, date);
            appId = account.getAppId();
            accountId = account.getId();
            String[] akArray = authorization.split(",");
            userToken.setLoginName(akArray[1]);
            userToken.setAppId(appId);
            userToken.setAccountVistor(account);
        } else {
            userToken = userTokenHelper.checkTokenAndGetUserForV2(authorization, null);
            appId = userToken.getAppId();
            accountId = userToken.getAccountId();
        }
        listRequest.checkParameter();

        // 设置每次返回结果数
        Limit limitObj = new Limit(listRequest.getOffset(), listRequest.getLimit());
        TeamSpace filter = new TeamSpace();
        filter.setType(listRequest.getType());
        filter.setName(listRequest.getKeyword());
        filter.setOwnerByUserName(listRequest.getOwnerByUserName());
        filter.setAppId(appId);
        filter.setAccountId(accountId);

        TeamSpaceList listTeamSpace;
        RestAllTeamSpaceList rs;
        try {
            listTeamSpace = teamSpaceService.listAllTeamSpaces(listRequest.getOrder(), limitObj, filter);
            rs = new RestAllTeamSpaceList(listTeamSpace);
        } catch (RuntimeException e) {
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_ALL_TEAMSPACE_ERR,
                    null, null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_ALL_TEAMSPACE, null,
                null);
        return new ResponseEntity<RestAllTeamSpaceList>(rs, HttpStatus.OK);
    }

	/**
	 * 获取指定用戶的空间列表
	 * 
	 * @param listRequest
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/items", method = RequestMethod.POST)
    @ApiOperation(value = "获取指定用戶的空间列表")
    @ResponseBody
	public ResponseEntity<?> listUserTeamSpaces(@RequestBody ListUserTeamSpaceRequest listRequest,
			@RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		UserToken userToken = null;
		try {
			userToken = userTokenHelper.getUserToken(token, date);

            listRequest.checkParameter();

            // 设置每次返回结果数
            Limit limitObj = new Limit(listRequest.getOffset(), listRequest.getLimit());

            TeamMemberList listTeamSpace = teamSpaceService.listUserTeamSpaces(userToken, listRequest.getOrder(),
                    limitObj, listRequest.getUserId(), listRequest.getType(), TeamSpaceMemberships.TYPE_USER);

            RestUserTeamSpaceList rs = new RestUserTeamSpaceList(listTeamSpace);

            return new ResponseEntity<RestUserTeamSpaceList>(rs, HttpStatus.OK);
        } catch (RuntimeException t) {
            String[] logParams = new String[]{String.valueOf(listRequest != null ? listRequest.getUserId() : null),
                    null};
            String keyword = "";
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_USER_TESMSPACE_ERR,
                    logParams, keyword);
            throw t;
        }
    }

    /**
     * 修改团队空间
     *
     * @param modifyRequest
     * @param teamId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{teamId}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新团队空间")
    @ResponseBody
    public ResponseEntity<?> modifyTeamSpace(@RequestBody RestTeamSpaceModifyRequest modifyRequest,
                                             @PathVariable Long teamId, @RequestHeader("Authorization") String authorization,
                                             @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
        UserToken user = null;
        try {
            modifyRequest.checkParameter();

            if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
                    || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX)) {
                Account account = userTokenHelper.checkAccountToken(authorization, date);
                String[] str = authorization.split(",");
                String ak = str[1];
                user = new UserToken();
                user.setLoginName(ak);
                user.setAccountId(account.getId());
                user.setAppId(account.getAppId());
                user.setId(User.APP_USER_ID);
                user.setAccountVistor(account);
            } else {
                user = userTokenHelper.checkTokenAndGetUserForV2(authorization, null);
            }

            // 校验regionId
            checkRegionValid(modifyRequest.getRegionId());

            TeamSpace ts = teamSpaceService.modifyTeamSpace(user, teamId, modifyRequest);

            return new ResponseEntity<RestTeamSpaceInfo>(new RestTeamSpaceInfo(ts), HttpStatus.OK);
        } catch (RuntimeException t) {
            String keyword = StringUtils.trimToEmpty(modifyRequest.getName());
            fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.MODIFY_TEAMSPACE_ERR, null,
                    keyword);
            throw t;
        }
    }

    /**
     * 检查团队空间名字
     *
     * @param request
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/checkname", method = RequestMethod.POST)
    @ApiOperation(value = "检查团队空间名字")
    @ResponseBody
    public ResponseEntity<?> getTeamSpaceByName(@RequestBody RestTeamSpaceCreateRequest request,
                                                @RequestHeader("Authorization") String token) throws BaseRunException {
        UserToken user = null;
        TeamSpace ts = null;
        try {
            user = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            ts = teamSpaceService.getTeamSpaceByName(user, request.getName());
            if (ts == null) {
                return new ResponseEntity<RestTeamSpaceInfo>(HttpStatus.OK);
            }
            return new ResponseEntity<RestTeamSpaceInfo>(new RestTeamSpaceInfo(ts), HttpStatus.OK);
        } catch (RuntimeException t) {
            String[] logParams = new String[]{
                    String.valueOf(request.getName()),
                    String.valueOf(INode.FILES_ROOT)};
            fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null,
                    UserLogType.GET_TEAMSPACE_ERR, logParams, null);
            throw t;
        }
    }

	@RequestMapping(value = "/{teamSpaceId}/attributes", method = RequestMethod.PUT)
    @ApiOperation(value = "更新团队空间属性")
    @ResponseBody
	public ResponseEntity<String> setAttribute(@PathVariable Long teamSpaceId,
			@RequestBody SetTeamSpaceAttrRequest request, @RequestHeader("Authorization") String token,
			@RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		request.checkParameter();

		UserToken userToken = userTokenHelper.getUserToken(token, date);

		try {

			String enterpriseId = "";
			if (userToken.getAccountVistor() != null) {
				enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
			}

			teamSpaceService.setTeamSpaceAttr(userToken, new TeamSpaceAttribute(request.getName(), request.getValue()), teamSpaceId, enterpriseId);
		} catch (RuntimeException e) {
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null,
					UserLogType.SET_TEAMSPACE_ATTRIBUTE_ERR, null, null);
			throw e;
		}
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.SET_TEAMSPACE_ATTRIBUTE,
				null, null);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

    /**
     * 检查存储区域参数
     *
     * @param regionId
     * @throws BaseRunException
     */
    private void checkRegionValid(Byte regionId) throws BaseRunException {
        if (regionId != null) {
            Region region = regionService.getRegion(regionId);
            if (null == region) {
                throw new NoSuchReginException();
            }
        }

	}
}
