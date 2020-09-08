package com.huawei.sharedrive.uam.openapi.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.RequestAttribute;
import com.huawei.sharedrive.uam.openapi.domain.RequestMail;
import com.huawei.sharedrive.uam.openapi.domain.RestMailSendRequest;
import com.huawei.sharedrive.uam.openapi.domain.mail.MailBean;
import com.huawei.sharedrive.uam.system.domain.MailServer;
import com.huawei.sharedrive.uam.system.service.CustomizeLogoService;
import com.huawei.sharedrive.uam.system.service.MailServerService;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.util.FormValidateUtil;

import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/api/v2/mail")
public class MailServiceAPIController
{
    private static final String UTF8 = "UTF-8";
    
    private static Logger logger = LoggerFactory.getLogger(MailServiceAPIController.class);
    
    private static final String CUSTOM_LOGO = "syscommon/logo";
    
    private static final String DEFAULT_LOGO = "static/skins/default/img/logo.png";
    
    private static final String LINK_SUBJECT = "linkSubject.ftl";
    
    private static final String LINK_CONTENT_WITH_PWD = "shareLinkWithPWD.ftl";
    
    private static final String LINK_CONTENT = "shareLink.ftl";
    
    private static final String SHARE_CONTENT = "share.ftl";
    
    private static final String SHARE_SUBJECT = "shareSubject.ftl";
    
    private static final String TEAMSPACE_CONTENT = "addTeamspaceMember.ftl";
    
