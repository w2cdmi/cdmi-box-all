package com.huawei.sharedrive.uam.openapi.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.exception.AuthFailedException;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.RequestAttribute;
import com.huawei.sharedrive.uam.openapi.domain.RequestMail;
import com.huawei.sharedrive.uam.openapi.domain.RestMailSendRequest;
import com.huawei.sharedrive.uam.openapi.domain.mail.MailBean;
import com.huawei.sharedrive.uam.system.domain.MailServer;
import com.huawei.sharedrive.uam.system.service.CustomizeLogoService;
import com.huawei.sharedrive.uam.system.service.MailServerService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.FormValidateUtil;

import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/api/v2/mails")
public class MailsAPIController {
	public static final String TYPE_ADD_TEAM_MEMBER = "addTeamMember";

	public static final String TYPE_ADD_GROUP_MEMBER = "addGroupMember";

	public static final String TYPE_DELETE_GROUP_MEMBER = "deleteGroupMember";

	public static final String TYPE_QUIT_GROUP = "quitGroup";

	public static final String TYPE_LINK = "link";

	public static final String TYPE_LINK_DYNAMIC = "dynamicLink";

	public static final String TYPE_SHARE = "share";

	public static final String TYPE_SPACEMEMBER = "spaceMember";

	private static final String SPACE_SEND_EMAIL_FILE = "spaceSendEmailFile.ftl";

	private static final String SPACE_SEND_EMAIL_FOLDER = "spaceSendEmailFolder.ftl";

	private static final String SPACE_SEND_EMAIL_SUBJECT = "spaceSendEmailSubject.ftl";

	private static final String ADD_TEAMMEMBER_CONTENT = "addTeamspaceMember.ftl";

	private static final String ADD_GROUPMEMBER_CONTENT = "addGroupMemberContent.ftl";

	private static final String ADD_GROUPMEMBER_SUBJECT = "addGroupMemberSubject.ftl";

	private static final String DELETE_GROUPMEMBER_CONTENT = "deleteGroupMemberContent.ftl";

	private static final String DELETE_GROUPMEMBER_SUBJECT = "deleteGroupMemberSubject.ftl";

	private static final String QUIT_GROUP_CONTENT = "quitGroupContent.ftl";

	private static final String QUIT_GROUP_SUBJECT = "quitGroupSubject.ftl";

	private static final String ADD_TEAMMEMBER_SUBJECT = "addMemberSubject.ftl";

	private static final String CUSTOM_LOGO = "syscommon/logo";

	private static final String DEFAULT_LOGO = "static/skins/default/img/logo.png";

	private static final String LINK_CONTENT = "shareLink.ftl";

	private static final String LINK_DYNAMIC_CONTENT = "shareDynamicLink.ftl";

	private static final String LINK_CONTENT_WITH_PWD = "shareLinkWithPWD.ftl";

	private static final String LINK_DYNAMIC_SUBJECT = "dynamicLinkSubject.ftl";

	private static final String LINK_SUBJECT = "linkSubject.ftl";

	private static Logger logger = LoggerFactory.getLogger(MailsAPIController.class);

	private static final String SHARE_CONTENT = "share.ftl";

	private static final String SHARE_SUBJECT = "shareSubject.ftl";

	public final static String TYPE_FILE = "1";

	public final static String TYPE_FOLDER = "0";

	@Autowired
	private AuthAppService authAppService;

	@Autowired
	private CustomizeLogoService customizeService;

	@Autowired
	private MailServerService mailServerService;

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Autowired
	private UserLogService userLogService;

