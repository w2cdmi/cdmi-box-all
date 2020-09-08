package pw.cdmi.box.disk.share.web;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.InvalidSessionException;
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
import org.springframework.web.util.HtmlUtils;
import pw.cdmi.box.disk.client.api.FileClient;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.PreviewUrlResponse;
import pw.cdmi.box.disk.client.domain.node.RestFileInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.share.RestLinkDynamicResponse;
import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.box.disk.files.service.FileService;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.*;
import pw.cdmi.box.disk.share.service.LinkService;
import pw.cdmi.box.disk.teamspace.domain.RestACL;
import pw.cdmi.box.disk.teamspace.domain.RestNodePermissionInfo;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.box.disk.utils.PatternRegUtil;
import pw.cdmi.box.disk.utils.ShareLinkExceptionUtil;
import pw.cdmi.core.exception.*;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.DateUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping(value = "/p")
public class LinkVisitorController extends CommonController
{
    private static Logger logger = LoggerFactory.getLogger(LinkController.class);
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private LinkService linkService;

    @Resource
    private RestClient ufmClientService;

    private FileClient fileClient;

    @PostConstruct
    void init() {
        this.fileClient = new FileClient(ufmClientService);
    }

    /**
     */
    @RequestMapping(value = "/{linkCode}", method = RequestMethod.GET)
    public String enterLink(@PathVariable("linkCode") String linkCode, Model model, HttpServletRequest request) {
        INode iNode = new INode();
        UserToken user = getUserToken(linkCode);
        try {
            return getLinkViewEnterPage(linkCode, model, request, iNode, user);
        } catch (DynamicMailForbidden e) {
            return "share/inputMailAccessCode";
        } catch (DynamicPhoneForbidden e) {
            return "share/inputPhoneAccessCode";
        } catch (AuthFailedException e) {
            return "share/inputAccessCode";
        } catch (RestException e) {
            model.addAttribute("exceptionName", e.getCode());
            return "share/linkViewError";
        } catch (BaseRunException e) {
            logger.error(e.getMessage(), e);
            String exceptionName = ShareLinkExceptionUtil.getClassName(e);
            model.addAttribute("exceptionName", exceptionName);
            return "share/linkViewError";
        }
    }

    private String getLinkViewEnterPage(String linkCode, Model model, HttpServletRequest request, INode iNode, UserToken user) {
        String accessCode = CommonTools.getAccessCode(linkCode);
        model.addAttribute("accessCode", accessCode);

        LinkAndNodeV2 linkNode = linkService.getNodeInfoByLinkCode(user, linkCode, accessCode);
        RestFileInfoV2 rFile = linkNode.getFile();
        RestFolderInfo rFolder = linkNode.getFolder();
        INodeLink iNodeLinkV2 = linkNode.getLink();

        if (null == iNodeLinkV2) {
            throw new NoSuchItemsException();
        }
        fillNodeAttr(iNode, rFile, rFolder);

        model.addAttribute("linkCode", linkCode);
        SecurityUtils.getSubject().getSession().setAttribute("linkCode", linkCode);

        String shareUserName = iNodeLinkV2.getCreator();
        model.addAttribute("shareUserName", shareUserName);
        model.addAttribute("requestURI", request.getRequestURI());
        SecurityUtils.getSubject().getSession().setAttribute("shareUserName", shareUserName);

        String linkType = iNodeLinkV2.getAccessCodeMode();
        if (LinkAccessCodeMode.TYPE_STATIC_STRING.equals(linkType)) {
            //要求提取码访问，但是没有输入或输入错误，返回输入界面
            if (StringUtils.isNotBlank(iNodeLinkV2.getPlainAccessCode()) && !StringUtils.equals(accessCode, iNodeLinkV2.getPlainAccessCode())) {
                return "share/inputAccessCode";
            }
        } else {
            //要求动态码访问, 但是没有输入动态码
            if (StringUtils.isBlank(accessCode)) {
                return "share/inputMailAccessCode";
            }
        }

        setLoginUserFlag(model, user);

        model.addAttribute("ownerId", iNodeLinkV2.getOwnedBy());
        model.addAttribute("folderId", iNodeLinkV2.getiNodeId());
        model.addAttribute("parentId", iNodeLinkV2.getiNodeId());
        model.addAttribute("linkCreateTime", iNodeLinkV2.getCreatedAt());
        model.addAttribute("iNodeName", iNode.getName());
        model.addAttribute("iNodeSize", iNode.getSize());
        model.addAttribute("iNodeData", iNode);
        model.addAttribute("subFileList", iNodeLinkV2.getSubFileList());
        if (FilesCommonUtils.isFolderType(iNode.getType())) {
            model.addAttribute("linkCode", linkCode);
            return "share/newLinkFolderIndex";
        }

        //如果是图片文件，
        if (iNode.getType() == INode.TYPE_FILE && FilesCommonUtils.isImage(iNode.getName())) {
            iNode.setId(iNodeLinkV2.getiNodeId());
            iNode.setOwnedBy(iNodeLinkV2.getOwnedBy());
            String downloadUrl = fileService.getFileThumbUrls(user, iNode, iNodeLinkV2.getId(), Constants.MEDIUM_THUMB_SIZE_URL);
            downloadUrl = htmlEscapeThumbnail(downloadUrl);
            model.addAttribute("thumbnailUrl", downloadUrl);
        }

        return "share/linkFileIndex";
    }
    
