package pw.cdmi.box.disk.group.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.box.disk.client.api.UserClient;
import pw.cdmi.box.disk.core.domain.PageV2;
import pw.cdmi.box.disk.core.domain.RestGroupMember;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.group.domain.GroupMembershipsInfoV2;
import pw.cdmi.box.disk.group.service.GroupMembershipService;
import pw.cdmi.box.disk.group.service.GroupService;
import pw.cdmi.box.disk.httpclient.rest.request.GroupMemberOrderRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestAddGroupRequest;
import pw.cdmi.box.disk.httpclient.rest.response.RestGroupResponse;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.box.disk.utils.FilesCommonUtils;

@Controller
@RequestMapping(value = "group/memberships")
public class GroupMembershipController extends CommonController
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupMembershipController.class);
    
    @Autowired
    private GroupMembershipService groupMembershipService;
    
    @Resource
    private RestClient uamClientService;
    
    private UserClient userClient;
    
    @Autowired
    private GroupService groupService;
    
    @Autowired
    private UserService userService;
    
    private static final int INITIAL_SIZE = 10;
    
    @PostConstruct
    public void init()
    {
        userClient = new UserClient(uamClientService);
    }
    
    @RequestMapping(value = "addMember", method = RequestMethod.POST)
    public ResponseEntity<?> addMembersAndSendMails(Long groupId, String trunkUsersInfo,
        @RequestParam(value = "groupRole", defaultValue = "member") String groupRole,
        HttpServletRequest request)
    {
        super.checkToken(request);
        if ("none".equals(groupRole))
        {
            groupRole = "member";
        }
        
        List<Integer> successList = new ArrayList<Integer>(INITIAL_SIZE);
        List<String> users = CommonTools.decodeTrunkDate(trunkUsersInfo);
        if (users == null)
        {
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        
        HashSet<String> set = new HashSet<String>(users);
        users.clear();
        users.addAll(set);
        
        try
        {
            groupService.getGroupInfoV2(groupId);
            String retCode = executeAddMembers(users, groupRole, groupId, successList);
            if (retCode == null || successList.size() == set.size())
            {
                return new ResponseEntity<String>("OK", HttpStatus.OK);
            }
            if (successList.isEmpty())
            {
                return new ResponseEntity<String>(retCode, HttpStatus.OK);
            }
            return new ResponseEntity<String>("P_OK", HttpStatus.OK);
        }
        catch (RestException restException)
        {
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(restException.getCode()),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "deleteMember", method = RequestMethod.GET)
    public ResponseEntity<?> deleteMember(Long groupId, Long userId)
    {
        try
        {
            
            if (getCurrentUser().getCloudUserId().longValue() == userId)
            {
                groupMembershipService.deleteMember(groupId, userId);
                return new ResponseEntity<String>("Delete Itself", HttpStatus.OK);
            }
            TextResponse response = groupMembershipService.deleteMember(groupId, userId);
            return new ResponseEntity<Integer>(response.getStatusCode(), HttpStatus.OK);
        }
        catch (RestException restException)
        {
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(restException.getCode()),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "executeUpdateGroupRole", method = RequestMethod.POST)
    public ResponseEntity<?> excecuteUpdateMemberGroupRole(Long groupId, Long userId,
        @RequestParam(value = "destGroupRole", defaultValue = "member") String destGroupRole,
        HttpServletRequest request)
    {
        super.checkToken(request);
        if (!checkGroupRoleValid(destGroupRole))
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        try
        {
            groupMembershipService.updateGroupRole(groupId, userId, destGroupRole);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RestException restException)
        {
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(restException.getCode()),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
    
    @RequestMapping(value = "/items/{groupId}")
    public ResponseEntity<?> listMember(@PathVariable(value = "groupId") Long id, Integer limit,
        String keyword, String groupRole, Long page)
    {
        try
        {
            if (null == page)
            {
                page = 1L;
            }
            FilesCommonUtils.checkPositiveIntegers(page);
            if (null == limit)
            {
                limit = 100;
            }
            Long offset = (page - 1) * limit;
            GroupMemberOrderRequest request = new GroupMemberOrderRequest(limit, offset);// limit,offset,field,direction
            if (StringUtils.isBlank(groupRole))
            {
                request.setGroupRole("all");
            }
            else
            {
                request.setGroupRole(groupRole);// groupRole
            }
            request.setKeyword(keyword);// keyword
            
            PageV2<RestGroupMember> groupPage = new PageV2<RestGroupMember>();
            groupPage.setPage(page);
            
            groupPage = groupMembershipService.listPageMember(groupPage, request, id);
            return new ResponseEntity<PageV2<RestGroupMember>>(groupPage, HttpStatus.OK);
        }
        catch (RestException restException)
        {
            return new ResponseEntity<String>(restException.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "openGroupMemberMgr/{groupId}", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public String openGroupMemberMgr(@PathVariable(value = "groupId") Long groupId, Model model)
    {
        UserToken userInfo = getCurrentUser();
        long userId = userInfo.getCloudUserId();
        boolean isMemberInfo = false;
        List<GroupMembershipsInfoV2> memberships;
        try
        {
            memberships = groupMembershipService.listMember(groupId).getMemberships();
        }
        catch (RestException e)
        {
            if (StringUtils.equals(e.getCode(), "Forbidden"))
            {
                return "user/settings/groupMemberError";
            }
            memberships = Collections.EMPTY_LIST;
        }
        
        for (GroupMembershipsInfoV2 groupMembership : memberships)
        {
            if (groupMembership.getMember().getUserId() == userId)
            {
                model.addAttribute("member", groupMembership.getMember());
                isMemberInfo = true;
                break;
            }
        }
        
        if (!isMemberInfo)
        {
            return "user/settings/groupMemberError";
        }
        model.addAttribute("groupId", groupId);
        return "user/settings/groupMemberMgr";
    }
    
    @RequestMapping(value = "updateGroupRole", method = RequestMethod.GET)
    public ResponseEntity<?> updateUserGroupRole(String loggerGroupRole, String originalGroupRole,
        Long groupId, Long userId,
        @RequestParam(value = "destGroupRole", defaultValue = "member") String destGroupRole)
    {
        if ("admin".equals(loggerGroupRole) && !"admin".equals(originalGroupRole))
        {
            if ("admin".equals(destGroupRole))
            {
                return new ResponseEntity<String>("Up2Admin", HttpStatus.OK);
            }
            return new ResponseEntity<String>("OK", HttpStatus.OK);
        }
        
        else if ("manager".equals(loggerGroupRole) && "member".equals(originalGroupRole))
        {
            return new ResponseEntity<String>("Up2Manager", HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
    }
    
    @RequestMapping(value = "sendMemberMail", method = RequestMethod.POST)
    public void sendMemberMail(Long groupId, Long userId, HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken userInfo = getCurrentUser();
        if (userInfo.getCloudUserId().longValue() != userId.longValue())
        {
            User user = userService.getUserByCloudUserId(userId);
            RestGroupResponse group = groupService.getGroupInfoV2(groupId);
            try
            {
                groupMembershipService.sendDeleteMemberMail(userInfo, group, user.getEmail(), null);
            }
            catch (BaseRunException e)
            {
                LOGGER.warn("send email failed");
            }
        }
    }
    
    @RequestMapping(value = "sendAdminMgrMail", method = RequestMethod.POST)
    public void sendAdminMgrMail(Long groupId, HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken userInfo = getCurrentUser();
        RestGroupResponse group = groupService.getGroupInfoV2(groupId);
        
        RestGroupMember member;
        for (GroupMembershipsInfoV2 groupMembership : groupMembershipService.listMember(groupId)
            .getMemberships())
        {
            member = groupMembership.getMember();
            if (userInfo.getCloudUserId().longValue() == member.getUserId().longValue())
            {
                continue;
            }
            if (!"member".equalsIgnoreCase(member.getGroupRole()))
            {
                String email = userService.getUserByCloudUserId(member.getUserId()).getEmail();
                try
                {
                    groupMembershipService.sendQuitGroupMail(userInfo, group, email, null);
                }
                catch (BaseRunException e)
                {
                    LOGGER.warn("send email failed");
                }
            }
        }
    }
    
    private String executeAddMembers(List<String> users, String groupRole, Long groupId,
        List<Integer> successList)
    {
        String userType;
        Long userId;
        String tempCode;
        String retCode = null;
        RestAddGroupRequest request = new RestAddGroupRequest();
        RestGroupMember member = new RestGroupMember();
        for (int i = 0; i < users.size(); i++)
        {
            if (StringUtils.isNotBlank(users.get(i)))
            {
                userType = getType(users.get(i));// userType
                try
                {
                    userId = Long.parseLong(getUserId(users.get(i)));// userId
                }
                catch (Exception e)
                {
                    userId = userClient.createUserFromLdap(getToken(), getLoginName(users.get(i)))
                        .getCloudUserId();
                }
                member.setUserId(userId);
                member.setUserType(userType);
                request.setMember(member);
                request.setGroupRole(groupRole);// groupRole
                tempCode = groupMembershipService.addMember(request, groupId);
                if (!String.valueOf(HttpStatus.CREATED.value()).equals(tempCode))
                {
                    retCode = tempCode;
                    continue;
                }
                successList.add(HttpStatus.CREATED.value());
            }
        }
        return retCode;
    }
    
    private String getType(String userInfoMsg)
    {
        int begin = userInfoMsg.indexOf("[");
        int end = userInfoMsg.indexOf("]");
        return userInfoMsg.substring(begin + 1, end);
    }
    
    private String getUserId(String userInfoMsg)
    {
        int begin = userInfoMsg.lastIndexOf("[");
        int end = userInfoMsg.lastIndexOf("]");
        return userInfoMsg.substring(begin + 1, end);
    }
    
    private String getLoginName(String userName) throws RestException
    {
        int begin = userName.indexOf("]");
        int end = userName.lastIndexOf("[");
        return userName.substring(begin + 1, end);
    }
    
    private boolean checkGroupRoleValid(String role)
    {
        if (!"admin".equals(role) && !"manager".equals(role) && !"member".equals(role))
        {
            return false;
        }
        return true;
    }
}
