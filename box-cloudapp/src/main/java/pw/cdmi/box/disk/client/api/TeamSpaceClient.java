package pw.cdmi.box.disk.client.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.teamspace.GetTeamSpaceAttrResponse;
import pw.cdmi.box.disk.client.domain.teamspace.SetTeamSpaceAttrRequest;
import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.request.RestGroupRequest;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.teamspace.domain.ListNodeACLRequest;
import pw.cdmi.box.disk.teamspace.domain.ListTeamSpaceMemberRequest;
import pw.cdmi.box.disk.teamspace.domain.ListTeamSpaceRequest;
import pw.cdmi.box.disk.teamspace.domain.RestNodeACLCreateRequest;
import pw.cdmi.box.disk.teamspace.domain.RestNodeACLInfo;
import pw.cdmi.box.disk.teamspace.domain.RestNodeACLList;
import pw.cdmi.box.disk.teamspace.domain.RestNodeACLModifyRequest;
import pw.cdmi.box.disk.teamspace.domain.RestNodePermissionInfo;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMember;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberCreateRequest;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberInfo;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberList;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberModifyRequest;
import pw.cdmi.box.disk.teamspace.domain.RestTeamSpaceCreateRequest;
import pw.cdmi.box.disk.teamspace.domain.RestTeamSpaceInfo;
import pw.cdmi.box.disk.teamspace.domain.RestTeamSpaceModifyRequest;
import pw.cdmi.box.disk.teamspace.domain.TeamMemberPage;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;


