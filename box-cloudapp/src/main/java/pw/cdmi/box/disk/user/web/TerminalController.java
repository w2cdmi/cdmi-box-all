package pw.cdmi.box.disk.user.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.TerminalClient;
import pw.cdmi.box.disk.client.domain.user.ListTerminalRequest;
import pw.cdmi.box.disk.client.domain.user.ListTerminalResonse;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.Terminal;
import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/user/terminal")
@SuppressWarnings({"unchecked", "rawtypes"})
public class TerminalController extends CommonController
{
    private static Logger logger = LoggerFactory.getLogger(TerminalController.class);
    
    private static final int TOTAL_SIZE = 999;
    
    @Resource
    private RestClient uamClientService;
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Terminal> enter(Integer pageNumber, HttpServletRequest request)
    {
        
        try
        {
            super.checkToken(request);
            ListTerminalRequest listTerminalRequest = generalRequest(0, TOTAL_SIZE);
            ListTerminalResonse list;
            list = new TerminalClient(uamClientService).listTerminal(getToken(), listTerminalRequest);
            List<Terminal> terminalList = list.getTerminalList();
            for (Terminal terminal : terminalList)
            {
                terminal.setDeviceAgent(HtmlUtils.htmlEscape(terminal.getDeviceAgent()));
                terminal.setDeviceName(HtmlUtils.htmlEscape(terminal.getDeviceName()));
                terminal.setDeviceOS(HtmlUtils.htmlEscape(terminal.getDeviceOS()));
                terminal.setDeviceSn(HtmlUtils.htmlEscape(terminal.getDeviceSn()));
            }
            PageRequest pageRequest = new PageRequest(1, TOTAL_SIZE);
            Page<Terminal> pageFilter = new PageImpl<Terminal>(terminalList, pageRequest,
                list.getTotalCount());
            return new ResponseEntity(pageFilter, HttpStatus.OK);
        }
        catch (BadRquestException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
        catch (RestException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String list()
    {
        return "user/settings/terminal";
    }
    
    /**
     * 
     * @param clientSN
     * @param status
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Terminal> updateTerminal(String deviceSN, Byte status, HttpServletRequest request)
    {
        if (StringUtils.isBlank(deviceSN))
        {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        try
        {
            super.checkToken(request);
            new TerminalClient(uamClientService).updateTerminalStatus(getToken(), deviceSN, status);
            
        }
        catch (BaseRunException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        catch (RestException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
    
    private ListTerminalRequest generalRequest(long offset, int limit)
    {
        ListTerminalRequest listTerminalRequest = new ListTerminalRequest();
        listTerminalRequest.setLimit(limit);
        listTerminalRequest.setOffset(offset);
        return listTerminalRequest;
    }
}
