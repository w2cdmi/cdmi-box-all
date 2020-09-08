package pw.cdmi.box.disk.teamspace.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.accountrole.domain.PageNodeRoleInfo;
import pw.cdmi.box.disk.accountrole.service.AccountRoleService;
import pw.cdmi.box.disk.client.api.TeamSpaceClient;
import pw.cdmi.box.disk.client.api.UserClient;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.files.service.FolderService;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.group.domain.GroupMembershipsInfo;
import pw.cdmi.box.disk.group.domain.GroupMembershipsList;
import pw.cdmi.box.disk.group.domain.RestGroup;
import pw.cdmi.box.disk.group.domain.RestGroupMemberOrderRequest;
import pw.cdmi.box.disk.group.service.GroupMemberService;
import pw.cdmi.box.disk.group.service.GroupService;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.mail.RequestAttribute;
import pw.cdmi.box.disk.teamspace.domain.ListTeamSpaceMemberRequest;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMember;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberCreateRequest;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberInfo;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberModifyRequest;
import pw.cdmi.box.disk.teamspace.domain.RestTeamSpaceInfo;
import pw.cdmi.box.disk.teamspace.domain.TeamMemberPage;
import pw.cdmi.box.disk.teamspace.service.TeamSpaceService;
import pw.cdmi.box.disk.user.domain.RestUserCreateResponse;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.JsonUtils;

