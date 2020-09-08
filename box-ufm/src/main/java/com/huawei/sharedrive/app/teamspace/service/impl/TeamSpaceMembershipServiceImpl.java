package com.huawei.sharedrive.app.teamspace.service.impl;

import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.share.service.OrderComparatorUtil;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceMembershipsDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.SpaceMemberIdGenerateService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Service("teamSpaceMembershipService")
public class TeamSpaceMembershipServiceImpl implements TeamSpaceMembershipService {
	private static Logger logger = LoggerFactory.getLogger(TeamSpaceMembershipServiceImpl.class);

	@Autowired
	private GroupMembershipsService groupMembershipsService;

	@Autowired
	private TeamSpaceMembershipsDAO teamSpaceMembershipsDAO;
	
	@Autowired
	private SpaceMemberIdGenerateService spaceMemberIdGenerateService;

	@Autowired
	private DepartmentService departmentService;

	@Override
	public boolean checkTeamSpaceMemberExist(long teamSpaceId, String userId, String type) throws BaseRunException {
		TeamSpaceMemberships teamSpaceMember = teamSpaceMembershipsDAO.getByUser(teamSpaceId, userId, type);
		if (teamSpaceMember == null) {
			return false;
		}
		if (teamSpaceMember.getStatus() != TeamSpaceMemberships.STATUS_NORMAL) {
			logger.info("teamSpaceMember abnormal, teamSpaceId:" + teamSpaceId + ",id:" + teamSpaceMember.getId()
					+ ",status:" + teamSpaceMember.getStatus());
			return false;
		}
		return true;
	}

	@Override
	public TeamSpaceMemberships createTeamSpaceMember(TeamSpaceMemberships teamSpaceMemberships)
			throws BaseRunException {
		// 如果成员已经存在,抛出异常
		if (checkTeamSpaceMemberExist(teamSpaceMemberships.getCloudUserId(), String.valueOf(teamSpaceMemberships.getUserId()), teamSpaceMemberships.getUserType())) {
			if ("admin".equals(teamSpaceMemberships.getTeamRole())) {
				return teamSpaceMemberships;
			}
			teamSpaceMembershipsDAO.updateTeamSpaceMemberRole(teamSpaceMemberships);
			String msg = "TeamSpaceMember already exixt";
			throw new ExistMemberConflictException(msg);
		}

		//未指定ID，自动生成
		if(teamSpaceMemberships.getId() == 0) {
            teamSpaceMemberships.setId(spaceMemberIdGenerateService.getNextMemberId(teamSpaceMemberships.getCloudUserId()));
        }

		teamSpaceMembershipsDAO.create(teamSpaceMemberships);

		return teamSpaceMemberships;
	}

	@Override
	public void deleteAllTeamSpaceMember(long teamSpaceId) throws BaseRunException {
		teamSpaceMembershipsDAO.deleteAll(teamSpaceId);
	}

	@Override
	public void deleteTeamSpaceMember(long teamSpaceId, String userId, String userType) {
		TeamSpaceMemberships member = new TeamSpaceMemberships();
		member.setCloudUserId(teamSpaceId);
		member.setUserId(Long.parseLong(userId));
		member.setUserType(userType);

		teamSpaceMembershipsDAO.delete(member);
	}

	@Override
	public void deleteTeamSpaceMemberById(long teamSpaceId, long id) throws BaseRunException {
		TeamSpaceMemberships teamSpaceMember = teamSpaceMembershipsDAO.getById(teamSpaceId, id);

		// 判断成员是否存在
		if (teamSpaceMember == null) {
			String msg = "no such teamSpaceMember, teamSpaceId:" + teamSpaceId + ",id:" + id;
			throw new NoSuchItemsException(msg);
		}

		// 检查状态是否正常
		if (teamSpaceMember.getStatus() != TeamSpaceMemberships.STATUS_NORMAL) {
			String msg = "teamSpaceMember abnormal, teamSpaceId:" + teamSpaceId + ",id:" + id + ",status:"
					+ teamSpaceMember.getStatus();
			throw new NoSuchItemsException(msg);
		}
		teamSpaceMembershipsDAO.delete(teamSpaceMember);
	}

