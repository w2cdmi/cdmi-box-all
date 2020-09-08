package com.huawei.sharedrive.uam.enterprise.web;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.teamspace.domain.ChangeOwnerRequest;
import com.huawei.sharedrive.uam.teamspace.domain.ListAllTeamSpaceRequest;
import com.huawei.sharedrive.uam.teamspace.domain.RestTeamSpaceInfo;
import com.huawei.sharedrive.uam.teamspace.domain.RestTeamSpaceModifyRequest;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.user.service.UserService;
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
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.utils.IpUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/enterprise/admin/teamspace/config")
public class EnterpriseTeamConfigController extends AbstractCommonController {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseTeamConfigController.class);

	private static final long GB_BYTE = 1024 * 1024 * 1024L;

	@Autowired
	private UserService userService;

	@Autowired
	private AuthAppService authAppService;

	@Autowired
	private TeamSpaceService teamSpaceService;

	@Autowired
	private AdminLogManager adminLogManager;

	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;

    @Autowired
    private EnterpriseManager enterpriseManager;

	@RequestMapping(value = "listUser/{appId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Page<User>> listUser(@PathVariable String appId, String userName, String token) {
		try {
			super.checkToken(token);
			PageRequest request = new PageRequest();
			User user = new User();
			if (StringUtils.isNotBlank(userName)) {
				user.setLoginName(userName);
				user.setName(userName);
			}
			user.setAppId(appId);
			Page<User> userPage = userService.getPagedUser(user, request);

			List<User> content = userPage.getContent();

			for (User item : content) {
				item.setAppId(HtmlUtils.htmlEscape(item.getAppId()));
				item.setDepartment(HtmlUtils.htmlEscape(item.getDepartment()));
				item.setDepartmentCode(HtmlUtils.htmlEscape(item.getDepartmentCode()));
				item.setDescription(HtmlUtils.htmlEscape(item.getDescription()));
				item.setDomain(HtmlUtils.htmlEscape(item.getDomain()));
				item.setEmail(HtmlUtils.htmlEscape(item.getEmail()));
				item.setLoginName(HtmlUtils.htmlEscape(item.getLoginName()));
				item.setName(HtmlUtils.htmlEscape(item.getName()));
				item.setLabel(HtmlUtils.htmlEscape(item.getLabel()));
				item.setValidateKey(HtmlUtils.htmlEscape(item.getValidateKey()));
			}

			return new ResponseEntity<Page<User>>(userPage, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Page<User>>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/{appId}", method = RequestMethod.GET)
	public String list(@PathVariable String appId, Model model) {
		enterpriseAccountManager.bindAppCheck(appId);
		model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
		return "enterprise/admin/app/teamSpaceList";
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String gotoPage(Model model) {
//		enterpriseAccountManager.bindAppCheck(appId);
//		model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
    	long enterpriseId = checkAdminAndGetId();
    	boolean isOrganizeEnabled= enterpriseManager.checkOrganizeEnabled(enterpriseId);
    	model.addAttribute("isOrganizeEnabled", isOrganizeEnabled);
		return "enterprise/admin/teamspace/teamSpaceList";
	}

	@RequestMapping(value = "/{appId}", method = RequestMethod.POST)
	public ResponseEntity<Page<RestTeamSpaceInfo>> list4Post(@PathVariable String appId,
			@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "type", defaultValue = "-1") int type,
			@RequestParam(value = "ownerByUserName", defaultValue = "") String ownerByUserName,
			@RequestParam(value = "keyword", defaultValue = "") String keyword, String token) {
		try {
			super.checkToken(token);
			PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
			ListAllTeamSpaceRequest listRequest = new ListAllTeamSpaceRequest(pageRequest.getLimit().getLength(),
					Long.parseLong(String.valueOf(pageRequest.getLimit().getOffset())));

			if (StringUtils.isNotBlank(keyword)) {
				listRequest.setKeyword(keyword);
			}
			if (StringUtils.isNotBlank(ownerByUserName)) {
				listRequest.setOwnerByUserName(ownerByUserName);
			}
			listRequest.setType(type);
			Page<RestTeamSpaceInfo> pageParam = teamSpaceService.getPagedTeamSpace(listRequest, appId, pageRequest);

			List<RestTeamSpaceInfo> content = pageParam.getContent();

			for (RestTeamSpaceInfo item : content) {
				item.setName(HtmlUtils.htmlEscape(item.getName()));
				item.setDescription(HtmlUtils.htmlEscape(item.getDescription()));
				item.setCreatedByUserName(HtmlUtils.htmlEscape(item.getCreatedByUserName()));
				item.setOwnedByUserName(HtmlUtils.htmlEscape(item.getOwnedByUserName()));
			}

			return new ResponseEntity<Page<RestTeamSpaceInfo>>(pageParam, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Page<RestTeamSpaceInfo>>(HttpStatus.OK);
		}
	}

    @RequestMapping(value = "/changeOwner/{appId}", method = RequestMethod.GET)
	public String openChangeOwner(@PathVariable String appId, Long teamId, Model model) {
		model.addAttribute("appId", appId);
		model.addAttribute("teamId", teamId);
		return "enterprise/admin/teamspace/changeSpaceOwner";
	}

	@RequestMapping(value = "/changeOwner/{appId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> changeOwner(@PathVariable String appId, @RequestParam Long teamId,
			@RequestParam String loginName, Model model, String token) {
		// AdminLogBuilder.builderLog().fillAppId(appId);
		try {
			super.checkToken(token);
			User user = userService.getUserByLoginNameAppId(loginName, appId);

			ChangeOwnerRequest request = new ChangeOwnerRequest();
			request.setNewOwnerId(user.getCloudUserId());
			RestTeamSpaceInfo teamSpaceInfo = teamSpaceService.changeOwner(teamId, appId, request);
			if (null != teamSpaceInfo) {
				teamSpaceInfo.setCreatedByUserName(HtmlUtils.htmlEscape(teamSpaceInfo.getCreatedByUserName()));
				teamSpaceInfo.setDescription(HtmlUtils.htmlEscape(teamSpaceInfo.getDescription()));
				teamSpaceInfo.setName(HtmlUtils.htmlEscape(teamSpaceInfo.getName()));
				teamSpaceInfo.setOwnedByUserName(HtmlUtils.htmlEscape(teamSpaceInfo.getOwnedByUserName()));
			}
			return new ResponseEntity<RestTeamSpaceInfo>(teamSpaceInfo, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<RestTeamSpaceInfo>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/setmaxmember/{appId}", method = RequestMethod.GET)
	public String openSetMaxMember(@PathVariable String appId, Model model) {
		model.addAttribute("appId", appId);
		return "enterprise/admin/teamspace/setSpaceMaxMember";
	}

	@RequestMapping(value = "/setquota/{appId}", method = RequestMethod.GET)
	public String openSetSpaceQuota(@PathVariable String appId, Model model) {
		model.addAttribute("appId", appId);
		return "enterprise/admin/teamspace/setSpaceMaxQuota";
	}
	
	@RequestMapping(value = "/setmaxversion/{appId}", method = RequestMethod.GET)
	public String openSetMaxVersion(@PathVariable String appId, Model model) {
		model.addAttribute("appId", appId);
		return "enterprise/admin/teamspace/setSpaceMaxVersion";
	}

	@RequestMapping(value = "/updateSpace/{appId}", method = RequestMethod.POST)
	public ResponseEntity<String> updateSpace(@PathVariable String appId, String teamIds, Integer maxMembers,Long maxSpaces,
			Integer maxVersions,String keyword, HttpServletRequest req, String token) {
		
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(req));
		String[] description = new String[] { getEnterpriseName(), appId, "[" + teamIds + "]",
				String.valueOf(maxMembers) };
		try {
				if (maxMembers == null) {
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}
				if (maxMembers == 0) {
					maxMembers = -1;
				}
				if (maxMembers < -1 || maxMembers > 999999) {
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}
				if (maxSpaces == null) {
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}

				if (maxSpaces < -1 || maxSpaces > 999999) {
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}
				EnterpriseAccount account = enterpriseAccountManager.getByEnterpriseApp(owner.getEnterpriseId(), appId);

				if (maxSpaces * 1024 > account.getMaxSpace()) {
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}

				if (maxSpaces == 0) {
					maxSpaces = -1L;
				} else {
					maxSpaces = maxSpaces * GB_BYTE;
				}
				
				if (maxVersions == null) {
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}

				if (maxVersions == 0) {
					maxVersions = -1;
				}

				if (maxVersions < -1 || maxVersions > 999999) {
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}

				RestTeamSpaceModifyRequest request = new RestTeamSpaceModifyRequest();
				request.setMaxMembers(maxMembers);
				request.setSpaceQuota(maxSpaces);
				request.setMaxVersions(maxVersions);
				String teamSpaceInfo = teamSpaceService.modifyTeamSpaces(teamIds, appId, request, keyword);
				adminLogManager.saveAdminLog(owner, AdminLogType.KEY_USER_TEAM_SPACE_MAX, description);
				return new ResponseEntity<String>(HtmlUtils.htmlEscape(teamSpaceInfo), HttpStatus.OK);

		 } catch (Exception e) {
		 	LOGGER.error(e.getMessage(), e);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_USER_TEAM_SPACE_MAX_ERROR, description);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		 }
	}

	
	@SuppressWarnings("PMD.ExcessiveParameterList")
	@RequestMapping(value = "/createSystemTeamSpace/{appId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> createSystemTeamSpace(@PathVariable String appId, String type, HttpServletRequest req, String token) {
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(req));
		long EnterpriseId=checkAdminAndGetId();
//		teamSpaceService.createDefaultTeamSpace(EnterpriseId, appId, Integer.parseInt(type), "收件箱");
	    return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@SuppressWarnings("PMD.ExcessiveParameterList")
	@RequestMapping(value = "/setmaxmember/{appId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> setSpaceMaxMember(@PathVariable String appId, String teamIds, Integer maxMembers,
			String keyword, HttpServletRequest req, String token) {
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(req));
		String[] description = new String[] { getEnterpriseName(), appId, "[" + teamIds + "]",
				String.valueOf(maxMembers) };
		try {
			if (maxMembers == null) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			if (maxMembers == 0) {
				maxMembers = -1;
			}

			if (maxMembers < -1 || maxMembers > 999999) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
			RestTeamSpaceModifyRequest request = new RestTeamSpaceModifyRequest();
			request.setMaxMembers(maxMembers);
			String teamSpaceInfo = teamSpaceService.modifyTeamSpaces(teamIds, appId, request, keyword);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_USER_TEAM_SPACE_MAX, description);
			return new ResponseEntity<String>(HtmlUtils.htmlEscape(teamSpaceInfo), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_USER_TEAM_SPACE_MAX_ERROR, description);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	@RequestMapping(value = "/setspacequota/{appId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> setSpaceMaxQuota(@PathVariable String appId, String teamIds, Long spaceQuota,
			String keyword, HttpServletRequest req, String token) {
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(req));
		String[] description = new String[] { getEnterpriseName(), appId, "[" + teamIds + "]",
				String.valueOf(spaceQuota) };
		try {
			RestTeamSpaceModifyRequest request = new RestTeamSpaceModifyRequest();

			if (spaceQuota == null) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			if (spaceQuota < -1 || spaceQuota > 999999) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			EnterpriseAccount account = enterpriseAccountManager.getByEnterpriseApp(owner.getEnterpriseId(), appId);

			if (spaceQuota * 1024 > account.getMaxSpace()) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			if (spaceQuota == 0) {
				spaceQuota = -1L;
			} else {
				spaceQuota = spaceQuota * GB_BYTE;
			}

			request.setSpaceQuota(spaceQuota);
			String teamSpaceInfo = teamSpaceService.modifyTeamSpaces(teamIds, appId, request, keyword);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_USER_TEAM_SPACE_QUOTA, description);
			return new ResponseEntity<String>(HtmlUtils.htmlEscape(teamSpaceInfo), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_USER_TEAM_SPACE_QUOTA_ERROR, description);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	@RequestMapping(value = "/setmaxversion/{appId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> setSpaceMaxVersion(@PathVariable String appId, String teamIds, Integer maxVersions,
			String keyword, HttpServletRequest req, String token) {
		super.checkToken(token);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(req));
		String[] description = new String[] { getEnterpriseName(), appId, "[" + teamIds + "]",
				String.valueOf(maxVersions) };
		try {
			if (maxVersions == null) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			if (maxVersions == 0) {
				maxVersions = -1;
			}

			if (maxVersions < -1 || maxVersions > 999999) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			RestTeamSpaceModifyRequest request = new RestTeamSpaceModifyRequest();
			request.setMaxVersions(maxVersions);
			String teamSpaceInfo = teamSpaceService.modifyTeamSpaces(teamIds, appId, request, keyword);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_USER_TEAM_SPACE_VERSIONS, description);
			return new ResponseEntity<String>(HtmlUtils.htmlEscape(teamSpaceInfo), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_USER_TEAM_SPACE_VERSIONS_ERROR, description);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
}
