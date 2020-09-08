package com.huawei.sharedrive.app.openapi.restv2.link;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.service.AccountConfigService;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.impl.FileBaseServiceImpl;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.link.LinkAndNodeV2;
import com.huawei.sharedrive.app.openapi.domain.link.RestLinkApproveList;
import com.huawei.sharedrive.app.openapi.domain.link.RestLinkFolderLists;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfoV2;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.share.*;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityCheckManager;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.share.dao.INodeLinkDAO;
import com.huawei.sharedrive.app.share.dao.ShareDAO;
import com.huawei.sharedrive.app.share.domain.*;
import com.huawei.sharedrive.app.share.service.*;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.user.domain.DepartmentAccount;
import com.huawei.sharedrive.app.user.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import com.huawei.sharedrive.app.user.service.PrivilegeService;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.PatternRegUtil;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.AccountConfigRepository;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.exception.LinkApprovingException;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 外链对外接口
 * 
 * 
 */
@Controller
@RequestMapping(value = "/api/v2/links")
public class LinkAPI {
	private static final Logger logger = LoggerFactory.getLogger(LinkAPI.class);

	@Autowired
	private FolderService folderService;

	@Autowired
	private LinkPrivilegeService linkPrivilegeService;

	@Autowired
	private LinkService linkService;

	@Autowired
	private LinkServiceV2 linkServiceV2;

	@Autowired
	private UserService userService;

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Autowired
	private FileBaseService fileBaseService;

	@Autowired
	private INodeLinkApproveService linkApproveService;

	@Autowired
	private LinkApproveUserService linkApproveUserService;

	@Autowired
	private SecurityCheckManager securityCheckManager;

	@Autowired
	private SecurityMatrixService securityMatrixService;

	@Autowired
	private SystemConfigDAO systemConfigDAO;

	@Autowired
	private AccountConfigService accountConfigService;

	@Autowired
	private INodeLinkDAO iNodeLinkDao;

	@Autowired
	private PrivilegeService privilegeService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
    private ShareService shareService;

	@Autowired
    private ShareDAO shareDAO;



	private static final String FORCE_RETURN = "true";
	private static final byte TYPE_OUTSOURCE = 3; //外包人员;
	/**
	 * 添加动态提取码
	 *
	 * @return
	 */
	@RequestMapping(value = "/dynamic", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addDynamicAccessCode(@RequestBody RestLinkDynamicRequest linkRequest)
			throws BaseRunException {
		try {
			linkRequest.checkParameter();
			RestLinkDynamicResponse result = linkServiceV2.addDynamicAccessCode(linkRequest.getLinkCode(),
					linkRequest.getIdentity());
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (RuntimeException t) {
			String[] logParams = new String[] { linkRequest.getLinkCode(), linkRequest.getIdentity(), "" };
			fileBaseService.sendINodeEvent(null, EventType.OTHERS, null, null, UserLogType.UPDATE_INODE_LINK_ERR,
					logParams, "");
			throw t;
		}
	}

	/**
	 * 创建外链
	 */
	@RequestMapping(value = "/{ownerId}/{nodeId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<INodeLink> createLinkByPut(@RequestBody RestLinkCreateRequestV2 linkRequest,
			@PathVariable long ownerId, @PathVariable long nodeId, @RequestHeader("Authorization") String token,
			HttpServletRequest request) throws BaseRunException {
		UserToken userInfo = null;
		INode iNode = null;
		try {
			// uam鉴权
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			// 检查用户状态
			userTokenHelper.checkUserStatus(userInfo.getAppId(), ownerId);
			// 校验请求参数
			checkRestLinkCreateRequestV2(linkRequest);

			// 校验安全矩阵
			securityMatrixService.checkSecurityMatrix(userInfo, ownerId, nodeId, SecurityMethod.NODE_SETLINK, headerCustomMap);

			// 是否越权判断
			linkPrivilegeService.checkCreatePrivilege(userInfo, ownerId, nodeId);

			// 填充请求参数
			INodeLink iNodeLink = fillNodeLinkFromRequest(linkRequest, ownerId, nodeId);
			iNode = checkAndGetNodeInfo(userInfo, ownerId, nodeId);
			iNodeLink = doCreate(linkRequest, userInfo, iNode, iNodeLink);

			shareService.addLinkShare(userInfo, iNodeLink.getOwnedBy(),iNodeLink.getiNodeId(), iNodeLink.getId(), iNodeLink.getRole());
			fillDirectUrl(iNodeLink);
			//生成外链审批信息
			checkLinkApprove(iNodeLink, userInfo);

			return new ResponseEntity<INodeLink>(iNodeLink, HttpStatus.CREATED);
		} catch (BaseRunException t) {
			if (null != iNode) {
				String keyword = StringUtils.trimToEmpty(iNode.getName());
				String parentId = String.valueOf(iNode.getParentId());
				String linkCode = StringUtils.trimToEmpty(iNode.getLinkCode());
				String[] logParams = new String[] { linkCode, String.valueOf(ownerId), parentId };
				fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.CREATE_LINK_ERR,
						logParams, keyword);
			} else {
				String[] logParams = new String[] { null, String.valueOf(ownerId), null };
				fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.CREATE_LINK_ERR,
						logParams, null);
			}
			throw t;
		}
	}



