package pw.cdmi.box.disk.share.web;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.INodeShareV2;
import pw.cdmi.box.disk.share.domain.MySharesPage;
import pw.cdmi.box.disk.share.domain.RestListShareResourceRequestV2;
import pw.cdmi.box.disk.share.service.ShareByMeService;
import pw.cdmi.box.disk.share.service.ShareService;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.CustomUtils;
import pw.cdmi.box.disk.utils.ShareLinkExceptionUtil;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;

@Controller
@RequestMapping(value = "/myShares")
public class ShareByMeController extends CommonController
{
    
    private static Logger logger = LoggerFactory.getLogger(ShareByMeController.class);
    
    @RequestMapping(method = RequestMethod.GET)
    public String gotoMySharesList(Model model)
    {
        model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false
            : CustomUtils.getValue("link.hidden"));
        return "share/shareByMeIndex";
    }
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    @Autowired
    private ShareByMeService shareByMeService;
    
    @RequestMapping(value = "/deleteOne", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteOneShare(long iNodeId, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            UserToken user = getCurrentUser();
            shareService.cancelAllShare(user, user.getId(), iNodeId);
            return new ResponseEntity<String>("OK", HttpStatus.OK);
        }
        catch (RestException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/deleteMul", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteMulShare(String iNodeId, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            String[] nodeIdArray = iNodeId.split(",");
            long nodeId;
            UserToken user;
            for (String nodeIdStr : nodeIdArray)
            {
                nodeId = Long.parseLong(StringUtils.trimToEmpty(nodeIdStr));
                user = getCurrentUser();
                shareService.cancelAllShare(user, user.getId(), nodeId);
            }
            return new ResponseEntity<String>("OK", HttpStatus.OK);
        }
        catch (NumberFormatException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
        }
        catch (BaseRunException e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
        }
    }
    
    @Autowired
    private ShareService shareService;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<INodeShareV2>> list(String name,
        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
        @RequestParam(value = "orderField", defaultValue = "ownerName") final String orderField,
        @RequestParam(value = "desc", defaultValue = "false") boolean desc,
        @RequestParam(value = "pageSize", defaultValue = "40") int pageSize, HttpServletRequest request)
    {
        super.checkToken(request);
        String ufmToken = userTokenManager.getToken();
        RestListShareResourceRequestV2 req = generalRequest(orderField, desc, pageSize, pageNumber);
        
        MySharesPage page;
        try
        {
            page = shareByMeService.getMySharesPage(ufmToken, req);
            Page<INodeShareV2> resultPage = convertToPage(page, pageNumber, pageSize);
            for (INodeShareV2 shareNode : page.getContents())
            {
                shareNode.setName(HtmlUtils.htmlEscape(shareNode.getName()));
                shareNode.setOwnerName(HtmlUtils.htmlEscape(shareNode.getOwnerName()));
                if (StringUtils.equals(shareNode.getExtraType(), INode.TYPE_BACKUP_COMPUTER_STR))
                {
                    shareNode.setType(INode.TYPE_BACKUP_COMPUTER);
                }
                else if (StringUtils.equals(shareNode.getExtraType(), INode.TYPE_BACKUP_DISK_STR))
                {
                    shareNode.setType(INode.TYPE_BACKUP_DISK);
                }
            }
            return new ResponseEntity<Page<INodeShareV2>>(resultPage, HttpStatus.OK);
        }
        catch (RestException e)
        {
            return new ResponseEntity<Page<INodeShareV2>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private Page<INodeShareV2> convertToPage(MySharesPage mySharesPage, int pageNo, int size)
    {
        PageRequest pageReq = new PageRequest(pageNo, size);
        Page<INodeShareV2> sharePage = new PageImpl<INodeShareV2>(mySharesPage.getContents(), pageReq,
            mySharesPage.getTotalCount());
        return sharePage;
    }
    
    private RestListShareResourceRequestV2 generalRequest(String orderField, boolean desc, int pageSize,
        long pageNumber)
    {
        RestListShareResourceRequestV2 shareByMeReq = new RestListShareResourceRequestV2();
        
        Thumbnail smallThumb = new Thumbnail(Thumbnail.DEFAULT_SMALL_WIDTH, Thumbnail.DEFAULT_SMALL_HEIGHT);
        Thumbnail bigThumb = new Thumbnail(Thumbnail.DEFAULT_BIG_WIDTH, Thumbnail.DEFAULT_BIG_HEIGHT);
        List<Thumbnail> thumbnailList = new ArrayList<Thumbnail>(2);
        thumbnailList.add(smallThumb);
        thumbnailList.add(bigThumb);
        shareByMeReq.setThumbnail(thumbnailList);
        
        List<Order> orderList = new ArrayList<Order>(2);
        Order orderByType = new Order("TYPE", "ASC");
        Order orderByField = new Order(orderField, desc ? "DESC" : "ASC");
        orderList.add(orderByType);
        orderList.add(orderByField);
        shareByMeReq.setOrder(orderList);
        
        shareByMeReq.setLimit(pageSize);
        shareByMeReq.setOffset((pageNumber - 1) * pageSize);
        return shareByMeReq;
    }
}