    private void setLoginUserFlag(Model model, UserToken user)
    {
        if (null == user.getCloudUserId() || user.getCloudUserId() <= 0)
        {
            model.addAttribute("isLoginUser", "false");
        }
        else
        {
            model.addAttribute("isLoginUser", "true");
        }
    }
    
    private void fillNodeAttr(INode iNode, RestFileInfoV2 rFile, RestFolderInfo rFolder)
    {
        if (null != rFile)
        {
            iNode.setName(rFile.getName());
            iNode.setType(rFile.getType());
            iNode.setSize(rFile.getSize());
            iNode.setPreviewable(rFile.isPreviewable());
        }
        else
        {
            iNode.setName(rFolder.getName());
            iNode.setType(rFolder.getType());
        }
    }
    
    private UserToken getUserToken(String linkCode)
    {
        UserToken user = getCurrentUser();
        if (null == user)
        {
            try
            {
                Thread.sleep(200);
                user = getCurrentUser();
            }
            catch (InterruptedException e)
            {
                logger.debug("", e);
            }
        }
        if (null == user)
        {
            user = new UserToken();
        }
        user.setLinkCode(linkCode);
        return user;
    }
    
    /**
     * 
     * @param linkCode
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/{linkCode}/list", method = RequestMethod.GET)
    public String gotoFolderList(@PathVariable("linkCode") String linkCode, Model model,
        HttpServletRequest request)
    {
        INode iNode = new INode();
        UserToken user = getCurrentUser();
        if (null == user)
        {
            user = new UserToken();
        }
        user.setLinkCode(linkCode);
        String linkType;
        try
        {
            String accessCode = CommonTools.getAccessCode(linkCode);
            LinkAndNodeV2 linkNode = linkService.getNodeInfoByLinkCode(user, linkCode, accessCode);
            
            RestFileInfoV2 rFile = linkNode.getFile();
            RestFolderInfo rFolder = linkNode.getFolder();
            INodeLink iNodeLinkV2 = linkNode.getLink();
            if (null == iNodeLinkV2)
            {
                throw new NoSuchItemsException();
            }
            fillNodeAttr(iNode, rFile, rFolder);
            
            model.addAttribute("linkCode", linkCode);
            SecurityUtils.getSubject().getSession().setAttribute("linkCode", linkCode);
            
            String shareUserName = iNodeLinkV2.getCreator();
            
            model.addAttribute("shareUserName", shareUserName);
            model.addAttribute("requestURI", request.getRequestURI());
            SecurityUtils.getSubject().getSession().setAttribute("shareUserName", shareUserName);
            
            String linkPWD = CommonTools.getAccessCode(linkCode);
            linkType = iNodeLinkV2.getAccessCodeMode();
            if (LinkAccessCodeMode.TYPE_STATIC_STRING.equals(linkType))
            {
                if (StringUtils.isNotBlank(iNodeLinkV2.getPlainAccessCode())
                    && !StringUtils.equals(linkPWD, iNodeLinkV2.getPlainAccessCode()))
                {
                    return "share/inputAccessCode";
                }
            }
            else
            {
                if (StringUtils.isBlank(linkPWD))
                {
                    return "share/inputMailAccessCode";
                }
            }
            
            setLoginUserFlag(model, user);
            
            model.addAttribute("ownerId", iNodeLinkV2.getOwnedBy());
            model.addAttribute("folderId", iNodeLinkV2.getiNodeId());
            model.addAttribute("parentId", iNodeLinkV2.getiNodeId());
            
            model.addAttribute("iNodeName", HtmlUtils.htmlEscape(iNode.getName()));
            model.addAttribute("iNodeSize", iNode.getSize());
            model.addAttribute("linkCreateTime",DateUtils.format(iNodeLinkV2.getCreatedAt(), DateUtils.DATE_FORMAT_PATTERN));
            
            model.addAttribute("iNodeData", iNode);
            return "share/linkFolderIndex";
        }
        catch (DynamicMailForbidden e)
        {
            return "share/inputMailAccessCode";
        }
        catch (AuthFailedException e)
        {
            return "share/inputAccessCode";
        }
        catch (BaseRunException e)
        {
            logger.error(e.getMessage(), e);
            String exceptionName = ShareLinkExceptionUtil.getClassName(e);
            model.addAttribute("exceptionName", exceptionName);
            return "share/linkViewError";
        }
    }

/*
    @RequestMapping(value = "inputAccessCode", method = RequestMethod.GET)
    public String gotoInputAccessCode(Model model) {
        model.addAttribute("linkCode", SecurityUtils.getSubject().getSession().getAttribute("linkCode"));
        model.addAttribute("shareUserName", SecurityUtils.getSubject().getSession().getAttribute("shareUserName"));

        return "share/inputAccessCode";
    }
*/

