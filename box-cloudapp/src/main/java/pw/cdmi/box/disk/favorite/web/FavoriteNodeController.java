package pw.cdmi.box.disk.favorite.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.favorite.domain.FavoriteNodeCreateRequest;
import pw.cdmi.box.disk.favorite.domain.FavoriteTreeNode;
import pw.cdmi.box.disk.favorite.service.FavoriteNodeService;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.core.exception.RestException;

@Controller
@RequestMapping(value = "/favorite")
public class FavoriteNodeController extends CommonController
{
    public static final Logger LOGGER = LoggerFactory.getLogger(FavoriteNodeController.class);
    
    @Autowired
    private FavoriteNodeService favoriteNodeService;
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity createFavoriteNode(FavoriteNodeCreateRequest favoriteNode,
        HttpServletRequest request)
    {
        super.checkToken(request);
        if (favoriteNode == null || StringUtils.isBlank(favoriteNode.getLinkCode()))
        {
            super.checkToken(request);
        }
        try
        {
            if (null != favoriteNode)
            {
                UserToken sessUser = (UserToken) SecurityUtils.getSubject().getPrincipal();
                favoriteNode.setName(HtmlUtils.htmlUnescape(favoriteNode.getName()));
                long userid = sessUser.getId();
                favoriteNodeService.create(favoriteNode, userid);
            }
        }
        catch (RestException e)
        {
            LOGGER.error(e.getMessage(), e);
            if ("NoSuchLink".equals(e.getCode()))
            {
                return new ResponseEntity(e.getCode(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(e.getCode(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity("exception", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
        
    }
    
    @SuppressWarnings({"rawtypes"})
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseEntity getFavoriteTreelist(Locale locale, Long id, HttpServletRequest request)
    {
        super.checkToken(request);
        if (id != null)
        {
            try
            {
                List<FavoriteTreeNode> list = favoriteNodeService.getFavoriteNodeList(locale, id);
                return new ResponseEntity<List<FavoriteTreeNode>>(list, HttpStatus.OK);
            }
            catch (RestException e)
            {
                LOGGER.error("Favorite list", e);
                return new ResponseEntity<String>(HtmlUtils.htmlEscape(e.getCode()), HttpStatus.BAD_REQUEST);
            }
            
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    
    @SuppressWarnings({"rawtypes"})
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public ResponseEntity deleteFavoriteTreeNode(Long id, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            boolean temp = favoriteNodeService.deleteTreeNode(id);
            if (temp)
            {
                return new ResponseEntity(HttpStatus.OK);
            }
            return new ResponseEntity<String>("exception", HttpStatus.BAD_REQUEST);
        }
        catch (RestException e)
        {
            LOGGER.error("Favorite Delete", e);
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(e.getCode()), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            LOGGER.error("Favorite Delete", e);
            return new ResponseEntity<String>("exception", HttpStatus.BAD_REQUEST);
        }
    }
    
    @SuppressWarnings({"rawtypes"})
    @RequestMapping(value = "getParentUrl", method = RequestMethod.POST)
    public ResponseEntity getParentURL(FavoriteTreeNode node, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            UserToken user = getCurrentUser();
            String url = favoriteNodeService.getParentUrl(user, node);
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(url), HttpStatus.OK);
        }
        catch (RestException e)
        {
            LOGGER.error("Favorite getParentUrl" + e);
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(e.getCode()), HttpStatus.BAD_REQUEST);
            
        }
        catch (Exception e)
        {
            return new ResponseEntity<String>("exception", HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @SuppressWarnings({"rawtypes"})
    @RequestMapping(value = "locationUrL", method = RequestMethod.POST)
    public ResponseEntity locationUrL(FavoriteTreeNode node, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            String url = favoriteNodeService.locationUrL(node);
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(url), HttpStatus.OK);
        }
        catch (RestException e)
        {
            LOGGER.error("Favorite getParentUrl" + e);
            return new ResponseEntity<String>(HtmlUtils.htmlEscape(e.getCode()), HttpStatus.BAD_REQUEST);
            
        }
        catch (Exception e)
        {
            LOGGER.debug("local error", e);
            return new ResponseEntity<String>("exception", HttpStatus.BAD_REQUEST);
        }
        
    }
    
}
