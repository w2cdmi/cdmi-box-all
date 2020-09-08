package pw.cdmi.box.disk.share.web;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import pw.cdmi.box.disk.client.api.AccountConfigClient;
import pw.cdmi.box.disk.client.api.MailMsgClient;
import pw.cdmi.box.disk.client.api.TeamSpaceClient;
import pw.cdmi.box.disk.client.api.UserClient;
import pw.cdmi.box.disk.client.domain.mailmsg.MailMsg;
import pw.cdmi.box.disk.client.domain.node.*;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.client.domain.share.RestLinkApproveDetail;
import pw.cdmi.box.disk.client.domain.share.RestLinkApproveList;
import pw.cdmi.box.disk.client.domain.share.RestLinkApproveListRequest;
import pw.cdmi.box.disk.client.domain.share.RestLinkDynamicResponse;
import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.box.disk.files.service.FileService;
import pw.cdmi.box.disk.files.service.FolderService;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.files.domain.Path;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.response.FilePreUploadResponse;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.*;
import pw.cdmi.box.disk.share.service.LinkService;
import pw.cdmi.box.disk.system.service.SecurityService;
import pw.cdmi.box.disk.teamspace.domain.RestACL;
import pw.cdmi.box.disk.teamspace.domain.RestNodePermissionInfo;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.*;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.*;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/share")
public class LinkController extends CommonController {

	private static Logger logger = LoggerFactory.getLogger(LinkController.class);
	
	private static final int PAGE_SIZE = 32;

	private static final String SHARE_LINK_INDEX_ERROR = "share/linkIndex404";

	@Autowired
	private FileService fileService;

	@Autowired
	private FolderService folderService;

	@Autowired
	private LinkService linkService;

	@Autowired
	private SecurityService securityService;

	@Resource
	private RestClient ufmClientService;

	@Autowired
	private UserTokenManager userTokenManager;

	@Resource
	private RestClient uamClientService;

	private UserClient userClient;

	private AccountConfigClient accountConfigClient;

	@PostConstruct
	void init() {
		this.accountConfigClient = new AccountConfigClient(uamClientService);
	}

