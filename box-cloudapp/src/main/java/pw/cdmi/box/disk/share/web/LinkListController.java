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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.disk.client.domain.share.RestLinkListRequest;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.RestLinkFileInfo;
import pw.cdmi.box.disk.share.domain.RestLinkFolderInfo;
import pw.cdmi.box.disk.share.domain.RestLinkFolderLists;
import pw.cdmi.box.disk.share.service.LinkService;
import pw.cdmi.box.disk.teamspace.domain.RestTeamSpaceInfo;
import pw.cdmi.box.disk.teamspace.service.TeamSpaceService;
import pw.cdmi.box.disk.utils.ShareLinkExceptionUtil;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;

@Controller
@RequestMapping(value = "/sharedlinks")
public class LinkListController extends CommonController
{
    private static Logger logger = LoggerFactory.getLogger(LinkListController.class);
    
    @Autowired
    private LinkService linkService;
    
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String gotoMyLinkList(Model model)
    {
        return "share/linkListIndex";
    }
    
    @RequestMapping(value = "/teamSpace/{teamId}", method = RequestMethod.GET)
    public String gotoTeamSpaceLinkList(@PathVariable("teamId") long teamId, Model model)
    {
        RestTeamSpaceInfo teamSpace = teamSpaceService.getTeamSpace(teamId, getToken());
        if (teamSpace == null)
        {
            model.addAttribute("exceptionName", "NoSuchTeamSpace");
            return "teamspace/teamSpace404";
        }
        
        model.addAttribute("teamId", teamId);
        model.addAttribute("teamName", teamSpace.getName());
        model.addAttribute("plainTeamName", teamSpace.getName());
        model.addAttribute("teamId", teamId);
        return "teamspace/linkListIndex";
    }
    
    @RequestMapping(value = "/deleteOne", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteOneLink(long ownerId, long iNodeId, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            UserToken user = getCurrentUser();
            INode iNode = new INode();
            iNode.setOwnedBy(ownerId);
            iNode.setId(iNodeId);
            linkService.deleteAllLink(user, iNode);
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
    public ResponseEntity<String> deleteMulLink(long ownerId, String iNodeId, HttpServletRequest request)
    {
        super.checkToken(request);
        try
        {
            if (StringUtils.isBlank(iNodeId))
            {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            String[] nodeIdArray = iNodeId.split(",");
            long nodeId;
            UserToken user;
            INode iNode;
            for (String nodeIdStr : nodeIdArray)
            {
                nodeId = Long.parseLong(StringUtils.trimToEmpty(nodeIdStr));
                user = getCurrentUser();
                iNode = new INode();
                iNode.setOwnedBy(ownerId);
                iNode.setId(nodeId);
                linkService.deleteAllLink(user, iNode);
            }
            return new ResponseEntity<String>("OK", HttpStatus.OK);
        }
        catch (RestException e)
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
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<INode>> list(Long ownerId,
        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
        @RequestParam(value = "orderField", defaultValue = "name") final String orderField,
        @RequestParam(value = "desc", defaultValue = "false") boolean desc,
        @RequestParam(value = "pageSize", defaultValue = "40") int pageSize, HttpServletRequest request)
    {
        super.checkToken(request);
        UserToken user = getCurrentUser();
        if (ownerId == null)
        {
            ownerId = user.getCloudUserId();
        }
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        pageRequest.setOrder(new Order(orderField, desc));
        
        long offset = 0L;
        if (pageNumber > 0)
        {
            offset = (pageNumber - 1) * pageSize;
        }
        
        RestLinkListRequest linkListRequest = generalRequest(orderField, desc, offset, pageSize);
        
        linkListRequest.setOwnedBy(ownerId);
        RestLinkFolderLists list = linkService.listLinkFolder(linkListRequest);
        
        List<INode> nodeList = transToNodeList(list);
        
        Page<INode> page = new PageImpl<INode>(nodeList, pageRequest, list.getTotalCount());
        return new ResponseEntity<Page<INode>>(page, HttpStatus.OK);
    }
    
    private RestLinkListRequest generalRequest(String orderField, boolean desc, long offset, int limit)
    {
        RestLinkListRequest linkListRequest = new RestLinkListRequest(limit, offset);
        Thumbnail smallThumb = new Thumbnail(Thumbnail.DEFAULT_SMALL_WIDTH, Thumbnail.DEFAULT_SMALL_HEIGHT);
        Thumbnail bigThumb = new Thumbnail(Thumbnail.DEFAULT_BIG_WIDTH, Thumbnail.DEFAULT_BIG_HEIGHT);
        linkListRequest.addThumbnail(smallThumb);
        linkListRequest.addThumbnail(bigThumb);
        Order orderByType = new Order("TYPE", "ASC");
        Order orderByField = new Order(orderField, desc ? "DESC" : "ASC");
        linkListRequest.addOrder(orderByType);
        linkListRequest.addOrder(orderByField);
        return linkListRequest;
    }
    
    private List<INode> transToNodeList(RestLinkFolderLists list)
    {
        List<INode> content = new ArrayList<INode>(list.getTotalCount());
        INode iNode = null;
        for (RestLinkFolderInfo folderInfo : list.getFolders())
        {
            iNode = new INode(folderInfo);
            content.add(iNode);
        }
        for (RestLinkFileInfo fileInfo : list.getFiles())
        {
            iNode = new INode(fileInfo);
            content.add(iNode);
        }
        
        for (INode inode : content)
        {
            inode.setName(HtmlUtils.htmlEscape(inode.getName()));
        }
        
        return content;
    }
}
