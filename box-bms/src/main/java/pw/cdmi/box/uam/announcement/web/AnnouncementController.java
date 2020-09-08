/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.box.uam.announcement.web;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.SecurityUtils;
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

import pw.cdmi.box.uam.announcement.domain.Announcement;
import pw.cdmi.box.uam.announcement.domain.AnnouncementConfig;
import pw.cdmi.box.uam.announcement.service.AnnouncementService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.user.domain.Admin;


@Controller
@RequestMapping(value = "/announcement")
public class AnnouncementController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementController.class);
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    private static final int MAX_CONTENT_LENGTH = 2047;
    
    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String enter()
    {
        return "announcement/announcementManageMain";
    }
    
    @RequestMapping(value = "enterList", method = {RequestMethod.GET})
    public String enterList()
    {
        return "announcement/announcementList";
    }
    
    @RequestMapping(value = "enterCreate", method = {RequestMethod.GET})
    public String enterCreate()
    {
        return "announcement/createAnnouncement";
    }
    
    @RequestMapping(value = "listAll", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<List<Announcement>> listAll()
    {
        List<Announcement> announcements = announcementService.listAll();
        for (Announcement a : announcements)
        {
            a.setTitle(HtmlUtils.htmlEscape(a.getTitle()));
            a.setContent(HtmlUtils.htmlEscape(a.getContent()));
        }
        return new ResponseEntity<List<Announcement>>(announcements, HttpStatus.OK);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "createAnnouncement", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> createAnnouncement(AnnouncementRequest request, String token,
        HttpServletRequest req)
    {
        super.checkToken(token);
        Set violations = validator.validate(request);
        if (!violations.isEmpty())
        {
            saveValidateLog(req, OperateType.SystemAnnouncement);
            throw new ConstraintViolationException(violations);
        }
        String[] description = new String[]{request.getTitle()};
        String id = systemLogManager.saveFailLog(req,
            OperateType.SystemAnnouncement,
            OperateDescription.PUBLISH_ANNOUNCEMENT,
            null,
            description);
        try
        {
            Announcement announcement = new Announcement();
            
            Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
            if (null != admin)
            {
                announcement.setPublisherId(admin.getId());
            }
            announcement.setTitle(request.getTitle());
            announcement.setContent(request.getContent());
            
            announcementService.createAnnouncement(announcement);
            systemLogManager.updateSuccess(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error("create announcement failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "/getLength", method = RequestMethod.POST)
    @ResponseBody
    public boolean isContentValid(AnnouncementRequest request, String token)
    {
        super.checkToken(token);
        boolean lengthFlag = false;
        if (null != request && request.getContent().length() > MAX_CONTENT_LENGTH)
        {
            lengthFlag = true;
        }
        return lengthFlag;
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "deleteAnnouncement", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> deleteAnnouncement(long[] ids, String token, HttpServletRequest req)
    {
        if (null == ids || ids.length == 0)
        {
            systemLogManager.saveFailLog(req,
                OperateType.SystemAnnouncement,
                OperateDescription.DELETE_ANNOUNCEMENT,
                null,
                new String[]{""});
            LOGGER.warn("id is null");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        super.checkToken(token);
        StringBuilder sb = new StringBuilder();
        Announcement announcement = null;
        for (long id : ids)
        {
            try
            {
                announcement = announcementService.getAnnouncement(id);
                if (announcement != null)
                {
                    sb.append(announcement.getTitle() + ",");
                }
            }
            catch (RuntimeException e)
            {
                sb.append(id + ",");
            }
        }
        String[] description = new String[]{sb.toString().substring(0, sb.toString().length() - 1)};
        String id = systemLogManager.saveFailLog(req,
            OperateType.SystemAnnouncement,
            OperateDescription.DELETE_ANNOUNCEMENT,
            null,
            description);
        try
        {
            announcementService.deleteAnnouncement(ids);
            systemLogManager.updateSuccess(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error("create announcement failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "enterConfig", method = {RequestMethod.GET})
    public String enterConfig(Model model)
    {
        AnnouncementConfig config = announcementService.getAnnouncementConfig();
        model.addAttribute("config", config);
        return "announcement/announcementConfig";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "saveConfig", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> saveConfig(AnnouncementConfig config, String token, HttpServletRequest req)
    {
        Set violations = validator.validate(config);
        if (!violations.isEmpty())
        {
            saveValidateLog(req, OperateType.SystemAnnouncement);
            throw new ConstraintViolationException(violations);
        }
        
        String[] description = new String[]{String.valueOf(config.getMessageSavingTimes())};
        String id = systemLogManager.saveFailLog(req,
            OperateType.SystemAnnouncement,
            OperateDescription.CONFIG_ANNOUNCEMENT,
            null,
            description);
        
        super.checkToken(token);
        
        announcementService.saveAnnouncementConfig(config);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