	/**
	 * 创建小程序单个文件分享
	 */
	@RequestMapping(value = "/program/{ownerId}/{nodeId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<INodeLink> createAndGetLink(@RequestBody RestLinkCreateRequestV2 linkRequest,
			@PathVariable long ownerId, @PathVariable long nodeId, @RequestHeader("Authorization") String token,
			HttpServletRequest request) throws BaseRunException {
		UserToken userInfo = null;
		INode iNode = null;
		try {
			// uam鉴权
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			// 检查用户状态
			userTokenHelper.checkUserStatus(userInfo.getAppId(), ownerId);
			// 校验请求参数
			checkRestLinkCreateRequestV2(linkRequest);

			// 校验安全矩阵
//			securityMatrixService.checkSecurityMatrix(userInfo, ownerId, nodeId, SecurityMethod.NODE_SETLINK, headerCustomMap);

			// 是否越权判断
//			linkPrivilegeService.checkCreatePrivilege(userInfo, ownerId, nodeId);


			List<INodeLink> list = linkServiceV2.listNodeAllLinks(userInfo, ownerId, nodeId);
			for(INodeLink iNodeLink:list){
				if(iNodeLink.getIsProgram()!=null && iNodeLink.getIsProgram()==true&&iNodeLink.getCreatedBy()==userInfo.getId()){
				 return	new ResponseEntity<INodeLink>(iNodeLink, HttpStatus.OK);
				}
			}

			// 填充请求参数
			INodeLink iNodeLink = fillNodeLinkFromRequest(linkRequest, ownerId, nodeId);
			iNode = fileBaseService.getINodeInfo(ownerId, nodeId);
			iNodeLink = doCreate(linkRequest, userInfo, iNode, iNodeLink);

			shareService.addLinkShare(userInfo, iNodeLink.getOwnedBy(),iNodeLink.getiNodeId(), iNodeLink.getId(), iNodeLink.getRole());
			fillDirectUrl(iNodeLink);
			//生成外链审批信息
			checkLinkApprove(iNodeLink, userInfo);

			return new ResponseEntity<INodeLink>(iNodeLink, HttpStatus.CREATED);
		} catch (BaseRunException t) {
			if (null != iNode) {
				String keyword = StringUtils.trimToEmpty(iNode.getName());
				String parentId = String.valueOf(iNode.getParentId());
				String linkCode = StringUtils.trimToEmpty(iNode.getLinkCode());
				String[] logParams = new String[] { linkCode, String.valueOf(ownerId), parentId };
				fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.CREATE_LINK_ERR,
						logParams, keyword);
			} else {
				String[] logParams = new String[] { null, String.valueOf(ownerId), null };
				fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.CREATE_LINK_ERR,
						logParams, null);
			}
			throw t;
		}
	}



	/**
	 *获取小程序分享外链
	 */
	@RequestMapping(value = "/program/{ownerId}/{nodeId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> GetProgramLink(
			@PathVariable long ownerId, @PathVariable long nodeId, @RequestHeader("Authorization") String token,
			HttpServletRequest request) throws BaseRunException {
		UserToken userInfo = null;
		INode iNode = null;
		try {
			// uam鉴权
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			// 检查用户状态
			userTokenHelper.checkUserStatus(userInfo.getAppId(), ownerId);

			// 校验安全矩阵
//			securityMatrixService.checkSecurityMatrix(userInfo, ownerId, nodeId, SecurityMethod.NODE_SETLINK, headerCustomMap);
//
//			// 是否越权判断
//			linkPrivilegeService.checkCreatePrivilege(userInfo, ownerId, nodeId);


			List<INodeLink> list = linkServiceV2.listNodeAllLinksNoCheck(userInfo, ownerId, nodeId);
			for(INodeLink iNodeLink:list){
				if(iNodeLink.getIsProgram()!=null && iNodeLink.getIsProgram()==true){
				 return	new ResponseEntity<INodeLink>(iNodeLink, HttpStatus.OK);
				}
			}

			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (BaseRunException t) {
			throw t;
		}
	}



	/**
	 * 创建外链
	 */
	@RequestMapping(value = "/nodes", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<INodeLink> createNodesLinkByPut(@RequestBody RestLinkCreateRequestV2 linkRequest,
			@RequestHeader("Authorization") String token,HttpServletRequest request) throws BaseRunException {
		UserToken userInfo = null;
		try {
			// uam鉴权
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			// 检查用户状态
			userTokenHelper.checkUserStatus(userInfo.getAppId(), userInfo.getId());
			// 校验请求参数
			checkRestLinkCreateRequestV2(linkRequest);

			List<INode> nodes = linkRequest.getNodeList();
			for(INode node : nodes){
				// 校验安全矩阵
				securityMatrixService.checkSecurityMatrix(userInfo, node.getOwnedBy(), node.getId(), SecurityMethod.NODE_SETLINK, headerCustomMap);
				// 是否越权判断
				linkPrivilegeService.checkCreatePrivilege(userInfo, node.getOwnedBy(), node.getId());
				checkAndGetNodeInfo(userInfo, node.getOwnedBy(), node.getId());

			}
			// 填充请求参数
			INodeLink iNodeLink = fillNodeLinkFromRequest(linkRequest, userInfo.getId(), ShareLinkCommon.INODEID_BATCH);
			iNodeLink = doCreate(linkRequest, userInfo, nodes, iNodeLink);
			fillDirectUrl(iNodeLink);
			if (iNodeLink.getiNodeId() == -1) {
				List<INode> subfiles = (List<INode>) JsonUtils.stringToList(iNodeLink.getSubINodes(), List.class,INode.class);
				iNodeLink.setSubFileList(subfiles);
			}

			shareService.addLinkShare(userInfo, iNodeLink.getOwnedBy(),linkRequest.getLinkName(), iNodeLink.getId(), iNodeLink.getRole());
			//生成外链审批信息
			AccountConfigRepository repository = accountConfigService.getConfigRepository(userInfo.getAccountId());
			setRequestParameterWithConfig(linkRequest, userInfo, repository);
            createLinkApprove(iNodeLink, userInfo, repository);
			return new ResponseEntity<INodeLink>(iNodeLink, HttpStatus.CREATED);
		} catch (BaseRunException t) {
				String[] logParams = new String[] { null, String.valueOf(userInfo.getId()), null };
				fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.CREATE_LINK_ERR,
						logParams, null);
			throw t;
		}
	}


	private void checkLinkApprove(INodeLink iNodeLink, UserToken userInfo) {
		if(userInfo.getAccountId()!=0){
			Account account=userInfo.getAccountVistor();
			List<DepartmentAccount> userDeptList=departmentService.listUserDepts(account.getId(),account.getEnterpriseId());

            INodeLinkApprove linkApprove = new INodeLinkApprove();
            linkApprove.setLinkStatus(INodeLinkApprove.LINK_STATUS_NORMAL);
            linkApprove.setAccountId(userInfo.getAccountId());
            linkApprove.setNodeId(iNodeLink.getiNodeId());
            linkApprove.setStatus(INodeLinkApprove.APPROVE_STATUS_APPROVAL);
            linkApprove.setLinkOwner(iNodeLink.getOwnedBy());
            INode inode = fileBaseService.getINodeInfo(iNodeLink.getOwnedBy(), iNodeLink.getiNodeId());
            linkApprove.setNodeName(inode.getName());
            linkApprove.setType(inode.getType());

			for(DepartmentAccount departmentAccount:userDeptList){
				if(departmentAccount.getFileNeedApprove()){
					EnterpriseSecurityPrivilege filter = new EnterpriseSecurityPrivilege();
					filter.setDepartmentId(departmentAccount.getDeptId());
					filter.setEnterpriseId(account.getEnterpriseId());
					filter.setEnterpriseUserId(account.getId());
					List<EnterpriseSecurityPrivilege> privileges=departmentService.listPrivilege(filter);
					for(EnterpriseSecurityPrivilege privilege:privileges){
                        LinkApproveUser deptManagerUser = new LinkApproveUser();
                        deptManagerUser.setLinkCode(iNodeLink.getId());
                        if(privilege.getRole()==EnterpriseSecurityPrivilege.ROLE_DEPT_DIRECTOR){
							deptManagerUser.setType(LinkApproveUser.TYPE_MASTER);
						}else if(privilege.getRole()==EnterpriseSecurityPrivilege.ROLE_ARCHIVE_MANAGER){
							deptManagerUser.setType(LinkApproveUser.TYPE_ASSISTANT);
						}
						if(deptManagerUser.getType()!=0){

							long cloudUserId=departmentService.getUserCloudIdByEnterpriseUserId(privilege.getId(),userInfo.getAccountId());
							if(cloudUserId==userInfo.getId()){
                                 return;
							}
							deptManagerUser.setCloudUserId(cloudUserId);
							linkApproveUserService.create(deptManagerUser);
						}
					}
				}
			}
		}
	}






	private void setRequestParameterWithConfig(RestLinkCreateRequestV2 linkRequest, UserToken user, AccountConfigRepository repository) {
        // 是否启用文档密级
        if (repository.getValue("customer.storbox.doc.secretlevel.enable", "false").equals("true")) {
            //密级文档审批处理
        } else {
            //开户外协账号禁止转发功能
            if (repository.getValue("customer.storbox.sendfile.normal.extuser.forbid", "false").equals("true")) {
                if (user.getType() == TYPE_OUTSOURCE) {
                    throw new InvalidParamException("TYPE_OUTSOURCE conn't create link");
                }
            }
        }

        //发送范围
        if (repository.getValue("customer.storbox.sendfile.normal.range", "all").equals("all")) {
            linkRequest.setNeedLogin(false);
        } else {
            linkRequest.setNeedLogin(true);
        }
    }

	private void createLinkApprove(INodeLink link, UserToken user, AccountConfigRepository repository) {
        //TODO:启用外发审批
        if (repository.getValue("customer.storbox.sendfile.normal.approve.enable", "false").equals("true")) {
        	//审批记录
			INodeLinkApprove linkApprove = new INodeLinkApprove();
			linkApprove.setLinkStatus(INodeLinkApprove.LINK_STATUS_NORMAL);
			linkApprove.setAccountId(user.getAccountId());
			linkApprove.setNodeId(link.getiNodeId());
			linkApprove.setStatus(INodeLinkApprove.APPROVE_STATUS_APPROVAL);
			linkApprove.setLinkOwner(link.getOwnedBy());

			INode inode = fileBaseService.getINodeInfo(link.getOwnedBy(), link.getiNodeId());
			linkApprove.setNodeName(inode.getName());
			linkApprove.setType(inode.getType());
			linkApproveService.create(linkApprove);

        	//查询部门审批人信息
			List<Long> managerList = privilegeService.getDeptManagerOfUser(user.getAccountVistor().getEnterpriseId(), user.getId(), user.getAccountId());

			//查询部门审批人信息,
			List<Long> ownerList = privilegeService.getArchiveOwnerOfUser(user.getAccountVistor().getEnterpriseId(), user.getId(), user.getAccountId());

			//生成审批单
			for(Long cloudUserId : managerList) {
				LinkApproveUser deptManagerUser = new LinkApproveUser();
				deptManagerUser.setLinkCode(link.getId());
				deptManagerUser.setType(LinkApproveUser.TYPE_MASTER);
				deptManagerUser.setCloudUserId(cloudUserId);

				linkApproveUserService.create(deptManagerUser);
			}

			for(Long ownerId : ownerList) {
				//没有作为部门主管添加审批单。
				if(!managerList.contains(ownerId)) {
					LinkApproveUser archiveOwnerUser = new LinkApproveUser();
					archiveOwnerUser.setLinkCode(link.getId());
					archiveOwnerUser.setType(LinkApproveUser.TYPE_ASSISTANT);
					archiveOwnerUser.setCloudUserId(ownerId);

					linkApproveUserService.create(archiveOwnerUser);
				}
			}

			//没有找可以审批的人
			if(managerList.isEmpty() && ownerList.isEmpty()) {
				logger.warn("The enterprise enable the link approve function, but don'st found the Department Manager or Archive Owner for user: enterpriseId={}, userId={}, accountId={}, userAccountId={}", user.getAccountVistor().getEnterpriseId(), user.getId(), user.getAccountId(), user.getCloudUserId());
				//TODO: 发送告警给管理员。
			}
        }
	}

	/**
	 * 删除外链
	 *
	 * @return
	 * @throws BaseRunException
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	@RequestMapping(value = "/{ownerId}/{nodeId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> deleteLink(@PathVariable long ownerId, @PathVariable long nodeId,
			@RequestHeader("Authorization") String token, String type, String linkCode, HttpServletRequest request)
			throws BaseRunException {
		UserToken userInfo = null;
		INode iNodeGet = null;
		try {
			// token认证
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			// 检查用户状态
			userTokenHelper.checkUserStatus(userInfo.getAppId(), ownerId);
			linkPrivilegeService.checkDeletePrivilege(userInfo, ownerId, nodeId);
			// 获取节点对象
			iNodeGet = checkAndGetNodeInfo(userInfo, ownerId, nodeId);
			doDelete(type, linkCode, userInfo, iNodeGet);

			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (RuntimeException t) {
			String keyword = null;
			String parentId = null;
			if (iNodeGet != null) {
				keyword = StringUtils.trimToEmpty(iNodeGet.getName());
				parentId = String.valueOf(iNodeGet.getParentId());
				linkCode = StringUtils.trimToEmpty(iNodeGet.getLinkCode());
			}
			String[] logParams = new String[] { linkCode, String.valueOf(ownerId), parentId };
			fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.DELETE_LINK_ERR,
					logParams, keyword);
			throw t;
		}
	}

	/**
	 * 列举所有外链
	 *
	 * @return
	 * @throws BadRequestException
	 */
	@RequestMapping(value = "/{ownerId}/{nodeId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<RestNodeLinksList> listNodeAllLinks(@PathVariable long ownerId,
															  @PathVariable long nodeId, @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
		UserToken curUser = null;
		try
		{
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			curUser = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			userTokenHelper.checkUserStatus(curUser.getAppId(), curUser.getCloudUserId());
			// 检查INode是否存在
			fileBaseService.getAndCheckNode(curUser, ownerId, nodeId, INode.TYPE_ALL);
			List<INodeLink> list = linkServiceV2.listNodeAllLinks(curUser, ownerId, nodeId);
			for(INodeLink iNodeLink:list){
				fillDirectUrl(iNodeLink);
			}

			return new ResponseEntity<RestNodeLinksList>(new RestNodeLinksList(list, (long) list.size()),
					HttpStatus.OK);
		}
		catch (RuntimeException t)
		{
			String[] logParams = new String[]{curUser != null ? String.valueOf(curUser.getId()) : null, null};
			fileBaseService.sendINodeEvent(curUser,
					EventType.OTHERS,
					null,
					null,
					UserLogType.LIST_MY_SHARES_ERR,
					logParams,
					null);
			throw t;
		}
	}

	/**
	 * 获取外链信息
	 *
	 * @return
	 */
	@RequestMapping(value = "/{ownerId}/{nodeId}/{linkCode}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getLinkInfo(@PathVariable long ownerId, @PathVariable long nodeId, @PathVariable String linkCode,
										 @RequestHeader("Authorization") String token, HttpServletRequest request)
			throws BaseRunException {
		UserToken userInfo = null;
		INode iNode = null;
		try {
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			// 检查用户状态
			userTokenHelper.checkUserStatus(userInfo.getAppId(), ownerId);
//			linkPrivilegeService.checkGetPrivilege(userInfo, ownerId, nodeId);

			// 检查INode是否存在
			iNode = checkAndGetNodeInfo(userInfo, ownerId, nodeId);
			checkLinkCodeIsValid(ownerId, nodeId, linkCode, userInfo);

			INodeLink iNodeLink = doGetLink(linkCode, userInfo, iNode);
			INodeLink iNodeLinkV2 = assembleINodeLinkV2Value(iNodeLink, userInfo, iNode);
			return new ResponseEntity<INodeLink>(iNodeLinkV2, HttpStatus.OK);
		} catch (RuntimeException t) {
			String keyword = null;
			String parentId = null;
			if (iNode != null) {
				keyword = StringUtils.trimToEmpty(iNode.getName());
				parentId = String.valueOf(iNode.getParentId());
			}
			String[] logParams = new String[] { String.valueOf(ownerId), parentId };
			fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.GET_LINKINFO_ERR,
					logParams, keyword);
			throw t;
		}
	}

	/**
	 * 获取外链指向的文件夹或文件信息 该接口可提供给匿名用户访问外链
	 *
	 * @return
	 */
	@RequestMapping(value = "/node", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getNodeInfo(@RequestHeader("Authorization") String token, HttpServletRequest request)
			throws BaseRunException {
		String dateStr = request.getHeader("Date");
		INodeLink iNodeLink = userTokenHelper.checkLinkToken(token, dateStr);
		if (StringUtils.isBlank(iNodeLink.getId())) {
			throw new NoSuchItemsException();
		}
		// 用户状态校验
		userTokenHelper.checkUserStatus(null, iNodeLink.getOwnedBy());
		INode inode;
		UserToken userToken = new UserToken();
		LinkAndNodeV2 lv;
		try {

			String[] array = token.split(",");
			userToken.setLoginName(array[1]);
			userToken.setDeviceAddress(request.getRemoteAddr());
			inode = folderService.getNodeInfoCheckTypeV2(iNodeLink.getOwnedBy(), iNodeLink.getiNodeId());
			if (inode == null) {
				throw new NoSuchItemsException();
			}

			INodeLinkApprove linkApprove = linkApproveService.getApproveByLinkCode(inode.getLinkCode());
			if (linkApprove != null) {
				if (linkApprove.getStatus() != INodeLinkApprove.APPROVE_STATUS_COMPLETE) {
					throw new LinkApprovingException("The Link is not approved. linkCode=" + inode.getLinkCode());
				}
			}

			// 封装节点对象
			lv = buildNodeInfoResponse(iNodeLink, inode);
		} catch (RuntimeException e) {
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_LINK_FILE_INFO_ERR,
					null, null);
			throw e;
		}
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_LINK_FILE_INFO, null,
				null);
		return new ResponseEntity<LinkAndNodeV2>(lv, HttpStatus.OK);

	}

	/**
	 * 列举具有外链的所有文件夹或文件
	 * @param token 认证token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/items", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestLinkFolderLists> listAllLinkNodes(
			@RequestBody(required = false) RestLinkListRequest request, @RequestHeader("Authorization") String token,
			HttpServletRequest requestServlet) throws BaseRunException {
		UserToken userToken = null;
		try {
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
			userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			request = buildListAllRequest(userToken, request);
			List<Order> orderList = request.getOrder();
			List<Thumbnail> thumbnailList = request.getThumbnail();
			int limit = request.getLimit();
			long offset = request.getOffset();
			FileINodesList reList = linkServiceV2.listAllLinkNodes(userToken, request.getOwnedBy(),
					StringUtils.trimToEmpty(request.getKeyword()), offset, limit, orderList, thumbnailList,
					headerCustomMap);
			RestLinkFolderLists folderList = new RestLinkFolderLists(reList);
			return new ResponseEntity<RestLinkFolderLists>(folderList, HttpStatus.OK);
		} catch (RuntimeException t) {
			String[] logParams = new String[] { userToken != null ? String.valueOf(userToken.getId()) : null, null };
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_MY_SHARES_ERR,
					logParams, null);
			throw t;
		}
	}



	/**
	 * 列举具有外链的所有文件夹或文件
	 * @param token 认证token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/linkCodes", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> listAllLinkCodes( @RequestHeader("Authorization") String token,
			HttpServletRequest requestServlet) throws BaseRunException {
		UserToken userToken = null;
		try {
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
			userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
//			FileINodesList reList = linkServiceV2.listAllLinkNodes(userToken, request.getOwnedBy(),
//					StringUtils.trimToEmpty(request.getKeyword()), offset, limit, orderList, thumbnailList,
//					headerCustomMap);
//			RestLinkFolderLists folderList = new RestLinkFolderLists(reList);
			List<String> linkCodes = linkServiceV2.listAllLinkCodes(userToken);
			return new ResponseEntity<List<String>>(linkCodes, HttpStatus.OK);
		} catch (RuntimeException t) {
			String[] logParams = new String[] { userToken != null ? String.valueOf(userToken.getId()) : null, null };
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_MY_SHARES_ERR,
					logParams, null);
			throw t;
		}
	}




	/**
	 * 修改外链
	 *
	 * @return
	 */
	@RequestMapping(value = "/{ownerId}/{nodeId}", method = RequestMethod.PUT)
	@ResponseBody
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public ResponseEntity<?> updateLink(@RequestBody RestLinkCreateRequestV2 linkRequest, @PathVariable long ownerId,
			@PathVariable long nodeId, @RequestHeader("Authorization") String token, String linkCode,
			HttpServletRequest requestServlet) throws BaseRunException {
		UserToken userInfo = null;
		INode iNode = null;
		try {
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			// 检查用户状态
			userTokenHelper.checkUserStatus(userInfo.getAppId(), ownerId);
			linkPrivilegeService.checkUpdatePrivilege(userInfo, ownerId, nodeId);

			// 检查INode是否存在
			iNode = checkAndGetNodeInfo(userInfo, ownerId, nodeId);

			// 安全扫描及判断
			securityCheckManager.checkSecurityStatus(iNode, true, true);

			INodeLink iNodeLink = doUpdate(linkRequest, ownerId, nodeId, linkCode, userInfo, iNode);

			return new ResponseEntity<INodeLink>(iNodeLink, HttpStatus.OK);
		} catch (RuntimeException t) {
			String keyword = null;
			String parentId = null;
			if (iNode != null) {
				keyword = StringUtils.trimToEmpty(iNode.getName());
				parentId = String.valueOf(iNode.getParentId());
				linkCode = StringUtils.trimToEmpty(iNode.getLinkCode());
			}
			String[] logParams = new String[] { linkCode, String.valueOf(ownerId), parentId };
			fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.UPDATE_INODE_LINK_ERR,
					logParams, keyword);
			throw t;
		}
	}

	private INodeLink assembleINodeLinkV2Value(INodeLink iNodeLink, UserToken userInfo, INode iNode) {

		fillDirectUrl(iNodeLink);
        return iNodeLink;
    }

	private RestLinkListRequest buildListAllRequest(UserToken userToken, RestLinkListRequest request) {
		if (request != null) {
			request.checkParameter();
		} else {
			request = new RestLinkListRequest();
		}

		if (request.getOwnedBy() == null) {
			request.setOwnedBy(userToken.getCloudUserId());
		}
		return request;
	}

	private LinkAndNodeV2 buildNodeInfoResponse(INodeLink iNodeLink, INode inode) {
		LinkAndNodeV2 lv = new LinkAndNodeV2();
		if(inode!=null){
			if (FilesCommonUtils.isFolderType(inode.getType())) {
				RestFolderInfo ri = new RestFolderInfo(inode);
				lv.setFolder(ri);
			} else {
				if (FilesCommonUtils.isImage(inode.getName())) {
					DataAccessURLInfo urlInfo = fileBaseService.getINodeInfoDownURL(inode.getOwnedBy(), inode);
					//96*96 的缩略图
					Thumbnail thumbnail = new Thumbnail(Thumbnail.DEFAULT_BIG_HEIGHT,Thumbnail.DEFAULT_BIG_WIDTH);
					ThumbnailUrl thumbnailUrl = new ThumbnailUrl(urlInfo.getDownloadUrl() + FileBaseServiceImpl.getThumbnaliSuffix(thumbnail));
					inode.setThumbnailUrl(thumbnailUrl.getThumbnailUrl());
					inode.addThumbnailUrl(thumbnailUrl);
					//200*200 的缩略图
					Thumbnail bigThumbnail = new Thumbnail(Thumbnail.DEFAULT_BIGBIG_HEIGHT,Thumbnail.DEFAULT_BIGBIG_WIDTH);
					ThumbnailUrl bigThumbnailUrl = new ThumbnailUrl(urlInfo.getDownloadUrl() + FileBaseServiceImpl.getThumbnaliSuffix(bigThumbnail));
					inode.addThumbnailUrl(bigThumbnailUrl);
				}
				RestFileInfoV2 rv = new RestFileInfoV2(inode);
				lv.setFile(rv);
			}
		}

		//
		User user = userService.get(iNodeLink.getCreatedBy());
		if (null == user) {
			throw new ForbiddenException();
		}
		iNodeLink.setAccessCodeMode(LinkAccessCodeMode.transTypeToString(iNodeLink.getStatus()));
		iNodeLink.setCreator(userService.get(iNodeLink.getCreatedBy()).getName());
		if (iNodeLink.getiNodeId() == -1) {
			List<INode> subfiles = (List<INode>) JsonUtils.stringToList(iNodeLink.getSubINodes(), List.class,INode.class);
			List<INode> tempList= new ArrayList<>();
			for(int i=0;i<subfiles.size();i++){
				INode iNode=fileBaseService.getINodeInfo(subfiles.get(i).getOwnedBy(), subfiles.get(i).getId());
				if (iNode.getType() == INode.TYPE_FILE) {
					if (FilesCommonUtils.isImage(iNode.getName())) {
						Thumbnail thumbnail = new Thumbnail(Thumbnail.DEFAULT_BIG_HEIGHT,Thumbnail.DEFAULT_BIG_WIDTH);
						DataAccessURLInfo urlInfo = fileBaseService.getINodeInfoDownURL(iNode.getOwnedBy(), iNode);
						ThumbnailUrl thumbnailUrl = new ThumbnailUrl(urlInfo.getDownloadUrl() + FileBaseServiceImpl.getThumbnaliSuffix(thumbnail));
						iNode.addThumbnailUrl(thumbnailUrl);
						
						//200*200 的缩略图
						Thumbnail bigThumbnail = new Thumbnail(Thumbnail.DEFAULT_BIGBIG_HEIGHT,Thumbnail.DEFAULT_BIGBIG_WIDTH);
						ThumbnailUrl bigThumbnailUrl = new ThumbnailUrl(urlInfo.getDownloadUrl() + FileBaseServiceImpl.getThumbnaliSuffix(bigThumbnail));
						inode.addThumbnailUrl(bigThumbnailUrl);
					}

				}
				tempList.add(iNode);
			}
			iNodeLink.setSubFileList(tempList);
		}
		lv.setLink(iNodeLink);
		INodeShare inodeshare =new INodeShare();
        inodeshare.setLinkCode(iNodeLink.getId());
        inodeshare.setOwnerId(iNodeLink.getOwnedBy());
        inodeshare.setCreatedBy(iNodeLink.getCreatedBy());
        inodeshare.setShareType(INodeShare.SHARE_TYPE_LINK);
        List<INodeShare> forwrardList = shareDAO.getForwardRecord(inodeshare);
        for(INodeShare iNodeShare : forwrardList){
        	iNodeLink.setAlias(iNodeShare.getName());
        }
		return lv;
	}

	private INode checkAndGetNodeInfo(UserToken userInfo, long ownerId, long id) {
		INode iNode = folderService.getNodeInfo(userInfo, ownerId, id);
		if (iNode == null) {
			String message = "inode not exist, ownerId:" + ownerId + ", inodeid:" + id;
			throw new NoSuchItemsException(message);
		}
		return iNode;
	}

	private void checkAndSetAccessCodeMode(RestLinkCreateRequestV2 rv) {

		if (StringUtils.isEmpty(rv.getAccessCodeMode())) {
			rv.setAccessCodeMode(LinkAccessCodeMode.TYPE_STATIC_STRING);
		}
	}

	private void checkLinkCodeIsValid(long ownerId, long nodeId, String linkCode, UserToken userInfo) {
		if (StringUtils.isBlank(linkCode)) {
			return;
		}
		List<INodeLink> list = linkServiceV2.listNodeAllLinks(userInfo, ownerId, nodeId);
		if (null == list || list.isEmpty()) {
			throw new NoSuchLinkException();
		}
		for (INodeLink item : list) {
			if (item.getId().equals(linkCode)) {
				return;
			}
		}
		throw new NoSuchLinkException();

	}

	private void checkRestLinkCreateRequestV2(RestLinkCreateRequestV2 rv) throws InvalidParamException {
		if (rv.getEffectiveAt() < 0 || rv.getExpireAt() < 0) {
			throw new InvalidParamException();
		}
		checkAndSetAccessCodeMode(rv);
	}

	private INodeLink doCreate(RestLinkCreateRequestV2 linkRequest, UserToken userInfo, INode iNode,
			INodeLink iNodeLink) {
		iNodeLink = linkServiceV2.createLinkV2(userInfo, iNodeLink, iNode);
		return iNodeLink;
	}

	private INodeLink doCreate(RestLinkCreateRequestV2 linkRequest, UserToken userInfo, List<INode> iNodes,
			INodeLink iNodeLink) {
		iNodeLink = linkServiceV2.createLinkV2(userInfo, iNodeLink, iNodes);
		return iNodeLink;
	}

	private void doDelete(String type, String linkCode, UserToken userInfo, INode iNodeGet) {
		linkServiceV2.deleteLinkByTypeOrId(userInfo, iNodeGet, type, linkCode);
	}

	private INodeLink doGetLink(String linkCode, UserToken userInfo, INode iNode) {
		INodeLink iNodeLink;
		if (StringUtils.isBlank(linkCode)) {
			iNodeLink = linkService.getLinkByINodeIdV2(userInfo, iNode);
		} else {
			iNodeLink = linkServiceV2.getLink(userInfo, iNode, linkCode);
		}

		if (null == iNodeLink) {
			throw new NoSuchLinkException();
		}
		return iNodeLink;
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private INodeLink doUpdate(RestLinkCreateRequestV2 linkRequest, long ownerId, long nodeId, String linkCode,
			UserToken userInfo, INode iNode) {
		INodeLink iNodeLink;
		if (StringUtils.isBlank(linkCode)) {
			iNodeLink = fillNodeLinkForUpdateOld(linkRequest, ownerId, nodeId);
			iNodeLink = linkService.updateLinkV2(userInfo, iNodeLink);
		} else {
			iNodeLink=new INodeLink(linkCode);
			INodeLink link = iNodeLinkDao.getV2(iNodeLink);
			if (link == null) {
				String msg = "inodelink not exist, linkCode:" + linkCode;
				throw new NoSuchLinkException(msg);
			}
			if (StringUtils.isNotEmpty(link.getEncryptedPassword())) {
				String tmpPlainAccessCode = EDToolsEnhance.decode(link.getEncryptedPassword(), link.getPasswordKey());
				link.setPlainAccessCode(tmpPlainAccessCode);
			}
			iNodeLink = fillNodeLinkForUpdate(linkRequest, link);
			iNodeLink = linkServiceV2.updateLink(userInfo, iNode, iNodeLink);
		}
		return iNodeLink;
	}

	private void fillDirectUrl(INodeLink iNodeLink) {
		String prefix = null;
		SystemConfig systemConfig = systemConfigDAO.get("link.access.address.prefix");
		if (null != systemConfig) {
			prefix = systemConfig.getValue();
		}
		if (StringUtils.isEmpty(prefix)) {
			prefix = PropertiesUtils.getProperty("link.access.address.prefix");
		}

		if(StringUtils.isBlank(prefix)) {
			logger.warn("Can't find link access address prefix from database or configuration file.");
			return;
		}

		if(!prefix.endsWith("/")) {
			prefix += "/";
		}

		if(StringUtils.isNotBlank(iNodeLink.getUrl())) {
			iNodeLink.setUrl(prefix + iNodeLink.getUrl());
			iNodeLink.setDownloadUrl(prefix + iNodeLink.getUrl());
		} else {
			if(iNodeLink.isNeedLogin()){
				iNodeLink.setUrl(prefix + "v/" + iNodeLink.getId());
				iNodeLink.setDownloadUrl(prefix + "v/" + iNodeLink.getId());
			}else{
				iNodeLink.setUrl(prefix + "p/" + iNodeLink.getId());
				iNodeLink.setDownloadUrl(prefix + "p/" + iNodeLink.getId());
			}

		}

		if(iNodeLink.getStatus()==INodeLink.STATUS_STATIC){
			iNodeLink.setAccessCodeMode(INodeLink.PLAINACCESSCODE_STATIC);
		}
		if(iNodeLink.getStatus()==INodeLink.STATUS_MAIL){
			iNodeLink.setAccessCodeMode(INodeLink.PLAINACCESSCODE_MAIL);
		}
		if(iNodeLink.getStatus()==INodeLink.STATUS_PHONE){
			iNodeLink.setAccessCodeMode(INodeLink.PLAINACCESSCODE_PHONE);
		}
	}

	private INodeLink fillNodeLinkForUpdate(RestLinkCreateRequestV2 linkRequest, INodeLink iNodeLink) {
		if (null == linkRequest) {
			return iNodeLink;
		}

		setLinkDate(linkRequest, iNodeLink);

		setLinkAccessCode(linkRequest, iNodeLink);

		setLinkIdentities(linkRequest, iNodeLink);

		setLinkRole(linkRequest, iNodeLink);

		iNodeLink.setNeedLogin(linkRequest.isNeedLogin());

		return iNodeLink;
	}

	private INodeLink fillNodeLinkForUpdateOld(RestLinkCreateRequestV2 linkRequest, long ownerId, long nodeId) {
		INodeLink iNodeLink = new INodeLink();
		iNodeLink.setiNodeId(nodeId);
		iNodeLink.setOwnedBy(ownerId);
		if (null == linkRequest) {
			return iNodeLink;
		}

		setLinkDate(linkRequest, iNodeLink);

		iNodeLink.setStatus(LinkAccessCodeMode.TYPE_STATIC_VALUE);
		if (StringUtils.isNotEmpty(linkRequest.getPlainAccessCode())) {
			PatternRegUtil.checkLinkAccessCodeLegal(linkRequest.getPlainAccessCode());
			iNodeLink.setPlainAccessCode(linkRequest.getPlainAccessCode());
		}
		iNodeLink.setIdentities(null);
		iNodeLink.setRole(ResourceRole.VIEWER);
		return iNodeLink;
	}

	private INodeLink fillNodeLinkFromRequest(RestLinkCreateRequestV2 linkRequest, long ownerId, long nodeId) {
		INodeLink iNodeLink = new INodeLink();
		iNodeLink.setiNodeId(nodeId);
		iNodeLink.setOwnedBy(ownerId);
		if (null == linkRequest) {
			return iNodeLink;
		}

		setLinkDate(linkRequest, iNodeLink);

		if (StringUtils.isNotEmpty(linkRequest.getPlainAccessCode())) {
			PatternRegUtil.checkLinkAccessCodeLegal(linkRequest.getPlainAccessCode());
			iNodeLink.setPlainAccessCode(linkRequest.getPlainAccessCode());
		}

		iNodeLink.setStatus(LinkAccessCodeMode.transTypeToValue(linkRequest.getAccessCodeMode()));
		if (linkRequest.getIdentities() == null) {
			linkRequest.setIdentities(new ArrayList<>(0));
		}
		setLinkIdentities(linkRequest, iNodeLink);
		iNodeLink.setRole(linkRequest.getRole());
		iNodeLink.setNeedLogin(linkRequest.isNeedLogin());
		if(linkRequest.getDisdump()!=null){
			iNodeLink.setDisdump(linkRequest.getDisdump());
		}
		if(linkRequest.getIsProgram()!=null){
			iNodeLink.setIsProgram(linkRequest.getIsProgram());
		}
		return iNodeLink;
	}


	private void setLinkAccessCode(RestLinkCreateRequestV2 linkRequest, INodeLink iNodeLink) {
		if (StringUtils.isNotBlank(linkRequest.getAccessCodeMode())) {
			iNodeLink.setStatus(LinkAccessCodeMode.transTypeToValue(linkRequest.getAccessCodeMode()));
			if (LinkAccessCodeMode.TYPE_STATIC_STRING.equals(linkRequest.getAccessCodeMode())) {
				if (StringUtils.isNotEmpty(linkRequest.getPlainAccessCode())) {
					PatternRegUtil.checkLinkAccessCodeLegal(linkRequest.getPlainAccessCode());
				}
				iNodeLink.setPlainAccessCode(linkRequest.getPlainAccessCode());
			} else if (StringUtils.equals(LinkAccessCodeMode.TYPE_MAIL_STRING, linkRequest.getAccessCodeMode())
					|| StringUtils.equals(LinkAccessCodeMode.TYPE_PHONE_STRING, linkRequest.getAccessCodeMode())) {
				/*
				 * if (CollectionUtils.isEmpty(linkRequest.getIdentities())) {
				 * throw new InvalidParamException("null identity"); }
				 * checkIdentities(linkRequest);
				 */
			} else {
				throw new InvalidParamException("Bad accessCodeMode: " + linkRequest.getAccessCodeMode());
			}
		} else {
			if (LinkAccessCodeMode.TYPE_STATIC_VALUE == iNodeLink.getStatus()) {
				if (StringUtils.isNotEmpty(linkRequest.getPlainAccessCode())) {
					PatternRegUtil.checkLinkAccessCodeLegal(linkRequest.getPlainAccessCode());
				}
				iNodeLink.setPlainAccessCode(linkRequest.getPlainAccessCode());
			}
		}
	}

	private void setLinkDate(RestLinkCreateRequestV2 linkRequest, INodeLink iNodeLink) {
		if (linkRequest.getEffectiveAt() != 0) {
			iNodeLink.setEffectiveAt(new Date(linkRequest.getEffectiveAt() / 1000 * 1000));
		} else {
			iNodeLink.setEffectiveAt(null);
		}
		if (linkRequest.getExpireAt() != 0) {
			iNodeLink.setExpireAt(new Date(linkRequest.getExpireAt() / 1000 * 1000));
		} else {
			iNodeLink.setExpireAt(null);
		}
	}

	private void setLinkIdentities(RestLinkCreateRequestV2 linkRequest, INodeLink iNodeLink) {
		iNodeLink.setIdentities(linkRequest.getIdentities());
	}

	private void setLinkRole(RestLinkCreateRequestV2 linkRequest, INodeLink iNodeLink) {
		if (StringUtils.isNotBlank(linkRequest.getRole())) {
			iNodeLink.setRole(linkRequest.getRole());
		}
	}

	/**
	 * 创建文件外发审批
	 *
	 * @return
	 * @throws BaseRunException
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/appcreate/{ownerId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> createLinkApprove(@PathVariable long ownerId, @RequestHeader("Authorization") String token,
			@RequestBody RestLinkApprove restRequest, HttpServletRequest request) throws BaseRunException {
		UserToken userInfo = null;
		INode iNode = null;
		try {
			// uam鉴权
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			// 检查用户状态
			userTokenHelper.checkUserStatus(userInfo.getAppId(), ownerId);
			// 校验安全矩阵
			INodeLinkApprove linkApprove = new INodeLinkApprove(restRequest);
			INode inode = fileBaseService.getINodeInfo(restRequest.getLinkOwner(), restRequest.getNodeId());
			linkApprove.setNodeName(inode.getName());
			linkApprove.setType(inode.getType());
			linkApproveService.create(linkApprove);

			return null;
		} catch (BaseRunException t) {
			String keyword = StringUtils.trimToEmpty(iNode.getName());
			String parentId = String.valueOf(iNode.getParentId());
			String[] logParams = new String[] { iNode.getId() + "", String.valueOf(ownerId), parentId };
			fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.CREATE_APPROVE_ERR,
					logParams, keyword);
			throw t;
		}
	}

	/**
	 * 创建文件外发审批
	 *
	 * @return
	 * @throws BaseRunException
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/createLinkApproveUsers", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> createLinkApproveUsers(@RequestHeader("Authorization") String token,
			@RequestBody List<LinkApproveUser> linkApproveUserList, HttpServletRequest request)
			throws BaseRunException {
		UserToken userInfo = null;
		try {
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			for (int i = 0; i < linkApproveUserList.size(); i++) {
				LinkApproveUser linkApproveUser = linkApproveUserList.get(i);
				linkApproveUserService.create(linkApproveUser);
			}
			return null;
		} catch (BaseRunException t) {
			throw t;
		}
	}

	/**
	 * 获取审批
	 *
	 * @param listRequest
	 * @return
	 * @throws BaseRunException
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/approve/items", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> listLinkApprove(@RequestHeader("Authorization") String token,
			@RequestBody RestLinkApproveListRequest listRequest, HttpServletRequest request) throws BaseRunException {
		try {
			// uam鉴权
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

			INodeLinkApprove approve = new INodeLinkApprove();
			if (listRequest.getAccountId() != null) {
				approve.setAccountId(listRequest.getAccountId());
			}
			if (listRequest.getLinkOwner() != null) {
				approve.setLinkOwner(listRequest.getLinkOwner());
			}
			if (listRequest.getApproveBy() != null) {
				approve.setApproveBy(listRequest.getApproveBy());
			}
			if (listRequest.getStatus() != null) {
				approve.setStatus(listRequest.getStatus());
			} else {
				approve.setStatus((byte) -1);
			}

			String order = "desc";
			if (listRequest.getOrder() != null && !listRequest.getOrder().isDesc()) {
				order = "asc";
			}

			String orderField = "startTime";
			if (listRequest.getOrder() != null && listRequest.getOrder().getField() != null) {
				orderField = listRequest.getOrder().getField();
			}

			RestLinkApproveList listApprove = linkApproveService.listLinkApprove(approve, listRequest.getOffset(),
					listRequest.getLimit(), orderField, order, listRequest.getType());
			return new ResponseEntity<>(listApprove, HttpStatus.OK);
		} catch (BaseRunException t) {
			throw t;
		}
	}

	/**
	 * 获取所有人审批
	 *
	 * @param listRequest
	 * @return
	 * @throws BaseRunException
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/approve/allitems", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> listAllLinkApprove(@RequestHeader("Authorization") String token,
			@RequestBody RestLinkApproveListRequest listRequest, HttpServletRequest request) throws BaseRunException {
		try {
			// uam鉴权
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

			INodeLinkApprove approve = new INodeLinkApprove();
			if (listRequest.getAccountId() != null) {
				approve.setAccountId(listRequest.getAccountId());
			}
			if (listRequest.getStatus() != null) {
				approve.setStatus(listRequest.getStatus());
			} else {
				approve.setStatus((byte) -1);
			}

			String order = "desc";
			if (listRequest.getOrder() != null && !listRequest.getOrder().isDesc()) {
				order = "asc";
			}

			String orderField = "startTime";
			if (listRequest.getOrder() != null && listRequest.getOrder().getField() != null) {
				orderField = listRequest.getOrder().getField();
			}

			RestLinkApproveList listApprove = linkApproveService.listAllLinkApprove(approve, listRequest.getOffset(),
					listRequest.getLimit(), orderField, order);
			return new ResponseEntity<>(listApprove, HttpStatus.OK);
		} catch (BaseRunException t) {
			throw t;
		}
	}

	/**
	 * 审批
	 *
	 * @param restRequest
	 * @return
	 * @throws BaseRunException
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/approvalLink", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> LinkApprove(@RequestHeader("Authorization") String token,
			@RequestBody RestLinkApprove restRequest, HttpServletRequest request) throws BaseRunException {
		UserToken userInfo = null;
		INode iNode = null;
		try {
			// uam鉴权
			Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
			// 检查用户状态
			userTokenHelper.checkUserStatus(userInfo.getAppId(), userInfo.getId());
			// 校验安全矩阵
			INodeLinkApprove linkApprove = new INodeLinkApprove(restRequest);
			linkApprove.setApproveName(userInfo.getName());
			linkApprove.setApproveBy(userInfo.getCloudUserId());
			linkApproveService.updateStatus(linkApprove);

			return null;
		} catch (BaseRunException t) {
			String keyword = StringUtils.trimToEmpty(iNode.getName());
			String parentId = String.valueOf(iNode.getParentId());
			String[] logParams = new String[] { iNode.getId() + "", String.valueOf(userInfo.getId()), parentId };
			fileBaseService.sendINodeEvent(userInfo, EventType.OTHERS, null, null, UserLogType.CREATE_APPROVE_ERR,
					logParams, keyword);
			throw t;
		}
	}

	/**
	 * 查询外发审批
	 *
	 * @return
	 * @throws BaseRunException
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/getLinkApprove", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> getApprove(@RequestHeader("Authorization") String token,
			@RequestHeader("linkCode") String linkCode, HttpServletRequest request) throws BaseRunException {
		UserToken userInfo = null;
		try {
			// uam鉴权
			// Map<String, String> headerCustomMap =
			// HeaderPacker.getCustomHeaderMap(request);
			// userInfo = userTokenHelper.checkTokenAndGetUserForV2(token,
			// headerCustomMap);
			// // 检查用户状态
			// userTokenHelper.checkUserStatus(userInfo.getAppId(),
			// userInfo.getId());
			// // 校验安全矩阵
			// INodeLinkApprove linkApprove=new INodeLinkApprove(restRequest);

			INodeLinkApprove linkApprove = linkApproveService.getApproveByLinkCode(linkCode);
			return new ResponseEntity<>(linkApprove, HttpStatus.OK);
		} catch (BaseRunException t) {
			throw t;
		}
	}

	@RequestMapping(value = "/getLinkApproveDetail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> getLinkApproveDetail(@RequestHeader("Authorization") String token,
			@RequestHeader("linkCode") String linkCode, HttpServletRequest request) throws BaseRunException {
		UserToken userInfo = null;
		try {
			// uam鉴权
			// Map<String, String> headerCustomMap =
			// HeaderPacker.getCustomHeaderMap(request);
			// userInfo = userTokenHelper.checkTokenAndGetUserForV2(token,
			// headerCustomMap);
			// // 检查用户状态
			// userTokenHelper.checkUserStatus(userInfo.getAppId(),
			// userInfo.getId());
			// // 校验安全矩阵
			// INodeLinkApprove linkApprove=new INodeLinkApprove(restRequest);

			RestLinkApproveDetail detail = linkApproveService.getApproveDetailByLinkCode(linkCode);
			return new ResponseEntity<>(detail, HttpStatus.OK);
		} catch (BaseRunException t) {
			throw t;
		}
	}

	/**
	 * 获取外链指向的文件夹或文件信息 该接口可提供给匿名用户访问外链
	 *
	 * @return
	 */
	@RequestMapping(value = "/getLinkOnlyByLinkCode", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> getLinkOnlyByLinkCode(@RequestHeader("Authorization") String token,
			HttpServletRequest request) throws BaseRunException {
		INodeLink iNodeLink = linkServiceV2.getLinkByLinkCodeForClient(token);
		if (StringUtils.isBlank(iNodeLink.getId())) {
			throw new NoSuchItemsException();
		}
		// 用户状态校验
		userTokenHelper.checkUserStatus(null, iNodeLink.getOwnedBy());
		INode inode;
		UserToken userToken = new UserToken();
		LinkAndNodeV2 lv;
		try {
			if(iNodeLink.getiNodeId()!=-1){
				inode = folderService.getNodeInfoCheckTypeV2(iNodeLink.getOwnedBy(), iNodeLink.getiNodeId());
				if (null == inode) {
					throw new NoSuchItemsException();
				}
				lv = buildNodeInfoResponse(iNodeLink, inode);
			}else{
				// 封装节点对象
				lv = buildNodeInfoResponse(iNodeLink, null);
			}


		} catch (RuntimeException e) {
			fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_LINK_FILE_INFO_ERR,
					null, null);
			throw e;
		}
		fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.GET_LINK_FILE_INFO, null,
				null);
		return new ResponseEntity<LinkAndNodeV2>(lv, HttpStatus.OK);

	}

}
