package pw.cdmi.box.disk.group.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.core.exception.RestException;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.group.service.GroupMemberService;
import pw.cdmi.box.disk.group.service.GroupService;

@Controller
@RequestMapping(value = "/group/member")
public class GroupMemberController extends CommonController
{
    private Logger logger = LoggerFactory.getLogger(GroupMemberController.class);
    
    @Autowired
    private GroupMemberService groupMemberService;
    
    @Autowired
    private GroupService groupService;
    
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> deleteMember(Long groupId, Long userId, Model model,
        HttpServletRequest httpServletRequest)
    {
        super.checkToken(httpServletRequest);
        try
        {
            boolean isOwner = false;
            try
            {
                groupMemberService.deleteMember(groupId, userId);
            }
            catch (RestException e)
            {
                if (!StringUtils.equals(e.getCode(), "Forbidden"))
                {
                    throw e;
                }
                if (groupService.getGroupInfo(groupId).getOwnedBy().longValue() == userId)
                {
                    model.addAttribute("G_OWNER", "1");
                    isOwner = true;
                }
                else
                {
                    throw e;
                }
                
            }
            if (!isOwner)
            {
                model.addAttribute("G_OWNER", "0");
            }
            return new ResponseEntity<Model>(model, HttpStatus.OK);
        }
        catch (RestException e)
        {
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(e.getCode()), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "multDelMember", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> multDelMember(String groupIds, String userIds, Model model,
        HttpServletRequest httpServletRequest)
    {
        super.checkToken(httpServletRequest);
        if (StringUtils.isEmpty(groupIds))
        {
            logger.error("groupIds is empty");
            return new ResponseEntity<String>("BadRequestException", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(userIds))
        {
            logger.error("userIds is empty");
            return new ResponseEntity<String>("BadRequestException", HttpStatus.BAD_REQUEST);
        }
        String[] groupIdList = groupIds.split(",");
        String[] userIdList = userIds.split(",");
        if (groupIdList.length != userIdList.length)
        {
            logger.error("groupIds length is not same as userIds length");
            return new ResponseEntity<String>("BadRequestException", HttpStatus.BAD_REQUEST);
        }
        
        try
        {
            boolean isOwner = false;
            for (int i = 0; i < groupIdList.length; i++)
            {
                isOwner = deleteMemberOne(model, groupIdList, userIdList, i);
            }
            if (!isOwner)
            {
                model.addAttribute("G_OWNER", "0");
            }
            return new ResponseEntity<Model>(model, HttpStatus.OK);
        }
        catch (RestException e)
        {
            
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(e.getCode()), HttpStatus.BAD_REQUEST);
        }
        catch (NumberFormatException e)
        {
            
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    
    private boolean deleteMemberOne(Model model, String[] groupIdList, String[] userIdList, int i)
    {
        long groupId = 0L;
        long userId = 0L;
        boolean isOwner = false;
        try
        {
            groupId = Long.parseLong(groupIdList[i]);
            userId = Long.parseLong(userIdList[i]);
            groupMemberService.deleteMember(groupId, userId);
        }
        catch (RestException e)
        {
            if (StringUtils.equals(e.getCode(), "Forbidden"))
            {
                
                if (groupService.getGroupInfo(groupId).getOwnedBy().longValue() == userId)
                {
                    model.addAttribute("G_OWNER", "1");
                    isOwner = true;
                }
                else
                {
                    throw e;
                }
            }
            else
            {
                throw e;
            }
        }
        return isOwner;
    }
}