    private static final String TEAMSPACE_SUBJECT = "addMemberSubject.ftl";
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private MailServerService mailServerService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private CustomizeLogoService customizeService;
    
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
        @RequestBody RestMailSendRequest mailRequest) throws BaseRunException
    {
        User userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        UserLog userLog = UserLogType.getUserLog(userToken);
        AuthApp authApp = authAppService.getByAuthAppID(userToken.getAppId());
        MailServer mailServer = checkAndGetMailServer(mailRequest, userToken, authApp);
        try
        {
            Map<String, Object> messageModel = new HashMap<String, Object>(10);
            String shareName = URLDecoder.decode(getParams(mailRequest, "sender"), UTF8);
            String shareNodeName = HtmlUtils.htmlEscape(URLDecoder.decode(getParams(mailRequest, "nodeName"),
                UTF8));
            MailBean mailBean = new MailBean();
            if (MailsAPIController.TYPE_SHARE.equals(mailRequest.getType()))
            {
                fillShareMail(mailRequest, messageModel, shareName, shareNodeName, mailBean);
            }
            else if (MailsAPIController.TYPE_LINK.equals(mailRequest.getType()))
            {
                fillLinkMail(mailRequest, messageModel, shareName, shareNodeName, mailBean);
            }
            else if (MailsAPIController.TYPE_ADD_TEAM_MEMBER.equals(mailRequest.getType()))
            {
                fillAddTeamMemberLink(mailRequest, messageModel, mailBean);
            }
            else
            {
                throw new InvalidParamterException();
            }
            mailServerService.sendHtmlMail(shareName,
                mailServer.getId(),
                covertRequestMail(mailRequest.getMailTo()),
                covertRequestMail(mailRequest.getCopyTo()),
                mailBean.getSubject(),
                mailBean.getMessage());
            userLogService.saveUserLog(userLog, UserLogType.KEY_SEND_MAIL, null);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (InvalidParamterException e)
        {
            logger.error("send mail fail", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_SEND_MAIL_ERR, null);
            throw e;
        }
        catch (Exception e)
        {
            logger.error("send mail fail", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_SEND_MAIL_ERR, null);
            throw new InternalServerErrorException();
        }
        
    }
    
    private void fillAddTeamMemberLink(RestMailSendRequest mailRequest, Map<String, Object> messageModel,
        MailBean mailBean) throws UnsupportedEncodingException, IOException
    {
        String senderName = URLDecoder.decode(getParams(mailRequest, "sender"), UTF8);
        String teamSpaceName = HtmlUtils.htmlEscape(URLDecoder.decode(getParams(mailRequest, "teamSpaceName"),
            UTF8));
        messageModel.put("reciveName", "");
        messageModel.put("operUserName", senderName);
        messageModel.put("teamSpaceName", teamSpaceName);
        messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
        messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
        
        String message = getParams(mailRequest, "message");
        if (StringUtils.isNotEmpty(message))
        {
            message = HtmlUtils.htmlEscape(URLDecoder.decode(message, UTF8));
        }
        else
        {
            message = "";
        }
        messageModel.put("message", message);
        
        Map<String, Object> subectModel = new HashMap<String, Object>(3);
        String appEmailTitle = customizeService.getAppEmailTitle();
        subectModel.put("appEmailTitle", appEmailTitle);
        subectModel.put("operUserName", senderName);
        subectModel.put("teamSpaceName", teamSpaceName);
        
        mailBean.setMessage(mailServerService.getEmailMsgByTemplate(TEAMSPACE_CONTENT, messageModel));
        mailBean.setSubject(mailServerService.getEmailMsgByTemplate(TEAMSPACE_SUBJECT, subectModel));
    }
    
    private void fillLinkMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel,
        String shareName, String shareNodeName, MailBean mailBean) throws UnsupportedEncodingException,
        IOException
    {
        String plainAccessCode = getParams(mailRequest, "plainAccessCode");
        messageModel.put("shareNodeName", shareNodeName);
        messageModel.put("shareName", shareName);
        messageModel.put("linkUrl", getParams(mailRequest, "linkUrl"));
        messageModel.put("accessCode", plainAccessCode);
        messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
        messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
        String message = getParams(mailRequest, "message");
        if (StringUtils.isNotEmpty(message))
        {
            message = HtmlUtils.htmlEscape(URLDecoder.decode(message, UTF8));
        }
        else
        {
            message = "";
        }
        messageModel.put("message", message);
        String appEmailTitle = customizeService.getAppEmailTitle();
        messageModel.put("appEmailTitle", appEmailTitle);
        mailBean.setMessage(mailServerService.getEmailMsgByTemplate(StringUtils.isEmpty(plainAccessCode) ? LINK_CONTENT
            : LINK_CONTENT_WITH_PWD,
            messageModel));
        mailBean.setSubject(mailServerService.getEmailMsgByTemplate(LINK_SUBJECT, messageModel));
    }
    
    private void fillShareMail(RestMailSendRequest mailRequest, Map<String, Object> messageModel,
        String shareName, String shareNodeName, MailBean mailBean) throws UnsupportedEncodingException,
        IOException
    {
        messageModel.put("reciveName", "");
        messageModel.put("shareName", shareName);
        messageModel.put("shareNodeName", shareNodeName);
        messageModel.put("AbsoluteUrlPrefix", getAbsoluteUrl());
        messageModel.put("logoString", HtmlUtils.htmlEscape(getLogoString()));
        
        String message = getParams(mailRequest, "message");
        if (StringUtils.isNotEmpty(message))
        {
            message = HtmlUtils.htmlEscape(URLDecoder.decode(message, UTF8));
        }
        else
        {
            message = "";
        }
        messageModel.put("message", message);
        String type = getParams(mailRequest, "type");
        String ownerId = getParams(mailRequest, "ownerId");
        String inodeId = getParams(mailRequest, "nodeId");
        if (StringUtils.isEmpty(inodeId))
        {
            inodeId = getParams(mailRequest, "inodeId");
        }
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(ownerId) || StringUtils.isEmpty(inodeId))
        {
            messageModel.put("shareDetail", "shared");
        }
        else
        {
            messageModel.put("shareDetail", "shared/detail/" + type + '/' + ownerId + '/' + inodeId);
        }
        Map<String, Object> subectModel = new HashMap<String, Object>(3);
        String appEmailTitle = customizeService.getAppEmailTitle();
        subectModel.put("appEmailTitle", appEmailTitle);
        subectModel.put("shareName", shareName);
        subectModel.put("shareNodeName", shareNodeName);
        
        mailBean.setMessage(mailServerService.getEmailMsgByTemplate(SHARE_CONTENT, messageModel));
        mailBean.setSubject(mailServerService.getEmailMsgByTemplate(SHARE_SUBJECT, subectModel));
    }
    
    private MailServer checkAndGetMailServer(RestMailSendRequest mailRequest, User userToken, AuthApp authApp)
    {
        if (authApp == null)
        {
            throw new AuthFailedException();
        }
        MailServer mailServer = mailServerService.getMailServerByAppId(userToken.getAppId());
        if (mailServer == null)
        {
            throw new BusinessException();
        }
        if (StringUtils.isBlank(getParams(mailRequest, "sender")))
        {
            throw new InvalidParamterException();
        }
        List<RequestMail> mailToList = mailRequest.getMailTo();
        if (mailToList == null)
        {
            throw new InvalidParamterException();
        }
        else
        {
            for (RequestMail mailTo : mailToList)
            {
                if (!FormValidateUtil.isValidEmail(mailTo.getEmail()))
                {
                    throw new InvalidParamterException();
                }
            }
        }
        List<RequestMail> copyToList = mailRequest.getCopyTo();
        if (copyToList != null)
        {
            for (RequestMail copyTo : copyToList)
            {
                if (!FormValidateUtil.isValidEmail(copyTo.getEmail()))
                {
                    throw new InvalidParamterException();
                }
            }
        }
        return mailServer;
    }
    
    private String getParams(RestMailSendRequest mailRequest, String key) throws InvalidParamterException
    {
        List<RequestAttribute> atrributeList = mailRequest.getParams();
        if (atrributeList == null)
        {
            throw new InvalidParamterException();
        }
        for (RequestAttribute attribute : atrributeList)
        {
            if (attribute.getName().equals(key))
            {
                return attribute.getValue();
            }
        }
        return "";
    }
    
    private String[] covertRequestMail(List<RequestMail> listMail) throws BaseRunException
    {
        if (listMail == null)
        {
            return new String[]{};
        }
        int size = listMail.size();
        String[] mails = new String[size];
        for (int i = 0; i < size; i++)
        {
            mails[i] = listMail.get(i).getEmail();
        }
        return mails;
    }
    
    private String getLogoString()
    {
        CustomizeLogo customize = customizeService.getCustomize();
        String domainName = customize.getDomainName();
        String withSuffix = "/";
        if (withSuffix.equals(domainName.substring(domainName.length() - 1, domainName.length())))
        {
            withSuffix = "";
        }
        if (customize.getLogo() == null)
        {
            return withSuffix + DEFAULT_LOGO;
        }
        return withSuffix + CUSTOM_LOGO;
    }
    
    private String getAbsoluteUrl()
    {
        CustomizeLogo customize = customizeService.getCustomize();
        String domainName = customize.getDomainName();
        String withSuffix = "/";
        if (!withSuffix.equals(domainName.substring(domainName.length() - 1, domainName.length())))
        {
            domainName = domainName + withSuffix;
        }
        return domainName;
    }
    
}