	/**
	 * 
	 * @param authorization
	 * @param mailRequest
	 * @return
	 * @throws BaseRunException
	 */
	@SuppressWarnings("PMD.PreserveStackTrace")
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> sendMail(@RequestHeader String authorization,
			@RequestBody RestMailSendRequest mailRequest, HttpServletRequest request) throws BaseRunException {
		AuthApp authApp;
		UserLog userLog = null;

		/*@SuppressWarnings("rawtypes")
		Iterator it = mailRequest.getMailTo().iterator();

		// Removing the email that its value is null;
		while (it.hasNext()) {
			RequestMail mail = (RequestMail) it.next();
			if (StringUtils.isBlank(mail.getEmail()))
				it.remove();
		}*/

		if (authorization.startsWith(UserTokenHelper.LINK_PREFIX)) {
			String date = request.getHeader("Date");
			userTokenHelper.checkDynamicLinkToken(authorization, date);
			String[] akArray = authorization.split(",");
			userLog = UserLogType.getUserLog(request, null, akArray[1], false);
			AuthApp defaultApp = authAppService.getDefaultWebApp();
			if (defaultApp == null || defaultApp.getType() != Constants.DEFAULT_WEB_APP_TYPE) {
				logger.error("dynamic link: defaultApp == null");
				throw new InternalServerErrorException("dynamic link: defaultApp == null");
			}
			authApp = defaultApp;
		} else {
			UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
			userLog = UserLogType.getUserLog(userToken);
			authApp = authAppService.getByAuthAppID(userToken.getAppId());
		}
		MailServer mailServer = checkAndGetMailServer(mailRequest, authApp);
		try {
			Map<String, Object> messageModel = new HashMap<String, Object>(10);
			String shareName = getParams(mailRequest, "sender");
			String shareNodeName = getParams(mailRequest, "nodeName");
			MailBean mailBean = new MailBean();

			fillMailTemplate(mailRequest, messageModel, shareName, shareNodeName, mailBean);

			mailServerService.sendHtmlMail(shareName, mailServer.getId(), covertRequestMail(mailRequest.getMailTo()),
					covertRequestMail(mailRequest.getCopyTo()), mailBean.getSubject(), mailBean.getMessage());
			userLogService.saveUserLog(userLog, UserLogType.KEY_SEND_MAIL, null);

			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (InvalidParamterException e) {
			logger.error("send mail fail", e);
			userLogService.saveUserLog(userLog, UserLogType.KEY_SEND_MAIL_ERR, null);

			throw e;
		} catch (Exception e) {
			logger.error("send mail fail", e);
			userLogService.saveUserLog(userLog, UserLogType.KEY_SEND_MAIL_ERR, null);

			throw new InternalServerErrorException(e.getMessage());
		}
	}

	private void fillMailTemplate(RestMailSendRequest mailRequest, Map<String, Object> messageModel, String shareName,
			String shareNodeName, MailBean mailBean) throws IOException {
		if (TYPE_SHARE.equals(mailRequest.getType())) {
			fillShareMail(mailRequest, messageModel, shareName, shareNodeName, mailBean);
		} else if (TYPE_LINK.equals(mailRequest.getType())) {
			fillLinkMail(mailRequest, messageModel, shareName, shareNodeName, mailBean);
		} else if (TYPE_LINK_DYNAMIC.equals(mailRequest.getType())) {
			fillDynamicLinkMail(mailRequest, messageModel, mailBean);
		} else if (TYPE_ADD_TEAM_MEMBER.equals(mailRequest.getType())) {
			fillAddTeamMemberMail(mailRequest, messageModel, mailBean);
		} else if (TYPE_SPACEMEMBER.equals(mailRequest.getType())) {
			fillSpaceMemberMail(mailRequest, messageModel, mailBean);
		} else if (TYPE_ADD_GROUP_MEMBER.equals(mailRequest.getType())) {
			fillAddGroupMemberMail(mailRequest, messageModel, mailBean);
		} else if (TYPE_DELETE_GROUP_MEMBER.equals(mailRequest.getType())) {
			fillDeleteGroupMemberMail(mailRequest, messageModel, mailBean);
		} else if (TYPE_QUIT_GROUP.equals(mailRequest.getType())) {
			fillQuitGroupMail(mailRequest, messageModel, mailBean);
		} else {
			throw new InvalidParamterException();
		}
	}

	private void fillSpaceMemberMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel,
			MailBean mailBean) throws IOException {
		String senderName = getParams(mailRequest, "sender");
		String teamSpaceName = HtmlUtils.htmlEscape(getParams(mailRequest, "teamSpaceName"));
		String nodeType = StringUtils.trim(getParams(mailRequest, "nodeType"));
		String nodeName = HtmlUtils.htmlEscape(getParams(mailRequest, "nodeName"));
		String nodeUrl = HtmlUtils.htmlEscape(getParams(mailRequest, "nodeUrl"));
		String teamSpaceUrl = HtmlUtils.htmlEscape(getParams(mailRequest, "teamSpaceUrl"));
		String message = StringUtils.trimToEmpty(getParams(mailRequest, "message"));
		if (StringUtils.isNotEmpty(message)) {
			message = HtmlUtils.htmlEscape(message);
		}
		messageModel.put("operUserName", senderName);
		messageModel.put("teamSpaceName", teamSpaceName);
		messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
		messageModel.put("logoString", getLogoString());
		messageModel.put("nodeName", nodeName);
		messageModel.put("nodeUrl", nodeUrl);
		messageModel.put("teamSpaceUrl", teamSpaceUrl);
		messageModel.put("message", message);
		if (TYPE_FOLDER.equals(nodeType)) {
			mailBean.setMessage(mailServerService.getEmailMsgByTemplate(SPACE_SEND_EMAIL_FOLDER, messageModel));
		} else if (TYPE_FILE.equals(nodeType)) {
			mailBean.setMessage(mailServerService.getEmailMsgByTemplate(SPACE_SEND_EMAIL_FILE, messageModel));
		}
		Map<String, Object> subjectModel = new HashMap<String, Object>(16);
		String appId = getParams(mailRequest, "appId");
		subjectModel.put("appId", HtmlUtils.htmlEscape(appId));
		mailBean.setSubject(mailServerService.getEmailMsgByTemplate(SPACE_SEND_EMAIL_SUBJECT, subjectModel));
	}

