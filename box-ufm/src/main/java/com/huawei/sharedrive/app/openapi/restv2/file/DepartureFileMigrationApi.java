package com.huawei.sharedrive.app.openapi.restv2.file;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.service.AccountService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.manager.PersistentEventManager;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.exception.NoSuchSourceException;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.dto.DataMigrationRequestDto;
import com.huawei.sharedrive.app.files.dto.DataMigrationResponseDto;
import com.huawei.sharedrive.app.files.dto.MigrationRecordDto;
import com.huawei.sharedrive.app.files.exception.MigrationException;
import com.huawei.sharedrive.app.files.manager.IDataMigrationManager;
import com.huawei.sharedrive.app.files.service.*;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.message.MessageParamName;
import com.huawei.sharedrive.app.openapi.domain.node.*;
import com.huawei.sharedrive.app.openapi.domain.teamspace.*;
import com.huawei.sharedrive.app.openapi.rest.FilesCommonApi;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityCheckManager;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.share.service.INodeLinkApproveService;
import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipManager;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.EnterpriseUser;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.domain.UserAccount;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Desc  : 数据迁移处理接口
 * Author: 77235
 * Date	 : 2016年12月24日
 */
@Controller
@RequestMapping("/api/v2/migration")
@Api(description = "数据迁移处理接口")
public class DepartureFileMigrationApi extends FilesCommonApi {
	private static final String CONST_DEFAULT_FOLDER_NAME_FOR_DEPARTURE =  "departure.recipient.folder";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DepartureFileMigrationApi.class);

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Autowired
	private IDataMigrationManager dataMigrationManager;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private PersistentEventManager persistentEventManager;

	@Autowired
	private UserService userService;

	@Autowired
	private FileBaseService fileBaseService;

	@Autowired
	private SecurityMatrixService securityMatrixService;

	@Autowired
	private FolderService folderService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private FolderServiceV2 folderServiceV2;

	@Autowired
	private TeamSpaceService teamSpaceService;

	@Autowired
	private SecurityCheckManager securityCheckManager;

	@Autowired
	private FileService fileService;

	@Autowired
	private FilePreviewManager filePreviewManager;

	@Autowired
	private RecentBrowseService recentBrowseService;

	@Autowired
	private TeamSpaceMembershipService teamSpaceMembershipService;

	@Autowired
	private NodeService nodeService;

	@Autowired
	private INodeLinkApproveService linkApproveService;

	@Autowired
	private TrashServiceV2 trashServiceV2;

	@Autowired
	private TeamSpaceMembershipManager teamSpaceMembershipManager;

	@Autowired
	private INodeDAOV2 iNodeDAOV2;

	@Autowired
	private DepartmentService departmentService;

	/**
	 * 移交账户信息
	 *
	 * @param token
	 * @param request
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/migrationAccount", method = { RequestMethod.POST })
	@ApiOperation(value = "移交账户信息")
	@ResponseBody
	public ResponseEntity<DataMigrationResponseDto> migrateAccount(@RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date, @RequestBody DataMigrationRequestDto migrationRequest, HttpServletRequest request) throws BaseRunException {
		// 响应信息
		DataMigrationResponseDto responseDto = new DataMigrationResponseDto();

		try {
			// 企业信息
			Account account = userTokenHelper.checkAccountToken(authorization, date);

			// 更新user_*表基本信息
			dataMigrationManager.updateUser(migrationRequest.getDepartureCloudUserId(), migrationRequest);

			// 更新account_user_*表基本信息
			dataMigrationManager.updateAccountUser(account.getId(), migrationRequest.getDepartureCloudUserId(), migrationRequest);

			responseDto.setRetCode(HttpStatus.OK.name());
		} catch (Exception e) {
			LOGGER.error("[DepartureFileMigrationApi] migrateAccount error:" + e.getMessage(), e);
		}

		return new ResponseEntity<DataMigrationResponseDto>(responseDto, HttpStatus.OK);
	}

	/**
	 * 发送数据迁移信息
	 * @param migrationRequest
	 */
	private void sendMigrationMsg(PersistentEvent event, long ownerBy, long receiveId, long providerId, String providerUserName) {
		event.setEventType(EventType.SHARE_CREATE);
		event.setOwnedBy(ownerBy);
		event.addParameter(MessageParamName.RECEIVER_ID, receiveId);
		event.addParameter(MessageParamName.PROVIDER_ID, providerId);
		event.addParameter(MessageParamName.PROVIDER_USERNAME, providerUserName);
		event.addParameter(MessageParamName.PRIMARY_NODE_TYPE, INode.TYPE_MIGRATION);

		persistentEventManager.fireEvent(event);
	}

	/**
	 * 移交数据信息
	 *
	 * @param token
	 * @param request
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/migrationData", method = { RequestMethod.POST })
	@ApiOperation(value = "移交数据信息")
	@ResponseBody
	public ResponseEntity<DataMigrationResponseDto> migrateUserData(@RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date, @RequestBody DataMigrationRequestDto migrationRequest, HttpServletRequest request) throws BaseRunException {

		DataMigrationResponseDto responseDto = new DataMigrationResponseDto();
		try {
			Account account = userTokenHelper.checkAccountToken(authorization, date);
			String[] akArr = authorization.split(",");
			UserToken userToken = new UserToken();
			userToken.setAppId(account.getAppId());
			userToken.setId(migrationRequest.getDepartureCloudUserId());
			userToken.setLoginName(akArr[1]);
			userToken.setAccountVistor(account);

			// 接受用户基本信息
			User recipientUser = dataMigrationManager.getUserByCloudUserId(migrationRequest.getRecipientCloudUserId());
			String folderName = messageSource.getMessage(CONST_DEFAULT_FOLDER_NAME_FOR_DEPARTURE, new Object[] { migrationRequest.getDepartureUserName() }, request.getLocale());
			Long inodeId = dataMigrationManager.migrateData(userToken, recipientUser, migrationRequest, folderName);

			responseDto.setFileName(folderName);
			responseDto.setInodeId(inodeId);
			responseDto.setRetCode(HttpStatus.OK.name());

			// 发送消息
			PersistentEvent event = new PersistentEvent();
			event.setNodeId(inodeId);
			event.setNodeName(folderName);
			long departureUserId = migrationRequest.getDepartureCloudUserId();
			long recipientUserId = migrationRequest.getRecipientCloudUserId();
			sendMigrationMsg(event, recipientUserId, recipientUserId, departureUserId, migrationRequest.getDepartureUserName());
		} catch (MigrationException e) {
			LOGGER.error("[DataMigrationApi] migrateUserData error:" + e.getMessage(), e);

			if (StringUtils.isNotBlank(e.getErrorCode())) {
				responseDto.setErrorCode(e.getErrorCode());
			} else {
				responseDto.setRetMsg(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.error("[DataMigrationApi] migrateUserData error:" + e.getMessage(), e);

			responseDto.setRetMsg(e.getMessage());
		}

		return new ResponseEntity<DataMigrationResponseDto>(responseDto, HttpStatus.OK);
	}

	@RequestMapping(value = "/clearDepartureUserInfo", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<Boolean> cleanMigrationRecord(@RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date, @RequestBody MigrationRecordDto migrationRecord, HttpServletRequest request) throws BaseRunException {

		boolean result = false;
		try {
			// 企业信息
			Account account = userTokenHelper.checkAccountToken(authorization, date);
			UserToken userToken = new UserToken();
			userToken.setAppId(account.getAppId());
			userToken.setAccountVistor(account);

			dataMigrationManager.cleanDepartureUserInfo(userToken, migrationRecord);
			result = true;
		} catch (Exception e) {
			LOGGER.error("[DataMigrationApi] cleanMigrationRecord error:" + e.getMessage(), e);
		}

		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}

	/**
	 * 移交数据信息
	 *
	 * @param token
	 * @param request
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/nodes/{ownerId}", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<?> migrateUserFolder(@PathVariable Long ownerId, @RequestHeader("Authorization") String token, @RequestBody MigrationFolderRequest migrationFolderRequest, HttpServletRequest request) throws BaseRunException {

		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
		long adminId = userToken.getId();
		// 用户状态校验
		userTokenHelper.assembleUserToken(adminId, userToken);
		userTokenHelper.checkUserStatus(userToken.getAppId(), adminId);
		userTokenHelper.checkAdminUser(ownerId, adminId, userToken);

		try {
			userTokenHelper.checkUserStatus(userToken.getAppId(), migrationFolderRequest.getDestOwnerId());
			User ownerUser = userService.get(ownerId);
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(ownerUser);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			// 空间及文件数校验
			fileBaseService.checkSpaceAndFileCount(migrationFolderRequest.getDestOwnerId(), userToken.getAccountId());
			INode destParent = new INode(migrationFolderRequest.getDestOwnerId(), migrationFolderRequest.getDestParent());
			if(migrationFolderRequest.getParentFolderName()!=null&&"".equals(migrationFolderRequest.getParentFolderName())){
				CreateFolderRequest createRequest=new CreateFolderRequest();
				createRequest.setParent(migrationFolderRequest.getDestParent());
				INode newFolder = createRequest.transToINode();
				boolean autoMerge = createRequest.getMergeValue();
				newFolder.setOwnedBy(ownerId);
				newFolder.setCreatedBy(userToken.getId());
				newFolder.setType(INode.TYPE_FOLDER);
				newFolder.setName(migrationFolderRequest.getParentFolderName());
				newFolder.setCreatedAt(new Date());
				newFolder.setModifiedAt(new Date());
				destParent = folderService.createFolder(ownerToken, newFolder, autoMerge);
			}

			for (INode inode : migrationFolderRequest.getSrcNodes()) {
				long folderId = inode.getId();
				long owner = inode.getOwnedBy();
				try {
					securityMatrixService.checkSecurityMatrix(userToken, inode.getOwnedBy(), inode.getId(), migrationFolderRequest.getDestOwnerId(), SecurityMethod.NODE_COPY, headerCustomMap);
				} catch (NoSuchItemsException e) {
					throw new NoSuchSourceException(e);
				}
				// boolean isAutoRename = migrationFolderRequest.isAutoRename();
				INode srcNode = new INode(owner, folderId);

				srcNode = fileBaseService.getINodeInfo(owner, folderId);

				if (srcNode == null) {
					String message = "inode not exist, ownerId:" + owner + ", inodeid:" + folderId;
					throw new NoSuchSourceException(message);
				}
				String srcName = srcNode.getName();
				folderService.moveNodeToFolderNoCheck(ownerToken, srcNode, destParent, srcName, srcNode.getType());

			}
			checkIsMigration(ownerToken);
			return new ResponseEntity<List<INode>>(migrationFolderRequest.getSrcNodes(),HttpStatus.OK);
		} catch (BaseRunException t) {
			throw t;
		}

	}

	/**
	 * 管理员列举用户文件
	 *
	 */
	@RequestMapping(value = "/folders/{ownerId}/{folderId}/items", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestFolderLists> listFolderForAdmin(@PathVariable Long ownerId, @PathVariable Long folderId, @RequestBody(required = false) ListFolderRequest listFolderRequest, @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		// Token 验证
		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
		Long adminId = userToken.getId();
		try {
			// 用户状态校验
			userTokenHelper.assembleUserToken(adminId, userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), adminId);
			FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
//			userTokenHelper.checkAdminUser(ownerId, adminId, userToken);

			if (listFolderRequest != null) {
				listFolderRequest.checkParameter();
			} else {
				listFolderRequest = new ListFolderRequest();
			}
			User owner = userService.get(ownerId);
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			INode filter = new INode();
			filter.setOwnedBy(ownerId);
			filter.setId(folderId);
			filter.setStatus(INode.STATUS_NORMAL);

			FileINodesList reList = folderServiceV2.listNodesByFilter(ownerToken, filter, listFolderRequest, headerCustomMap);
			RestFolderLists folderList = new RestFolderLists(reList, ownerToken.getDeviceType(), messageSource, request.getLocale());
			fillListUserInfo(folderList);
			return new ResponseEntity<RestFolderLists>(folderList, HttpStatus.OK);
		} catch (BaseRunException t) {
			throw t;
		}
	}

	/**
	 * 获取指定用戶的空间列表
	 *
	 * @param listRequest
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/teamspaces/{userId}/spaces", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> listUserTeamSpaces(@PathVariable Long userId, @RequestBody ListUserTeamSpaceRequest listRequest, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		UserToken userToken = null;
		try {
			userToken = userTokenHelper.getUserToken(token, date);
			// 用户状态校验
			userTokenHelper.assembleUserToken(userToken.getId(), userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
			// userTokenHelper.checkAdminUser(ownerId, adminId, userToken);

			listRequest.checkParameter();
			User owner = userService.get(userId);
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			// 设置每次返回结果数
			Limit limitObj = new Limit(listRequest.getOffset(), listRequest.getLimit());

//			TeamMemberList listTeamSpace = teamSpaceService.listUserTeamSpaces(ownerToken, listRequest.getOrder(), limitObj, listRequest.getUserId(), listRequest.getType(), TeamSpaceMemberships.TYPE_USER);
			TeamMemberList listTeamSpace = teamSpaceService.listTeamSpacesByOwner(ownerToken, listRequest.getOrder(), limitObj, listRequest.getUserId(), listRequest.getType(), TeamSpaceMemberships.TYPE_USER);
			RestUserTeamSpaceList rs = new RestUserTeamSpaceList(listTeamSpace);

			return new ResponseEntity<RestUserTeamSpaceList>(rs, HttpStatus.OK);
		} catch (RuntimeException t) {
			String[] logParams = new String[] { String.valueOf(listRequest != null ? listRequest.getUserId() : null), null };
			String keyword = "";
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_USER_TESMSPACE_ERR, logParams, keyword);
			throw t;
		}
	}

	/**
	 *
	 * @param listRequest
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/teamspaces/{teamId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> changeTeamSpaces(@PathVariable Long teamId, @RequestBody RestTeamSpaceInfo restTeamSpaceInfo, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {

		UserToken userToken = null;
		try {
			userToken = userTokenHelper.getUserToken(token, date);
			// 用户状态校验
			userTokenHelper.assembleUserToken(userToken.getId(), userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
			userTokenHelper.checkAdminUser(restTeamSpaceInfo.getCreatedBy(), userToken.getId(), userToken);

			User owner = userService.get(restTeamSpaceInfo.getOwnerBy());
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);
			// 判断空间是否存在
			TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
			teamSpaceService.migrationTeamSpace(teamSpace, restTeamSpaceInfo.getOwnerBy());
			checkIsMigration(ownerToken);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException t) {
			throw t;
		}
	}

	/**
	 *
	 * @param listRequest
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/teamspaces/batch", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> batchChangeTeamSpaces( @RequestBody RestBatchMigrationTeamSpace restBatchMigrationTeamSpace, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {

		UserToken userToken = null;
		try {
			userToken = userTokenHelper.getUserToken(token, date);
			// 用户状态校验
			userTokenHelper.assembleUserToken(userToken.getId(), userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());

			for(TeamSpace teamspace : restBatchMigrationTeamSpace.getTeams()){
				userTokenHelper.checkAdminUser(teamspace.getOwnerBy(), userToken.getId(), userToken);
				// 判断空间是否存在
				TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamspace.getCloudUserId());
				teamSpaceService.migrationTeamSpace(teamSpace, restBatchMigrationTeamSpace.getDestUserId());

			}

			User owner = userService.get(restBatchMigrationTeamSpace.getDestUserId());
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);
			checkIsMigration(ownerToken);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException t) {
			throw t;
		}
	}

	/**
	 * 删除空间
	 *
	 * @param listRequest
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/teamspaces/{teamId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteTeamSpaces(@PathVariable Long teamId, @RequestBody RestTeamSpaceInfo restTeamSpaceInfo, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {

		UserToken userToken = null;
		try {
			userToken = userTokenHelper.getUserToken(token, date);
			// 用户状态校验
			userTokenHelper.assembleUserToken(userToken.getId(), userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
			userTokenHelper.checkAdminUser(restTeamSpaceInfo.getCreatedBy(), userToken.getId(), userToken);

			User owner = userService.get(restTeamSpaceInfo.getOwnerBy());
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);
			// 判断空间是否存在
			teamSpaceService.checkAndGetTeamSpaceExist(teamId);
			teamSpaceService.deleteTeamSpace(ownerToken, teamId, String.valueOf(account.getEnterpriseId()));
			checkIsMigration(ownerToken);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException t) {
			throw t;
		}
	}

	/**
	 *
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/files/{ownerId}/{fileId}/download", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getFileDownloadUrl(@PathVariable Long ownerId, @PathVariable Long fileId, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date, HttpServletRequest request) throws BaseRunException {
		UserToken userToken = null;
		// Token 验证
		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		try {
			userToken = userTokenHelper.getUserToken(token, date);
			// 用户状态校验
			userTokenHelper.assembleUserToken(userToken.getId(), userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
			userTokenHelper.checkAdminUser(ownerId, userToken.getId(), userToken);

			User owner = userService.get(ownerId);
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			INode node = fileBaseService.getINodeInfo(ownerId, fileId);

			String xUserToken = request.getHeader("x-usertoken");
			if (StringUtils.isNotEmpty(xUserToken)) {
				UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, headerCustomMap);
				securityMatrixService.checkSecurityMatrix(accessUserToken, ownerId, fileId, SecurityMethod.FILE_DOWNLOAD, headerCustomMap);
			} else {
				securityMatrixService.checkSecurityMatrix(ownerToken, ownerId, fileId, SecurityMethod.FILE_DOWNLOAD, headerCustomMap);
			}

			HttpHeaders header = new HttpHeaders();
			header.set("Connection", "close");
			String downloadUrl = fileService.getFileDownloadUrl(ownerToken, node, header);
			DownloadResponse response = new DownloadResponse(downloadUrl);
			return new ResponseEntity<DownloadResponse>(response, header, HttpStatus.OK);
		} catch (RuntimeException t) {
			// sendLogEvent(ownerId, userToken, node);
			throw t;
		}
	}

	/**
	 *
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/files/{ownerId}/{fileId}/preview", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getFilePreviewUrl(@PathVariable Long ownerId, @PathVariable Long fileId, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date, HttpServletRequest request) throws BaseRunException {
		UserToken userToken = null;
		// Token 验证
		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		try {
			userToken = userTokenHelper.getUserToken(token, date);
			// 用户状态校验
			userTokenHelper.assembleUserToken(userToken.getId(), userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
			userTokenHelper.checkAdminUser(ownerId, userToken.getId(), userToken);

			User owner = userService.get(ownerId);
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			// 获取文件信息
			INode node = fileBaseService.getINodeInfo(ownerId, fileId);

			String xUserToken = request.getHeader("x-usertoken");
			if (StringUtils.isNotEmpty(xUserToken)) {
				UserToken accessUserToken = userTokenHelper.checkTokenAndGetUserForV2(xUserToken, headerCustomMap);
				securityMatrixService.checkSecurityMatrix(accessUserToken, ownerId, fileId, SecurityMethod.FILE_PREVIEW, headerCustomMap);
			} else {
				securityMatrixService.checkSecurityMatrix(userToken, ownerId, fileId, SecurityMethod.FILE_PREVIEW, headerCustomMap);
			}

			String previewUrl = filePreviewManager.getPreviewUrl(ownerToken, node);

			// 非外链访问，保存浏览记录
			if (!token.startsWith(UserTokenHelper.LINK_PREFIX)) {
				recentBrowseService.createByNode(userToken, node);
			}

			return new ResponseEntity<>(new PreviewResponse(previewUrl), HttpStatus.OK);
		} catch (RuntimeException t) {
			// sendLogEvent(ownerId, userToken, node, UserLogType.PREVIEW_URL_ERROR);
			throw t;
		}
	}

	/**
	 * 删除节点并清空回收站
	 *
	 * @param ownerId
	 * @param nodeId
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/folders/{ownerId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> deleteNodeByAdmin(@PathVariable Long ownerId, @RequestBody List<INode> deleteNodes, @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		// Token 验证
		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
		long adminId = userToken.getId();
		try {
			// 用户状态校验
			userTokenHelper.assembleUserToken(adminId, userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), adminId);
			userTokenHelper.checkAdminUser(ownerId, adminId, userToken);

			User owner = userService.get(ownerId);
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			for (INode node : deleteNodes) {
				INode nodeInfo = fileBaseService.getAndCheckNode(node.getOwnedBy(), node.getId(), node.getType());
				nodeService.deleteNode(ownerToken, nodeInfo);
				linkApproveService.deleteByNodeId(ownerToken, node);
				trashServiceV2.deleteTrashItem(ownerToken, nodeInfo);
			}

			checkIsMigration(ownerToken);
			ResponseEntity<String> rsp = new ResponseEntity<String>(HttpStatus.OK);

			return rsp;
		} catch (RuntimeException t) {
			throw t;
		}
	}

	/**
	 *
	 */
	@RequestMapping(value = "/teamspaces/{teamId}/members", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestTeamMemberList> listTeamMember(@PathVariable Long teamId, @RequestBody ListTeamSpaceMemberRequest listRequest, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date, HttpServletRequest request) throws BaseRunException {
		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
		long adminId = userToken.getId();
		try {
			// 用户状态校验
			userTokenHelper.assembleUserToken(adminId, userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), adminId);
			userTokenHelper.checkAdminUser(teamSpace.getOwnerBy(), adminId, userToken);

			User owner = userService.get(teamSpace.getCreatedBy());
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			listRequest.checkParameter();
			// 设置每次返回结果数
			Limit limitObj = new Limit(listRequest.getOffset(), listRequest.getLimit());
			// 判断空间是否存在

			TeamMemberList teamMemberList = teamSpaceMembershipManager.listTeamSpaceMemberships(ownerToken, teamId, listRequest.getOrder(), limitObj, listRequest.getTeamRole(), listRequest.getKeyword(), false);

			return new ResponseEntity<>(new RestTeamMemberList(teamMemberList), HttpStatus.OK);
		} catch (RuntimeException t) {
			// sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.LIST_TEAMSPACE_MEMBER_ERR, teamId, null, null);
			throw t;
		}
	}

	/**
	 *
	 */
	@RequestMapping(value = "/teamspaces/{teamId}/members/count", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> countTeamMember(@PathVariable Long teamId, @RequestBody ListTeamSpaceMemberRequest listRequest, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date, HttpServletRequest request) throws BaseRunException {
		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
		TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
		long adminId = userToken.getId();
		try {
			// 用户状态校验
			userTokenHelper.assembleUserToken(adminId, userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), adminId);
			userTokenHelper.checkAdminUser(teamSpace.getOwnerBy(), adminId, userToken);

			User owner = userService.get(teamSpace.getCreatedBy());
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			listRequest.checkParameter();
			// 设置每次返回结果数
			Limit limitObj = new Limit(listRequest.getOffset(), listRequest.getLimit());
			// 判断空间是否存在

			long totalCount = teamSpaceMembershipManager.countTeamSpaceMemberships(ownerToken, teamId, listRequest.getOrder(), limitObj, listRequest.getTeamRole(), listRequest.getKeyword(), false);

			return new ResponseEntity<Long>(totalCount, HttpStatus.OK);
		} catch (RuntimeException t) {
			// sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.LIST_TEAMSPACE_MEMBER_ERR, teamId, null, null);
			throw t;
		}
	}


	private void checkIsMigration(UserToken user) {
		UserAccount userAccount = departmentService.getUserAccountByCloudUserId(user.getId(), user.getAccountVistor().getId());
		// 设置每次返回结果数
		Limit limit = new Limit(0l, 1000);
        List<Order>  orderlist = new ArrayList<>();
		TeamMemberList membershipsList = teamSpaceMembershipService.listUserTeamSpaceMemberships(user, String.valueOf(user.getId()), TeamSpace.TYPE_PERSONAL, TeamSpaceMemberships.TYPE_USER, orderlist, limit);
		List<TeamSpaceMemberships> memberList = membershipsList.getTeamMemberList();
		for (int i = memberList.size() - 1; i >= 0; i--) {
			TeamSpaceMemberships teamSpaceMemberships = memberList.get(i);
			if (!teamSpaceMemberships.getUserType().equals(TeamSpaceMemberships.TYPE_USER) || !teamSpaceMemberships.getTeamRole().equals(TeamRole.ROLE_ADMIN)) {
				// teamSpaceMembershipService.deleteTeamSpaceMemberById(teamSpaceMemberships.getTeamSpace().getCloudUserId(), teamSpaceMemberships.getId());
				memberList.remove(i);
				membershipsList.setTotalCount(membershipsList.getTotalCount() - 1);
			}
		}
		INode filter = new INode();
		filter.setOwnedBy(user.getId());
		filter.setId(INode.FILES_ROOT);
		filter.setStatus(INode.STATUS_NORMAL);
		ListFolderRequest listFolderRequest = new ListFolderRequest();
		int total = iNodeDAOV2.getSubINodeCount(filter, listFolderRequest);
		if (membershipsList.getTotalCount() == 0 && total == 0) {
			departmentService.updateEnterpriseUserStatus(userAccount.getUserId(), user.getAccountVistor().getEnterpriseId(), EnterpriseUser.STATUS_HAND_OVER);
		}
	}
	
	
	@RequestMapping(value = "/{userId}/fileCheck", method = { RequestMethod.GET })
	@ApiOperation(value = "检测离职用户是否还有文件")
	@ResponseBody
	public ResponseEntity<?> checkMigrate(@PathVariable Long userId, @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		// 响应信息
		Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
		UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
		long adminId = userToken.getId();
		try {
			// 用户状态校验
			userTokenHelper.assembleUserToken(adminId, userToken);
			userTokenHelper.checkUserStatus(userToken.getAppId(), adminId);

			User owner = userService.get(userId);
			UserToken ownerToken = new UserToken();
			ownerToken.copyFrom(owner);
			Account account = accountService.getById(userToken.getAccountId());
			ownerToken.setAccountVistor(account);

			// 设置每次返回结果数
			Limit limit = new Limit(0l, 1000);
			List<Order> orderlist = new ArrayList<>();
			TeamMemberList membershipsList = teamSpaceMembershipService.listUserTeamSpaceMemberships(ownerToken, String.valueOf(ownerToken.getId()), TeamSpace.TYPE_PERSONAL, TeamSpaceMemberships.TYPE_USER, orderlist, limit);
			List<TeamSpaceMemberships> memberList = membershipsList.getTeamMemberList();
			for (int i = memberList.size() - 1; i >= 0; i--) {
				TeamSpaceMemberships teamSpaceMemberships = memberList.get(i);
				if (!teamSpaceMemberships.getUserType().equals(TeamSpaceMemberships.TYPE_USER) || !teamSpaceMemberships.getTeamRole().equals(TeamRole.ROLE_ADMIN)) {
					memberList.remove(i);
					membershipsList.setTotalCount(membershipsList.getTotalCount() - 1);
				}
			}
			INode filter = new INode();
			filter.setOwnedBy(ownerToken.getId());
			filter.setId(INode.FILES_ROOT);
			filter.setStatus(INode.STATUS_NORMAL);
			ListFolderRequest listFolderRequest = new ListFolderRequest();
			int total = iNodeDAOV2.getSubINodeCount(filter, listFolderRequest);
			if (membershipsList.getTotalCount() == 0 && total == 0) {
				return	new ResponseEntity<Boolean>(false, HttpStatus.OK);
			}
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} catch (RuntimeException t) {
			// sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.LIST_TEAMSPACE_MEMBER_ERR, teamId, null, null);
			throw t;
		}
	}
	

}