	/**
	 * 
	 * @param ownerId
	 * @param folderId
	 * @return
	 */
	@RequestMapping(value = "deleteLink/{ownerId}/{folderId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteLinkById(@PathVariable("ownerId") long ownerId,
			@PathVariable("folderId") long folderId, String linkCode, HttpServletRequest httpServletRequest) {
		try {
			super.checkToken(httpServletRequest);
			UserToken user = getCurrentUser();
			INode iNode = new INode();
			iNode.setOwnedBy(ownerId);
			iNode.setId(folderId);
			linkService.deleteLinkById(user, iNode, linkCode);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (BadRquestException e) {
			logger.error("BadRquestException when link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (RestException e) {
			logger.error("RestException when delete link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (BaseRunException e) {
			logger.error("RestException when delete link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param ownerId
	 * @param folderId
	 * @return
	 */
	@RequestMapping(value = "deleteLink/{ownerId}/{folderId}/all", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteNodeAllLink(@PathVariable("ownerId") long ownerId,
			@PathVariable("folderId") long folderId, HttpServletRequest httpServletRequest) {
		try {
			super.checkToken(httpServletRequest);
			UserToken user = getCurrentUser();
			INode iNode = new INode();
			iNode.setOwnedBy(ownerId);
			iNode.setId(folderId);
			linkService.deleteAllLink(user, iNode);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (BadRquestException e) {
			logger.error("delete link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (BaseRunException e) {
			logger.error("BaseRunException delete link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (RestException e) {
			logger.error("RestException delete link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param ownerId
	 * @param folderId
	 * @return
	 */
	@RequestMapping(value = "batchDeleteLink/{ownerId}/{folderId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> batchDeleteNodeAllLink(@PathVariable("ownerId") long ownerId,
			@PathVariable("folderId") long folderId, String linkCodes, HttpServletRequest httpServletRequest) {
		try {
			super.checkToken(httpServletRequest);
			UserToken user = getCurrentUser();
			INode iNode = new INode();
			iNode.setOwnedBy(ownerId);
			iNode.setId(folderId);
			String[] linkCodeArr = linkCodes.split(",");
			for (int i = 0; i < linkCodeArr.length; i++) {
				linkService.deleteLinkById(user, iNode, linkCodeArr[i]);
			}
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (BadRquestException e) {
			logger.error("delete link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (BaseRunException e) {
			logger.error("BaseRunException delete link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (RestException e) {
			logger.error("RestException delete link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "getDownloadUrl/{folderId}/{linkCode}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> getDownloadUrl(@PathVariable("folderId") long folderId,
			@PathVariable("linkCode") String linkCode) {
		try {
			UserToken user = getCurrentUser();
			if (null == user) {
				user = new UserToken();
			}
			user.setLinkCode(linkCode);
			String accessCode = CommonTools.getAccessCode(linkCode);
			LinkAndNodeV2 linkNode = linkService.getNodeInfoByLinkCode(user, linkCode, accessCode);
			INodeLink iNodeLinkV2 = linkNode.getLink();
			String url = fileService.getFileDownloadUrl(user, iNodeLinkV2.getOwnedBy(), folderId, linkCode);
			url = HtmlUtils.htmlEscape(url);
			return new ResponseEntity<String>(url, HttpStatus.OK);
		} catch (RestException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (BaseRunException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param iNodeId
	 * @return
	 */
	@RequestMapping(value = "getlink/{ownerId}/{iNodeId}", method = RequestMethod.GET)
	public ResponseEntity<?> getLinks(@PathVariable("ownerId") long ownerId, @PathVariable("iNodeId") long iNodeId) {
		try {
			UserToken user = getCurrentUser();
			List<INodeLinkView> iNodeLinks = linkService.getLinkByINodeId(user, ownerId, iNodeId);
			if (CollectionUtils.isEmpty(iNodeLinks)) {
				logger.warn("iNodeLink not exist, ownerId: " + ownerId + ", iNodeId:" + iNodeId);
				return new ResponseEntity<INodeLink>(HttpStatus.OK);
			}
			for (INodeLinkView iNode : iNodeLinks) {
				iNode.setAccessCodeMode(HtmlUtils.htmlEscape(iNode.getAccessCodeMode()));
				iNode.setCreator(HtmlUtils.htmlEscape(iNode.getCreator()));
				iNode.setId(HtmlUtils.htmlEscape(iNode.getId()));
				iNode.setIdentities(HtmlUtils.htmlEscape(iNode.getIdentities()));
				iNode.setPlainAccessCode(HtmlUtils.htmlEscape(iNode.getPlainAccessCode()));
				iNode.setUrl(HtmlUtils.htmlEscape(iNode.getUrl()));
			}
			return new ResponseEntity<List<INodeLinkView>>(iNodeLinks, HttpStatus.OK);
		} catch (BaseRunException e) {
			logger.error("get link error", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * 
	 * @param ownerId
	 * @return
	 */
	@RequestMapping(value = "getPaths", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPaths(long ownerId, String linkCode, long inodeId, long parentId) {
		if (inodeId == INode.FILES_ROOT) {
			return new ResponseEntity<String>(HttpStatus.OK);
		}

		try {
			UserToken access = getCurrentUser();
			if (access == null) {
				access = new UserToken();
			}
			access.setLinkCode(linkCode);
			List<Path> paths = new ArrayList<Path>(BusinessConstants.INITIAL_CAPACITIES);
			buildNodePath(access, ownerId, paths, inodeId, parentId);
			return new ResponseEntity<List<Path>>(paths, HttpStatus.OK);
		} catch (RestException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/linkApproveList", method = RequestMethod.GET)
	public String gotoLinkApproveList(Model model) {
		UserToken userInfo = getCurrentUser();
		model.addAttribute("accountId", userInfo.getAccountId());
		model.addAttribute("cloudId", userInfo.getCloudUserId());

		return "share/linkApproveList";
	}
	
	@RequestMapping(value = "/linkAuditList", method = RequestMethod.GET)
	public String gotoLinkAuditListt(Model model) {
		UserToken userInfo = getCurrentUser();
		model.addAttribute("accountId", userInfo.getAccountId());
		model.addAttribute("cloudId", userInfo.getCloudUserId());

		return "share/linkAuditList";
	}

	@RequestMapping(value = "/approveLinkDetail/{linkCode}", method = RequestMethod.GET)
	public String gotoApproveLinkDetail(@PathVariable("linkCode") String linkCode, String type, Model model) {
		model.addAttribute("linkCode", linkCode);
		model.addAttribute("user", type);

		return "share/approveLinkDetail";
	}

	@RequestMapping(value = "/myLinkApproveDetail/{linkCode}", method = RequestMethod.GET)
	public String gotoMyLinkApproveDetail(@PathVariable("linkCode") String linkCode, Model model) {
		model.addAttribute("linkCode", linkCode);

		return "share/myLinkApproveDetail";
	}

	@RequestMapping(value = "/reciveFolder/{ownerId}/{folderId}", method = RequestMethod.GET)
	public String gotoReciveFolder(@PathVariable("folderId") long folderId, @PathVariable("ownerId") long ownerId,
			Model model) {
		try {
			UserToken user = getCurrentUser();
			INode folderNode = folderService.getNodeInfo(user, ownerId, folderId);
			if (null == folderNode) {
				return SHARE_LINK_INDEX_ERROR;
			}
			model.addAttribute("folderId", folderId);
			model.addAttribute("ownerId", ownerId);
			model.addAttribute("userName", user.getName());
			model.addAttribute("name", folderNode.getName());
			model.addAttribute("type", folderNode.getType());
			return "share/createReciveFolder";
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("createReciveFolder", e);
		}
		return SHARE_LINK_INDEX_ERROR;
	}

	@RequestMapping(value = "/link/{ownerId}/{folderId}", method = RequestMethod.GET)
	public String gotoLinkIndex(@PathVariable("ownerId") long ownerId, @PathVariable("folderId") long folderId,
			Model model) {
		try {
			UserToken user = getCurrentUser();

			INode folderNode = folderService.getNodeInfo(user, ownerId, folderId);
			if (null == folderNode) {
				return SHARE_LINK_INDEX_ERROR;
			}
			userClient = new UserClient(uamClientService);
			boolean checkOrgEnabled = userClient.checkOrgEnabled(user.getToken());
			model.addAttribute("isDepartment", checkOrgEnabled);
			model.addAttribute("folderId", folderId);
			model.addAttribute("ownerId", ownerId);
			model.addAttribute("name", folderNode.getName());
			model.addAttribute("userName", user.getName());
			model.addAttribute("type", folderNode.getType());
			model.addAttribute("size", folderNode.getSize());
			model.addAttribute("modifiedAt", folderNode.getModifiedAt());
			model.addAttribute("isComplexCode", securityService.getSecurityConfig().isDisableSimpleLinkCode());
			fillLinkStatus(model, folderNode);
			if (folderNode.getType() == INode.TYPE_FILE && FilesCommonUtils.isImage(folderNode.getName())) {
				String downloadUrl;
				try {
					downloadUrl = fileService.getFileThumbUrls(user, folderNode.getOwnedBy(), folderNode.getId());
					downloadUrl = htmlEscapeThumbnail(downloadUrl);
				} catch (RestException e) {
					downloadUrl = "";
				}
				model.addAttribute("thumbnailUrl", downloadUrl);
			}

/*			删除未使用的变量
			MailMsg msg = new MailMsgClient(ufmClientService).getMailMsg(userTokenManager.getToken(), MailMsg.SOURCE_LINK, ownerId, folderId);
			model.addAttribute("mailmsg", msg == null ? "" : msg.getMessage());
*/
			return "share/linkIndex";
		} catch (NoSuchItemsException e) {
			logger.debug("", e);
			return SHARE_LINK_INDEX_ERROR;
		} catch (NoSuchLinkException e) {
			logger.debug("no link exists", e);
			return "share/linkIndex";
		} catch (BaseRunException e) {
			logger.error("node not exist!", e);
			return SHARE_LINK_INDEX_ERROR;
		}
	}

	@RequestMapping(value = "/link/{ownerId}/{folderId}/modify", method = RequestMethod.GET)
	public String gotoLinkCreate(@PathVariable("ownerId") long ownerId, @PathVariable("folderId") long folderId,
			String linkCode, Model model) {
		try {
			UserToken user = getCurrentUser();

			INode folderNode = folderService.getNodeInfo(user, ownerId, folderId);
			if (null == folderNode) {
				return SHARE_LINK_INDEX_ERROR;
			}
			userClient = new UserClient(uamClientService);
			boolean checkOrgEnabled = userClient.checkOrgEnabled(user.getToken());
			model.addAttribute("isDepartment", checkOrgEnabled);
			if (linkCode != null && !linkCode.equals("")) {
				model.addAttribute("linkCode", linkCode);
			}
			model.addAttribute("isDepartment", checkOrgEnabled);
			model.addAttribute("folderId", folderId);
			model.addAttribute("ownerId", ownerId);
			model.addAttribute("name", folderNode.getName());
			model.addAttribute("type", folderNode.getType());
			model.addAttribute("isComplexCode", securityService.getSecurityConfig().isDisableSimpleLinkCode());
			fillLinkStatus(model, folderNode);
			if (folderNode.getType() == INode.TYPE_FILE && FilesCommonUtils.isImage(folderNode.getName())) {
				String downloadUrl;
				try {
					downloadUrl = fileService.getFileThumbUrls(user, folderNode.getOwnedBy(), folderNode.getId());
					downloadUrl = htmlEscapeThumbnail(downloadUrl);
				} catch (RestException e) {
					downloadUrl = "";
				}
				model.addAttribute("thumbnailUrl", downloadUrl);
			}
			MailMsg msg = new MailMsgClient(ufmClientService).getMailMsg(userTokenManager.getToken(),
					MailMsg.SOURCE_LINK, ownerId, folderId);
			model.addAttribute("mailmsg", msg == null ? "" : msg.getMessage());
			return "share/createLink";
		} catch (NoSuchItemsException e) {
			logger.debug("", e);
			return SHARE_LINK_INDEX_ERROR;
		} catch (NoSuchLinkException e) {
			logger.debug("no link exists", e);
			return "share/linkIndex";
		} catch (BaseRunException e) {
			logger.error("node not exist!", e);
			return SHARE_LINK_INDEX_ERROR;
		}

	}

	/**
	 * 
	 * @param parentId
	 * @param pageNumber
	 * @param orderField
	 * @param desc
	 * @return
	 */
	@RequestMapping(value = "list", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> list(long parentId, int pageNumber, String linkCode, String orderField, boolean desc,
			long ownerId, HttpServletRequest request) {

		super.checkToken(request);
		PageRequest pageRequest = new PageRequest(pageNumber, PAGE_SIZE);

		try {
			Order order = new Order();
			order.setDesc(desc);
			order.setField(orderField);
			pageRequest.setOrder(order);

			UserToken access = getCurrentUser();
			if (null == access) {
				access = new UserToken();
			}
			access.setLinkCode(linkCode);

			long offset = 0L;
			if (pageNumber > 0) {
				offset = (long) (pageNumber - 1) * PAGE_SIZE;
			}
			BasicNodeListRequest listFolderRequest = generalRequest(orderField, desc, offset);

			RestFolderLists list = linkService.listFolderLinkByFilter(listFolderRequest, new INode(ownerId, parentId),
					access, linkCode);
			List<INode> nodeList = transToNodeList(list);
			Page<INode> page = new PageImpl<INode>(nodeList, pageRequest, list.getTotalCount());
			for (INode node : page.getContent()) {
				node.setName(HtmlUtils.htmlEscape(node.getName()));
			}
			return new ResponseEntity<Page<INode>>(page, HttpStatus.OK);
		} catch (RestException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param linkCode
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "needAuth/{linkCode}", method = RequestMethod.GET)
	public String needAuth(@PathVariable("linkCode") String linkCode, Model model) {
		model.addAttribute("linkCode", linkCode);
		return "share/inputAccessCode";
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	@RequestMapping(value = "sendLink", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> sendLinkByEmail(long ownerId, long iNodeId, String emails, String linkUrl,
			String plainAccessCode, String message, HttpServletRequest request) {

		if (StringUtils.isBlank(emails) || StringUtils.isBlank(linkUrl)) {
			return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
		}
		if (StringUtils.isNotBlank(message) && message.length() > 2000) {
			return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
		}

		if (StringUtils.isNotBlank(plainAccessCode) && plainAccessCode.length() > 20) {
			return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
		}

		UserToken user = getCurrentUser();

		try {
			super.checkToken(request);
			RestNodePermissionInfo pInfo = new TeamSpaceClient(ufmClientService).getNodePermission(ownerId, iNodeId,
					user.getCloudUserId());
			if (pInfo == null || pInfo.getPermissions() == null || pInfo.getPermissions().getPublishLink() != 1) {
				return new ResponseEntity<String>(ErrorCode.FORBIDDEN_OPER.getCode(), HttpStatus.FORBIDDEN);
			}

			linkService.sendLinkMail(user, ownerId, iNodeId, linkUrl, plainAccessCode, emails, message);
			return new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (BadRquestException e) {
			logger.error("BadRquestException " + e.getMessage(), e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (RestException e) {
			logger.error("RestException " + e.getMessage(), e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param iNodeId
	 * @return
	 */
	@RequestMapping(value = "setlink/{ownerId}/{iNodeId}", method = RequestMethod.POST)
	public ResponseEntity<?> setLink(@PathVariable("ownerId") long ownerId, @PathVariable("iNodeId") long iNodeId, LinkRequest request, HttpServletRequest httpServletRequest) {
		try {
			super.checkToken(httpServletRequest);
			UserToken user = getCurrentUser();
			checkRestLinkCreateRequest(request);

			INodeLinkView iNodeLink = linkService.createLink(user, ownerId, iNodeId, request);
			return new ResponseEntity<>(iNodeLink, HttpStatus.OK);
		} catch (RestException e) {
			logger.error("set link error RestException", e);
			return new ResponseEntity<>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (BadRquestException e) {
			logger.error("set link error BadRquestException", e);
			return new ResponseEntity<>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param iNodeId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "createReciveLink/{ownerId}/{iNodeId}", method = RequestMethod.POST)
	public ResponseEntity<?> createReciveLink(@PathVariable("ownerId") long ownerId,
			@PathVariable("iNodeId") long iNodeId, Model model, LinkRequest request,
			HttpServletRequest httpServletRequest) {
		try {
			super.checkToken(httpServletRequest);
			UserToken user = getCurrentUser();
			checkRestLinkCreateRequest(request);
			request.setNeedLogin(false);
			INodeLinkView iNodeLink = linkService.createLink(user, ownerId, iNodeId, request);
			return new ResponseEntity<INodeLinkView>(iNodeLink, HttpStatus.OK);
		} catch (RestException e) {
			logger.error("set link error RestException", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (BadRquestException e) {
			logger.error("set link error BadRquestException", e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param request
	 * @throws BadRquestException
	 */
	private void checkRestLinkCreateRequest(LinkRequest request) throws BadRquestException {
		if (StringUtils.isBlank(request.getAccessCodeMode())) {
			request.setAccessCodeMode(LinkAccessCodeMode.TYPE_STATIC_STRING);
		}
		if (LinkAccessCodeMode.TYPE_STATIC_STRING.equals(request.getAccessCodeMode())
				&& StringUtils.isNotEmpty(request.getAccessCode())) {
			if (securityService.getSecurityConfig().isDisableSimpleLinkCode()) {
				PatternRegUtil.checkComplexLinkAccessCodeLegal(request.getAccessCode());
			} else {
				PatternRegUtil.checkSimpleLinkAccessCodeLegal(request.getAccessCode());
			}
		}

		if (LinkAccessCodeMode.TYPE_MAIL_STRING.equals(request.getAccessCodeMode())
				&& StringUtils.isNotEmpty(request.getIdentities())) {
			String[] email = request.getIdentities().split(";");

			for (String to : email) {
				if (!PatternRegUtil.checkMailLegal(to)) {
					continue;
				}
			}
		}

	}

	/**
	 * 
	 * @param request
	 * @throws BadRquestException
	 */
	private void checkRestLinkUpdateRequest(LinkRequest request) throws BadRquestException {
		if (StringUtils.isBlank(request.getAccessCodeMode())) {
			request.setAccessCodeMode(LinkAccessCodeMode.TYPE_STATIC_STRING);
		}
		if (LinkAccessCodeMode.TYPE_STATIC_STRING.equals(request.getAccessCodeMode())
				&& StringUtils.isNotEmpty(request.getAccessCode())) {
			if (securityService.getSecurityConfig().isDisableSimpleLinkCode()) {
				PatternRegUtil.checkComplexLinkAccessCodeLegal(request.getAccessCode());
			} else {
				PatternRegUtil.checkSimpleLinkAccessCodeLegal(request.getAccessCode());
			}
		}

		if (LinkAccessCodeMode.TYPE_MAIL_STRING.equals(request.getAccessCodeMode())
				&& StringUtils.isNotEmpty(request.getIdentities())) {
			String[] email = request.getIdentities().split(";");

			for (String to : email) {
				if (!PatternRegUtil.checkMailLegal(to)) {
					continue;
				}
			}
		}
		if (LinkAccessCodeMode.TYPE_PHONE_STRING.equals(request.getAccessCodeMode())
				&& StringUtils.isNotEmpty(request.getIdentities())) {
			String[] phone = request.getIdentities().split(";");

			for (String to : phone) {
				PatternRegUtil.checkPhoneLegal(to);
			}
		}
	}

	/**
	 * 
	 * @param iNodeId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "updateLink/{ownerId}/{iNodeId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> updateLink(@PathVariable("ownerId") long ownerId, @PathVariable("iNodeId") long iNodeId,
			LinkRequest request, String linkCode, HttpServletRequest httpServletRequest) {
		try {
			super.checkToken(httpServletRequest);
			UserToken user = getCurrentUser();
			checkRestLinkUpdateRequest(request);
			INodeLinkView iNodeLink = linkService.updateLink(user, ownerId, iNodeId, request, linkCode);

			try {
				if (StringUtils.equals(LinkAccessCodeMode.TYPE_MAIL_STRING, request.getAccessCodeMode())) {
					linkService.sendLinkMailForUpdate(user, ownerId, iNodeId, linkCode, request.getIdentities());
				}
			} catch (RestException e) {
				logger.warn("send dynamic mail failed." + e.getMessage());
			}
			return new ResponseEntity<INodeLinkView>(iNodeLink, HttpStatus.OK);
		} catch (BadRquestException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		} catch (RestException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * @param ownerId
	 * @throws BaseRunException
	 */
	private void buildNodePath(UserToken access, long ownerId, List<Path> paths, long inodeId, long parentId)
			throws RestException {
		if (inodeId == INode.FILES_ROOT) {
			return;
		}
		INode node = folderService.getNodeInfo(access, ownerId, inodeId);
		node.setName(HtmlUtils.htmlEscape(node.getName()));
		node.setDescription(HtmlUtils.htmlEscape(node.getDescription()));
		node.setLinkCode(HtmlUtils.htmlEscape(node.getLinkCode()));
		node.setModifiedByName(HtmlUtils.htmlEscape(node.getModifiedByName()));
		node.setPath(HtmlUtils.htmlEscape(node.getPath()));
		node.setVersion(HtmlUtils.htmlEscape(node.getVersion()));

		paths.add(new Path(node));
		if (inodeId != parentId) {
			buildNodePath(access, ownerId, paths, node.getParentId(), parentId);
		}
	}

	/**
	 * 
	 * @param model
	 * @param folderNode
	 * @throws BaseRunException
	 */
	private void fillLinkStatus(Model model, INode folderNode) throws BaseRunException {
		model.addAttribute("linkStatus", BusinessConstants.STATUS_LINK_SET);
		if (folderNode.getLinkStatus() != BusinessConstants.HASSETLINK) {
			model.addAttribute("linkStatus", BusinessConstants.STATUS_LINK_NOT_SET);
		}
	}

	private BasicNodeListRequest generalRequest(String orderField, boolean desc, long offset) {
		BasicNodeListRequest listFolderRequest = new BasicNodeListRequest(PAGE_SIZE, offset);
		Thumbnail smallThumb = new Thumbnail(Thumbnail.DEFAULT_SMALL_WIDTH, Thumbnail.DEFAULT_SMALL_HEIGHT);
		Thumbnail bigThumb = new Thumbnail(Thumbnail.DEFAULT_BIG_WIDTH, Thumbnail.DEFAULT_BIG_HEIGHT);
		listFolderRequest.addThumbnail(smallThumb);
		listFolderRequest.addThumbnail(bigThumb);
		Order orderByType = new Order("TYPE", "ASC");
		Order orderByField = new Order(orderField, desc ? "DESC" : "ASC");
		listFolderRequest.addOrder(orderByType);
		listFolderRequest.addOrder(orderByField);
		return listFolderRequest;
	}

	private List<INode> transToNodeList(RestFolderLists list) {
		List<INode> content = new ArrayList<INode>(list.getTotalCount());
		INode iNode = null;
		for (RestFolderInfo folderInfo : list.getFolders()) {
			iNode = new INode(folderInfo);
			content.add(iNode);
		}
		for (RestFileInfo fileInfo : list.getFiles()) {
			iNode = new INode(fileInfo);
			content.add(iNode);
		}
		return content;
	}

	/**
	 * 
	 * @param parentId
	 * @param name
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	@RequestMapping(value = "/link/preUpload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> preUpload(long ownerId, long parentId, String name, long size, String linkCode,
			HttpServletRequest httpServletRequest) throws BaseRunException {
		super.checkToken(httpServletRequest);
		if (StringUtils.isBlank(linkCode)) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		int i = 1;
		String uri = Constants.RESOURCE_FILE + '/' + ownerId;
		Map<String, String> headerMap = assembleLink(linkCode);
		FilePreUploadRequest request = new FilePreUploadRequest(name, parentId, size);
		TextResponse response;
		int status = -1;
		while (true) {
			response = ufmClientService.performJsonPutTextResponse(uri, headerMap, request);
			status = response.getStatusCode();
			if (status == HttpStatus.OK.value()) {
				String content = response.getResponseBody();
				FilePreUploadResponse preUploadRsp = JsonUtils.stringToObject(content, FilePreUploadResponse.class);
				return new ResponseEntity<String>(preUploadRsp.getUploadUrl(), HttpStatus.OK);
			} else if (status == HttpStatus.CONFLICT.value()) {
				String newName = FilesCommonUtils.getNewName(INode.TYPE_FILE, name, i);
				request.setName(newName);
				i++;
				continue;
			} else if (status == HttpStatus.BAD_REQUEST.value()) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			} else if (status == HttpStatus.FORBIDDEN.value()) {
				return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
			} else if (status == HttpStatus.PRECONDITION_FAILED.value()) {
				return new ResponseEntity<String>(HttpStatus.PRECONDITION_FAILED);
			} else if (status == HttpStatus.NOT_FOUND.value()) {
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	}

	@RequestMapping(value = "/listLinkApprove", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> listLinkApprove(@RequestParam(value = "accountId", required = true) Long accountId,
			@RequestParam(value = "linkOwner", required = false) Long linkOwner,
			@RequestParam(value = "approveBy", required = false) Long approveBy,
			@RequestParam(value = "status", required = false) Byte status,
			@RequestParam(value = "type", required = false) Byte type,
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
			@RequestParam(value = "orderField", defaultValue = "startTime") String orderField,
			@RequestParam(value = "isDesc", defaultValue = "true") boolean isDesc, HttpServletRequest request) {
		try {
			super.checkToken(request);
			RestLinkApproveListRequest listRequest = new RestLinkApproveListRequest();
			listRequest.setAccountId(accountId);
			listRequest.setLinkOwner(linkOwner);
			listRequest.setApproveBy(getCurrentUser().getCloudUserId());
			listRequest.setStatus(status);
			listRequest.setType(type);
			// 页码从1开始
			if (pageNumber > 0) {
				listRequest.setOffset((long) (pageNumber - 1) * pageSize);
			} else {
				listRequest.setOffset(0L);
			}
			listRequest.setLimit(pageSize);
			listRequest.setOrder(new Order(orderField, isDesc));

			RestLinkApproveList listLinkApprove = linkService.listLinkApprove(listRequest);
			Page<INodeLinkApprove> page = new PageImpl<>(listLinkApprove.getLinkApproveList(),
					new PageRequest(pageNumber, pageSize), listLinkApprove.getTotalCount());

			return new ResponseEntity<>(page, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	

	@RequestMapping(value = "/listAllLinkApprove", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> listAllLinkApprove(
			@RequestParam(value = "status", required = false) Byte status,
			@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
			@RequestParam(value = "orderField", defaultValue = "startTime") String orderField,
			@RequestParam(value = "isDesc", defaultValue = "true") boolean isDesc, HttpServletRequest request) {
		try {
			UserToken user=getCurrentUser();
			RestLinkApproveListRequest listRequest = new RestLinkApproveListRequest();
			listRequest.setAccountId(user.getAccountId());
			listRequest.setStatus(status);

			// 页码从1开始
			if (pageNumber > 0) {
				listRequest.setOffset((long) (pageNumber - 1) * pageSize);
			} else {
				listRequest.setOffset(0L);
			}
			listRequest.setOrder(new Order(orderField, isDesc));

			RestLinkApproveList listLinkApprove = linkService.listAllLinkApprove(listRequest);
			Page<INodeLinkApprove> page = new PageImpl<>(listLinkApprove.getLinkApproveList(),
					new PageRequest(pageNumber, pageSize), listLinkApprove.getTotalCount());

			return new ResponseEntity<>(page, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/getLinkApproveDetail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> getLinkApproveDetail(@RequestParam(value = "linkCode") String linkCode,
			HttpServletRequest request) {
		try {
			super.checkToken(request);

			RestLinkApproveDetail detail = linkService.getLinkApproveDetailByLinkCode(linkCode);

			LinkAndNodeV2 linkAndNode = linkService.getLinkOnlyByLinkCode(linkCode);
			detail.setLinkAndNode(linkAndNode);

			return new ResponseEntity<>(detail, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/approvalLink/{status}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> approvalLink(@PathVariable byte status, HttpServletRequest request) {
		try {
			super.checkToken(request);
			UserToken user = getCurrentUser();
			INodeLinkApprove restLinkApprove = new INodeLinkApprove();
			long linkOwner = Long.parseLong(request.getParameter("linkOwner"));
			String linkCode = request.getParameter("linkCode");
			restLinkApprove.setApproveBy(user.getCloudUserId());
			restLinkApprove.setApproveName(user.getAppName());
			restLinkApprove.setLinkCode(linkCode);
			restLinkApprove.setLinkOwner(linkOwner);
			restLinkApprove.setStatus(status);
			linkService.updateApproveStatus(restLinkApprove);
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

}
