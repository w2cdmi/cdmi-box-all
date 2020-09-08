package pw.cdmi.box.disk.announcement.web;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.UAMMessageClient;
import pw.cdmi.box.disk.client.domain.message.Announcement;
import pw.cdmi.box.disk.client.domain.message.AnnouncementList;
import pw.cdmi.box.disk.client.domain.message.ListAnnouncementRequest;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/announcement")
public class AnnouncementController extends CommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementController.class);
    
    @Autowired
    @Qualifier("uamClientService")
    private RestClient uamClientService;
    
    private UAMMessageClient uamMessageClient;
    
    private static final String ERROR_NO_SUCH_ANNOUNCEMENT = "NoSuchAnnouncement";
    
    @PostConstruct
    public void init()
    {
        uamMessageClient = new UAMMessageClient(uamClientService);
    }
    
    @RequestMapping(value = "/enter/{announcementId}", method = RequestMethod.GET)
    public String enter(@PathVariable long announcementId, Model model)
    {
        if (announcementId >= 0)
        {
            try
            {
                Announcement announcement = uamMessageClient.getAnnouncement(getToken(), announcementId);
                model.addAttribute("announcement", announcement);
                return "announcement/announcementList";
            }
            catch (RestException e)
            {
                LOGGER.error(ReflectionToStringBuilder.toString(e));
                if (ERROR_NO_SUCH_ANNOUNCEMENT.equals(e.getCode()))
                {
                    model.addAttribute("noSuchAnnouncement", true);
                }
            }
            catch (Exception e)
            {
                LOGGER.error("get announcement [ {} ] failed.", announcementId, e);
            }
        }
        
        initPageByDefault(model);
        
        return "announcement/announcementList";
    }
    
    private void initPageByDefault(Model model)
    {
        ListAnnouncementRequest listAnnouncementRequest = new ListAnnouncementRequest();
        listAnnouncementRequest.setOffset(0L);
        listAnnouncementRequest.setLimit(1);
        
        AnnouncementList announcementList = uamMessageClient.listAnnouncement(getToken(),
            listAnnouncementRequest);
        if (null != announcementList && null != announcementList.getAnnouncements()
            && announcementList.getAnnouncements().size() > 0)
        {
            Announcement a = announcementList.getAnnouncements().get(0);
            htmlEscapeAnnouncement(a);
            model.addAttribute("announcement", a);
        }
    }
    
    @RequestMapping(value = "/listAnnouncement", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<Announcement>> listAnnouncement(
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
        HttpServletRequest request)
    {
        super.checkToken(request);
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        
        long offset = 0L;
        if (pageNumber > 0)
        {
            offset = (long) (pageNumber - 1) * pageSize;
        }
        
        ListAnnouncementRequest listAnnouncementRequest = new ListAnnouncementRequest();
        listAnnouncementRequest.setOffset(offset);
        listAnnouncementRequest.setLimit(pageSize);
        
        AnnouncementList announcementList = uamMessageClient.listAnnouncement(getToken(),
            listAnnouncementRequest);
        
        if (null != announcementList.getAnnouncements() && !announcementList.getAnnouncements().isEmpty())
        {
            for (Announcement a : announcementList.getAnnouncements())
            {
                htmlEscapeAnnouncement(a);
            }
        }
        
        Page<Announcement> page = new PageImpl<Announcement>(announcementList.getAnnouncements(),
            pageRequest, announcementList.getTotalCount());
        return new ResponseEntity<Page<Announcement>>(page, HttpStatus.OK);
    }
    
    private void htmlEscapeAnnouncement(Announcement announcement)
    {
        if (null != announcement)
        {
            announcement.setTitle(HtmlUtils.htmlEscape(announcement.getTitle()));
            announcement.setContent(HtmlUtils.htmlEscape(announcement.getContent()));
        }
    }
}