	@Override
	public void deleteTeamSpaceMemberByTeamIdAndUserId(long teamSpaceId, long memberId) throws BaseRunException {
		TeamSpaceMemberships teamSpaceMember = teamSpaceMembershipsDAO.getByTeamIdAndUserId(teamSpaceId, memberId);

		// 判断成员是否存在
		if (teamSpaceMember == null) {
			String msg = "no such teamSpaceMember, teamSpaceId:" + teamSpaceId + ",memberId:" + memberId;
			throw new NoSuchItemsException(msg);
		}

		// 检查状态是否正常
		if (teamSpaceMember.getStatus() != TeamSpaceMemberships.STATUS_NORMAL) {
			String msg = "teamSpaceMember abnormal, teamSpaceId:" + teamSpaceId + ",memberId:" + memberId + ",status:" + teamSpaceMember.getStatus();
			throw new NoSuchItemsException(msg);
		}
		teamSpaceMembershipsDAO.delete(teamSpaceMember);
	}

	@Override
	public List<TeamSpaceMemberships> getByUserId(String userId, String userType) {
		return teamSpaceMembershipsDAO.getByUserId(userId, userType);
	}

	@Override
	public TeamSpaceMemberships getTeamSpaceMemberById(long teamSpaceId, long id) throws BaseRunException {
		TeamSpaceMemberships teamSpaceMember = teamSpaceMembershipsDAO.getById(teamSpaceId, id);

		if (teamSpaceMember == null) {
			String msg = "no such teamSpaceMember, teamSpaceId:" + teamSpaceId + ",id:" + id;
			throw new NoSuchUserException(msg);
		}

		// 检查状态是否正常
		if (teamSpaceMember.getStatus() != TeamSpaceMemberships.STATUS_NORMAL) {
			String msg = "teamSpaceMember abnormal, teamSpaceId:" + teamSpaceId + ",id:" + id + ",status:"
					+ teamSpaceMember.getStatus();
			throw new NoSuchUserException(msg);
		}

		return teamSpaceMember;
	}

	@Override
	public TeamSpaceMemberships getByTeamIdAndUserId(long teamSpaceId, long userId) throws BaseRunException {
		return teamSpaceMembershipsDAO.getByTeamIdAndUserId(teamSpaceId, userId);
	}

	@Override
	public TeamSpaceMemberships getTeamSpaceMemberByUser(long teamSpaceId, String userId, String type)
			throws BaseRunException {
		TeamSpaceMemberships teamSpaceMember = teamSpaceMembershipsDAO.getByUser(teamSpaceId, userId, type);

		if (teamSpaceMember == null) {
			String msg = "no such teamSpaceMember, teamSpaceId:" + teamSpaceId + ", userID:" + userId + ", type:"
					+ type;
			throw new NoSuchItemsException(msg);
		}

		// 检查状态是否正常
		if (teamSpaceMember.getStatus() != TeamSpaceMemberships.STATUS_NORMAL) {
			String msg = "teamSpaceMember abnormal, cloudUserID:" + teamSpaceId + ", userID:" + userId + ", type:"
					+ type + ",status:" + teamSpaceMember.getStatus();
			throw new NoSuchItemsException(msg);
		}

		return teamSpaceMember;
	}

	@Override
	public TeamSpaceMemberships getTeamSpaceMemberByUserNoCheck(long teamSpaceId, String userId, String type)
			throws BaseRunException {
		TeamSpaceMemberships teamSpaceMember = teamSpaceMembershipsDAO.getByUser(teamSpaceId, userId, type);

		if (teamSpaceMember == null) {
			return null;
		}

		// 检查状态是否正常
		if (teamSpaceMember.getStatus() != TeamSpaceMemberships.STATUS_NORMAL) {
			String msg = "teamSpaceMember abnormal, cloudUserID:" + teamSpaceId + ", userID:" + userId + ", type:"
					+ type + ",status:" + teamSpaceMember.getStatus();
			throw new NoSuchItemsException(msg);
		}

		return teamSpaceMember;
	}

	@Override
	public long getTeamSpaceMembersCount(long teamSpaceId) throws BaseRunException {
		// 不需要过滤, 所以 teamRole, keyword 传null
		return teamSpaceMembershipsDAO.getTeamSpaceMembershipsCount(teamSpaceId);
	}

	@Override
	public TeamSpaceMemberships getUserMemberShips(long teamSpaceId, long userId, String enterpriseId) throws ForbiddenException {
		TeamSpaceMemberships result = teamSpaceMembershipsDAO.getByUser(teamSpaceId, String.valueOf(userId), TeamSpaceMemberships.TYPE_USER);

		// 群组用户会设置成管理员，故判断其角色大小
		TeamSpaceMemberships memberShipOfGroup = getBigerGroupMemberShips(teamSpaceId, userId);
		if (memberShipOfGroup != null) {
			return setMemberWithCheck(result, memberShipOfGroup);
		}

		TeamSpaceMemberships memberShipOfSystem = teamSpaceMembershipsDAO.getByUser(teamSpaceId, enterpriseId,
				TeamSpaceMemberships.TYPE_SYSTEM);
		if (memberShipOfSystem != null) {
			return setMemberWithCheck(result, memberShipOfSystem);
		}
		return result;
	}

