package pw.cdmi.box.disk.teamspace.web;

import org.apache.commons.collections.CollectionUtils;
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
import pw.cdmi.box.disk.accountbaseconfig.domain.AccountBasicConfig;
import pw.cdmi.box.disk.accountbaseconfig.server.AccountBasicConfigService;
import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.client.api.TeamSpaceClient;
import pw.cdmi.box.disk.client.api.UserClient;
import pw.cdmi.box.disk.client.domain.teamspace.GetTeamSpaceAttrResponse;
import pw.cdmi.box.disk.client.domain.teamspace.SetTeamSpaceAttrRequest;
import pw.cdmi.box.disk.client.domain.teamspace.TeamSpaceAttribute;
import pw.cdmi.box.disk.client.domain.teamspace.TeamSpaceAttributeEnum;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.group.domain.GroupConstants;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.request.RestGroupRequest;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.RestMultiUser;
import pw.cdmi.box.disk.share.service.ShareService;
import pw.cdmi.box.disk.system.service.AppBasicConfigService;
import pw.cdmi.box.disk.teamspace.domain.*;
import pw.cdmi.box.disk.teamspace.service.TeamSpaceService;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.CustomUtils;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.ForbiddenException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/teamspace")
public class TeamSpaceController extends CommonController {
	private static final int COPY_SPACE_PAGE_SIZE = 10;