public class TeamSpaceClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamSpaceClient.class);

	private static final int ALL_MEMBER_LIMIT = 10000;

	private static final int ALL_NODEACL_LIMIT = 10000;

	private static final String TEAM_MEMBER_ORDER_ROLE = "teamRole";

	private static final String TEAM_MEMBER_ORDER_ASC = "ASC";

	private static final String TEAM_ROLE_ADMIN = "admin";

	private static final String TEAM_ROLE_MANAGER = "manager";

	private static final String TEAM_ROLE_MEMBER = "member";

	private static final int INITIAL_SIZE = 10;

	private static UserTokenManager userTokenManager;

	private RestClient ufmClientService;

	public TeamSpaceClient(RestClient ufmClientService) {
		this.ufmClientService = ufmClientService;
	}

	public static UserTokenManager getUserTokenManager() {
		if (null == userTokenManager) {
			userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
		}
		return userTokenManager;
	}

	/**
	 * 
	 * @param ownerId
	 *            OwenerId
	 * @param nodeId
	 *            NodeId
	 * @param user
	 *            user
	 * @param role
	 *            role
	 * @return String
	 */
	public final RestNodeACLInfo addNodeACL(final long ownerId, final long nodeId, final List<RestTeamMember> userList,
			final String role) throws RestException {
		Map<String, String> headerMap = assembleToken();

		RestNodeACLCreateRequest request = new RestNodeACLCreateRequest();
		pw.cdmi.box.disk.teamspace.domain.Resource resource = new pw.cdmi.box.disk.teamspace.domain.Resource();
		resource.setNodeId(nodeId);
		resource.setOwnerId(ownerId);
		request.setResource(resource);
		request.setRole(role);
		request.setUserList(userList);
		TextResponse response = ufmClientService.performJsonPostTextResponse(Constants.NODES_API_ACL, headerMap,
				request);
		String content = response.getResponseBody();
		if (response.getStatusCode() == HttpStatus.CREATED.value()) {
			return JsonUtils.stringToObject(content, RestNodeACLInfo.class);
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}

	/**
	 * 
	 * @param modifyRequest
	 * @param teamId
	 * @param teamMemberId
	 * @param token
	 * @return
	 */
	public void changeOwner( Long teamId, Long cloudUserId, String token)
			throws RestException {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamId).append("/memberships/admin/").append(cloudUserId);
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performJsonPutTextResponse(uri.toString(), headerMap,null);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			LOGGER.debug("disbandTeam success");
			return;
		}
		LOGGER.error("disbandTeam failure, response:" + response.getResponseBody());
		RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
		throw exception;
	}

	/**
	 * 
	 * @param createRequest
	 * @param teamId
	 * @param token
	 * @return
	 */
	public String createTeamMember(RestTeamMemberCreateRequest createRequest, Long teamId, String token) {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamId).append("/memberships");
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap, createRequest);
		if (response.getStatusCode() == HttpStatus.CREATED.value()) {
			return String.valueOf(response.getStatusCode());
		}

		LOGGER.error("createTeamMember failure, response:" + response.getResponseBody());
		return getErrorCodeFromResponse(response);
	}

	/**
	 * 
	 * @param createRequest
	 * @param teamId
	 * @param token
	 * @return
	 */
	public RestTeamMemberInfo createTeamMemberBackEntity(RestTeamMemberCreateRequest createRequest, Long teamId,
			String token) {
		RestTeamMemberInfo teamMemberInfo = null;
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamId).append("/memberships");
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap, createRequest);

		if (response.getStatusCode() == HttpStatus.CREATED.value()) {
			teamMemberInfo = JsonUtils.stringToObject(response.getResponseBody(), RestTeamMemberInfo.class);
		}

		LOGGER.error("createTeamMember failure, response:" + response.getResponseBody());
		return teamMemberInfo;
	}

	/**
	 * 
	 * @param token
	 * @param restTeamSpaceCreateRequest
	 * @param pageRequest
	 * @return
	 */
	public RestTeamSpaceInfo createTeamSpace(String token, RestTeamSpaceCreateRequest restTeamSpaceCreateRequest)
			throws RestException {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap,
				restTeamSpaceCreateRequest);
		LOGGER.info("createTeamSpace, response:" + response.getResponseBody());
		if (response.getStatusCode() == HttpStatus.CREATED.value()) {
			RestTeamSpaceInfo teamSpace = JsonUtils.stringToObject(response.getResponseBody(), RestTeamSpaceInfo.class);
			return teamSpace;
		}
		RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
		throw exception;
	}

	/**
	 * 
	 * @param ownerId
	 * @param nodeId
	 */
	public String deleteNodeACL(long ownerId, long aclId) {
		StringBuilder uri = new StringBuilder(Constants.NODES_API_ACL);
		uri.append('/').append(ownerId).append('/').append(aclId);
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performDelete(uri.toString(), headerMap);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return String.valueOf(response.getStatusCode());
		}

		LOGGER.error("deleteNodeACL failure, response:" + response.getResponseBody());
		return getErrorCodeFromResponse(response);
	}

	/**
	 * 
	 * @param userId
	 * @param token
	 * @param pageRequest
	 * @return
	 */
	public String deleteTeamMember(Long teamID, Long teamMembershipsID, String token) {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamID).append("/memberships/").append(teamMembershipsID);
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performDelete(uri.toString(), headerMap);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return String.valueOf(response.getStatusCode());
		}

		LOGGER.error("deleteTeamMember failure, response:" + response.getResponseBody());
		return getErrorCodeFromResponse(response);
	}

	/**
	 * 
	 * @param teamId
	 * @param token
	 * @return
	 */
	public boolean deleteTeamSpace(long teamId, String token) {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamId);
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performDelete(uri.toString(), headerMap);

		LOGGER.info("deleteTeamSpace failure, response:" + response.getResponseBody());
		return response.getStatusCode() == HttpStatus.OK.value();
	}

	/**
	 * @param teamSpaceId
	 * @param name
	 * @throws RestException
	 */
	public GetTeamSpaceAttrResponse getAttributes(long teamSpaceId, String name) throws RestException {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamSpaceId).append("/attributes");
		if (StringUtils.isNotBlank(name)) {
			uri.append("?name=").append(name);
		}
		Map<String, String> headers = assembleToken();

		TextResponse response = ufmClientService.performGetText(uri.toString(), headers);
		String content = response.getResponseBody();

		if (response.getStatusCode() == HttpStatus.OK.value()) {
			GetTeamSpaceAttrResponse attrs = JsonUtils.stringToObject(content, GetTeamSpaceAttrResponse.class);
			return attrs;
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}

	/**
	 * 
	 * @param ownerId
	 * @param nodeId
	 * @param offset
	 * @param limit
	 * @return
	 */
	public Page<RestNodeACLInfo> getNodeACLList(long ownerId, long nodeId, PageRequest pageRequest) {
		StringBuilder uri = new StringBuilder(Constants.NODES_API_ACL);
		uri.append('/').append(ownerId);
		Map<String, String> headerMap = assembleToken();
		ListNodeACLRequest request = new ListNodeACLRequest();
		request.setNodeId(nodeId);
		request.setLimit(pageRequest.getLimit().getLength());
		request.setOffset(Long.valueOf(pageRequest.getLimit().getOffset()));

		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap, request);
		String content = response.getResponseBody();

		Page<RestNodeACLInfo> rsltList = new PageImpl<RestNodeACLInfo>(
				new ArrayList<RestNodeACLInfo>(BusinessConstants.INITIAL_CAPACITIES));
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			RestNodeACLList aclList = JsonUtils.stringToObject(content, RestNodeACLList.class);
			if (aclList != null && aclList.getAcls() != null) {
				rsltList = new PageImpl<RestNodeACLInfo>(aclList.getAcls(), pageRequest,
						Integer.parseInt(String.valueOf(aclList.getTotalCount())));
			}
		}
		LOGGER.error("getNodeACLList failure, response:" + response.getResponseBody());
		return rsltList;
	}

	public INode getNodeInfo(UserToken user, long ownerId, long nodeId) throws BaseRunException {
		StringBuilder uri = new StringBuilder(Constants.RESOURCE_NODE);
		uri.append('/').append(ownerId).append('/').append(nodeId);
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performGetText(uri.toString(), headerMap);
		String content = response.getResponseBody();

		if (response.getStatusCode() == HttpStatus.OK.value()) {
			INode node = new INode();
			RestFolderInfo folderInfo = JsonUtils.stringToObject(content, RestFolderInfo.class);
			node.setId(folderInfo.getId());
			node.setParentId(folderInfo.getParent());
			node.setName(HtmlUtils.htmlEscape(folderInfo.getName()));
			return node;
		}

		LOGGER.error("getNodeInfo failure, response:" + response.getResponseBody());
		return null;
	}

	/**
	 * 
	 * @param ownerId
	 * @param nodeId
	 * @param userId
	 * @return
	 */
	public RestNodePermissionInfo getNodePermission(long ownerId, long nodeId, long userId) {
		StringBuilder uri = new StringBuilder(Constants.NODES_API_PERMISSION);
		uri.append('/').append(ownerId).append('/').append(nodeId).append('/').append(userId);
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performGetText(uri.toString(), headerMap);
		String content = response.getResponseBody();
		RestNodePermissionInfo pInfo = null;
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			pInfo = JsonUtils.stringToObject(content, RestNodePermissionInfo.class);
		}

		LOGGER.info("getNodePermission failure, response:" + response.getResponseBody());
		return pInfo;
	}

	/**
	 * 
	 * @param userId
	 * @param token
	 * @param pageRequest
	 * @return
	 */
	public RestTeamMemberInfo getTeamMember(Long teamId, Long teamMembershipsId) {
		RestTeamMemberInfo teamMemberInfo = null;
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamId).append("/memberships/").append(teamMembershipsId);
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performGetText(uri.toString(), headerMap);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			teamMemberInfo = JsonUtils.stringToObject(response.getResponseBody(), RestTeamMemberInfo.class);
		}

		LOGGER.error("getTeamMember failure, response:" + response.getResponseBody());
		return teamMemberInfo;
	}

	/**
	 * 
	 * @param teamId
	 * @param token
	 * @return
	 */
	public RestTeamSpaceInfo getTeamSpace(long teamId, String token) {
		RestTeamSpaceInfo restTeamSpaceInfo = null;

		try {
			StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
			uri.append('/').append(teamId);

			Map<String, String> headerMap = assembleToken();
			TextResponse response = ufmClientService.performGetText(uri.toString(), headerMap);
			String content = response.getResponseBody();

			if (response.getStatusCode() == HttpStatus.OK.value()) {
				restTeamSpaceInfo = JsonUtils.stringToObject(content, RestTeamSpaceInfo.class);

				LOGGER.info(ToStringBuilder.reflectionToString(restTeamSpaceInfo));
			} else {
				RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
				LOGGER.error(exception.getMessage());
			}

		} catch (ServiceException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return restTeamSpaceInfo;

	}

	/**
	 * 
	 * @param aclList
	 * @param ownerId
	 * @param nodeId
	 * @return
	 */
	public Page<TeamMemberPage> getUnNodeACLList(long ownerId, long nodeId, PageRequest pageRequest, String keyword,
			RestTeamMemberInfo memberInfo) {
		List<RestTeamMemberInfo> rsltList = new ArrayList<RestTeamMemberInfo>(BusinessConstants.INITIAL_CAPACITIES);
		PageRequest pRequest = new PageRequest(1, ALL_NODEACL_LIMIT);
		Page<RestNodeACLInfo> aclList = getNodeACLList(ownerId, nodeId, pRequest);
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(ownerId).append("/memberships/items");
		Map<String, String> headerMap = assembleToken();
		ListTeamSpaceMemberRequest request = new ListTeamSpaceMemberRequest();
		List<Order> order = new ArrayList<Order>(INITIAL_SIZE);
		order.add(new Order(TEAM_MEMBER_ORDER_ROLE, TEAM_MEMBER_ORDER_ASC));
		request.setLimit(ALL_MEMBER_LIMIT);
		request.setOffset(0L);
		request.setTeamRole("");
		request.setOrder(order);
		request.setKeyword(keyword);
		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap, request);
		String content = response.getResponseBody();
		RestTeamMemberList memberList = null;
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			memberList = JsonUtils.stringToObject(content, RestTeamMemberList.class);
		}

		// No member has authorized the situation
		if (aclList.getSize() <= 0) {
			if (isMembershipsNotNull(memberList)) {
				for (RestTeamMemberInfo mList : memberList.getMemberships()) {
					fillUnNodeACLList(memberInfo, rsltList, mList);
				}
			}
		} else {
			// There have been authorized members, members have been authorized
			// to filter
			// out
			if (isMembershipsNotNull(memberList)) {
				checkAndFillUnNodeACLList(memberInfo, rsltList, aclList, memberList);
			}
		}

		Page<TeamMemberPage> pageList = new PageImpl<TeamMemberPage>(
				new ArrayList<TeamMemberPage>(BusinessConstants.INITIAL_CAPACITIES));
		List<TeamMemberPage> tempList = new ArrayList<TeamMemberPage>(BusinessConstants.INITIAL_CAPACITIES);
		long beginIndex = pageRequest.getLimit().getOffset();
		long endIndex = beginIndex + pageRequest.getLimit().getLength();
		if (!rsltList.isEmpty()) {
			for (long i = beginIndex; i < endIndex; i++) {
				if (i >= rsltList.size()) {
					break;
				}
				tempList.add(transTeamMember(rsltList.get((int) i)));
			}
			pageList = new PageImpl<TeamMemberPage>(tempList, pageRequest, rsltList.size());
		}
		return pageList;
	}

	private void checkAndFillUnNodeACLList(RestTeamMemberInfo memberInfo, List<RestTeamMemberInfo> rsltList,
			Page<RestNodeACLInfo> aclList, RestTeamMemberList memberList) {
		RestTeamMember allmember;
		boolean existFlag = false;
		for (RestTeamMemberInfo mList : memberList.getMemberships()) {
			allmember = mList.getMember();
			existFlag = isExistInACL(aclList, allmember);
			if (!existFlag) {
				fillUnNodeACLList(memberInfo, rsltList, mList);
			}
		}
	}

	private boolean isMembershipsNotNull(RestTeamMemberList memberList) {
		return memberList != null && memberList.getMemberships() != null;
	}

	private boolean isExistInACL(Page<RestNodeACLInfo> aclList, RestTeamMember allmember) {
		boolean existFlag = false;
		for (RestNodeACLInfo tmember : aclList) {
			if (tmember.getUser().getType().equals(allmember.getType())
					&& (Constants.SPACE_TYPE_SYSTEM.equals(allmember.getType())
							|| Constants.SPACE_TYPE_PUBLIC.equals(allmember.getType()))) {
				existFlag = true;
				break;
			}
			if (tmember.getUser().getType().equals(allmember.getType())
					&& tmember.getUser().getId().longValue() == allmember.getId().longValue()) {
				existFlag = true;
				break;
			}
		}
		return existFlag;
	}

	/**
	 * 
	 * @param userId
	 * @param token
	 * @param pageRequest
	 * @return
	 */
	public Page<RestTeamMemberInfo> listTeamMembers(Long teamId, ListTeamSpaceMemberRequest listRequest,
			PageRequest pageRequest) {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamId).append("/memberships/items");
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap, listRequest);

		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String content = response.getResponseBody();
			if (content != null
					&& StringUtils.isNotBlank(content.substring(content.indexOf("[") + 1, content.indexOf("]")))) {
				RestTeamMemberList restTeamMemberList = JsonUtils.stringToObject(content, RestTeamMemberList.class);
				// fillTeamMemberRole(teamId, restTeamMemberList);
				return new PageImpl<RestTeamMemberInfo>(restTeamMemberList.getMemberships(), pageRequest,
						Integer.parseInt(String.valueOf(restTeamMemberList.getTotalCount())));
			}
		}
		List<RestTeamMemberInfo> list = new ArrayList<RestTeamMemberInfo>(BusinessConstants.INITIAL_CAPACITIES);
		return new PageImpl<RestTeamMemberInfo>(list, pageRequest, list.size());
	}

	public List<RestTeamMemberInfo> listTeamMembers(Long teamId, ListTeamSpaceMemberRequest listRequest) {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamId).append("/memberships/items");
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap, listRequest);

		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String content = response.getResponseBody();
			if (content != null
					&& StringUtils.isNotBlank(content.substring(content.indexOf("[") + 1, content.indexOf("]")))) {
				RestTeamMemberList restTeamMemberList = JsonUtils.stringToObject(content, RestTeamMemberList.class);
				// fillTeamMemberRole(teamId, restTeamMemberList);
				return restTeamMemberList.getMemberships();
			}
		}
		List<RestTeamMemberInfo> list = new ArrayList<RestTeamMemberInfo>(BusinessConstants.INITIAL_CAPACITIES);
		return list;
	}

	/**
	 * 
	 * @param userId
	 * @param token
	 * @param pageRequest
	 * @return
	 */
	public Page<RestTeamMemberInfo> listTeamSpace(ListTeamSpaceRequest listRequest, String token,
			PageRequest pageRequest) {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append("/items");

		Map<String, String> headerMap = assembleToken();

		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap, listRequest);
		String content = response.getResponseBody();
		List<RestTeamMemberInfo> list = new ArrayList<RestTeamMemberInfo>(BusinessConstants.INITIAL_CAPACITIES);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			if (StringUtils.isNotBlank(content.substring(content.indexOf("[") + 1, content.indexOf("]")))) {
				RestTeamMemberList restTeamSpaceList = JsonUtils.stringToObject(content, RestTeamMemberList.class);
				return new PageImpl<RestTeamMemberInfo>(restTeamSpaceList.getMemberships(), pageRequest,
						Integer.parseInt(String.valueOf(restTeamSpaceList.getTotalCount())));
			}
		}
		return new PageImpl<RestTeamMemberInfo>(list, pageRequest, list.size());
	}

	/**
	 * 
	 * @param ownerId
	 * @param nodeId
	 * @param user
	 * @param role
	 * @return
	 */
	public RestNodeACLInfo modifyNodeACL(long ownerId, long nodeId, long aclId, RestTeamMember user, String role)
			throws RestException {
		StringBuilder uri = new StringBuilder(Constants.NODES_API_ACL);
		uri.append('/').append(ownerId);
		uri.append('/').append(aclId);
		Map<String, String> headerMap = assembleToken();

		RestNodeACLModifyRequest request = new RestNodeACLModifyRequest();
		request.setRole(role);
		TextResponse response = ufmClientService.performJsonPutTextResponse(uri.toString(), headerMap, request);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return JsonUtils.stringToObject(response.getResponseBody(), RestNodeACLInfo.class);
		}
		RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
		throw exception;
	}

	/**
	 * 
	 * @param teamId
	 * @param token
	 * @return
	 */
	public RestTeamMemberInfo modifyTeamMemberRole(Long teamId, Long teamMemberId, String token,
			RestTeamMemberModifyRequest modifyRequest) {
		StringBuilder uri = new StringBuilder();
		uri.append(Constants.API_TEAM_SPACES).append('/').append(teamId).append("/memberships/").append(teamMemberId);
		Map<String, String> headerMap = assembleToken();
		TextResponse response = ufmClientService.performJsonPutTextResponse(uri.toString(), headerMap, modifyRequest);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return JsonUtils.stringToObject(response.getResponseBody(), RestTeamMemberInfo.class);
		}
		RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
		throw exception;
	}

	/**
	 * 
	 * @param teamId
	 * @param token
	 * @param spaceModifyRequest
	 * @return
	 */
	public RestTeamSpaceInfo modifyTeamSpace(long teamId, String token, RestTeamSpaceModifyRequest spaceModifyRequest)
			throws RestException {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamId);
		Map<String, String> headerMap = assembleToken();

		TextResponse response = ufmClientService.performJsonPutTextResponse(uri.toString(), headerMap,
				spaceModifyRequest);
		String content = response.getResponseBody();

		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return JsonUtils.stringToObject(content, RestTeamSpaceInfo.class);
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}

	/**
	 * @param teamSpaceId
	 * @param request
	 * @throws RestException
	 */
	public void setAttribute(long teamSpaceId, SetTeamSpaceAttrRequest request) throws RestException {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
		uri.append('/').append(teamSpaceId).append("/attributes");
		Map<String, String> headerMap = assembleToken();

		TextResponse response = ufmClientService.performJsonPutTextResponse(uri.toString(), headerMap, request);
		String content = response.getResponseBody();

		if (response.getStatusCode() != HttpStatus.OK.value()) {
			RestException exception = JsonUtils.stringToObject(content, RestException.class);
			throw exception;
		}
	}

	private Map<String, String> assembleToken() {
		Map<String, String> headers = new HashMap<String, String>(1);
		headers.put(RestConstants.HEADER_AUTHORIZATION, getUserTokenManager().getToken());
		return headers;
	}

	private void fillUnNodeACLList(RestTeamMemberInfo memberInfo, List<RestTeamMemberInfo> rsltList,
			RestTeamMemberInfo mList) {
		String teamRole = mList.getTeamRole();
		if (TEAM_ROLE_ADMIN.equals(memberInfo.getTeamRole())) {
			if (!TEAM_ROLE_ADMIN.equalsIgnoreCase(teamRole)) {
				rsltList.add(mList);
			}
		} else if (TEAM_ROLE_MANAGER.equals(memberInfo.getTeamRole())) {
			if (TEAM_ROLE_MEMBER.equalsIgnoreCase(teamRole)) {
				rsltList.add(mList);
			} else if (TEAM_ROLE_MANAGER.equalsIgnoreCase(teamRole)) {
				Long memberId = memberInfo.getId();
				Long listId = mList.getId();
				if (memberId != null && listId != null && (memberId.longValue() == listId.longValue())) {
					rsltList.add(mList);
				} else if (memberId == null && listId == null) {
					throw new BusinessException();
				}
			}
		}
	}

	private String getErrorCodeFromResponse(TextResponse response) {
		Map<String, Object> temp = JsonUtils.stringToMap(response.getResponseBody());
		return temp != null ? temp.get("code").toString() : null;
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
	
	   /**
     * 
     * @param ownerId
     * @param nodeId
     * @param userId
     * @return
     */
    public String modifyNodeIsVisible(long ownerId, long nodeId, long userId,long isavalible)
    {
        StringBuilder uri = new StringBuilder(Constants.NODES_API_ACL);
        uri.append("/isVisible/").append(ownerId).append('/').append(nodeId);
        Map<String, String> headerMap = assembleToken();
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap,isavalible);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return String.valueOf(response.getStatusCode());
        }
        
        LOGGER.error("modifyNodeIsVisible failure, response:" + response.getResponseBody());
        return getErrorCodeFromResponse(response);
    }
    
    public String getNodeIsVisible(Long ownerId, Long nodeId) {
        StringBuilder uri = new StringBuilder(Constants.NODES_API_ACL);
        uri.append('/').append(ownerId).append('/').append(nodeId);
        Map<String, String> headerMap = assembleToken();
        TextResponse response = ufmClientService.performGetText(uri.toString(), headerMap);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return String.valueOf(response.getResponseBody());
        }
        
        LOGGER.error("modifyNodeIsVisible failure, response:" + response.getResponseBody());
        return getErrorCodeFromResponse(response);
    }

	public boolean getTeamSpaceByName(RestGroupRequest groupRequest) {
		StringBuilder uri = new StringBuilder(Constants.API_TEAM_SPACES);
        uri.append('/').append("checkname");
        Map<String, String> headerMap = assembleToken();
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap, groupRequest);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
        	RestTeamSpaceInfo teamSpace = JsonUtils.stringToObject(response.getResponseBody(), RestTeamSpaceInfo.class);
            return teamSpace != null;
        }
		return false;
	}
}