	@Override
	public long getUserTeamSpaceCount(String userId, String type) throws BaseRunException {
		return teamSpaceMembershipsDAO.getUserTeamSpaceCount(userId, type);
	}

	@Override
	public TeamMemberList listTeamSpaceMemberships(long teamSpaceId, List<Order> orderList, Limit limit,
			String teamRole, String keyword) throws BaseRunException {
		TeamMemberList teamMemberList = new TeamMemberList();
		teamMemberList
				.setTotalCount(teamSpaceMembershipsDAO.getTeamSpaceMembershipsCount(teamSpaceId, teamRole, keyword));
		teamMemberList.setLimit(limit.getLength());
		teamMemberList.setOffset(limit.getOffset());
		// 如果order为空，则设置默认排序规则
		if (CollectionUtils.isEmpty(orderList)) {
			orderList = getDefaultOrderList();
		}
		List<TeamSpaceMemberships> itemList = teamSpaceMembershipsDAO.listTeamSpaceMemberships(teamSpaceId, orderList,
				limit, teamRole, keyword);
		teamMemberList.setTeamMemberList(itemList);
		return teamMemberList;
	}

	@Override
	public TeamMemberList listUserTeamSpaceMemberships(UserToken userToken, String userId, int type, String userType,
													   List<Order> orderList, Limit limit) throws BaseRunException {
		TeamMemberList teamMemberList = new TeamMemberList();
		// 如果order为空，则设置默认排序规则
		if (CollectionUtils.isEmpty(orderList)) {
			orderList = getDefaultOrderList();
		}

		String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}

		List<TeamSpaceMemberships> resultList = new ArrayList<TeamSpaceMemberships>(
				BusinessConstants.INITIAL_CAPACITIES);
		List<TeamSpaceMemberships> filterList = getMatchFilters(userToken, userId, type, orderList);
		filterList = getMatchFiltersByEnterpriseId(filterList, enterpriseId, type, orderList);

		long tmpOffset = limit.getOffset();
		int tmpLimit = limit.getLength();

		long totalSize = filterList.size();

		long endIndex = (tmpOffset + tmpLimit) <= totalSize ? (tmpOffset + tmpLimit) : totalSize;