	private static final long GB_BYTE = 1024 * 1024 * 1024L;

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamSpaceController.class);

	private static final String ROLE_ADMIN = "admin";

	private static final String ROLE_MANAGER = "manager";

	private static final String TEAM_ORDER_ASC = "ASC";

	private static final String TEAM_ORDER_DESC = "DESC";

	private static final String TEAM_ORDER_FIELD_CREATEDAT = "createdAt";

	private static final String TEAM_ORDER_FIELD_TEAMROLE = "teamRole";

	@Autowired
	private AppBasicConfigService appBasicConfigService;

	@Autowired
	private AuthAppService authAppService;

	private boolean enableLdap = true;

	@Autowired
	private ShareService shareService;

	private TeamSpaceClient teamSpaceHttpClient;

	@Resource
	private TeamSpaceService teamSpaceService;

	@Resource
	private RestClient uamClientService;

	@Resource
	private RestClient ufmClientService;

	private UserClient userClient;

	@Autowired
	private UserService userService;

	@Autowired
	private AccountBasicConfigService accountBasicConfigService;

	@RequestMapping(value = "/changeOwner", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> changeOwner(long teamId, long cloudUserId, HttpServletRequest request) {
		super.checkToken(request);
		try {
			teamSpaceHttpClient.changeOwner( teamId,cloudUserId, getToken());
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (ForbiddenException e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
		} catch (BaseRunException e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/createTeamSpace", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> createTeamSpace(RestTeamSpaceCreateRequest restTeamSpaceCreateRequest,
			HttpServletRequest httpServletRequest,Model model) {
		try {
			super.checkToken(httpServletRequest);
			UserToken userInfo = getCurrentUser();
			User newOwner = userService.getUserByCloudUserId(userInfo.getCloudUserId());
			if (newOwner.getTeamSpaceFlag() == User.TEAMSPACE_FLAG_UNSET) {
				return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
			}

			if (!checkMaxTeamSpaces(newOwner)) {
				return new ResponseEntity<String>(HttpStatus.INSUFFICIENT_STORAGE);
			}
			AppBasicConfig config = appBasicConfigService.getAppBasicConfig(authAppService.getCurrentAppId());
			AccountBasicConfig accountBasicConfig = new AccountBasicConfig();
			accountBasicConfig.setAccountId(userInfo.getAccountId());
			accountBasicConfig = accountBasicConfigService.get(accountBasicConfig, authAppService.getCurrentAppId());

			if (!accountBasicConfig.isEnableTeamSpace()) {
				restTeamSpaceCreateRequest.setMaxMembers(-1);
				restTeamSpaceCreateRequest.setMaxVersions(-1);
				restTeamSpaceCreateRequest.setSpaceQuota((long) -1);
			} else {

				if (!accountBasicConfig.getTeamSpaceQuota().equals("-1")) {
					config.setTeamSpaceQuota(Long.parseLong(accountBasicConfig.getTeamSpaceQuota()));
				}
				if (!accountBasicConfig.getTeamSpaceVersions().equals("-1")) {
					config.setMaxFileVersions(Integer.parseInt(accountBasicConfig.getTeamSpaceVersions()));
				}
				Integer value = config.getTeamSpaceMaxMembers();
				if (value != null) {
					restTeamSpaceCreateRequest.setMaxMembers(value);
				}

				value = config.getMaxFileVersions();
				if (value != null) {
					restTeamSpaceCreateRequest.setMaxVersions(value);
				}

				Long spaceQuota = config.getTeamSpaceQuota();
				if (spaceQuota != null && spaceQuota != -1) {
					restTeamSpaceCreateRequest.setSpaceQuota(spaceQuota * GB_BYTE);
				}
			}

			String setUploadNotice = restTeamSpaceCreateRequest.getUploadNotice();

			restTeamSpaceCreateRequest.setUploadNotice(null);
			RestTeamSpaceInfo teamSpace = teamSpaceHttpClient.createTeamSpace(getToken(), restTeamSpaceCreateRequest);

			if (StringUtils.isNotBlank(setUploadNotice)) {
				SetTeamSpaceAttrRequest request = new SetTeamSpaceAttrRequest(
						TeamSpaceAttributeEnum.UPLOADNOTICE.getName(), setUploadNotice);
				teamSpaceHttpClient.setAttribute(teamSpace.getId(), request);
			}
			return new ResponseEntity<String>(teamSpace.getId()+"",HttpStatus.OK);
		} catch (BadRquestException e) {
			LOGGER.error("BadRquestException", e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch (RestException e) {
			LOGGER.error("RestException", e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/deleteTeamSpace", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteTeamSpace(long teamId, String result, HttpServletRequest request) {
		try {
			super.checkToken(request);
			if (!"YES".equalsIgnoreCase(result)) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			Boolean flag = teamSpaceHttpClient.deleteTeamSpace(teamId, getToken());
			if (flag) {
				return new ResponseEntity<String>(HttpStatus.OK);
			}
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch (BadRquestException e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/editTeamSpace", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> editTeamSpace(long teamId, RestTeamSpaceModifyRequest spaceModifyRequest,
			HttpServletRequest httpServletRequest) {
		try {
			super.checkToken(httpServletRequest);
			String uploadNotice = spaceModifyRequest.getUploadNotice();
			spaceModifyRequest.setUploadNotice(null);

			teamSpaceHttpClient.modifyTeamSpace(teamId, getToken(), spaceModifyRequest);

			SetTeamSpaceAttrRequest request = new SetTeamSpaceAttrRequest(TeamSpaceAttributeEnum.UPLOADNOTICE.getName(),
					uploadNotice);
			teamSpaceHttpClient.setAttribute(teamId, request);

			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (BadRquestException e) {
			LOGGER.error("BadRquestException " + e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch (RestException e) {
			LOGGER.error("RestException " + e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/exitTeamSpace", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> exitTeamSpace(Long teamMembershipsId, Long teamId, HttpServletRequest request) {
		super.checkToken(request);
		try {
			String code = teamSpaceHttpClient.deleteTeamMember(teamId, teamMembershipsId, getToken());
			if (StringUtils.equals(code, String.valueOf(HttpStatus.OK.value()))) {
				return new ResponseEntity<String>("OK", HttpStatus.OK);
			}
			return new ResponseEntity<String>(code, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/copyToSpace", method = RequestMethod.GET)
    public String goCopyToSpace(HttpServletRequest request,@RequestParam(defaultValue = "", required = false, value = "startPoint") String startPoint,
        @RequestParam(defaultValue = "", required = false, value = "endPoint") String endPoint,Model model) {
       
        model.addAttribute("startPoint", startPoint);
        model.addAttribute("endPoint", endPoint);
		return "teamspace/copyToSpace";
	}

	@RequestMapping(value = "/openChangeOwner", method = RequestMethod.GET)
	public String gotoChangOwner(long teamId, Model model, HttpServletRequest request) {
		model.addAttribute("teamId", teamId);
		return "teamspace/changeOwner";
	}

	@PostConstruct
	public void init() {
		teamSpaceHttpClient = new TeamSpaceClient(ufmClientService);
		userClient = new UserClient(uamClientService);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String listTeamSpace(Model model) {
		model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false : CustomUtils.getValue("link.hidden"));
		return "teamspace/teamSpaceList";
	}

	@RequestMapping(value = "/deptSpaceList", method = RequestMethod.GET)
	public String deptSpaceList(Model model) {
		model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false : CustomUtils.getValue("link.hidden"));
		return "teamspace/deptSpaceList";
	}

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param orderField
	 * @param desc
	 * @return
	 */
	@RequestMapping(value = "/teamSpaceList", method = RequestMethod.POST)
	public ResponseEntity<Page<TeamSpacePage>> list(
			@RequestParam(value = "type", defaultValue = "0") Integer type,
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "orderField", defaultValue = TEAM_ORDER_FIELD_TEAMROLE) String orderField,
			@RequestParam(value = "desc", defaultValue = TEAM_ORDER_ASC) String desc, HttpServletRequest request) {
		try {
			super.checkToken(request);
			UserToken user = getCurrentUser();
			PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
			Order order = new Order(orderField, desc);
			ListTeamSpaceRequest listTeamSpaceRequest = generalListTeamSpaceRequest(user.getCloudUserId(), pageRequest.getLimit().getLength(), Long.parseLong(String.valueOf(pageRequest.getLimit().getOffset())), order);			listTeamSpaceRequest.setType(type);
			Page<RestTeamMemberInfo> pageParam = teamSpaceHttpClient.listTeamSpace(listTeamSpaceRequest, getToken(), pageRequest);

			List<TeamSpacePage> restTeamSpaceInfo = new ArrayList<TeamSpacePage>((int) pageParam.getTotalElements());
			for (RestTeamMemberInfo s : pageParam.getContent()) {
				restTeamSpaceInfo.add(transTeamSpace(s));
			}

			Page<TeamSpacePage> pageParamSpaceInfo = new PageImpl<TeamSpacePage>(restTeamSpaceInfo, pageRequest, pageParam.getTotalElements());
			return new ResponseEntity<>(pageParamSpaceInfo, HttpStatus.OK);
		} catch (BadRquestException e) {
			LOGGER.error("", e);
			return new ResponseEntity<>(new PageImpl<>(new ArrayList<>(BusinessConstants.INITIAL_CAPACITIES)), HttpStatus.OK);
		} catch (NumberFormatException e) {
			LOGGER.error("", e);
			return new ResponseEntity<>(new PageImpl<>(new ArrayList<>(BusinessConstants.INITIAL_CAPACITIES)), HttpStatus.OK);
		}
	}

    /**
     *
     * @param ownerId
     * @param orderField
     * @param desc
     * @param model
     * @return
     */
    @RequestMapping(value = "/listAll", method = RequestMethod.POST)
    public ResponseEntity<Page<RestTeamSpaceInfo>> listAll(
        @RequestParam(value = "pageParam", defaultValue = "1") Integer pageNumber,
        @RequestParam(value = "orderField", defaultValue = "teamRole") String orderField,
        @RequestParam(value = "desc", defaultValue = "ASC") String desc, Model model,
        HttpServletRequest request)
    {
        try
        {
            super.checkToken(request);
            UserToken user = getCurrentUser();
            PageRequest pageRequest = new PageRequest(pageNumber, COPY_SPACE_PAGE_SIZE);
            Order order = new Order(orderField, desc);
            ListTeamSpaceRequest listTeamSpaceRequest = generalListTeamSpaceRequest(user.getCloudUserId(),
                pageRequest.getLimit().getLength(),
                Long.parseLong(String.valueOf(pageRequest.getLimit().getOffset())),
                order);
            Page<RestTeamMemberInfo> pageParam = teamSpaceHttpClient.listTeamSpace(listTeamSpaceRequest,
                getToken(),
                pageRequest);
            
            List<RestTeamSpaceInfo> restTeamSpaceInfo = new ArrayList<RestTeamSpaceInfo>(
                (int) pageParam.getTotalElements());
            RestTeamSpaceInfo teamSpace;
            for (RestTeamMemberInfo s : pageParam.getContent())
            {
                teamSpace = s.getTeamspace();
                teamSpace.setName(HtmlUtils.htmlEscape(teamSpace.getName()));
                teamSpace.setDescription(HtmlUtils.htmlEscape(teamSpace.getDescription()));
                teamSpace.setCreatedByUserName(HtmlUtils.htmlEscape(teamSpace.getCreatedByUserName()));
                teamSpace.setOwnedByUserName(HtmlUtils.htmlEscape(teamSpace.getOwnedByUserName()));
                
                restTeamSpaceInfo.add(teamSpace);
            }
            Page<RestTeamSpaceInfo> pageParamSpaceInfo = new PageImpl<RestTeamSpaceInfo>(restTeamSpaceInfo,
                pageRequest, pageParam.getTotalElements());
            model.addAttribute("pageParam", pageParamSpaceInfo);
            return new ResponseEntity<Page<RestTeamSpaceInfo>>(pageParamSpaceInfo, HttpStatus.OK);
        }
        catch (BadRquestException e)
        {
            LOGGER.error("", e);
            return new ResponseEntity<Page<RestTeamSpaceInfo>>(new PageImpl<RestTeamSpaceInfo>(
                new ArrayList<RestTeamSpaceInfo>(BusinessConstants.INITIAL_CAPACITIES)), HttpStatus.OK);
        }
    }
    
    @RequestMapping(value = "/openAddTeamSpace", method = RequestMethod.GET)
    public String openAddTeamSpace()
    {
        return "teamspace/addTeamSpace";
    }
    
    /**
     * 
     * @param teamId
     * @param model
     * @return
     */
    @RequestMapping(value = "/openDeleteTeamSpace", method = RequestMethod.GET)
    public String openDeletePage(long teamId, Model model)
    {
        RestTeamSpaceInfo teamSpaceInfo = teamSpaceHttpClient.getTeamSpace(teamId, getToken());
        model.addAttribute("teamId", teamId);
        model.addAttribute("teamName", HtmlUtils.htmlEscape(teamSpaceInfo.getName()));
        return "teamspace/deleteTeamSpace";
    }
    
    @RequestMapping(value = "/openEditTeamSpace", method = RequestMethod.GET)
    public String openEditTeamSpace(long teamId, Model model)
    {
        UserToken user = getCurrentUser();
        RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(teamId,
            user.getLoginName(),
            null,
            user.getLoginName());
        if (memberInfo == null)
        {
            model.addAttribute("exceptionName", "Forbidden");
            return "teamspace/spacePromptError";
        }
        
        RestTeamSpaceInfo teamSpaceInfo = teamSpaceHttpClient.getTeamSpace(teamId, getToken());
        teamSpaceInfo.setName(teamSpaceInfo.getName());
        teamSpaceInfo.setDescription(teamSpaceInfo.getDescription());
        
        GetTeamSpaceAttrResponse response = teamSpaceHttpClient.getAttributes(teamId,
            TeamSpaceAttributeEnum.UPLOADNOTICE.getName());
        if (CollectionUtils.isNotEmpty(response.getAttributes()))
        {
            TeamSpaceAttribute attribute = response.getAttributes().get(0);
            if (attribute != null)
            {
                teamSpaceInfo.setUploadNotice(attribute.getValue());
            }
        }
        
        model.addAttribute("teamSpaceInfo", teamSpaceInfo);
        return "teamspace/editTeamSpace";
    }
    
    @RequestMapping(value = "/openGetTeamInfo", method = RequestMethod.GET)
    public String openGetTeamInfo(long teamId, Model model)
    {
        RestTeamSpaceInfo teamSpaceInfo = teamSpaceHttpClient.getTeamSpace(teamId, getToken());
        teamSpaceInfo.setName(teamSpaceInfo.getName());
        teamSpaceInfo.setDescription(teamSpaceInfo.getDescription());
        
        model.addAttribute("teamSpaceInfo", teamSpaceInfo);
        return "teamspace/teamSpaceInfo";
    }
    
    @RequestMapping(value = "sendEmail/{teamId}", method = RequestMethod.GET)
    public String sendEmail(@PathVariable("teamId") long teamId, Model model)
    {
        model.addAttribute("teamId", teamId);
        return "teamspace/sendEmail";
    }
    
    @RequestMapping(value = "/checkSameName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Boolean> checkSameNameTeamSpaceExist(@Valid RestGroupRequest groupRequest)
    {
        boolean isExist = teamSpaceHttpClient.getTeamSpaceByName(groupRequest);
        return new ResponseEntity<Boolean>(isExist, HttpStatus.OK);
    }
    
    private boolean checkMaxTeamSpaces(User userInfo)
    {
        PageRequest pageRequest;
        ListTeamSpaceRequest listTeamSpaceRequest;
        Page<RestTeamMemberInfo> pageParam;
        int page = 1;
        long total = 0L;
        while (true)
        {
            pageRequest = new PageRequest(page, 1000);
            
            listTeamSpaceRequest = generalListTeamSpaceRequest(userInfo.getCloudUserId(),
                pageRequest.getLimit().getLength(),
                Long.parseLong(String.valueOf(pageRequest.getLimit().getOffset())),
                null);
            pageParam = teamSpaceHttpClient.listTeamSpace(listTeamSpaceRequest, getToken(), pageRequest);
            for (RestTeamMemberInfo item : pageParam.getContent())
            {
                if (Constants.SPACE_ROLE_ADMIN.equals(item.getTeamRole()))
                {
                    total++;
                }
            }
            
            if (pageParam.getTotalElements() < 1000)
            {
                break;
            }
            page++;
        }
        int maxNum = userInfo.getTeamSpaceMaxNum();
        return maxNum == -1 || maxNum > total;
    }
    
    private ListTeamSpaceRequest generalListTeamSpaceRequest(Long userId, Integer limit, Long offset,
        Order order)
    {
        ListTeamSpaceRequest listTeamSpaceRequest = new ListTeamSpaceRequest(limit, offset);
        listTeamSpaceRequest.setUserId(userId);
        listTeamSpaceRequest.addOrder(order);
        listTeamSpaceRequest.addOrder(new Order(TEAM_ORDER_FIELD_CREATEDAT, TEAM_ORDER_DESC));
        return listTeamSpaceRequest;
    }

	private RestTeamMemberCreateRequest generalRequestTeamMemberCreate(String memberType, Long memberId, String teamRole, String role) {
		RestTeamMemberCreateRequest restTeamMemberCreateRequest = new RestTeamMemberCreateRequest();
		restTeamMemberCreateRequest.setTeamRole(teamRole);
		restTeamMemberCreateRequest.setRole(role);
		RestTeamMember restTeamMember = new RestTeamMember();
		restTeamMember.setType(memberType);
		restTeamMember.setId(memberId);

		List<RestTeamMember> memberList = new ArrayList<>();
		memberList.add(restTeamMember);
		restTeamMemberCreateRequest.setMemberList(memberList);

		return restTeamMemberCreateRequest;
	}
    
    private RestTeamMemberInfo getMemberInfo(long teamId, User userInfo)
    {
        RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(teamId,
            userInfo.getLoginName(),
            null,
            userInfo.getLoginName());
        
        if (memberInfo == null || Constants.SPACE_TYPE_SYSTEM.equals(memberInfo.getMember().getType()))
        {
            RestTeamMemberCreateRequest createRequest = generalRequestTeamMemberCreate(Constants.SPACE_TYPE_USER,
                userInfo.getCloudUserId(),
                ROLE_MANAGER,
                null);
            memberInfo = teamSpaceHttpClient.createTeamMemberBackEntity(createRequest, teamId, getToken());
        }
        return memberInfo;
    }
    
    private int getRoleType(String teamRole)
    {
        if (Constants.SPACE_ROLE_ADMIN.equals(teamRole))
        {
            return 0;
        }
        if (Constants.SPACE_ROLE_MANAGER.equals(teamRole))
        {
            return 1;
        }
        return 2;
    }
    
    private User getUserInfo(String userName)
    {
        User userInfo = null;
        List<UserToken> res = shareService.getListUser(userName);
        RestMultiUser multiUser;
        for (User user : res)
        {
            multiUser = RestMultiUser.buildObject(user, null, GroupConstants.GROUP_USERTYPE_USER);
            if (StringUtils.equals(userName, multiUser.getLoginName()))
            {
                userInfo = multiUser;
                break;
            }
        }
        if (userInfo == null)
        {
            if (enableLdap)
            {
                try
                {
                    userInfo = userClient.createUserFromLdap(getToken(), userName);
                }
                catch (RestException e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return userInfo;
    }
    
    private TeamSpacePage transTeamSpace(RestTeamMemberInfo s)
    {
        TeamSpacePage result = new TeamSpacePage();
        RestTeamSpaceInfo spaceInfo = s.getTeamspace();
        result.setCreatedAt(spaceInfo.getCreatedAt());
        result.setCreatedBy(spaceInfo.getCreatedBy());
        result.setCreatedByUserName(HtmlUtils.htmlEscape(spaceInfo.getCreatedByUserName()));
        result.setDescription(HtmlUtils.htmlEscape(spaceInfo.getDescription()));
        result.setId(spaceInfo.getId());
        result.setCurNumbers(spaceInfo.getCurNumbers());
        result.setMaxMembers(spaceInfo.getMaxMembers());
        result.setMaxVersions(spaceInfo.getMaxVersions());
        result.setMemberId(s.getId());
        result.setName(HtmlUtils.htmlEscape(spaceInfo.getName()));
        result.setOwnedBy(spaceInfo.getOwnedBy());
        result.setOwnedByUserName(HtmlUtils.htmlEscape(spaceInfo.getOwnedByUserName()));
        result.setSpaceQuota(spaceInfo.getSpaceQuota());
        result.setSpaceUsed(spaceInfo.getSpaceUsed());
        result.setRoleType(getRoleType(s.getTeamRole()));
        RestTeamMember member = s.getMember();
        if (member != null)
        {
            result.setUserType(member.getType());
        }
        return result;
    }
}
