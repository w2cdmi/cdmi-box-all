package pw.cdmi.box.disk.group.web;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.core.domain.PageV2;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.group.domain.GroupMembershipsInfoV2;
import pw.cdmi.box.disk.group.domain.GroupOrder;
import pw.cdmi.box.disk.group.service.GroupService;
import pw.cdmi.box.disk.httpclient.rest.request.GroupOrderUserRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestGroupRequest;
import pw.cdmi.box.disk.httpclient.rest.response.RestGroupResponse;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.system.service.AppBasicConfigService;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.common.domain.AppBasicConfig;

@Controller
@RequestMapping(value = "user/group")
public class GroupController extends CommonController
{
    public static final String BLANK_HYPHEN = "-";
    
    public static final Integer DEFAULT_LIMIT = 100;
    
    public static final String DISABLE_STATUS = "disable";
    
    public static final String ENABLE_STATUS = "enable";
    
    public static final String PRIVATE_GROUP = "private";
    
    public static final String PUBLIC_GROUP = "public";
    
    public static final String NAME_REGEX = "^[^.·!#/<>\\%?'\"&,;]{2,255}";
    
    public static final int MAX_LENGTH_OF_GROUP_DESC = 1023;
    
    private static final int INITIAL_SIZE = 10;
    
    @Autowired
    private AppBasicConfigService appBasicConfigService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private GroupService groupService;
    
