package pw.cdmi.box.disk.accountrole.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.disk.accountrole.domain.PageNodeRoleInfo;
import pw.cdmi.box.disk.accountrole.service.AccountRoleService;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.web.LinkController;
import pw.cdmi.core.exception.LinkExpiredException;
import pw.cdmi.core.exception.NoSuchItemsException;
import pw.cdmi.core.exception.NoSuchLinkException;
import pw.cdmi.core.exception.RestException;

@Controller
@RequestMapping(value = "/accountrole")
public class AccountRoleController extends CommonController
{
    private static Logger logger = LoggerFactory.getLogger(LinkController.class);
    
    @Autowired
    private AccountRoleService accountRoleService;
    
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<PageNodeRoleInfo>> listAccountRoles()
    {
        try
        {
            UserToken userInfo = getCurrentUser();
            List<PageNodeRoleInfo> pageList = accountRoleService.listAccountRoles(userInfo.getAccountId());
            
            return new ResponseEntity<List<PageNodeRoleInfo>>(pageList, HttpStatus.OK);
        }
        catch (LinkExpiredException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<List<PageNodeRoleInfo>>(HttpStatus.NOT_FOUND);
        }
        catch (NoSuchItemsException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<List<PageNodeRoleInfo>>(HttpStatus.NOT_FOUND);
        }
        catch (NoSuchLinkException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<List<PageNodeRoleInfo>>(HttpStatus.NOT_FOUND);
        }
        catch (RestException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<List<PageNodeRoleInfo>>(HttpStatus.BAD_REQUEST);
        }
    }
}
