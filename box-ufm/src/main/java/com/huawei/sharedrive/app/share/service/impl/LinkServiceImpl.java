package com.huawei.sharedrive.app.share.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.huawei.sharedrive.app.exception.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wcc.crypt.EncryptHelper;

import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.INodeACLIdGenerateService;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.acl.service.ResourceRoleService;
import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.exception.BadRequestException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.share.dao.INodeLinkDAO;
import com.huawei.sharedrive.app.share.dao.INodeLinkReverseDao;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.domain.LinkAccessCodeMode;
import com.huawei.sharedrive.app.share.service.LinkService;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.system.service.CustomizeLogoService;
import com.huawei.sharedrive.app.utils.LinkCodeCaculator;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.utils.EDTools;
import pw.cdmi.core.utils.EDToolsEnhance;

@Component
@Service("linkService")
public class LinkServiceImpl implements LinkService
{
    
    private static Logger logger = LoggerFactory.getLogger(LinkServiceImpl.class);
    
    
    /** 外链码生成失败的最大重试次数 */
    private static final int MAX_RETRY_TIMES = 3;
    
    private static final String PUBLIC_LINKC_PATH = "p/";
    
    private static final String LINK_SECURITY_CONFIG_FLAG = "system.linkcode.security.enable";
    
    private static final String LINK_SECURITY_CONFIG_BYTE_SIZE = "system.linkcode.security.byte.size";
    
    private static final boolean DEFAULT_SECURE_LINK_FLAG = true;
    
    private static final int DEFAULT_SECURE_LINK_BYTE_SIZE = 16;
    
    @Autowired
    private CustomizeLogoService customizeLogoService;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private FolderService folderService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private INodeLinkDAO iNodeLinkDao;
    
    @Autowired
    private INodeLinkReverseDao iNodeLinkReverseDao;
    
    @Autowired
    private ResourceRoleService resourceRoleService;
    
    @Autowired
    private INodeACLIdGenerateService nodeACLIdGenerateService;
    
    @Autowired
    private INodeACLService nodeACLService;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    private SystemConfig localCacheLinkFlag;
    
    private SystemConfig localCacheLinkCodeSize;
    
    @Value("${link.cache.supported}")
    private boolean linkCacheSupported;
    
    private void updateNodeLinkCode(UserToken user, INode iNode, String linkCode)
    {
        // 更新节点链接状态
        int total = iNodeLinkReverseDao.getCountByNode(iNode.getOwnedBy(), iNode.getId(), null);
        if (StringUtils.isBlank(linkCode))
        {
            if (total > 0)
            {
                folderService.updateNodeLinkCode(user, iNode, INode.LINKCODE_NEW_SET);
            }
            else
            {
                folderService.updateNodeLinkCode(user, iNode, "");
            }
        }
        else
        {
            folderService.updateNodeLinkCode(user, iNode, linkCode);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void createLinkNoCheck(INodeLink link)
    {
        String linkCode = buildLinkID();
        // 产生外链码
        link.setId(linkCode);
        
        // 设置外链访问地址
        link.setUrl(PUBLIC_LINKC_PATH + link.getId());
        Date date = new Date();
        link.setCreatedAt(new Date(date.getTime() / 1000 * 1000));
        link.setModifiedAt(new Date(date.getTime() / 1000 * 1000));
        link.setStatus(LinkAccessCodeMode.TYPE_STATIC_VALUE);
        iNodeLinkDao.create(link);
    }
    
    /**
     * 创建链接
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INodeLink createLinkV2(UserToken user, INodeLink iNodeLink, INode iNode) throws BaseRunException
    {
        if (StringUtils.isNotBlank(iNode.getLinkCode())
            && !INode.LINKCODE_NEW_SET.equals(iNode.getLinkCode()))
        {
            String message = "inode already set a link, ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:"
                + iNodeLink.getId() + ", linkCode:" + iNode.getLinkCode();
            throw new LinkConflictException(message);
        }
        
        iNodeLink.setPlainAccessCode(StringUtils.trimToEmpty(iNodeLink.getPlainAccessCode()));
        
        INodeLink link = reTryCreateLink(user, iNodeLink);
        
        addNodeLinkACL(user, iNode, link.getId(), link.getRole());
        
        // 访问地址加上域名
        link.setUrl(link.getUrl());
        
        /**
         * 发送日志
         */
        sendEvent(user, EventType.LINK_CREATE, iNode);
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(link.getId()),
            String.valueOf(iNode.getOwnedBy()), String.valueOf(iNode.getParentId())};
        String keyword = StringUtils.trimToEmpty(iNode.getName());
        
        fileBaseService.sendINodeEvent(user,
            EventType.LINK_CREATE,
            iNode,
            null,
            UserLogType.CREATE_LINK,
            logMsgs,
            keyword);
        // 更新节点链接状态
        updateNodeLinkCode(user, iNode, link.getId());
        return link;
    }
    