	private MailServer checkAndGetMailServer(RestMailSendRequest mailRequest, AuthApp authApp) {
		if (authApp == null) {
			throw new AuthFailedException();
		}
		MailServer mailServer = mailServerService.getMailServerByAppId(authApp.getAuthAppId());
		if (mailServer == null) {
			throw new BusinessException();
		}
		if (StringUtils.isBlank(getParams(mailRequest, "sender"))) {
			throw new InvalidParamterException();
		}
		List<RequestMail> mailToList = mailRequest.getMailTo();
		if (mailToList == null) {
			throw new InvalidParamterException();
		} else {
			for (RequestMail mailTo : mailToList) {
				if (!FormValidateUtil.isValidEmail(mailTo.getEmail())) {
					throw new InvalidParamterException();
				}
			}
		}
		List<RequestMail> copyToList = mailRequest.getCopyTo();
		if (copyToList != null) {
			for (RequestMail copyTo : copyToList) {
				if (!FormValidateUtil.isValidEmail(copyTo.getEmail())) {
					throw new InvalidParamterException();
				}
			}
		}
		return mailServer;
	}

	private String[] covertRequestMail(List<RequestMail> listMail) throws BaseRunException {
		if (listMail == null) {
			return new String[] {};
		}
		int size = listMail.size();

		List<String> tempList = new ArrayList<String>();
		for (RequestMail mail : listMail) {
			String email = mail.getEmail();
			if (StringUtils.isBlank(email)) {
				continue;
			}
			tempList.add(email);
		}
		String[] mails = new String[tempList.size()];
		for (int i = 0; i < size; i++) {
			mails[i] = tempList.get(i);
		}
		return mails;
	}