		for (long i = tmpOffset; i < endIndex; i++) {
			resultList.add(filterList.get((int) i));
		}
		teamMemberList.setTotalCount(totalSize);
		teamMemberList.setLimit(limit.getLength());
		teamMemberList.setOffset(limit.getOffset());
		teamMemberList.setTeamMemberList(resultList);
		return teamMemberList;
	}

	@Override
	public TeamMemberList listUserTeamSpaceMemberships(String userId, String userType, List<Order> orderList,
			Limit limit) throws BaseRunException {
		TeamMemberList teamMemberList = new TeamMemberList();

		// 如果order为空，则设置默认排序规则
		if (CollectionUtils.isEmpty(orderList)) {
			orderList = getDefaultOrderList();
		}

		List<TeamSpaceMemberships> resultList = new ArrayList<TeamSpaceMemberships>(
				BusinessConstants.INITIAL_CAPACITIES);
		List<TeamSpaceMemberships> filterList = getMatchFilters(null, userId, null, orderList);

		long tmpOffset = limit.getOffset();
		int tmpLimit = limit.getLength();

		long totalSize = filterList.size();

		long endIndex = (tmpOffset + tmpLimit) <= totalSize ? (tmpOffset + tmpLimit) : totalSize;

		for (long i = tmpOffset; i < endIndex; i++) {
			resultList.add(filterList.get((int) i));
		}
		teamMemberList.setTotalCount(totalSize);
		teamMemberList.setLimit(limit.getLength());
		teamMemberList.setOffset(limit.getOffset());
		teamMemberList.setTeamMemberList(resultList);
		return teamMemberList;
	}

	@Override
	public TeamSpaceMemberships modifyTeamSpaceMemberRole(TeamSpaceMemberships teamSpaceMemberships)
			throws BaseRunException {
		teamSpaceMembershipsDAO.updateTeamSpaceMemberRole(teamSpaceMemberships);

		return teamSpaceMemberships;
	}

	@Override
	public void updateUsername(User user, String userType) {
		teamSpaceMembershipsDAO.updateUsername(String.valueOf(user.getId()), userType, user.getName(),
				user.getLoginName());
	}

	private TeamSpaceMemberships getBigerGroupMemberShips(long teamSpaceId, long userId) {
		List<GroupMemberships> groupMembershipses = groupMembershipsService.getUserList(null, null, userId, GroupConstants.GROUP_USERTYPE_USER, null);
		// 为了性能考虑，一次性查出所有群组类型的团队空间成员
		List<TeamSpaceMemberships> teamSpaceMembershipsList = teamSpaceMembershipsDAO.getByUserType(teamSpaceId, TeamSpaceMemberships.TYPE_GROUP);
		TeamSpaceMemberships result = null;
		for (GroupMemberships gm : groupMembershipses) {
			for (TeamSpaceMemberships teamSpaceMemberships : teamSpaceMembershipsList) {
				if (gm.getGroupId() == teamSpaceMemberships.getUserId()) {
					result = setMemberWithCheck(result, teamSpaceMemberships);
				}
			}
		}

		return result;
	}

	private List<Order> getDefaultOrderList() {
		List<Order> orderList = new ArrayList<Order>(2);
		// 默认按照时间降序排列
		orderList.add(new Order("teamRole", "asc"));
		orderList.add(new Order("createdAt", "asc"));
		return orderList;
	}

	private List<TeamSpaceMemberships> getMatchFiltersByEnterpriseId(List<TeamSpaceMemberships> filterList, String enterpriseId,int type,
			List<Order> orderList) {
		List<TeamSpaceMemberships> systemList = teamSpaceMembershipsDAO.listUserTeamSpaceMemberships(enterpriseId, type,
				TeamSpaceMemberships.TYPE_SYSTEM, null, null);
		filterList = addToListCheckTeamRole(filterList, systemList);

		OrderComparatorUtil orderComparatorUtil = null;
		int size = orderList.size();
		for (int i = 0; i < size; i++) {
			orderComparatorUtil = new OrderComparatorUtil(orderList.get(size - i - 1).getField(),
					orderList.get(size - i - 1).getDirection());
			Collections.sort(filterList, orderComparatorUtil);
		}
		
		return filterList;
	}

	private List<TeamSpaceMemberships> getMatchFilters(UserToken userToken, String userId, Integer type, List<Order> orderList) {
		List<TeamSpaceMemberships> filterList = new ArrayList<TeamSpaceMemberships>(
				BusinessConstants.INITIAL_CAPACITIES);
		// 获取自己的
		List<TeamSpaceMemberships> userList = teamSpaceMembershipsDAO.listUserTeamSpaceMemberships(userId, type,
				TeamSpaceMemberships.TYPE_USER, null, null);

		if (CollectionUtils.isNotEmpty(userList)) {
			filterList.addAll(userList);
		}

        //UFM中没有保存部门信息，需要从UAM中反向查询。
        if(userToken != null) {
            List<Long> deptList = departmentService.getDeptCloudUserIdByCloudUserId(userToken.getAccountVistor().getEnterpriseId(), userToken.getId(), userToken.getAccountId());
            for(Long deptId : deptList) {
                List<TeamSpaceMemberships> departList = teamSpaceMembershipsDAO.listUserTeamSpaceMemberships(String.valueOf(deptId), type, TeamSpaceMemberships.TYPE_DEPT, null, null);

                if (CollectionUtils.isNotEmpty(departList)) {
                    filterList.addAll(departList);
                }
            }
        }

		// 获取群组
		List<GroupMemberships> groupMembershipses = groupMembershipsService.getUserList(null, null,
				Long.valueOf(userId), GroupConstants.GROUP_USERTYPE_USER, null);

		List<TeamSpaceMemberships> groupList = null;
		for (GroupMemberships gm : groupMembershipses) {
			groupList = teamSpaceMembershipsDAO.listUserTeamSpaceMemberships(String.valueOf(gm.getGroupId()), TeamSpace.TYPE_PERSONAL,
					TeamSpaceMemberships.TYPE_GROUP, null, null);
			filterList = addToListCheckTeamRole(filterList, groupList);
		}

		OrderComparatorUtil orderComparatorUtil = null;
		int size = orderList.size();
		for (int i = 0; i < size; i++) {
			orderComparatorUtil = new OrderComparatorUtil(orderList.get(size - i - 1).getField(),
					orderList.get(size - i - 1).getDirection());
			Collections.sort(filterList, orderComparatorUtil);
		}
        //去重空间
		for  ( int  i  =   0 ; i  <  filterList.size()  -   1 ; i ++ )  {
			 for  ( int  j  =  filterList.size()  -   1 ; j  >  i; j -- )  {
				  if  (filterList.get(j).getCloudUserId()==filterList.get(i).getCloudUserId())  {
					  filterList.remove(j);
				  }
			 }
		 }

		return filterList;
	}

	private List<TeamSpaceMemberships> addToListCheckTeamRole(List<TeamSpaceMemberships> filterList,
			List<TeamSpaceMemberships> inputList) {
		if (CollectionUtils.isEmpty(inputList)) {
			return filterList;
		}
		List<TeamSpaceMemberships> result = new ArrayList<TeamSpaceMemberships>(BusinessConstants.INITIAL_CAPACITIES);

		TeamSpaceMemberships temp = null;
		int filterSize = filterList.size();
		int inputSize = inputList.size();
		boolean isFind = false;
		for (int i = 0; i < filterSize; i++) {
			for (int j = 0; j < inputSize; j++) {
				if (filterList.get(i).getCloudUserId() == inputList.get(j).getCloudUserId()) {
					isFind = true;
					temp = setMemberWithCheck(filterList.get(i), inputList.get(j));
					break;
				}
			}
			if (isFind) {
				result.add(temp);
				isFind = false;
			} else {
				result.add(filterList.get(i));
			}

		}

		isFind = false;
		for (int i = 0; i < inputSize; i++) {
			for (int j = 0; j < filterSize; j++) {
				if (filterList.get(j).getCloudUserId() == inputList.get(i).getCloudUserId()) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				result.add(inputList.get(i));
			}
			isFind = false;
		}
		return result;
	}

	private TeamSpaceMemberships setMemberWithCheck(TeamSpaceMemberships result, TeamSpaceMemberships input) {
		if (result == null) {
			return input;
		}
		if (TeamRole.ROLE_ADMIN.equals(result.getTeamRole())) {
			return result;
		}
		if (TeamRole.ROLE_ADMIN.equals(input.getTeamRole())) {
			return input;
		}

		if (TeamRole.ROLE_MANAGER.equals(result.getTeamRole())) {
			return result;
		}
		return input;

	}

	@Override
	public TeamSpaceMemberships  getTeamSpaceMemberByTeamIdAndRole(Long teamId, String userType,String role) {
		return teamSpaceMembershipsDAO.getTeamSpaceMemberByTeamIdAndRole(teamId,userType,role);
		
	}

	@Override
	public long countTeamSpaceMemberships(long teamSpaceId, List<Order> orderList, Limit limit, String teamRole, String keyword) {
		// TODO Auto-generated method stub

	    long totalCount = teamSpaceMembershipsDAO.getTeamSpaceMembershipsCount(teamSpaceId, teamRole, keyword);
		return totalCount;
	
	}

	@Override
	public TeamMemberList listTeamSpacesByOwner(UserToken userToken, String userId, int type, String userType, List<Order> orderList, Limit limit) {
		TeamMemberList teamMemberList = new TeamMemberList();
		// 如果order为空，则设置默认排序规则
		if (CollectionUtils.isEmpty(orderList)) {
			orderList = getDefaultOrderList();
		}
		List<TeamSpaceMemberships> resultList = new ArrayList<TeamSpaceMemberships>(BusinessConstants.INITIAL_CAPACITIES);
		List<TeamSpaceMemberships> filterList = teamSpaceMembershipsDAO.listUserTeamSpaceMemberships(userId, type,
				TeamSpaceMemberships.TYPE_USER, null, null);
		for(int i=filterList.size()-1;i>=0;i-- ){
			if(!filterList.get(i).getTeamRole().equals(TeamRole.ROLE_ADMIN)){
				filterList.remove(i);
			}
		}

		long tmpOffset = limit.getOffset();
		int tmpLimit = limit.getLength();

		long totalSize = filterList.size();

		long endIndex = (tmpOffset + tmpLimit) <= totalSize ? (tmpOffset + tmpLimit) : totalSize;

		for (long i = tmpOffset; i < endIndex; i++) {
			resultList.add(filterList.get((int) i));
		}
		teamMemberList.setTotalCount(totalSize);
		teamMemberList.setLimit(limit.getLength());
		teamMemberList.setOffset(limit.getOffset());
		teamMemberList.setTeamMemberList(resultList);
		return teamMemberList;
	}

}