    /**
     *
     */
    @RequestMapping(value = "inputAccessCode", method = RequestMethod.POST)
    public ResponseEntity<String> inputAccessCode(String linkCode, String accessCode, String captcha, String mail, HttpServletRequest request) {
        super.checkToken(request);
        // System.out.println(request.getParameter("linkCode"));
        if (StringUtils.isBlank(linkCode)) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
        UserToken user = getCurrentUser();
        if (null == user) {
            user = new UserToken();
        }
        user.setLinkCode(linkCode);
        user.setEmail(mail);
        try {
/*
            String codeTemp = (String) SecurityUtils.getSubject().getSession().getAttribute("HWVerifyCode");
            SecurityUtils.getSubject().getSession().setAttribute("HWVerifyCode", "");
*/
            /*
             * if (captcha.length() != BusinessConstants.LENGTH_OF_CAPTCHA ||
             * !StringUtils.equalsIgnoreCase(captcha, codeTemp)) { return new
             * ResponseEntity<String>(HttpStatus.UNAUTHORIZED); }
             */
            INodeLinkView iNodeLink = linkService.getLinkByLinkCode(user, linkCode, accessCode);
            if (StringUtils.isNotBlank(iNodeLink.getPassword()) && !StringUtils.equals(accessCode, iNodeLink.getPassword())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            SecurityUtils.getSubject().getSession().setAttribute(RestConstants.SESSION_KEY_LINK_ACCESSCODE + linkCode, accessCode);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (LinkExpiredException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (NoSuchItemsException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (NoSuchLinkException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (InvalidSessionException e) {
            logger.error("InvalidSessionException" + e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } catch (BaseRunException e) {
            logger.error("BaseRunException" + e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "sendAccessCode", method = RequestMethod.POST)
    public ResponseEntity<String> sendAccessCode(String linkCode, String mail, HttpServletRequest httpServletRequest) {
        try {
            super.checkToken(httpServletRequest);
            if (StringUtils.isBlank(linkCode)) {
                return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }

            RestLinkDynamicResponse result = linkService.updateDynamicLink(linkCode, mail);
            if (!PatternRegUtil.checkMailLegal(mail)) {
                return new ResponseEntity<String>("mail unlegal :", HttpStatus.UNAUTHORIZED);
            }
            linkService.sendDynamicMail(linkCode, result.getPlainAccessCode(), mail, null);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (RestException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "inputMailAccessCode", method = RequestMethod.POST)
    public ResponseEntity<String> inputMailAccessCode(String linkCode, String acessCode, String captcha,
                                                      HttpServletRequest httpServletRequest) {
        super.checkToken(httpServletRequest);
        if (StringUtils.isBlank(linkCode)) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
        UserToken user = getCurrentUser();
        if (null == user) {
            user = new UserToken();
        }
        user.setLinkCode(linkCode);
        try {
            String codeTemp = (String) SecurityUtils.getSubject().getSession().getAttribute("HWVerifyCode");

            SecurityUtils.getSubject().getSession().setAttribute("HWVerifyCode", "");
            // if (captcha.length() != BusinessConstants.LENGTH_OF_CAPTCHA
            // || !StringUtils.equalsIgnoreCase(captcha, codeTemp))
            // {
            // return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            // }
            INodeLinkView iNodeLink = linkService.getLinkByLinkCode(user, linkCode, acessCode);
            if (StringUtils.isNotBlank(iNodeLink.getPassword())
                    && !StringUtils.equals(acessCode, iNodeLink.getPassword())) {
                return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
            SecurityUtils.getSubject().getSession().setAttribute(RestConstants.SESSION_KEY_LINK_ACCESSCODE + linkCode, acessCode);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (LinkExpiredException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (NoSuchItemsException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (NoSuchLinkException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (InvalidSessionException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    //外链预览, 如果外链为文件夹时，需要指定文件的ownerId和nodeId.
   @RequestMapping(value = "/preview/{linkCode}", method = RequestMethod.GET)
    public String filePreviewReader(@PathVariable("linkCode") String linkCode, @RequestParam(required = false) String ownerId,
                                    @RequestParam(required = false) String nodeId, Model model) {
        //直接从session中获取提取码或动态码，如果用户已经输入过，不需要再重新输入
        String accessCode = CommonTools.getAccessCode(linkCode);
        model.addAttribute("accessCode", accessCode);
        model.addAttribute("linkCode", linkCode);

        try {
            UserToken user = getUserToken(linkCode);

            LinkAndNodeV2 linkNode = linkService.getNodeInfoByLinkCode(user, linkCode, accessCode);
            INodeLink link = linkNode.getLink();

            //检查外链是否存在
            if (link == null) {
                throw new NoSuchItemsException("No Such Link: " + linkCode);
            }

            //外链权限
            if (LinkAccessCodeMode.TYPE_STATIC_STRING.equals(link.getAccessCodeMode())) {
                //要求提取码访问，但是没有输入或输入错误，返回输入界面
                if (StringUtils.isNotBlank(link.getPlainAccessCode()) && !StringUtils.equals(accessCode, link.getPlainAccessCode())) {
                    return "share/inputAccessCode";
                }
            } else {
                //要求动态码访问, 但是没有输入动态码
                if (StringUtils.isBlank(accessCode)) {
                    return "share/inputMailAccessCode";
                }
            }

            //指定了文件ownerId + nodeId，访问外链下的文件
            if(StringUtils.isNotBlank(ownerId) && StringUtils.isNotBlank(nodeId)) {
                try {
                    RestFileInfo fileInfo = fileService.getFileInfo(user, Long.parseLong(ownerId), Long.parseLong(nodeId));
                    if (fileInfo == null) {
                        throw new NoSuchItemsException("No Such File: linkCode=" + linkCode + ", ownerId=" + ownerId + ", nodeId=" + nodeId);
                    }

                    logger.error("File Status: " + fileInfo.getStatus());

                    model.addAttribute("name", fileInfo.getName());
                    model.addAttribute("ownerId", ownerId);
                    model.addAttribute("nodeId", nodeId);
                } catch (NumberFormatException e) {
                    logger.warn("Wrong File Parameter: linkCode={}, ownerId={}, nodeId={}", linkCode, ownerId, nodeId);
                    throw new InvalidParamException("Wrong File Parameter: linkCode=" + linkCode + ", ownerId=" + ownerId + ", nodeId=" + nodeId);
                }
            } else {
                if (linkNode.getFolder() != null) {
                    return "redirect:share/newLinkFolderIndex/" + linkCode;
                }

                model.addAttribute("ownerId", link.getOwnedBy());
                model.addAttribute("nodeId", link.getiNodeId());
            }
        } catch (LinkApprovingException e) {
            return "share/approving";
        } catch (DynamicMailForbidden e) {
            return "share/inputMailAccessCode";
        } catch (DynamicPhoneForbidden e) {
            return "share/inputPhoneAccessCode";
        } catch (AuthFailedException e) {
            return "share/inputAccessCode";
        } catch (RestException e) {
            model.addAttribute("exceptionName", e.getCode());
            return "share/linkViewError";
        } catch (BaseRunException e) {
            logger.error(e.getMessage(), e);
            String exceptionName = ShareLinkExceptionUtil.getClassName(e);
            model.addAttribute("exceptionName", exceptionName);
            return "share/linkViewError";
        }

        return "files/previewReader";
    }

    /**
     * 外链访问界面，通过linkCode（token验证）、ownerId + nodeId 获取文件预览地址
     */
    @Deprecated
    @RequestMapping(value = "/getPreviewUrl/{ownerId}/{nodeId}", method = RequestMethod.GET)
    public ResponseEntity<PreviewUrlResponse> getPreviewUrl(@PathVariable("ownerId") long ownerId, @PathVariable("nodeId") long nodeId, @RequestParam("linkCode") String linkCode) {
        PreviewUrlResponse response = fileClient.getPreviewUrl("link," + linkCode, ownerId, nodeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