    @RequestMapping(value = "createGroup", method = RequestMethod.POST)
    public ResponseEntity<?> createGroup(@Valid RestGroupRequest groupRequest, BindingResult result,
        HttpServletRequest request)
    {
        try
        {
            super.checkToken(request);
            if (result.hasErrors())
            {
                ResponseEntity<String> rs = new ResponseEntity<String>(new InvalidParamException().getCode(),
                    HttpStatus.BAD_REQUEST);
                return rs;
            }
            if (!checkNameRegex(groupRequest.getName()))
            {
                ResponseEntity<String> rs = new ResponseEntity<String>(new InvalidParamException().getCode(),
                    HttpStatus.BAD_REQUEST);
                return rs;
            }
            if (!checkDesc(groupRequest.getDescription()))
            {
                ResponseEntity<String> rs = new ResponseEntity<String>(new InvalidParamException().getCode(),
                    HttpStatus.BAD_REQUEST);
                return rs;
            }
            
            if (StringUtils.isBlank(groupRequest.getDescription()))
            {
                groupRequest.setDescription(BLANK_HYPHEN);
            }
            
            groupRequest.setOwnedBy(getCurrentUser().getCloudUserId());// owenBy
            groupRequest.setStatus(ENABLE_STATUS);// status
            
            AppBasicConfig config = appBasicConfigService.getAppBasicConfig(authAppService.getCurrentAppId());
            
            Integer value = config.getGroupMaxMembers();
            if (value != null)
            {
                groupRequest.setMaxMembers(value);
            }
            
            RestGroupResponse response = groupService.createGroup(groupRequest);
            if (null != response)
            {
                response.setAppId(HtmlUtils.htmlEscape(response.getAppId()));
                response.setCreatedByUserName(HtmlUtils.htmlEscape(response.getCreatedByUserName()));
                response.setDescription(HtmlUtils.htmlEscape(response.getDescription()));
                response.setName(HtmlUtils.htmlEscape(response.getName()));
                response.setOwnedByUserName(HtmlUtils.htmlEscape(response.getOwnedByUserName()));
                response.setStatus(HtmlUtils.htmlEscape(response.getStatus()));
                response.setType(HtmlUtils.htmlEscape(response.getType()));
            }
            return new ResponseEntity<RestGroupResponse>(response, HttpStatus.CREATED);
        }
        catch (InvalidParamException invalidParamEx)
        {
            return new ResponseEntity<String>(invalidParamEx.getCode(), HttpStatus.BAD_REQUEST);
        }
        catch (RestException restEx)
        {
            return new ResponseEntity<String>(restEx.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteGroup/{groupId}", method = RequestMethod.POST)
    public ResponseEntity<?> deleteGroup(@PathVariable(value = "groupId") long groupId,
        HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            UserToken userInfo = getCurrentUser();
            
            if (userInfo.getCloudUserId().longValue() != groupService.getGroupInfoV2(groupId)
                .getOwnedBy()
                .longValue())
            {
                return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
            }
            
            TextResponse response = groupService.deleteGroup(groupId);
            return new ResponseEntity<Integer>(response.getStatusCode(), HttpStatus.OK);
        }
        catch (RestException restEx)
        {
            return new ResponseEntity<String>(restEx.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(Model model)
    {
        UserToken userInfo = getCurrentUser();
        model.addAttribute("user", userInfo);
        return "user/settings/group";
    }
    
    /**
     * 
     * @param limit
     * @param keyword
     * @param type
     * @param page
     * @param field
     * @param direction
     * @return
     */
    @RequestMapping(value = "/listUserGroups", method = RequestMethod.POST)
    public ResponseEntity<?> listAllUserGroups(Integer limit, String keyword, String type, Long page,
        HttpServletRequest httpServletRequest)
    {
        super.checkToken(httpServletRequest);
        try
        {
            Long offset;
            GroupOrderUserRequest request = new GroupOrderUserRequest();// 请求
            List<GroupOrder> orders = new ArrayList<GroupOrder>(INITIAL_SIZE);
            GroupOrder orderRole = new GroupOrder("groupRole", "ASC");
            GroupOrder orderName = new GroupOrder("name", "ASC");
            orders.add(orderRole);
            orders.add(orderName);
            PageV2<GroupMembershipsInfoV2> groupPage = new PageV2<GroupMembershipsInfoV2>();
            request.setOrder(orders);// order
            
            request.setListRole("true");
            if (null == limit)
            {
                request.setLimit(DEFAULT_LIMIT);
            }
            else
            {
                request.setLimit(limit);// limit
            }
            if (StringUtils.isBlank(type))
            {
                request.setType("all");
            }
            else
            {
                request.setType(type);// type
            }
            
            request.setKeyword(keyword);// keyword
            
            if (null == page)
            {
                page = 1L;
            }
            FilesCommonUtils.checkPositiveIntegers(page);
            groupPage.setPage(page);
            offset = (page - 1) * request.getLimit();
            request.setOffset(offset);// offset
            
            groupPage = groupService.listUserGroups(groupPage, request);
            for (GroupMembershipsInfoV2 gm : groupPage.getData())
            {
                gm.getGroup().setName(HtmlUtils.htmlEscape(gm.getGroup().getName()));
                gm.getGroup().setDescription(HtmlUtils.htmlEscape(gm.getGroup().getDescription()));
                gm.getGroup().setAppId(HtmlUtils.htmlEscape(gm.getGroup().getAppId()));
                gm.getGroup()
                    .setCreatedByUserName(HtmlUtils.htmlEscape(gm.getGroup().getCreatedByUserName()));
                gm.getGroup().setOwnedByUserName(HtmlUtils.htmlEscape(gm.getGroup().getOwnedByUserName()));
                gm.getGroup().setStatus(HtmlUtils.htmlEscape(gm.getGroup().getStatus()));
                gm.getGroup().setType(HtmlUtils.htmlEscape(gm.getGroup().getStatus()));
            }
            return new ResponseEntity<PageV2<GroupMembershipsInfoV2>>(groupPage, HttpStatus.OK);
        }
        catch (RestException restEx)
        {
            return new ResponseEntity<String>(restEx.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 
     * @param groupId
     * @param groupRequest
     * @return
     */
    @RequestMapping(value = "editGroupInfo", method = RequestMethod.POST)
    public ResponseEntity<?> modifyGroup(long groupId, @Valid RestGroupRequest groupRequest,
        BindingResult result, HttpServletRequest httpServletRequest)
    {
        super.checkToken(httpServletRequest);
        try
        {
            if (result.hasErrors())
            {
                ResponseEntity<String> rs = new ResponseEntity<String>(new InvalidParamException().getCode(),
                    HttpStatus.BAD_REQUEST);
                return rs;
            }
            
            if (!checkNameRegex(groupRequest.getName()))
            {
                ResponseEntity<String> rs = new ResponseEntity<String>(new InvalidParamException().getCode(),
                    HttpStatus.BAD_REQUEST);
                return rs;
            }
            if (!checkDesc(groupRequest.getDescription()))
            {
                ResponseEntity<String> rs = new ResponseEntity<String>(new InvalidParamException().getCode(),
                    HttpStatus.BAD_REQUEST);
                return rs;
            }
            
            if (StringUtils.isBlank(groupRequest.getDescription()))
            {
                groupRequest.setDescription(BLANK_HYPHEN);
            }
            RestGroupResponse response = groupService.modifyGroup(groupId, groupRequest);
            if (response != null)
            {
                response.setAppId(HtmlUtils.htmlEscape(response.getAppId()));
                response.setCreatedByUserName(HtmlUtils.htmlEscape(response.getCreatedByUserName()));
                response.setDescription(HtmlUtils.htmlEscape(response.getDescription()));
                response.setName(HtmlUtils.htmlEscape(response.getName()));
                response.setOwnedByUserName(HtmlUtils.htmlEscape(response.getOwnedByUserName()));
                
                return new ResponseEntity<RestGroupResponse>(response, HttpStatus.OK);
            }
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        catch (InvalidParamException invalidParamEx)
        {
            return new ResponseEntity<String>(invalidParamEx.getCode(), HttpStatus.BAD_REQUEST);
        }
        catch (RestException restEx)
        {
            return new ResponseEntity<String>(restEx.getCode(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/addGroup", method = RequestMethod.GET)
    public String openAddGroup()
    {
        return "user/settings/addGroup";
    }
    
    @RequestMapping(value = "/listGroupInfo/{groupId}", method = RequestMethod.GET)
    public String openListGroupInfo(@PathVariable("groupId") Long id, Model model)
    {
        RestGroupResponse groupInfo = groupService.getGroupInfoV2(id);
        
        model.addAttribute("group", groupInfo);
        model.addAttribute("groupId", id);
        return "user/settings/groupInfo";
    }
    
    /**
     * 
     * @param ownerId
     * @param parentId
     * @param name
     * @return
     */
    @RequestMapping(value = "/checkSameName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Boolean> checkSameNameGroupExist(@Valid RestGroupRequest groupRequest)
    {
        boolean isExist = groupService.isNameExist(groupRequest);
        return new ResponseEntity<Boolean>(isExist, HttpStatus.OK);
    }
    
    private boolean checkNameRegex(String name)
    {
        return Pattern.compile(NAME_REGEX).matcher(name).matches();
    }
    
    private boolean checkDesc(String desc)
    {
        boolean isValid = true;
        if (StringUtils.isNotBlank(desc) && desc.length() > MAX_LENGTH_OF_GROUP_DESC)
        {
            isValid = false;
        }
        return isValid;
    }
    
}
