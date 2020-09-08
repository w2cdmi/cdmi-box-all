package pw.cdmi.box.disk.teamspace.web;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import pw.cdmi.box.disk.accountrole.domain.PageNodeRoleInfo;
import pw.cdmi.box.disk.accountrole.service.AccountRoleService;
import pw.cdmi.box.disk.client.api.TeamSpaceClient;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.files.domain.Path;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.teamspace.domain.*;
import pw.cdmi.box.disk.teamspace.service.TeamSpaceService;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.CharEscapeUtil;
import pw.cdmi.box.disk.utils.CustomUtils;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/teamspace/file")
public class TeamSpaceFileController extends CommonController
{
    private static final long ROOT_FOLDER_ID = 0L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamSpaceFileController.class);
    
    private static final int GRANT_AUTHORITY_PAGE_SIZE = 10;
    
    private TeamSpaceClient teamSpaceHttpClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Resource
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private AccountRoleService accountRoleService;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "changeFolderAuth", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Boolean> changeFolderAuth(long ownerId, long nodeId, long aclId, String userId,
        String authType, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            if (userId == null)
            {
                return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
            }
            RestTeamMember member = new RestTeamMember();
            member.setId(Long.parseLong(userId));
            member.setType(Constants.SPACE_TYPE_USER);
            RestNodeACLInfo aclInfo = teamSpaceHttpClient.modifyNodeACL(ownerId,
                nodeId,
                aclId,
                member,
                authType);
            if (aclInfo != null)
            {
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
        catch (RestException e)
        {
            LOGGER.error("", e);
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "deleteFolderAuth", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteFolderAuth(long ownerId, long aclId, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            String deleteFlag = teamSpaceHttpClient.deleteNodeACL(ownerId, aclId);
            if (StringUtils.equals(deleteFlag, String.valueOf(HttpStatus.OK.value())))
            {
                return new ResponseEntity<String>("OK", HttpStatus.OK);
            }
            return new ResponseEntity<String>(deleteFlag, HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "copyAndMove/{destOwnerId}", method = RequestMethod.GET)
    public String enterCopyAndMove(@PathVariable("destOwnerId") long destOwnerId, 
        @RequestParam(defaultValue = "", required = false, value = "startPoint") String startPoint,
        @RequestParam(defaultValue = "", required = false, value = "endPoint") String endPoint,
        Model model, HttpServletRequest request)
    {
        RestTeamSpaceInfo teamSpace = teamSpaceHttpClient.getTeamSpace(destOwnerId, getToken());
        
        model.addAttribute("startPoint", startPoint);
        model.addAttribute("endPoint", endPoint);
        model.addAttribute("teamName", HtmlUtils.htmlEscape(teamSpace.getName()));
        model.addAttribute("destOwnerId", destOwnerId);
        return "teamspace/copyAndMove";
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/grantAuthority/{ownerId}/{nodeId}", method = RequestMethod.POST)
    public ResponseEntity<Page<NodeACLPage>> getNodeACLList(@PathVariable Long ownerId,
        @PathVariable Long nodeId, @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "pageSize", defaultValue = GRANT_AUTHORITY_PAGE_SIZE + "") Integer pageSize,
        @RequestParam String keyword, HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken user = getCurrentUser();
        RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(ownerId,
            user.getLoginName(),
            null,
            user.getLoginName());
        if (memberInfo == null)
        {
            return new ResponseEntity<Page<NodeACLPage>>(new PageImpl<NodeACLPage>(
                new ArrayList<NodeACLPage>(BusinessConstants.INITIAL_CAPACITIES)), HttpStatus.OK);
        }
        int wPage = (page == null) ? 1 : page;
        PageRequest hasGrantPageRequest = new PageRequest(wPage, pageSize);
        try
        {
            Page<RestNodeACLInfo> hConts = teamSpaceHttpClient.getNodeACLList(ownerId,
                nodeId,
                hasGrantPageRequest);
            
            List<NodeACLPage> restNodeACL = new ArrayList<NodeACLPage>((int) hConts.getTotalElements());
            for (RestNodeACLInfo s : hConts.getContent())
            {
                restNodeACL.add(transNodeACL(s));
            }
            Page<NodeACLPage> pageParam = new PageImpl<NodeACLPage>(restNodeACL, hasGrantPageRequest,
                hConts.getTotalElements());
            return new ResponseEntity<Page<NodeACLPage>>(pageParam, HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<Page<NodeACLPage>>(new PageImpl<NodeACLPage>(
                new ArrayList<NodeACLPage>(BusinessConstants.INITIAL_CAPACITIES)), HttpStatus.OK);
        }
    }
    
   
    
    @RequestMapping(value = "/nodePermission", method = RequestMethod.GET)
    public ResponseEntity<?> getNodePermission(Long ownerId, Long nodeId, HttpServletRequest request)
    {
        UserToken user = getCurrentUser();
        try
        {
            RestNodePermissionInfo pInfo = teamSpaceHttpClient.getNodePermission(ownerId,
                nodeId,
                user.getCloudUserId());
            if (pInfo != null)
            {
                return new ResponseEntity<RestACL>(pInfo.getPermissions(), HttpStatus.OK);
            }
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        catch (RestException e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "getPaths/{ownerId}/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPaths(@PathVariable("ownerId") long ownerId, @PathVariable("id") long id,
        HttpServletRequest request)
    {
        if (id == INode.FILES_ROOT)
        {
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        try
        {
            List<Path> paths = new ArrayList<Path>(BusinessConstants.INITIAL_CAPACITIES);
            buildNodePath(getCurrentUser(), ownerId, paths, id, request);
            return new ResponseEntity<List<Path>>(paths, HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/modifyNodeIsVisible/{ownerId}/{nodeId}", method = RequestMethod.POST)
    public ResponseEntity<?> modifyNodeIsVisible(@PathVariable Long ownerId,@PathVariable Long nodeId,
    		HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken user = getCurrentUser();
        String  isavalible=request.getParameter("isavalible");
        String result = teamSpaceHttpClient.modifyNodeIsVisible(ownerId,
            nodeId,user.getId(),Long.parseLong(isavalible));
        if (StringUtils.equals(result, String.valueOf(HttpStatus.OK.value())))
        {
            return new ResponseEntity<String>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
    }
    
    @RequestMapping(value = "/getIsVisibleNodeACL/{ownerId}/{nodeId}", method = RequestMethod.GET)
    public ResponseEntity<?> getIsVisibleNodeACL(@PathVariable Long ownerId,@PathVariable Long nodeId,
    		HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken user = getCurrentUser();
        try
        {
        	String result = teamSpaceHttpClient.getNodeIsVisible(ownerId, nodeId);
        	return new ResponseEntity<String>(result, HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
        
        
    }
    
    
    
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/unGrantAuthority/{ownerId}/{nodeId}", method = RequestMethod.POST)
    public ResponseEntity<Page<TeamMemberPage>> getUnNodeACLList(@PathVariable Long ownerId,
        @PathVariable Long nodeId, @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "pageSize", defaultValue = GRANT_AUTHORITY_PAGE_SIZE + "") Integer pageSize,
        @RequestParam String keyword, HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken userInfo = getCurrentUser();
        RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(ownerId,
            userInfo.getLoginName(),
            null,
            userInfo.getLoginName());
        
        if (memberInfo == null)
        {
            return new ResponseEntity<Page<TeamMemberPage>>(new PageImpl<TeamMemberPage>(
                new ArrayList<TeamMemberPage>(BusinessConstants.INITIAL_CAPACITIES)), HttpStatus.OK);
        }
        
        int wPage = (page == null) ? 1 : page;
        PageRequest notGrantPageRequest = new PageRequest(wPage, pageSize);
        
        try
        {
            Page<TeamMemberPage> nConts = teamSpaceHttpClient.getUnNodeACLList(ownerId,
                nodeId,
                notGrantPageRequest,
                keyword,
                memberInfo);
            return new ResponseEntity<Page<TeamMemberPage>>(nConts, HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<Page<TeamMemberPage>>(new PageImpl<TeamMemberPage>(
                new ArrayList<TeamMemberPage>(BusinessConstants.INITIAL_CAPACITIES)), HttpStatus.OK);
        }
    }
    
    
    
    @RequestMapping(value = "/grantAuthority/{ownerId}/{nodeId}", method = RequestMethod.GET)
    public String gotoGrantAuthority(@PathVariable Long ownerId, @PathVariable Long nodeId, String viewMode,
        Model model)
    {
        UserToken user = getCurrentUser();
        RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(ownerId,
            user.getLoginName(),
            null,
            user.getLoginName());
        if (memberInfo == null)
        {
            model.addAttribute("exceptionName", "Forbidden");
            return "teamspace/spacePromptError";
        }
        
        INode inode = teamSpaceHttpClient.getNodeInfo(getCurrentUser(), ownerId, nodeId);
        if (inode != null)
        {
            model.addAttribute("fileName", inode.getName());
        }
        RestTeamSpaceInfo teamSpace = memberInfo.getTeamspace();
        model.addAttribute("memberInfo", memberInfo);
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("nodeId", nodeId);
        model.addAttribute("teamType",teamSpace.getType());
        List<PageNodeRoleInfo> systemRoles = accountRoleService.listAccountRoles(user.getAccountId());
        
        model.addAttribute("systemRoles", systemRoles);
        
        return "teamspace/grantAuthority";
    }

    @RequestMapping(value = "grantAuthToFolder", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> grantAuthToFolder(long ownerId, long nodeId, String users, String authType,
                                               HttpServletRequest request) {
        super.checkToken(request);
        try {
            if (StringUtils.isBlank(users)) {
                return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
            }

            List<RestTeamMember> userList = new ArrayList<>();


            JSONArray jsonUsers=JSONArray.readFrom(users);
           
            List<String> messages = new ArrayList<String>(BusinessConstants.INITIAL_CAPACITIES);

            for (int i = 0; i < jsonUsers.size(); i++) {
            	JSONObject jsonUser = jsonUsers.getJSONObject(i);
            	RestTeamMember restTeamMember = new RestTeamMember();
            	restTeamMember.setLoginName(jsonUser.getString("username"));
            	restTeamMember.setName(jsonUser.getString("username"));
            	restTeamMember.setType(jsonUser.getString("type"));
            	restTeamMember.setId(jsonUser.getLong("userId", 0));
                userList.add(restTeamMember);
            }

            if (userList.isEmpty()) {
                LOGGER.warn("Create acl failed. no user is specified.");
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
            }

            teamSpaceHttpClient.addNodeACL(ownerId, nodeId, userList, authType);

            if (messages.size() == jsonUsers.size()) {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            } else if (!messages.isEmpty()) {
                return new ResponseEntity<>("P_OK", HttpStatus.OK);
            }
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException", e);
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        } catch (RestException e) {
            LOGGER.info("RestException", e);
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostConstruct
    public void init()
    {
        teamSpaceHttpClient = new TeamSpaceClient(ufmClientService);
    }
    
    @RequestMapping(value = "/{teamId}", method = RequestMethod.GET)
    public String listSpaceDetail(Model model, @PathVariable long teamId, HttpServletRequest request)
    {
        UserToken user = getCurrentUser();
        
        RestTeamSpaceInfo teamSpace = teamSpaceHttpClient.getTeamSpace(teamId, getToken());
        if (teamSpace == null)
        {
            model.addAttribute("exceptionName", "NoSuchTeamSpace");
            return "teamspace/teamSpace404";
        }
        
        RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(teamId,
            user.getLoginName(),
            null,
            user.getLoginName());
        if (memberInfo == null)
        {
            model.addAttribute("exceptionName", "Forbidden");
            return "teamspace/teamSpace404";
        }
        model.addAttribute("teamId", teamId);
        model.addAttribute("memberInfo", memberInfo);
        model.addAttribute("teamRole", memberInfo.getTeamRole());
        model.addAttribute("role", memberInfo.getRole());
        //面包屑根目录节点名称
        
        model.addAttribute("rootPath", CharEscapeUtil.transferString(teamSpace.getName()));
        model.addAttribute("plainTeamName", teamSpace.getName());
        if(request.getParameter("parentId")!=null&&!"".equals(request.getParameter("parentId"))){
        	 long parentId=Long.parseLong(request.getParameter("parentId"));
        	 model.addAttribute("parentId",parentId);
        }else{
        	 model.addAttribute("parentId", ROOT_FOLDER_ID);
        }
       
        model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false : CustomUtils.getValue("link.hidden"));
    	model.addAttribute("modelDocType", "");
        model.addAttribute("teamType", teamSpace.getType());

        return "teamspace/spaceDetail";
    }
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param nodes
     * @param id
     * @throws BaseRunException
     */
    private void buildNodePath(UserToken user, long ownerId, List<Path> paths, long id,
        HttpServletRequest request) throws BaseRunException
    {
        INode inode = teamSpaceHttpClient.getNodeInfo(user, ownerId, id);
        if (inode == null)
        {
            return;
        }
        if (inode.getParentId() == INode.FILES_ROOT)
        {
            paths.add(new Path(inode));
            return;
        }
        buildNodePath(user, ownerId, paths, inode.getParentId(), request);
        paths.add(new Path(inode));
    }
    
    private NodeACLPage transNodeACL(RestNodeACLInfo s)
    {
        NodeACLPage result = new NodeACLPage();
        result.setId(s.getId());
        result.setNodeId(s.getResource().getNodeId());
        result.setOwnerId(s.getResource().getOwnerId());
        result.setLoginName(HtmlUtils.htmlEscape(s.getUser().getLoginName()));
        result.setRole(s.getRole());
        result.setDesc(HtmlUtils.htmlEscape(s.getUser().getDescription()));
        result.setUserId(s.getUser().getId());
        result.setUsername(HtmlUtils.htmlEscape(s.getUser().getName()));
        result.setUserType(s.getUser().getType());
        return result;
    }
    
}