	/**
	 * @param mailRequest
	 * @param messageModel
	 * @param mailBean
	 * @throws IOException
	 */
	private void fillAddTeamMemberMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel,
			MailBean mailBean) throws IOException {
		String senderName = HtmlUtils.htmlEscape(getParams(mailRequest, "sender"));
		String teamSpaceName = HtmlUtils.htmlEscape(getParams(mailRequest, "teamSpaceName"));
		messageModel.put("reciveName", "");
		messageModel.put("operUserName", HtmlUtils.htmlEscape(senderName));
		messageModel.put("teamSpaceName", HtmlUtils.htmlEscape(teamSpaceName));
		messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
		messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
		String message = StringUtils.trimToEmpty(getParams(mailRequest, "message"));
		if (StringUtils.isNotEmpty(message)) {
			message = HtmlUtils.htmlEscape(message);
		}
		messageModel.put("message", message);
		Map<String, Object> subectModel = new HashMap<String, Object>(3);
		String appEmailTitle = customizeService.getAppEmailTitle();
		subectModel.put("appEmailTitle", HtmlUtils.htmlEscape(appEmailTitle));
		subectModel.put("operUserName", senderName);
		subectModel.put("teamSpaceName", teamSpaceName);
		mailBean.setMessage(mailServerService.getEmailMsgByTemplate(ADD_TEAMMEMBER_CONTENT, messageModel));
		mailBean.setSubject(mailServerService.getEmailMsgByTemplate(ADD_TEAMMEMBER_SUBJECT, subectModel));
	}

	private void fillAddGroupMemberMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel,
			MailBean mailBean) throws IOException {
		String senderName = HtmlUtils.htmlEscape(getParams(mailRequest, "sender"));
		String groupName = HtmlUtils.htmlEscape(getParams(mailRequest, "groupName"));
		messageModel.put("operUserName", senderName);
		messageModel.put("groupName", groupName);
		messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
		messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
		String message = StringUtils.trimToEmpty(getParams(mailRequest, "message"));
		if (StringUtils.isNotEmpty(message)) {
			message = HtmlUtils.htmlEscape(message);
		}
		messageModel.put("message", message);

		Map<String, Object> subectModel = new HashMap<String, Object>(3);
		String appEmailTitle = customizeService.getAppEmailTitle();
		subectModel.put("appEmailTitle", HtmlUtils.htmlEscape(appEmailTitle));
		subectModel.put("operUserName", senderName);
		subectModel.put("groupName", groupName);

		mailBean.setMessage(mailServerService.getEmailMsgByTemplate(ADD_GROUPMEMBER_CONTENT, messageModel));
		mailBean.setSubject(mailServerService.getEmailMsgByTemplate(ADD_GROUPMEMBER_SUBJECT, subectModel));
	}

	private void fillDeleteGroupMemberMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel,
			MailBean mailBean) throws IOException {
		String senderName = HtmlUtils.htmlEscape(getParams(mailRequest, "sender"));
		String groupName = HtmlUtils.htmlEscape(getParams(mailRequest, "groupName"));
		messageModel.put("operUserName", senderName);
		messageModel.put("groupName", groupName);
		messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
		messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
		String message = StringUtils.trimToEmpty(getParams(mailRequest, "message"));
		if (StringUtils.isNotEmpty(message)) {
			message = HtmlUtils.htmlEscape(message);
		}
		messageModel.put("message", message);

		Map<String, Object> subectModel = new HashMap<String, Object>(3);
		String appEmailTitle = customizeService.getAppEmailTitle();
		subectModel.put("appEmailTitle", HtmlUtils.htmlEscape(appEmailTitle));
		subectModel.put("operUserName", senderName);
		subectModel.put("groupName", groupName);

		mailBean.setMessage(mailServerService.getEmailMsgByTemplate(DELETE_GROUPMEMBER_CONTENT, messageModel));
		mailBean.setSubject(mailServerService.getEmailMsgByTemplate(DELETE_GROUPMEMBER_SUBJECT, subectModel));
	}

	private void fillQuitGroupMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel, MailBean mailBean)
			throws IOException {
		String senderName = HtmlUtils.htmlEscape(getParams(mailRequest, "sender"));
		String groupName = HtmlUtils.htmlEscape(getParams(mailRequest, "groupName"));
		messageModel.put("operUserName", senderName);
		messageModel.put("groupName", groupName);
		messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
		messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
		String message = StringUtils.trimToEmpty(getParams(mailRequest, "message"));
		if (StringUtils.isNotEmpty(message)) {
			message = HtmlUtils.htmlEscape(message);
		}
		messageModel.put("message", message);

		Map<String, Object> subectModel = new HashMap<String, Object>(3);
		String appEmailTitle = customizeService.getAppEmailTitle();
		subectModel.put("appEmailTitle", HtmlUtils.htmlEscape(appEmailTitle));
		subectModel.put("operUserName", senderName);
		subectModel.put("groupName", groupName);

		mailBean.setMessage(mailServerService.getEmailMsgByTemplate(QUIT_GROUP_CONTENT, messageModel));
		mailBean.setSubject(mailServerService.getEmailMsgByTemplate(QUIT_GROUP_SUBJECT, subectModel));
	}

	/**
	 * @param mailRequest
	 * @param messageModel
	 * @param shareName
	 * @param shareNodeName
	 * @param mailBean
	 * @throws IOException
	 */
	private void fillLinkMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel, String shareName,
			String shareNodeName, MailBean mailBean) throws IOException {
		String plainAccessCode = getParams(mailRequest, "plainAccessCode");
		messageModel.put("shareNodeName", HtmlUtils.htmlEscape(shareNodeName));
		messageModel.put("shareName", HtmlUtils.htmlEscape(shareName));
		messageModel.put("linkUrl", getParams(mailRequest, "linkUrl"));
		messageModel.put("accessCode", HtmlUtils.htmlEscape(plainAccessCode));
		messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
		messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
		String message = StringUtils.trimToEmpty(getParams(mailRequest, "message"));
		if (StringUtils.isNotEmpty(message)) {
			message = HtmlUtils.htmlEscape(message);
		}
		messageModel.put("message", message);
		String appEmailTitle = customizeService.getAppEmailTitle();
		messageModel.put("appEmailTitle", HtmlUtils.htmlEscape(appEmailTitle));

		Map<String, Object> subectModel = new HashMap<String, Object>(3);
		subectModel.put("appEmailTitle", HtmlUtils.htmlEscape(appEmailTitle));
		subectModel.put("shareName", HtmlUtils.htmlEscape(shareName));
		subectModel.put("shareNodeName", HtmlUtils.htmlEscape(shareNodeName));
		mailBean.setMessage(mailServerService.getEmailMsgByTemplate(
				StringUtils.isEmpty(plainAccessCode) ? LINK_CONTENT : LINK_CONTENT_WITH_PWD, messageModel));
		mailBean.setSubject(mailServerService.getEmailMsgByTemplate(LINK_SUBJECT, subectModel));
	}

	/**
	 * @param mailRequest
	 * @param messageModel
	 * @param shareName
	 * @param shareNodeName
	 * @param mailBean
	 * @throws IOException
	 */
	private void fillDynamicLinkMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel,
			MailBean mailBean) throws IOException {
		String plainAccessCode = getParams(mailRequest, "plainAccessCode");
		messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
		messageModel.put("accessCode", HtmlUtils.htmlEscape(plainAccessCode));
		messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
		String message = StringUtils.trimToEmpty(getParams(mailRequest, "message"));
		if (StringUtils.isNotEmpty(message)) {
			message = HtmlUtils.htmlEscape(message);
		}
		messageModel.put("message", message);
		String appEmailTitle = customizeService.getAppEmailTitle();
		messageModel.put("appEmailTitle", HtmlUtils.htmlEscape(appEmailTitle));
		mailBean.setMessage(mailServerService.getEmailMsgByTemplate(LINK_DYNAMIC_CONTENT, messageModel));
		mailBean.setSubject(mailServerService.getEmailMsgByTemplate(LINK_DYNAMIC_SUBJECT, messageModel));
	}

	/**
	 * @param mailRequest
	 * @param messageModel
	 * @param shareName
	 * @param shareNodeName
	 * @param mailBean
	 * @throws IOException
	 */
	private void fillShareMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel, String shareName,
			String shareNodeName, MailBean mailBean) throws IOException {
		messageModel.put("reciveName", "");
		messageModel.put("shareName", HtmlUtils.htmlEscape(shareName));
		messageModel.put("shareNodeName", HtmlUtils.htmlEscape(shareNodeName));
		messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
		messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
		String message = StringUtils.trimToEmpty(getParams(mailRequest, "message"));
		if (StringUtils.isNotEmpty(message)) {
			message = HtmlUtils.htmlEscape(message);
		}
		messageModel.put("message", message);
		String type = getParams(mailRequest, "type");
		String ownerId = getParams(mailRequest, "ownerId");
		String nodeId = getParams(mailRequest, "nodeId");
		if (StringUtils.isEmpty(nodeId)) {
			nodeId = getParams(mailRequest, "inodeId");
		}

		if (StringUtils.isEmpty(type) || StringUtils.isEmpty(ownerId) || StringUtils.isEmpty(nodeId)) {
			messageModel.put("shareDetail", "shared");
		} else {
			messageModel.put("shareDetail", "shared/detail/" + type + '/' + ownerId + '/' + nodeId);
		}
		Map<String, Object> subectModel = new HashMap<String, Object>(3);
		String appEmailTitle = customizeService.getAppEmailTitle();
		subectModel.put("appEmailTitle", HtmlUtils.htmlEscape(appEmailTitle));
		subectModel.put("shareName", HtmlUtils.htmlEscape(shareName));
		subectModel.put("shareNodeName", HtmlUtils.htmlEscape(shareNodeName));
		mailBean.setMessage(mailServerService.getEmailMsgByTemplate(SHARE_CONTENT, messageModel));
		mailBean.setSubject(mailServerService.getEmailMsgByTemplate(SHARE_SUBJECT, subectModel));
	}

	private String getLogoString() {
		CustomizeLogo customize = customizeService.getCustomize();
		String domainName = customize.getDomainName();
		String withSuffix = "/";
		if (withSuffix.equals(domainName.substring(domainName.length() - 1, domainName.length()))) {
			withSuffix = "";
		}
		if (customize.getLogo() == null) {
			return withSuffix + DEFAULT_LOGO;
		}
		return withSuffix + CUSTOM_LOGO;
	}

	private String getAbsoluteUrl() {
		CustomizeLogo customize = customizeService.getCustomize();
		String domainName = customize.getDomainName();
		String withSuffix = "/";
		if (!withSuffix.equals(domainName.substring(domainName.length() - 1, domainName.length()))) {
			domainName = domainName + withSuffix;
		}
		return domainName;
	}

	private String getParams(RestMailSendRequest mailRequest, String key) throws InvalidParamterException {
		List<RequestAttribute> atrributeList = mailRequest.getParams();
		if (atrributeList == null) {
			throw new InvalidParamterException();
		}
		for (RequestAttribute attribute : atrributeList) {
			if (attribute.getName().equals(key)) {
				return attribute.getValue();
			}
		}
		return "";
	}

}