@Controller
@RequestMapping(value = "teamspace/member")
public class TeamSpaceMemberController extends CommonController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamSpaceMemberController.class);

	private static final int PAGE_SIZE_MEMBER = 11;

	private static final String RESOURCE_ROLE_AUTHER = "auther";

	private static final String ROLE_MANAGER = "manager";

	private static final String ROLE_MEMBER = "member";

	private static final String TEAM_MEMBER_ORDER_ASC = "ASC";

	private static final String TEAM_MEMBER_ORDER_FIELD_1 = "teamRole";

	private static final String TEAM_MEMBER_ORDER_FIELD_2 = "createdAt";

	@Autowired
	private FolderService folderService;

	@Autowired
	private GroupMemberService groupMemberService;
	
	@Autowired
	private GroupService groupService;

	private TeamSpaceClient teamSpaceHttpClient;

	@Autowired
	private TeamSpaceService teamSpaceService;

	@Resource
	private RestClient uamClientService;

	@Resource
	private RestClient ufmClientService;

	private UserClient userClient;

	@Autowired
	private UserService userService;

	@Autowired
	private AccountRoleService accountRoleService;

	
	
	/**
	 * 
	 * @param teamId
	 * @param cloudUserIds
	 * @param cloudUserNames
	 * @param authType
	 * @return
	 */
	@RequestMapping(value = "/gotoAddMember", method = RequestMethod.GET)
	public String gotoAddMember(Long teamId,HttpServletRequest request,Model model) {
		model.addAttribute("teamId", teamId);
		return "teamspace/addMemberMgr";
	}
	

	
	/**
	 * 
	 * @param teamId
	 * @param cloudUserIds
	 * @param cloudUserNames
	 * @param authType
	 * @return
	 */
	@RequestMapping(value = "addMember", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> addMember(Long teamId, String cloudUserIds, String authType,
			HttpServletRequest request) {
		super.checkToken(request);
		UserToken user = getCurrentUser();
		ArrayList<String> users = (ArrayList<String>) CommonTools.decodeTrunkDate(cloudUserIds);
		if (users == null) {
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		String token = getToken();
		try {
			List<String> list = new ArrayList<String>(BusinessConstants.INITIAL_CAPACITIES);
			createUsersForAddMembers(users, token, list);
			HashSet<String> set = new HashSet<String>(BusinessConstants.INITIAL_CAPACITIES);
			set.addAll(list);
			List<Long> successList = new ArrayList<Long>(BusinessConstants.INITIAL_CAPACITIES);
			String retCode = addMembersAndSendMail(teamId, authType, user, token, set, successList);
			if (retCode == null || successList.size() == set.size()) {
				return new ResponseEntity<String>("OK", HttpStatus.OK);
			}
			if (successList.isEmpty()) {
				return new ResponseEntity<String>(retCode, HttpStatus.OK);
			}
			return new ResponseEntity<String>("P_OK", HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	

	/**
	 * 
	 * @param teamSpaceId
	 * @param teamMemberId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "deleteMember", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteMember(Long teamSpaceId, Long teamMembershipsId, HttpServletRequest request) {
		try {
			super.checkToken(request);
			String code = teamSpaceHttpClient.deleteTeamMember(teamSpaceId, teamMembershipsId, getToken());
			if (StringUtils.equals(code, String.valueOf(HttpStatus.OK.value()))) {
				return new ResponseEntity<String>("OK", HttpStatus.OK);
			}
			return new ResponseEntity<String>(code, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/{teamId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<RestTeamMemberInfo> getMemberInfo(@PathVariable("teamId") long teamId,
			HttpServletRequest request) {
		try {
			UserToken user = getCurrentUser();
			RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(teamId, user.getLoginName(), null,
					user.getLoginName());
			if (memberInfo == null) {
				return new ResponseEntity<RestTeamMemberInfo>(HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<RestTeamMemberInfo>(memberInfo, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<RestTeamMemberInfo>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostConstruct
	public void init() {
		teamSpaceHttpClient = new TeamSpaceClient(ufmClientService);
		userClient = new UserClient(uamClientService);
	}

	/**
	 * 
	 * @param teamspaceId
	 * @param teamName
	 * @param teamDescription
	 * @return
	 */
	@RequestMapping(value = "isHaveMemberMgr", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Long> isHaveMemberMgr(long teamId, String loginName, String keyWord, Model model,
			HttpServletRequest request) {
		super.checkToken(request);
		try {
			RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(teamId, loginName, null, keyWord);
			if (memberInfo == null) {
				return new ResponseEntity<Long>(0L, HttpStatus.OK);
			}
			return new ResponseEntity<Long>(memberInfo.getId(), HttpStatus.OK);
		} catch (BaseRunException e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Long>(0L, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/openMemberMgr/{teamId}", method = RequestMethod.POST)
	public ResponseEntity<Page<TeamMemberPage>> listMembers(@PathVariable("teamId") long teamId,
			@RequestParam String keyWord, @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = PAGE_SIZE_MEMBER + "") Integer pageSize,
			HttpServletRequest httpServletRequest) {
		super.checkToken(httpServletRequest);
		try {
			UserToken user = getCurrentUser();
			RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(teamId, user.getLoginName(), null,
					user.getLoginName());
			if (memberInfo == null) {
				return new ResponseEntity<Page<TeamMemberPage>>(HttpStatus.FORBIDDEN);
			}

			PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
			List<Order> orderList = new ArrayList<Order>(2);
			orderList.add(new Order(TEAM_MEMBER_ORDER_FIELD_1, TEAM_MEMBER_ORDER_ASC));
			orderList.add(new Order(TEAM_MEMBER_ORDER_FIELD_2, TEAM_MEMBER_ORDER_ASC));
			ListTeamSpaceMemberRequest request = generalListTeamSpaceMemberRequest(null, keyWord,
					pageRequest.getLimit().getLength(), Long.valueOf(pageRequest.getLimit().getOffset()), orderList);
			Page<RestTeamMemberInfo> pageParam = teamSpaceHttpClient.listTeamMembers(teamId, request, pageRequest);

			List<TeamMemberPage> restTeamMemberList = new ArrayList<TeamMemberPage>((int) pageParam.getTotalElements());
			for (RestTeamMemberInfo s : pageParam.getContent()) {
				restTeamMemberList.add(transTeamMember(s));
			}
			Page<TeamMemberPage> pageParamMember = new PageImpl<TeamMemberPage>(restTeamMemberList, pageRequest,
					pageParam.getTotalElements());
			return new ResponseEntity<Page<TeamMemberPage>>(pageParamMember, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("", e);
			return new ResponseEntity<Page<TeamMemberPage>>(
					new PageImpl<TeamMemberPage>(new ArrayList<TeamMemberPage>(BusinessConstants.INITIAL_CAPACITIES)),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/openMemberMgr/{teamId}", method = RequestMethod.GET)
	public String openMgrMember(@PathVariable("teamId") long teamId, Model model, HttpServletRequest request) {
		UserToken user = getCurrentUser();
		RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(teamId, user.getLoginName(), null,
				user.getLoginName());
		if (memberInfo == null) {
			return "teamspace";
		}
		boolean checkOrgEnabled = userClient.checkOrgEnabled(user.getToken());
		model.addAttribute("isDepartment", checkOrgEnabled);
		model.addAttribute("teamId", teamId);
		model.addAttribute("memberInfo", memberInfo);

		List<PageNodeRoleInfo> systemRoles = accountRoleService.listAccountRoles(user.getAccountId());

		model.addAttribute("systemRoles", systemRoles);

		return "teamspace/memberMgr";
	}

	@RequestMapping(value = "sendEmail/{teamId}", method = RequestMethod.POST)
	public ResponseEntity<String> sendEmail(@PathVariable("teamId") long teamId,
			@RequestParam("message") String message, @RequestParam("ids") String ids,
			HttpServletRequest httpServletRequest) {
		super.checkToken(httpServletRequest);
		UserToken user = getCurrentUser();
		try {
			if (StringUtils.isBlank(ids)) {
				return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
			}
			if (StringUtils.isNotBlank(message) && message.length() > 2000) {
				return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
			}

			RestTeamSpaceInfo teamSpace = teamSpaceHttpClient.getTeamSpace(teamId, getToken());
			ListTeamSpaceMemberRequest request = new ListTeamSpaceMemberRequest(1000, 0L);
			Page<RestTeamMemberInfo> pageParam = teamSpaceHttpClient.listTeamMembers(teamId, request, null);
			List<RestTeamMemberInfo> memberInfos = pageParam.getContent();
			String[] idArray = ids.split(",");
			List<INode> nodes = new ArrayList<INode>(idArray.length);
			INode inode;
			for (String srcIdStr : idArray) {
				inode = folderService.getNodeInfo(user, teamId, Long.parseLong(srcIdStr));
				nodes.add(inode);
			}
			doSendMail(teamId, message, teamSpace, memberInfos, nodes);
			return new ResponseEntity<String>("OK", HttpStatus.OK);
		} catch (RestException e) {
			LOGGER.error("RestException", e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch (NumberFormatException e) {
			LOGGER.error("NumberFormatException", e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}

	}

	private void doSendMail(long teamId, String message, RestTeamSpaceInfo teamSpace,
			List<RestTeamMemberInfo> memberInfos, List<INode> nodes) {
		List<RequestAttribute> params = null;
		for (INode node : nodes) {
			for (RestTeamMemberInfo rtm : memberInfos) {
				if (rtm.getMember() != null && Constants.SPACE_TYPE_USER.equals(rtm.getMember().getType())) {
					RestTeamMember teamMember = rtm.getMember();
					String email = getEmail(teamMember.getId());
					params = fillMailAttribute(teamId, message, teamSpace, node);
					teamSpaceService.sendEmailAllMember(email, params);
				} else if (rtm.getMember() != null && Constants.SPACE_TYPE_GROUP.equals(rtm.getMember().getType())) {
					sendEmailForGrouToNode(teamId, message, teamSpace, node, rtm);
				}
			}
		}
	}

	@RequestMapping(value = "updateAuthType", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> updateAuthType(Long teamId, Long teamMemberId, String authType,
			HttpServletRequest request) {
		super.checkToken(request);
		try {
			String teamRole = ROLE_MEMBER;
			if (RESOURCE_ROLE_AUTHER.equals(authType)) {
				teamRole = ROLE_MANAGER;
			}
			RestTeamMemberModifyRequest restTeamMemberModifyRequest = generalRequestTeamMemberModify(teamRole,
					authType);
			teamSpaceHttpClient.modifyTeamMemberRole(teamId, teamMemberId, getToken(), restTeamMemberModifyRequest);
			return new ResponseEntity<String>("OK", HttpStatus.OK);
		} catch (RestException e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<String>(HtmlUtils.htmlEscape(e.getCode()), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "showDept", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> showDepts(HttpServletRequest request) {
		super.checkToken(request);
		String token = getToken();
		String covertDep = covertDep(token);
		return new ResponseEntity<String>(covertDep,HttpStatus.OK);
	}
	
	@RequestMapping(value = "showDepAndUsers", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> showDepAndUsers(HttpServletRequest request,String id) {
		super.checkToken(request);
		String token = getToken();
		String covertUser = covertDepAndUser(token,id);
		return new ResponseEntity<String>(covertUser,HttpStatus.OK);
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private String addMembersAndSendMail(Long teamId, String authType, UserToken user, String token, Set<String> set, List<Long> successList) {
		String teamRole = ROLE_MEMBER;
		if (RESOURCE_ROLE_AUTHER.equals(authType)) {
			teamRole = ROLE_MANAGER;
		}

		RestTeamMemberCreateRequest restTeamMemberCreateRequest = new RestTeamMemberCreateRequest();
		restTeamMemberCreateRequest.setTeamRole(teamRole);
		restTeamMemberCreateRequest.setRole(authType);
		List<RestTeamMember> memberList = new ArrayList<>();
		for (String element : set) {
			if (StringUtils.isBlank(element)) {
				continue;
			}
			String userType;
			long tempId = 0;
			if (Constants.SPACE_TYPE_SYSTEM.equals(element)) {
				tempId = Constants.SPACE_ID_TEAM_PUBLIC;
				userType = Constants.SPACE_TYPE_SYSTEM;
			} else {
				tempId = Long.parseLong(getKeyWord(element));
				if (tempId == 0) {
					continue;
				}
				userType = getType(element);
			}
			RestTeamMember restTeamMember = new RestTeamMember();
			restTeamMember.setType(userType);
			restTeamMember.setId(tempId);
			memberList.add(restTeamMember);
		}
		restTeamMemberCreateRequest.setMemberList(memberList);

		return teamSpaceHttpClient.createTeamMember(restTeamMemberCreateRequest, teamId, getToken());
	}

	private String covertDep(String token) {
		
		return userClient.listDept(token);
	}
	
	private String covertDepAndUser(String token,String id) {
		
		return userClient.listUsersAndDepByPid(token, id);
	}
	
	private void createUsersForAddMembers(List<String> users, String token, List<String> list) {
		if (users == null) {
			return;
		}
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < users.size(); i++) {
			if (StringUtils.isNotBlank(users.get(i))) {
				String type = getType(users.get(i));
				if (Constants.SPACE_TYPE_SYSTEM.equals(type)) {
					list.add(Constants.SPACE_TYPE_SYSTEM);
				} else if (Constants.SPACE_TYPE_GROUP.equals(type)) {
					list.add("[" + type + "]" + getUserId(users.get(i)) + "[null]");
				}else if(Constants.SPACE_TYPE_ORGANIZATION.equals(type)){
					list.add("[" + type + "]" + getUserId(users.get(i)) + "[null]");
				} else {
					fillListWithCloudUserId(users, token, i, type,map);
				}
			}
		}
		for (String string : map.keySet()) {
			list.add(map.get(string));
		}
	}

	private void fillListWithCloudUserId(List<String> users, String token, int i, String type,Map<String, String> map) {
		Long cloudUserId;
		String userId = getUserId(users.get(i));
		int indexOf = userId.indexOf("user");
        if(indexOf>0){
        	userId=userId.substring(0,indexOf);
        }
		String email = getSrcEmail(users.get(i));
		try {
			cloudUserId = Long.parseLong(userId);
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			cloudUserId = userClient.createUserFromLdap(token, getLoginName(users.get(i))).getCloudUserId();
		}
		String data = "[" + type + "]" + cloudUserId.toString() + "[" + email + "]";
		map.put(cloudUserId.toString(), data);
	}

	private ListTeamSpaceMemberRequest generalListTeamSpaceMemberRequest(String teamRole, String keyword, Integer limit,
			Long offset, List<Order> orderList) {
		ListTeamSpaceMemberRequest listTeamSpaceMemberRequest = new ListTeamSpaceMemberRequest(limit, offset);
		listTeamSpaceMemberRequest.setOrder(orderList);
		listTeamSpaceMemberRequest.setTeamRole(teamRole);
		listTeamSpaceMemberRequest.setKeyword(keyword);
		return listTeamSpaceMemberRequest;
	}

	private RestTeamMemberModifyRequest generalRequestTeamMemberModify(String teamRole, String role) {
		RestTeamMemberModifyRequest restTeamMemberModifyRequest = new RestTeamMemberModifyRequest();
		restTeamMemberModifyRequest.setTeamRole(teamRole);
		restTeamMemberModifyRequest.setRole(role);
		return restTeamMemberModifyRequest;
	}

	private String getEmail(Long cloudUserId) {
		Map<String, String> headerMap = new HashMap<String, String>(2);

		String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
		EnterpriseAccount userAccount = userService.getEnterpriseAccountByCloudUserId(cloudUserId);
		String decodedKey = EDToolsEnhance.decode(userAccount.getSecretKey(), userAccount.getSecretKeyEncodeKey());
		String sign = SignatureUtils.getSignature(decodedKey, dateStr);

		String authorization = "account," + userAccount.getAccessKeyId() + ',' + sign;
		headerMap.put("Authorization", authorization);
		headerMap.put("Date", dateStr);

		TextResponse restResponse = ufmClientService.performGetText("/api/v2/users/" + cloudUserId, headerMap);
		if (null == restResponse || restResponse.getStatusCode() != 200) {
			return null;
		}
		String responseBody = restResponse.getResponseBody();
		if (StringUtils.isBlank(responseBody)) {
			return null;
		}
		RestUserCreateResponse restUserCreateResponse = JsonUtils.stringToObject(responseBody,
				RestUserCreateResponse.class);
		return restUserCreateResponse.getEmail();
	}

	private String getEmail(String userName) {
		int begin = userName.lastIndexOf("[");
		int end = userName.lastIndexOf("]");
		return userName.substring(begin + 1, end);
	}

	private String getKeyWord(String userStr) {
		int begin = userStr.indexOf("]");
		int end = userStr.lastIndexOf("[");
		return userStr.substring(begin + 1, end);
	}

	private String getLoginName(String userName) throws RestException {
		int begin = userName.indexOf("]");
		int end = userName.lastIndexOf("[");
		return userName.substring(begin + 1, end);
	}

	private String getSrcEmail(String userName) {
		int begin = userName.lastIndexOf("]");
		return userName.substring(begin + 1, userName.length());
	}

	private String getType(String userName) {
		int begin = userName.indexOf("[");
		int end = userName.indexOf("]");
		return userName.substring(begin + 1, end);
	}

	private String getUserId(String userName) {
		int begin = userName.lastIndexOf("[");
		int end = userName.lastIndexOf("]");
		return userName.substring(begin + 1, end);
	}

	private void sendEmail(UserToken user, RestTeamSpaceInfo teamspace, String userType, long tempId, String element) {
		if (Constants.SPACE_TYPE_USER.equals(userType) || StringUtils.equals(Constants.SPACE_TYPE_GROUP, userType)) {
			if (StringUtils.equals(Constants.SPACE_TYPE_USER, userType)) {
				try {
					teamSpaceService.sendAddMemberMail(user, teamspace, getEmail(element), null);
				} catch (Exception e) {
					LOGGER.warn("send mail fail");
				}
			} else if (StringUtils.equals(Constants.SPACE_TYPE_GROUP, userType)) {
				try {
					sendEmailForGroup(user, teamspace, tempId);
				} catch (Exception e) {
					LOGGER.warn("send mail fail");
				}
			}
		}
	}

	private void sendEmailForGrouToNode(long teamId, String message, RestTeamSpaceInfo teamSpace, INode node,
			RestTeamMemberInfo rtm) {
		RestGroup group = groupService.getGroupInfo(rtm.getMember().getId());
		RestGroupMemberOrderRequest groupOrder = new RestGroupMemberOrderRequest();
		GroupMembershipsList membershipsList = groupMemberService.getUserList(group.getId(), groupOrder);
		List<GroupMembershipsInfo> memberInfoes = new ArrayList<GroupMembershipsInfo>(
				BusinessConstants.INITIAL_CAPACITIES);
		if (membershipsList != null) {
			memberInfoes = membershipsList.getMemberships();
		}
		User userInfo;
		String email;
		List<RequestAttribute> params = null;
		for (GroupMembershipsInfo gm : memberInfoes) {
			userInfo = userService.getUserByCloudUserId(gm.getMember().getUserId());
			if (userInfo == null) {
				continue;
			}
			email = userInfo.getEmail();

			params = fillMailAttribute(teamId, message, teamSpace, node);
			teamSpaceService.sendEmailAllMember(email, params);
		}
	}

	private List<RequestAttribute> fillMailAttribute(long teamId, String message, RestTeamSpaceInfo teamSpace,
			INode node) {
		UserToken user = getCurrentUser();
		List<RequestAttribute> params = new ArrayList<RequestAttribute>(1);
		RequestAttribute ra = new RequestAttribute();
		ra.setName("message");
		ra.setValue("");
		if (StringUtils.isNotBlank(message)) {
			ra.setValue(message);
		}
		params.add(ra);

		ra = new RequestAttribute();
		ra.setName("appId");
		ra.setValue(user.getAppId());
		params.add(ra);

		ra = new RequestAttribute();
		ra.setName("nodeName");
		ra.setValue("");
		if (StringUtils.isNotBlank(node.getName())) {
			ra.setValue(node.getName());
		}
		params.add(ra);

		ra = new RequestAttribute();
		ra.setName("sender");
		String from = user.getName();
		if (StringUtils.isNotBlank(from)) {
			ra.setValue(from);
		}
		params.add(ra);

		ra = new RequestAttribute();
		ra.setName("nodeType");
		ra.setValue(Byte.toString(node.getType()));
		params.add(ra);

		ra = new RequestAttribute();
		ra.setName("teamSpaceName");
		ra.setValue("");
		if (teamSpace != null) {
			ra.setValue(teamSpace.getName());
		}
		params.add(ra);

		ra = new RequestAttribute();
		ra.setName("teamSpaceUrl");
		String teamSpaceUrl = transUrl(null, teamId);
		ra.setValue(teamSpaceUrl);
		params.add(ra);

		ra = new RequestAttribute();
		ra.setName("nodeUrl");
		String nodeUrl = transUrl(node, teamId);
		ra.setValue(nodeUrl);
		params.add(ra);

		return params;
	}

	private String transUrl(INode node, Long teamId) {
		StringBuffer url = new StringBuffer();
		url.append("teamspace/file/");
		url.append(teamId);
		if (node != null) {
			url.append("?#file/1/");
			url.append(node.getParentId());
		}
		return url.toString();
	}

	private void sendEmailForGroup(UserToken user, RestTeamSpaceInfo teamspace, long tempId) {
		RestGroup group = groupService.getGroupInfo(tempId);
		RestGroupMemberOrderRequest groupOrder = new RestGroupMemberOrderRequest();
		GroupMembershipsList membershipsList = groupMemberService.getUserList(group.getId(), groupOrder);
		List<GroupMembershipsInfo> memberInfoes = new ArrayList<GroupMembershipsInfo>(
				BusinessConstants.INITIAL_CAPACITIES);
		if (membershipsList != null) {
			memberInfoes = membershipsList.getMemberships();
		}
		User userInfo;
		UserToken userToken;
		ListTeamSpaceMemberRequest request = new ListTeamSpaceMemberRequest(1000, 0L);
		List<RestTeamMemberInfo> teamMemberList = teamSpaceHttpClient.listTeamMembers(teamspace.getId(), request);
		for (GroupMembershipsInfo gm : memberInfoes) {
			userInfo = userService.getUserByCloudUserId(gm.getMember().getUserId());

			if (userInfo == null || userInfo.getName().equals(user.getName())) {
				continue;
			}
			boolean isContain = false;
			for (RestTeamMemberInfo memberInfo : teamMemberList) {
				if (memberInfo != null && memberInfo.getMember() != null
						&& userInfo.getName().equals(memberInfo.getMember().getName())) {
					isContain = true;
					break;
				}
			}
			if(isContain){
				continue;
			}
			userToken = new UserToken();
			userToken.setName(user.getName());
			teamSpaceService.sendAddMemberMail(userToken, teamspace, userInfo.getEmail(), null);
		}
	}

	private TeamMemberPage transTeamMember(RestTeamMemberInfo s) {
		TeamMemberPage result = new TeamMemberPage();
		result.setId(s.getId());
		result.setLoginName(HtmlUtils.htmlEscape(s.getMember().getLoginName()));
		result.setRole(s.getRole());
		result.setTeamId(s.getTeamId());
		result.setTeamRole(s.getTeamRole());
		result.setUserDesc(HtmlUtils.htmlEscape(s.getMember().getDescription()));
		result.setUserId(s.getMember().getId());
		result.setUsername(HtmlUtils.htmlEscape(s.getMember().getName()));
		result.setUserType(s.getMember().getType());
		return result;
	}

}