    @Override
    public void deleteByOwner(long ownerId)
    {
        iNodeLinkReverseDao.deleteByOwner(ownerId);
        this.iNodeLinkDao.deleteByOwner(ownerId);
    }
    
    private void addNodeLinkACL(UserToken user, INode node, String linkCode, String role)
        throws BaseRunException
    {
        INodeACL iNodeACL = new INodeACL();
        Date date = new Date();
        iNodeACL.setId(nodeACLIdGenerateService.getNextNodeACLId(node.getOwnedBy()));
        iNodeACL.setAccessUserId(String.valueOf(linkCode));
        iNodeACL.setUserType(INodeACL.TYPE_LINK);
        iNodeACL.setCreatedAt(date);
        iNodeACL.setCreatedBy(user.getId());
        iNodeACL.setiNodeId(node.getId());
        iNodeACL.setiNodePid(node.getParentId());
        iNodeACL.setOwnedBy(node.getOwnedBy());
        iNodeACL.setModifiedAt(date);
        iNodeACL.setModifiedBy(user.getId());
        iNodeACL.setResourceRole(role);
        
        nodeACLService.addINodeACL(iNodeACL);
    }
    
    /**
     * 删除链接
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteLinkByNode(UserToken user, INode iNode) throws BaseRunException
    {
        // 检查入参
        if (user == null)
        {
            throw new BadRequestException("user ownerId or inodeLink is null");
        }
        
        String linkCode = iNode.getLinkCode();
        if (StringUtils.isBlank(linkCode) || INode.LINKCODE_NEW_SET.equals(linkCode))
        {
            String msg = "inode not set a link, ownerId:" + iNode.getOwnedBy() + ", inodeid:" + iNode.getId();
            throw new NoSuchLinkException(msg);
        }
        
        // 更新节点链接状态
        updateNodeLinkCode(user, iNode, "");
        
        INodeLink linkToDelete = new INodeLink();
        linkToDelete.setId(linkCode);
        iNodeLinkDao.delete(linkToDelete);
        /**
         * 发送日志
         */
        sendEvent(user, EventType.LINK_DELETE, iNode);
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(linkCode), String.valueOf(iNode.getOwnedBy()),
            String.valueOf(iNode.getParentId())};
        String keyword = StringUtils.trimToEmpty(iNode.getName());
        
        fileBaseService.sendINodeEvent(user,
            EventType.LINK_DELETE,
            iNode,
            null,
            UserLogType.DELETE_LINK,
            logMsgs,
            keyword);
    }
    
    /**
     * 获取下载地址
     */
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String getFileLinkDownloadUrl(UserToken user, String linkCode, Long fileId) throws BaseRunException
    {
        // 获取外链并检查外链是否有效
        INodeLink iNodeLink = getLinkByLinkCode(user, linkCode);
        
        // 校验节点是否存在
        INode iNodeRoot = folderService.getNodeInfo(user, iNodeLink.getOwnedBy(), iNodeLink.getiNodeId());
        if (iNodeRoot == null)
        {
            logger.error(
                "inode not exist, ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:" + iNodeLink.getiNodeId());
            throw new NoSuchItemsException();
        }
        
        if (StringUtils.isBlank(iNodeRoot.getLinkCode())
            || INode.LINKCODE_NEW_SET.equals(iNodeRoot.getLinkCode()))
        {
            logger.error("inodelink status error, ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:"
                + iNodeLink.getiNodeId() + ", linkCode:" + linkCode);
            iNodeLinkDao.delete(iNodeLink);
            throw new NoSuchItemsException();
        }
        
        // 如果是下载链接文件下的子文件
        if (fileId != null && fileId != iNodeLink.getiNodeId())
        {
            INode iNode = folderService.getNodeInfoCheckType(user,
                iNodeLink.getOwnedBy(),
                fileId,
                INode.TYPE_FILE);
                
            if (iNode == null)
            {
                logger.error("inode not exist, ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:" + fileId);
                throw new NoSuchItemsException();
            }
            /**
             * 发送日志
             */
            sendEvent(user, EventType.LINK_DOWNLOAD, iNode);
            return fileService.getFileDownloadUrl(user, iNodeLink.getOwnedBy(), fileId);
            
        }
        // 如果链接就是文件
        if (iNodeRoot.getType() != INode.TYPE_FILE)
        {
            logger.error("inode not file,type:" + iNodeRoot.getType() + ",Status:" + iNodeRoot.getStatus());
            throw new NoSuchItemsException();
        }
        /**
         * 发送日志
         */
        sendEvent(user, EventType.LINK_DOWNLOAD, iNodeRoot);
        
        return fileService.getFileDownloadUrl(user, iNodeLink.getOwnedBy(), iNodeLink.getiNodeId());
    }
    
    @Override
    public String getLinkBasePath()
    {
        CustomizeLogo temp = customizeLogoService.getCustomize();
        if (null != temp)
        {
            StringBuffer webDomain = new StringBuffer(StringUtils.trimToEmpty(temp.getDomainName()));
            String webDomainStr = webDomain.toString();
            
            if (webDomainStr.length() > 0 && !(webDomainStr.charAt(webDomainStr.length() - 1) == '/'))
            {
                webDomain.append('/');
            }
            return webDomain.toString();
        }
        
        String message = "Please Config WebDomain Name In ISystem.";
        logger.warn(message);
        throw new BusinessException(message);
    }
    
    /**
     * 根据节点ID获取节点信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INodeLink getLinkByINodeId(UserToken user, long ownerId, long iNodeId) throws BaseRunException
    {
        // 检查入参
        if (user == null)
        {
            logger.error("user is null");
            throw new BadRequestException();
        }
        
        // 检查INode是否存在
        INode iNode = folderService.getNodeInfo(user, ownerId, iNodeId);
        if (iNode == null)
        {
            logger.error("inode not exist, ownerId:" + ownerId + ", inodeid:" + iNodeId);
            throw new NoSuchItemsException();
        }
        
        if (StringUtils.isBlank(iNode.getLinkCode()) || INode.LINKCODE_NEW_SET.equals(iNode.getLinkCode()))
        {
            logger.warn("inodelink not exist, ownerId:" + ownerId + ", inodeid:" + iNodeId);
            return null;
        }
        
        // 检查链接是否存在
        INodeLink iNodeLink = getINodeLinkInfo(iNode.getLinkCode());
        
        if (null == iNodeLink)
        {
            logger.warn("inodelink not exist, ownerId:" + ownerId + ", inodeid:" + iNodeId + ", linkCode:"
                + iNode.getLinkCode());
            updateNodeLinkCode(user, iNode, "");
            return null;
        }
        
        // 检查链接是否超期，如果超期设置状态不可用
        // 该方法供获取外链详情场景使用,故生效日期不做校验
        if (isLinkExpired(iNodeLink))
        {
            logger.warn("inode is expired, expireAt:" + iNodeLink.getExpireAt());
            updateNodeLinkCode(user, iNode, "");
            iNodeLinkDao.delete(iNodeLink);
            return null;
        }
        
        return iNodeLink;
    }
    
    /**
     * 根据节点ID获取节点信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INodeLink getLinkByINodeIdV2(UserToken user, INode iNode) throws BaseRunException
    {
        INodeLink iNodeLink = check(user, iNode);
        if (iNodeLink == null)
        {
            return null;
        }
        String[] logMsgs = new String[]{String.valueOf(iNode.getOwnedBy()),
            String.valueOf(iNode.getParentId())};
        String keyword = StringUtils.trimToEmpty(iNode.getName());
        
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            iNode,
            null,
            UserLogType.GET_LINKINFO,
            logMsgs,
            keyword);
        return iNodeLink;
    }
    
    private INodeLink check(UserToken user, INode iNode)
    {
        if (StringUtils.isBlank(iNode.getLinkCode()) || INode.LINKCODE_NEW_SET.equals(iNode.getLinkCode()))
        {
            return null;
        }
        
        // 检查链接是否存在
        INodeLink iNodeLink = getINodeLinkInfoV2(iNode.getLinkCode());
        
        if (null == iNodeLink)
        {
            updateNodeLinkCode(user, iNode, "");
            return null;
        }
        
        // 检查链接是否超期，如果超期设置状态不可用
        // 该方法供获取外链详情场景使用,故生效日期不做校验
        if (isLinkExpired(iNodeLink))
        {
            updateNodeLinkCode(user, iNode, "");
            iNodeLinkDao.deleteV2(iNodeLink);
            return null;
        }
        return iNodeLink;
    }
    
    /**
     * 根据节点ID获取节点信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INodeLink getLinkByINodeIdV2(UserToken user, long ownerId, long iNodeId) throws BaseRunException
    {
        // 检查入参
        if (user == null)
        {
            String message = "user is null";
            throw new BadRequestException(message);
        }
        
        // 检查INode是否存在
        INode iNode = folderService.getNodeInfo(user, ownerId, iNodeId);
        if (iNode == null)
        {
            String message = "inode not exist, ownerId:" + ownerId + ", inodeid:" + iNodeId;
            throw new NoSuchSourceException(message);
        }
        
        return check(user, iNode);
    }
    
    @Override
    public INodeLink getLinkByLinkCode(String linkCode) throws BaseRunException
    {
        INodeLink nodeLink = getINodeLinkInfo(linkCode);
        if (null == nodeLink)
        {
            String message = "The nodelink  not exist, linkCode:" + linkCode;
            throw new NoSuchLinkException(message);
        }
        if (isLinkNotEffective(nodeLink))
        {
            String message = "The node not effective, EffectiveAt:" + nodeLink.getEffectiveAt();
            throw new LinkNotEffectiveException(message);
        }
        if (isLinkExpired(nodeLink))
        {
            logger.error("The node expired, expireAt:" + nodeLink.getExpireAt());
            try
            {
                int total = iNodeLinkReverseDao.getCountByNode(nodeLink.getOwnedBy(),
                    nodeLink.getiNodeId(),
                    null);
                    
                INode node = new INode();
                node.setOwnedBy(nodeLink.getOwnedBy());
                node.setId(nodeLink.getiNodeId());
                if (total == 0)
                {
                    node.setLinkCode(null);
                }
                else
                {
                    node.setLinkCode(INode.LINKCODE_NEW_SET);
                }
                iNodeDAO.updateINodeLinkCode(node);
                iNodeLinkDao.delete(nodeLink);
            }
            catch (RuntimeException e)
            {
                logger.warn("Can not delete expired link " + linkCode);
            }
            catch (Exception e)
            {
                logger.warn("Can not delete expired link " + linkCode);
            }
            throw new LinkExpiredException();
        }
        return nodeLink;
    }
    
    /**
     * 根据提取码获取节点信息
     */
    @Override
    public INodeLink getLinkByLinkCode(UserToken user, String linkCode) throws BaseRunException
    {
        // 检查入参
        if (user == null || linkCode == null)
        {
            logger.error("user ownerId or linkCode is null, linkCode:" + linkCode);
            throw new BadRequestException();
        }
        // 检查链接是否存在
        INodeLink iNodeLink = getINodeLinkInfo(linkCode);
        
        if (null == iNodeLink)
        {
            logger.error("inodeLink not exist, linkCode:" + linkCode);
            throw new NoSuchLinkException();
        }
        
        // 检查链接是否生效
        if (isLinkNotEffective(iNodeLink))
        {
            logger.error("inode not effective, EffectiveAt:" + iNodeLink.getEffectiveAt().toString());
            throw new LinkNotEffectiveException();
        }
        
        // 检查链接是否过期
        if (isLinkExpired(iNodeLink))
        {
            logger.error("inode expired, expireAt:" + iNodeLink.getExpireAt());
            throw new LinkExpiredException();
        }
        
        return iNodeLink;
        
    }
    
    /**
     * 根据提取码获取节点信息
     */
    @Override
    public INodeLink getLinkByLinkCodeForClientV2(String linkCode) throws BaseRunException
    {
        // 检查入参
        if (StringUtils.isBlank(linkCode))
        {
            String msg = "user ownerId or linkCode is null";
            throw new InvalidParamException(msg);
        }
        // 检查链接是否存在
        INodeLink iNodeLink = getINodeLinkInfo(linkCode);
        
        if (null == iNodeLink)
        {
            String msg = "inodeLink not exist, linkCode:" + linkCode;
            throw new NoSuchLinkException(msg);
        }
        
        // 检查链接是否生效
        if (isLinkNotEffective(iNodeLink))
        {
            String msg = "inode not effective, EffectiveAt:" + iNodeLink.getEffectiveAt().toString();
            throw new LinkNotEffectiveException(msg);
        }
        
        // 检查链接是否生效
        if (isLinkNotEffective(iNodeLink))
        {
            String msg = "inode not effective, EffectiveAt:" + iNodeLink.getEffectiveAt().toString();
            throw new LinkNotEffectiveException(msg);
        }
        
        // 检查链接是否过期
        if (isLinkExpired(iNodeLink))
        {
            String msg = "inode expired, expireAt:" + iNodeLink.getExpireAt();
            throw new LinkExpiredException(msg);
        }
        
        return iNodeLink;
        
    }
    
    @Override
    public INodeLink getLinkForDirect(UserToken userToken, String linkCode)
    {
        if (userToken == null || linkCode == null)
        {
            logger.error("user ownerId or linkCode is null, linkCode:" + linkCode);
            throw new BadRequestException();
        }
        
        INodeLink iNodeLink = getINodeLinkInfoV2(linkCode);
        if (null == iNodeLink)
        {
            return null;
        }
        INode iNode = folderService.getNodeInfo(userToken, iNodeLink.getOwnedBy(), iNodeLink.getiNodeId());
        if (iNode == null)
        {
            String message = "file not exist  , ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:"
                + iNodeLink.getiNodeId();
            throw new NoSuchFileException(message);
        }
        if (iNode.getType() != INode.TYPE_FILE)
        {
            String message = "not file  , ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:"
                + iNodeLink.getiNodeId() + ",type:" + iNode.getType();
            throw new NoSuchFileException(message);
        }
        
        // 检查链接是否生效
        if (isLinkNotEffective(iNodeLink))
        {
            String msg = "inode not effective, EffectiveAt:" + iNodeLink.getEffectiveAt().toString();
            throw new LinkNotEffectiveException(msg);
        }
        
        // 检查链接是否超期，如果超期设置状态不可用
        if (isLinkExpired(iNodeLink))
        {
            updateNodeLinkCode(userToken, iNode, "");
            iNodeLinkDao.deleteV2(iNodeLink);
            String msg = "inode expired, expireAt:" + iNodeLink.getExpireAt();
            throw new LinkExpiredException(msg);
        }
        return iNodeLink;
    }
    
    /**
     * 根据提取码获取节点信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean getLinkStatusByLinkCode(UserToken user, String linkCode) throws BaseRunException
    {
        
        // 检查入参
        if (user == null || linkCode == null)
        {
            String msg = "user ownerId or linkCode is null, linkCode:" + linkCode;
            throw new BadRequestException(msg);
        }
        // 检查链接是否存在
        INodeLink iNodeLink = getINodeLinkInfo(linkCode);
        
        if (null == iNodeLink)
        {
            String msg = "inodeLink not exist, linkCode:" + linkCode;
            throw new NoSuchItemsException(msg);
        }
        
        // 检查链接是否生效
        if (isLinkNotEffective(iNodeLink))
        {
            logger.error("inode not effective, EffectiveAt:" + iNodeLink.getEffectiveAt().toString());
            return false;
        }
        
        // 检查链接是否过期
        if (isLinkExpired(iNodeLink))
        {
            INode iNode = folderService.getNodeInfo(user, iNodeLink.getOwnedBy(), iNodeLink.getiNodeId());
            if (iNode != null)
            {
                updateNodeLinkCode(user, iNode, "");
                iNodeLinkDao.delete(iNodeLink);
            }
            return true;
        }
        return false;
    }
    
    /**
     * 根据提取码获取节点信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode getNodeInfoByLinkCode(UserToken user, String linkCode) throws BaseRunException
    {
        // 获取外链并检查外链是否有效
        INodeLink iNodeLink = getLinkByLinkCode(user, linkCode);
        
        return folderService.getNodeInfo(user, iNodeLink.getOwnedBy(), iNodeLink.getiNodeId());
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FileINodesList listFolderLinkByFilter(UserToken user, String linkCode, Long folderId,
        OrderV1 order, Limit limit) throws BaseRunException
    {
        // 获取外链并检查外链是否有效
        INodeLink iNodeLink = getLinkByLinkCode(user, linkCode);
        
        // 校验节点是否存在
        INode iNodeRoot = folderService.getNodeInfoCheckType(user,
            iNodeLink.getOwnedBy(),
            iNodeLink.getiNodeId(),
            INode.TYPE_FOLDER_ALL);
            
        if (iNodeRoot == null)
        {
            String msg = "inode not exist, ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:"
                + iNodeLink.getiNodeId();
            throw new NoSuchItemsException(msg);
        }
        
        if (StringUtils.isBlank(iNodeRoot.getLinkCode())
            || INode.LINKCODE_NEW_SET.equals(iNodeRoot.getLinkCode()))
        {
            String msg = "inodelink status error, ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:"
                + iNodeLink.getiNodeId() + ", linkCode:" + linkCode;
            iNodeLinkDao.delete(iNodeLink);
            throw new NoSuchItemsException(msg);
        }
        
        // 如果是下载链接文件下的子文件
        if (folderId != null && folderId != iNodeLink.getiNodeId())
        {
            INode inode = folderService.getNodeInfoCheckType(user,
                iNodeLink.getOwnedBy(),
                folderId,
                INode.TYPE_FOLDER);
                
            // inode的有效性在folderService层判断
            return getNodesListWithoutStatus(folderService.listNodesbyFilter(user, inode, order, limit));
        }
        
        return getNodesListWithoutStatus(folderService.listNodesbyFilter(user, iNodeRoot, order, limit));
    }
    
    
    /**
     * 修改连接
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INodeLink updateLinkV2(UserToken user, INodeLink iNodeLink) throws BaseRunException
    {
        // 检查入参
        if (user == null || iNodeLink == null)
        {
            String msg = "user ownerId or inodeLink is null";
            throw new BadRequestException(msg);
        }
        
        // 检查INode是否存在
        INode iNode = folderService.getNodeInfo(user, iNodeLink.getOwnedBy(), iNodeLink.getiNodeId());
        if (iNode == null)
        {
            String msg = "inode not exist, ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:"
                + iNodeLink.getiNodeId();
            throw new NoSuchItemsException(msg);
        }
        
        if (StringUtils.isBlank(iNode.getLinkCode()) || INode.LINKCODE_NEW_SET.equals(iNode.getLinkCode()))
        {
            String msg = "inodelink not exist, ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:"
                + iNodeLink.getId();
            throw new NoSuchLinkException(msg);
        }
        
        INodeLink link = getINodeLinkInfoV2(iNode.getLinkCode());
        if (null == link)
        {
            String msg = "inodelink not exist, ownerId:" + iNodeLink.getOwnedBy() + ", inodeid:"
                + iNodeLink.getId() + ", linkCode:" + iNode.getLinkCode();
            throw new NoSuchItemsException(msg);
        }
        
        // 需要对提取码进行加密
        iNodeLink.setPlainAccessCode(StringUtils.trimToNull(iNodeLink.getPlainAccessCode()));
        link.setPlainAccessCode(iNodeLink.getPlainAccessCode());
        if (StringUtils.isNotEmpty(iNodeLink.getPlainAccessCode()))
        {
            Map<String, String> encodedKeys = EDToolsEnhance.encode(iNodeLink.getPlainAccessCode());
            link.setPassword(null);
            link.setEncryptedPassword(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
            link.setPasswordKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("change crypt in ufm.LinkService v2");
        }
        else
        // 如果未带提取码则表示删除提取码
        {
            link.setPassword(null);
            link.setEncryptedPassword(null);
            link.setPasswordKey(null);
        }
        
        link.setAccess(iNodeLink.getAccess());
        link.setRole(ResourceRole.VIEWER);
        
        // 设置外链失效时间，并判断时间有效性
        checkAndSetLinkDate(link, iNodeLink);
        
        link.setModifiedAt(new Date());
        link.setModifiedBy(user.getId());
        iNodeLinkDao.updateV2(link);
        
        // 访问地址加上域名
        link.setUrl(link.getUrl());
        
        /**
         * 发送日志
         */
        sendEvent(user, EventType.LINK_UPDATE, iNode);
        String[] logMsgs = new String[]{StringUtils.trimToEmpty(iNode.getLinkCode()),
            String.valueOf(iNode.getOwnedBy()), String.valueOf(iNode.getParentId())};
        String keyword = StringUtils.trimToEmpty(iNode.getName());
        
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            iNode,
            null,
            UserLogType.UPDATE_INODE_LINK,
            logMsgs,
            keyword);
        return link;
    }
    
    /**
     * 验证与填充生效时间
     * 
     * @param link
     * @param iNodeLink
     * @throws BadRequestException
     */
    private void checkAndSetLinkDate(INodeLink link, INodeLink iNodeLink) throws BadRequestException
    {
        Date dateOfEffective = iNodeLink.getEffectiveAt();
        Date dateOfExpireAt = iNodeLink.getExpireAt();
        
        // 如果有失效时间，则生效时间必须设置
        if (null == dateOfEffective && null != dateOfExpireAt)
        {
            String msg = "dateOfEffective cann't be null.";
            throw new BadRequestException(msg);
        }
        // 如果生效和失效时间都设置，必须判断参数合法性
        else if (null != dateOfEffective && null != dateOfExpireAt && dateOfEffective.after(dateOfExpireAt))
        {
            String msg = "date setting invalid, effectiveAt:" + dateOfEffective.toString() + ",expireAt:"
                + dateOfExpireAt;
            throw new BadRequestException(msg);
        }
        
        link.setEffectiveAt(iNodeLink.getEffectiveAt());
        link.setExpireAt(iNodeLink.getExpireAt());
    }
    
    /**
     * 获取外链信息
     * 
     * @param linkCode
     * @return
     * @throws BadRequestException
     */
    private INodeLink getINodeLinkInfo(String linkCode) throws BaseRunException
    {
        INodeLink iNodeLink = new INodeLink();
        iNodeLink.setId(linkCode);
        iNodeLink = iNodeLinkDao.get(iNodeLink);
        if (null == iNodeLink)
        {
            return null;
        }
        // 需要对提取码进行解密
        decryptPlainAccessCode(iNodeLink);
        // 访问地址加上域名
        iNodeLink.setUrl(getLinkBasePath() + iNodeLink.getUrl());
        
        return iNodeLink;
    }
    
    private void decryptPlainAccessCode(INodeLink iNodeLink)
    {
        if (StringUtils.isNotEmpty(iNodeLink.getEncryptedPassword()))
        {
            if(linkCacheSupported
                && StringUtils.isNotBlank(iNodeLink.getPlainAccessCode()))
            {
                logger.info("accessCode for iNodeLink : {} is not null.", iNodeLink.getId());
                // 如果accessCode已解密，则不需要重复解密了
                return;
            }
            
            iNodeLink.setPlainAccessCode(
                EDToolsEnhance.decode(iNodeLink.getEncryptedPassword(), iNodeLink.getPasswordKey()));
        }
        else if (StringUtils.isNotEmpty(iNodeLink.getPassword()))
        {
            String plainAccessCode = EDTools.decode(iNodeLink.getPassword());
            iNodeLink.setPlainAccessCode(plainAccessCode);
            Map<String, String> encodedKeys = EDToolsEnhance.encode(plainAccessCode);
            iNodeLink.setEncryptedPassword(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
            iNodeLink.setPasswordKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
            iNodeLink.setPassword(null);
            iNodeLinkDao.upgradePassword(iNodeLink);
        }
    }
    
    /**
     * 获取外链信息
     * 
     * @param linkCode
     * @return
     * @throws BadRequestException
     */
    private INodeLink getINodeLinkInfoV2(String linkCode) throws BaseRunException
    {
        INodeLink queryLink = new INodeLink();
        queryLink.setId(linkCode);
        INodeLink iNodeLink = iNodeLinkDao.getV2(queryLink);
        if (null == iNodeLink)
        {
            return null;
        }
        // 需要对提取码进行解密
        decryptPlainAccessCode(iNodeLink);
        // 访问地址加上域名
        iNodeLink.setUrl(iNodeLink.getUrl());
        
        return iNodeLink;
    }
    
    
    
    /**
     * 外链列举文件夹时需要时删除其各种状态
     * 
     * @param list
     * @return
     */
    private FileINodesList getNodesListWithoutStatus(FileINodesList list)
    {
        FileINodesList tmpList = list;
        for (INode iNode : tmpList.getFiles())
        {
            iNode.setShareStatus(INode.SHARE_STATUS_UNSHARED);
            iNode.setShareStatus(INode.SYNC_STATUS_UNSET);
            iNode.setLinkCode(null);
        }
        for (INode iNode : tmpList.getFolders())
        {
            iNode.setShareStatus(INode.SHARE_STATUS_UNSHARED);
            iNode.setShareStatus(INode.SYNC_STATUS_UNSET);
            iNode.setLinkCode(null);
        }
        
        return tmpList;
    }
    
    /**
     * 判断外链是否生效
     * 
     * @param inodeLink
     * @return
     */
    private boolean isLinkExpired(INodeLink inodeLink)
    {
        Date now = new Date();
        return inodeLink.getExpireAt() != null && now.after(inodeLink.getExpireAt());
    }
    
    /**
     * 判断外链是否生效
     * 
     * @param inodeLink
     * @return
     */
    private boolean isLinkNotEffective(INodeLink inodeLink)
    {
        Date now = new Date();
        return inodeLink.getEffectiveAt() != null && now.before(inodeLink.getEffectiveAt());
    }
    
    private INodeLink reTryCreateLink(UserToken user, INodeLink iNodeLink) throws BadRequestException
    {
        // 写入新的外链元数据
        INodeLink link = new INodeLink();
        link.setOwnedBy(iNodeLink.getOwnedBy());
        link.setiNodeId(iNodeLink.getiNodeId());
        
        // 需要对提取码进行加密
        if (StringUtils.isNotEmpty(iNodeLink.getPlainAccessCode()))
        {
            link.setPlainAccessCode(iNodeLink.getPlainAccessCode());
            Map<String, String> encodedKeys = EDToolsEnhance.encode(iNodeLink.getPlainAccessCode());
            link.setEncryptedPassword(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
            link.setPasswordKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("set crypt in ufm.LinkService");
        }
        
        link.setAccess(iNodeLink.getAccess());
        link.setRole(checkAndGetRoleValid(iNodeLink.getRole()));
        
        // 设置外链失效时间，并判断时间有效性
        checkAndSetLinkDate(link, iNodeLink);
        
        link.setCreatedBy(user.getId());
        link.setModifiedBy(user.getId());
        
        int times = 0;
        while (true)
        {
            try
            {
                // 创建外链，下面方法及时提交数据库，避免出现死锁
                createLinkNoCheck(link);
                break;
            }
            catch (DataIntegrityViolationException e)
            {
                // 如果出错达到最大次数，直接抛出异常，否则继续重试
                times++;
                if (times == MAX_RETRY_TIMES)
                {
                    logger.error("create link failed", e);
                    throw e;
                }
            }
        }
        
        return link;
    }
    
    private String checkAndGetRoleValid(String role) throws InvalidPermissionRoleException
    {
        if (StringUtils.isNotBlank(role))
        {
            ResourceRole roleInfo = resourceRoleService.getResourceRole(role);
            // 检查权限角色合法性
            if (roleInfo == null)
            {
                String errorMsg = "role is not valid, role:" + role;
                throw new InvalidPermissionRoleException(errorMsg);
            }
            // 数据库不区分大小写，需要判断
            if (!StringUtils.equals(role, roleInfo.getResourceRole()))
            {
                String errorMsg = "role is not valid, role:" + role;
                throw new InvalidPermissionRoleException(errorMsg);
            }
        }
        else
        {
            role = ResourceRole.VIEWER;
        }
        return role;
    }
    
    private String buildLinkID()
    {
        String linkCode = "";
        if (isSecureLink())
        {
            linkCode = generateKey();
        }
        else
        {
            linkCode = LinkCodeCaculator.buildLinkID();
        }
        return linkCode.toLowerCase(Locale.getDefault());
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    private String generateKey()
    {
        try
        {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] keyBytes = new byte[getSecureLinkSize()];
            sr.nextBytes(keyBytes);
            return EncryptHelper.parseByte2HexStr(keyBytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new InternalServerErrorException("NoSuchAlgorithm:" + e.getMessage());
        }
        
    }
    
    /**
     * 发送INodeLink日志
     * 
     * @param user
     * @param type
     * @param srcNode
     */
    private void sendEvent(UserToken user, EventType type, INode srcNode)
    {
        try
        {
            if (null == user)
            {
                return;
            }
            Event event = new Event(user);
            event.setSource(srcNode);
            event.setCreatedAt(new Date());
            event.setCreatedBy(user.getId());
            event.setType(type);
            eventService.fireEvent(event);
        }
        catch (Exception e)
        {
            logger.warn(e.getMessage(), e);
        }
        
    }
    
    private boolean isSecureLink()
    {
        if (null == localCacheLinkFlag)
        {
            localCacheLinkFlag = systemConfigDAO.get(LINK_SECURITY_CONFIG_FLAG);
        }
        
        if (null != localCacheLinkFlag)
        {
            return Boolean.parseBoolean(localCacheLinkFlag.getValue());
        }
        return DEFAULT_SECURE_LINK_FLAG;
    }
    
    private int getSecureLinkSize()
    {
        if (null == localCacheLinkCodeSize)
        {
            localCacheLinkCodeSize = systemConfigDAO.get(LINK_SECURITY_CONFIG_BYTE_SIZE);
        }
        
        if (null != localCacheLinkCodeSize)
        {
            return Integer.parseInt(localCacheLinkCodeSize.getValue());
        }
        return DEFAULT_SECURE_LINK_BYTE_SIZE;
    }
    
}
