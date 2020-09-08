package pw.cdmi.box.disk.message.web;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.UAMMessageClient;
import pw.cdmi.box.disk.client.api.UFMMessageClient;
import pw.cdmi.box.disk.client.domain.message.ListMessageRequest;
import pw.cdmi.box.disk.client.domain.message.MessageList;
import pw.cdmi.box.disk.client.domain.message.MessageParamName;
import pw.cdmi.box.disk.client.domain.message.MessageResponse;
import pw.cdmi.box.disk.client.domain.message.MessageStatus;
import pw.cdmi.box.disk.client.domain.message.MessageType;
import pw.cdmi.box.disk.client.domain.message.NodeType;
import pw.cdmi.box.disk.client.domain.message.UpdateMessageRequest;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.message.freemarker.MessageFreemarker;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/message")
public class MessageController extends CommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);
    
    @Autowired
    @Qualifier("ufmClientService")
    private RestClient ufmClientService;
    
    @Autowired
    @Qualifier("uamClientService")
    private RestClient uamClientService;
    
    private UFMMessageClient ufmMessageClient;
    
    private UAMMessageClient uamMessageClient;
    
    @Autowired
    @Qualifier("messageSource")
    private ResourceBundleMessageSource messageSource;
    
    private static final int DEFALUT_ANNOUNCEMENT_SIZE = 3;
    
    private static final int DEFALUT_INITIAL_SIZE = 10;
    
    @RequestMapping(value = "/{messageId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable Long messageId, HttpServletRequest request)
    {
        super.checkToken(request);
        ufmMessageClient.deleteMessage(getToken(), messageId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        return "message/messageList";
    }
    
    @RequestMapping(value = "/listener", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getListener(HttpServletRequest request)
    {
        
        return new ResponseEntity<String>(ufmMessageClient.getMessageListener(getToken()).getUrl(),
            HttpStatus.OK);
    }
    
    @RequestMapping(value = "/getMessageCount", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Long> getMessageCount(HttpServletRequest request)
    {
        super.checkToken(request);
        ListMessageRequest listMessageRequest = generalRequest(0L, 0, MessageStatus.UNREAD.getStatus());
        String token = getToken();
        
        long count = getUserMessageCount(token, listMessageRequest);
        count += getSystemMessageCount(token);
        return new ResponseEntity<Long>(count, HttpStatus.OK);
    }
    
    @PostConstruct
    public void init()
    {
        ufmMessageClient = new UFMMessageClient(ufmClientService);
        uamMessageClient = new UAMMessageClient(uamClientService);
    }
    
    @RequestMapping(value = "/listSystemMessage", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<MessageResponse>> listSystemMessage(HttpServletRequest request)
    {
        super.checkToken(request);
        String token = getToken();
        
        ListMessageRequest listMessageRequest = generalRequest(null,
            DEFALUT_ANNOUNCEMENT_SIZE,
            ListMessageRequest.DEFAULT_STATUS);
        MessageList messageList = uamMessageClient.listMessage(getToken(), listMessageRequest);
        
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        fillMessageContent(messageList, locale);
        
        updateStatusToRead(token, messageList);
        
        return new ResponseEntity<List<MessageResponse>>(messageList.getMessages(), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/listUserMessage", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<MessageResponse>> listUserMessage(Long receiverId,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
        HttpServletRequest request)
    {
        super.checkToken(request);
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        
        long offset = 0L;
        if (pageNumber > 0)
        {
            offset = (long) (pageNumber - 1) * pageSize;
        }
        
        String token = getToken();
        
        ListMessageRequest listMessageRequest = generalRequest(offset,
            pageSize,
            ListMessageRequest.DEFAULT_STATUS);
        MessageList list = ufmMessageClient.listMessage(token, listMessageRequest);
        
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        
        fillMessageContent(list, locale);
        Page<MessageResponse> page = new PageImpl<MessageResponse>(list.getMessages(), pageRequest,
            list.getTotalCount());
        
        updateStatusToRead(token, list);
        
        return new ResponseEntity<Page<MessageResponse>>(page, HttpStatus.OK);
    }
    
    private void fillMessageContent(MessageList messageList, Locale locale)
    {
        if (null == messageList || null == messageList.getMessages() || messageList.getMessages().isEmpty())
        {
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>(DEFALUT_INITIAL_SIZE);
        StringBuffer ftlFileName;
        String content;
        for (MessageResponse message : messageList.getMessages())
        {
            ftlFileName = getFtlAndFillParams(message, params, locale);
            if (ftlFileName == null)
            {
                continue;
            }
            content = MessageFreemarker.getFreeMarkerContent(ftlFileName.toString(), params, locale.getLanguage());
            message.setContent(content);
        }
    }
    
    private ListMessageRequest generalRequest(Long offset, Integer pageSize, String status)
    {
        ListMessageRequest request = new ListMessageRequest();
        request.setOffset(offset);
        request.setLimit(pageSize);
        request.setStatus(status);
        return request;
    }
    
    private StringBuffer getFtlAndFillParams(MessageResponse message, Map<String, Object> params, Locale locale)
    {
        if (MessageType.getMessageType(message.getType()) == null)
        {
            return null;
        }
        StringBuffer ftlFileName = new StringBuffer("");
        switch (MessageType.getMessageType(message.getType()))
        {
            case SHARE:
            {
            	Object primaryNodeType = message.getParam(MessageParamName.PRIMARY_NODE_TYPE);
            	if (null != primaryNodeType && primaryNodeType.toString().equalsIgnoreCase(INode.TYPE_MIGRATION + "")){
            		ftlFileName.append("dataMigration");
            	} else {
            		 ftlFileName.append(NodeType.File.getType()
                             .equals(String.valueOf(message.getParam(MessageParamName.NODE_TYPE))) ? "shareFile"
                             : "shareFolder");
            	}

                params.put("providerName", message.getProviderName());
                params.put("nodeName", message.getParam(MessageParamName.NODE_NAME));
                break;
            }
            case DELETE_SHARE:
            {
                ftlFileName.append(NodeType.File.getType()
                    .equals(String.valueOf(message.getParam(MessageParamName.NODE_TYPE))) ? "cancelFileShare"
                    : "cancelFolderShare");
                params.put("providerName", message.getProviderName());
                params.put("nodeName", message.getParam(MessageParamName.NODE_NAME));
                break;
            }
            case TEAMSPACE_UPLOAD:
            {
                ftlFileName.append("teamSpaceNewFile");
                params.put("providerName", message.getProviderName());
                params.put("nodeName", message.getParam(MessageParamName.NODE_NAME));
                params.put("teamSpaceName", message.getParam(MessageParamName.TEAMSPACE_NAME));
                break;
            }
            case TEAMSPACE_ADD_MEMBER:
            {
                ftlFileName.append("joinTeamSpace");
                params.put("providerName", message.getProviderName());
                params.put("teamSpaceName", message.getParam(MessageParamName.TEAMSPACE_NAME));
                break;
            }
            case LEAVE_TEAMSPACE:
            {
                ftlFileName.append("quitTeamSpace");
                params.put("providerName", message.getProviderName());
                params.put("teamSpaceName", message.getParam(MessageParamName.TEAMSPACE_NAME));
                break;
            }
            case TEAMSPACE_DELETE_MEMBER:
            {
                ftlFileName.append("removeFromTeamSpace");
                params.put("providerName", message.getProviderName());
                params.put("teamSpaceName", message.getParam(MessageParamName.TEAMSPACE_NAME));
                break;
            }
            case TEAMSPACE_ROLE_UPDATE:
            {
                ftlFileName.append("teamSpaceRoleUpdate");
                params.put("providerName", message.getProviderName());
                params.put("teamSpaceName", message.getParam(MessageParamName.TEAMSPACE_NAME));
                String roleName = getTeamSpaceI18NRoleName(String.valueOf(message.getParam(MessageParamName.CURRENT_ROLE)),
                    locale);
                params.put("currentRole", roleName);
                break;
            }
            case GROUP_ADD_MEMBER:
            {
                ftlFileName.append("joinGroup");
                params.put("providerName", message.getProviderName());
                params.put("groupName", message.getParam(MessageParamName.GROUP_NAME));
                break;
            }
            case LEAVE_GROUP:
            {
                ftlFileName.append("quitGroup");
                params.put("providerName", message.getProviderName());
                params.put("groupName", message.getParam(MessageParamName.GROUP_NAME));
                break;
            }
            case GROUP_DELETE_MEMBER:
            {
                ftlFileName.append("removeFromGroup");
                params.put("providerName", message.getProviderName());
                params.put("groupName", message.getParam(MessageParamName.GROUP_NAME));
                break;
            }
            case GROUP_ROLE_UPDATE:
            {
                ftlFileName.append("groupRoleUpdate");
                params.put("providerName", message.getProviderName());
                params.put("groupName", message.getParam(MessageParamName.GROUP_NAME));
                String roleName = getGroupI18NRoleName(String.valueOf(message.getParam(MessageParamName.CURRENT_ROLE)),
                    locale);
                params.put("currentRole", roleName);
                break;
            }
            case SYSTEM:
            {
                ftlFileName.append("systemAnnouncement");
                params.put("title", message.getParam(MessageParamName.TITLE));
                break;
            }
            default:
            {
                break;
            }
        }
        appendFileSurffix(locale, ftlFileName);
        htmlParaEscape(params);
        return ftlFileName;
    }

    private void htmlParaEscape(Map<String, Object> params)
    {
        for (Map.Entry<String, Object> entry : params.entrySet())
        {
            if (entry.getValue() instanceof String)
            {
                params.put(entry.getKey(), HtmlUtils.htmlEscape((String) entry.getValue()));
            }
        }
    }

    private void appendFileSurffix(Locale locale, StringBuffer ftlFileName)
    {
        String language = locale.getLanguage();
        if ("zh_CN".equals(language) || "zh".equals(language))
        {
            ftlFileName.append("_zh.ftl");
        }
        else
        {
            ftlFileName.append("_en.ftl");
        }
    }
    
    private String getGroupI18NRoleName(String role, Locale locale)
    {
        switch (role)
        {
            case "member":
            {
                return messageSource.getMessage("group.title.user", null, locale);
            }
            case "manager":
            {
                return messageSource.getMessage("group.title.manager", null, locale);
            }
            case "admin":
            {
                return messageSource.getMessage("group.title.admin", null, locale);
            }
            default:
            {
                return "";
            }
        }
    }
    
    private long getSystemMessageCount(String token)
    {
        long count = 0L;

        try
        {
            count = doGetUnReadMsgCount(token);
        }
        catch (RestException e)
        {
            LOGGER.error("getSystemMessageCount failed.", e);
        }
        
        return count;
    }

    private long doGetUnReadMsgCount(String token) throws RestException
    {
        long count = 0L;
        ListMessageRequest request = new ListMessageRequest();
        request.setOffset(0L);
        request.setLimit(DEFALUT_ANNOUNCEMENT_SIZE);
        
        MessageList list = uamMessageClient.listMessage(token, request);
        if (null != list && null != list.getMessages())
        {
            for (MessageResponse m : list.getMessages())
            {
                if (MessageStatus.UNREAD.getStatus().equals(m.getStatus()))
                {
                    count++;
                }
            }
        }
        return count;
    }
    
    private String getTeamSpaceI18NRoleName(String role, Locale locale)
    {
        switch (role)
        {
            case "editor":
            {
                return messageSource.getMessage("systemRole.title.editor", null, locale);
            }
            case "uploader":
            {
                return messageSource.getMessage("systemRole.title.uploader", null, locale);
            }
            case "previewer":
            {
                return messageSource.getMessage("systemRole.title.previewer", null, locale);
            }
            case "uploadAndView":
            {
                return messageSource.getMessage("systemRole.title.uploadAndView", null, locale);
            }
            case "viewer":
            {
                return messageSource.getMessage("systemRole.title.viewer", null, locale);
            }
            case "manager":
            {
                return messageSource.getMessage("teamSpace.label.manager", null, locale);
            }
            case "admin":
            {
                return messageSource.getMessage("teamSpace.label.owner", null, locale);
            }
            default:
            {
                return "";
            }
        }
    }
    
    private long getUserMessageCount(String token, ListMessageRequest listMessageRequest)
    {
        try
        {
            MessageList list = ufmMessageClient.listMessage(token, listMessageRequest);
            if (null != list)
            {
                return list.getTotalCount();
            }
        }
        catch (RestException e)
        {
            LOGGER.error("getUserMessageCount failed.", e);
        }
        return 0L;
    }
    
    private void updateStatus(String token, UpdateMessageRequest request, MessageResponse message)
    {
        try
        {
            if (MessageType.SYSTEM.getType().equals(message.getType()))
            {
                uamMessageClient.updateMessageStatus(token, message.getId(), request);
            }
            else
            {
                ufmMessageClient.updateMessageStatus(getToken(), message.getId(), request);
            }
        }
        catch (RestException e)
        {
            LOGGER.error("update message [ {} ] to read status [ {} ] failed.",
                message.getId(),
                request.getStatus(),
                e);
        }
    }
    
    /**
     * 
     * @param receiverId
     * @param messageList
     */
    private void updateStatusToRead(String token, MessageList messageList)
    {
        if (null == messageList || null == messageList.getMessages() || messageList.getMessages().isEmpty())
        {
            return;
        }
        
        UpdateMessageRequest request = new UpdateMessageRequest();
        request.setStatus(MessageStatus.READ.getStatus());
        
        for (MessageResponse message : messageList.getMessages())
        {
            if (MessageStatus.UNREAD.getStatus().equals(message.getStatus()))
            {
                updateStatus(token, request, message);
            }
        }
    }
}
