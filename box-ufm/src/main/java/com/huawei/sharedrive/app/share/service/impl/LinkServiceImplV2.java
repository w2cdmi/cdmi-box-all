package com.huawei.sharedrive.app.share.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.INodeACLIdGenerateService;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.acl.service.ResourceRoleService;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.share.LinkIdentityInfo;
import com.huawei.sharedrive.app.openapi.domain.share.RestLinkDynamicResponse;
import com.huawei.sharedrive.app.share.dao.INodeLinkDAO;
import com.huawei.sharedrive.app.share.dao.INodeLinkDynamicDao;
import com.huawei.sharedrive.app.share.dao.INodeLinkReverseDao;
import com.huawei.sharedrive.app.share.dao.ShareDAO;
import com.huawei.sharedrive.app.share.dao.ShareToMeDAO;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.domain.INodeLinkDynamic;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import com.huawei.sharedrive.app.share.domain.LinkAccessCodeMode;
import com.huawei.sharedrive.app.share.service.INodeLinkApproveService;
import com.huawei.sharedrive.app.share.service.LinkServiceV2;
import com.huawei.sharedrive.app.share.service.ShareService;
import com.huawei.sharedrive.app.share.service.ShareToMeService;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.LinkCodeCaculator;

import org.apache.commons.collections.CollectionUtils;
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
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.utils.EDTools;
import pw.cdmi.core.utils.EDToolsEnhance;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Component
@Service("linkServiceV2")
public class LinkServiceImplV2 implements LinkServiceV2
{
    private static Logger logger = LoggerFactory.getLogger(LinkServiceImplV2.class);
    
    private static final int MAX_RETRY_TIMES = 3;
    
    private static final int MAX_LINKS_PER_NODE_DEFAULT = 3;
    
    private static final String PUBLIC_LINKC_PATH = "p/";
    
    private static final String LINK_SECURITY_CONFIG_FLAG = "system.linkcode.security.enable";
    
    private static final String LINK_SECURITY_CONFIG_BYTE_SIZE = "system.linkcode.security.byte.size";
    
    private static final boolean DEFAULT_SECURE_LINK_FLAG = true;
    
    private static final int DEFAULT_SECURE_LINK_BYTE_SIZE = 16;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FolderService folderService;
    
    @Autowired
    private FolderServiceV2 folderServiceV2;
    
    @Autowired
    private INodeDAO iNodeDao;
    
    @Autowired
    private INodeDAOV2 iNodeDAOV2;
    
    @Autowired
    private INodeLinkDAO iNodeLinkDao;
    
    @Autowired
    private INodeLinkReverseDao iNodeLinkReverseDao;
    
    @Autowired
    private INodeLinkDynamicDao iNodeLinkDynamicDao;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private INodeACLIdGenerateService nodeACLIdGenerateService;
    
    @Autowired
    private INodeACLService nodeACLService;
    
    @Autowired
    private ResourceRoleService resourceRoleService;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    private SystemConfig localCacheLinkFlag;
    
    private SystemConfig localCacheLinkCodeSize;
    
    @Autowired
    private INodeLinkApproveService linkApproveService;
    
    @Autowired
    private ShareDAO shareDAO;
    
    @Autowired
    private ShareToMeDAO shareToMeDAO;
    
    @Value("${link.cache.supported}")
    private boolean linkCacheSupported;
    
    @Override
    public RestLinkDynamicResponse addDynamicAccessCode(String linkCode, String identity)
        throws BaseRunException
    {
        INodeLink link = checkAndGetLinkById(linkCode);
        INodeLinkDynamic dynamic = checkDynamicValid(linkCode, identity, link);
        
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.SECOND, userTokenHelper.getAccessCodeExpiredAt());
        String accessCode = LinkCodeCaculator.buildAccessCode();
        Map<String, String> encodedKeys = EDToolsEnhance.encode(accessCode);
        dynamic.setPassword(null);
        dynamic.setEncryptedPassword(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
        dynamic.setPasswordKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
        dynamic.setExpiredAt(ca.getTime());
        logger.info("change crypt in ufm.LinkServiceV2");
        iNodeLinkDynamicDao.updatePassword(dynamic);
        
        RestLinkDynamicResponse result = new RestLinkDynamicResponse();
        result.setAccessCodeMode(LinkAccessCodeMode.transTypeToString(link.getStatus()));
        result.setPlainAccessCode(accessCode);
        return result;
    }
    
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void createLinkNoCheck(INodeLink link)
    {
        // 产生外链码
        String linkCode = buildLinkID();
        link.setId(linkCode);
        // 设置外链访问地址
        link.setUrl(PUBLIC_LINKC_PATH  + link.getId());
        Date date = new Date();
        link.setCreatedAt(new Date(date.getTime() / 1000 * 1000));
        link.setModifiedAt(new Date(date.getTime() / 1000 * 1000));
        
        iNodeLinkDao.create(link);
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
     * 创建链接
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INodeLink createLinkV2(UserToken user, INodeLink iNodeLink, INode iNode) throws BaseRunException
    {
        String linkCodeSet = getLinkSetFlag(iNode);
        
        checkMaxLink(iNode);
        iNodeLink.setPlainAccessCode(StringUtils.trimToEmpty(iNodeLink.getPlainAccessCode()));
        
        INodeLink link = reTryCreateLink(user, iNodeLink);
        
        addNodeLinkACL(user, iNode, link.getId(), link.getRole());
        
        // 访问地址加上域名
        link.setUrl(link.getUrl());
        
        iNodeLinkReverseDao.createV2(link);
        
        if (link.getStatus() != LinkAccessCodeMode.TYPE_STATIC_VALUE)
        {
            createDynamicAttr(link);
            
        }
        
        // 更新节点链接状态
        folderService.updateNodeLinkCode(user,
            iNode,
            linkCodeSet == null ? String.valueOf(link.getId()) : linkCodeSet);
            
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
            
        return link;
    }
    
    /**
     * 创建链接
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INodeLink createLinkV2(UserToken user, INodeLink iNodeLink, List<INode> iNodes) throws BaseRunException
    {
        iNodeLink.setPlainAccessCode(StringUtils.trimToEmpty(iNodeLink.getPlainAccessCode()));
        JsonArray jsonINodes= new JsonArray();
        for(INode iNode : iNodes){
     	   JsonObject jinode=new JsonObject();
     	   jinode.addProperty("id", iNode.getId());
     	   jinode.addProperty("ownedBy", iNode.getOwnedBy());
     	   jsonINodes.add(jinode);
        }
        iNodeLink.setSubINodes(jsonINodes.toString());
        INodeLink link = reTryCreateLink(user, iNodeLink);
     
        for(INode iNode : iNodes){
        	   addNodeLinkACL(user, iNode, link.getId(), link.getRole());
        }
        
        // 访问地址加上域名
        link.setUrl(link.getUrl());
        
        iNodeLinkReverseDao.createV2(link);
        
        if (link.getStatus() != LinkAccessCodeMode.TYPE_STATIC_VALUE)
        {
            createDynamicAttr(link);
        }
        
//        // 更新节点链接状态
//        folderService.updateNodeLinkCode(user,
//            iNode,
//            linkCodeSet == null ? String.valueOf(link.getId()) : linkCodeSet);
            
        /**
         * 发送日志
         */
//        sendEvent(user, EventType.LINK_CREATE, iNode);
//        String[] logMsgs = new String[]{StringUtils.trimToEmpty(link.getId()),
//            String.valueOf(iNode.getOwnedBy()), String.valueOf(iNode.getParentId())};
//        String keyword = StringUtils.trimToEmpty(iNode.getName());
//        
//        fileBaseService.sendINodeEvent(user,
//            EventType.LINK_CREATE,
//            iNode,
//            null,
//            UserLogType.CREATE_LINK,
//            logMsgs,
//            keyword);
            
        return link;
    }
    
    @Override
    public void deleteAllLinkByNode(INode node) throws BaseRunException
    {
        if (StringUtils.isNotBlank(node.getLinkCode()) && !INode.LINKCODE_NEW_SET.equals(node.getLinkCode()))
        {
            INodeLink linkToDelete = new INodeLink();
            linkToDelete.setId(node.getLinkCode());
            iNodeLinkDao.delete(linkToDelete);
            nodeACLService.deleteByResourceAndUser(node.getOwnedBy(),
                node.getId(),
                node.getLinkCode(),
                INodeACL.TYPE_LINK);
        }
        List<INodeLink> items = iNodeLinkReverseDao.listByNode(node.getOwnedBy(), node.getId(), null);
        
        for (INodeLink item : items)
        {
            iNodeLinkDynamicDao.deleteAll(item.getId());
            iNodeLinkDao.deleteV2(item);
            nodeACLService.deleteByResourceAndUser(node.getOwnedBy(),
                node.getId(),
                item.getId(),
                INodeACL.TYPE_LINK);
        }
        iNodeLinkReverseDao.deleteByNode(node.getOwnedBy(), node.getId(), null);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteLinkByTypeOrId(UserToken userInfo, INode iNode, String type, String linkCode)
        throws BaseRunException
    {
        doDeleteLink(userInfo, iNode, type, linkCode);
        INodeShare inodeshare =new INodeShare();
        inodeshare.setLinkCode(linkCode);
        inodeshare.setOwnerId(userInfo.getId());
        inodeshare.setCreatedBy(userInfo.getId());
        List<INodeShare> forwrardList = shareDAO.getForwardRecord(inodeshare);
        for(INodeShare iNodeShare : forwrardList){
        	shareDAO.deleteByInodeAndSharedUser(iNodeShare);
        	if(iNodeShare.getSharedUserId()!=0){
        		shareToMeDAO.deleteByInode(iNodeShare);
        	}
        	
        }
        
    }
    
    @Override
    public void deleteLinkDynamicCode(String linkCode, String identity) throws BaseRunException
    {
        iNodeLinkDynamicDao.delete(linkCode, identity);
    }
    
    /**
     * 根据节点ID获取节点信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INodeLink getLink(UserToken user, INode iNode, String linkCode) throws BaseRunException
    {
        INodeLink iNodeLink = getINodeLinkInfoV2(linkCode);
        if (deleteInvalidLink(user, iNode, iNodeLink))
        {
            return null;
        }
        
        fillLinkIdentityInfo(iNodeLink);
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
        fillLinkRoleInfo(iNodeLink);
        return iNodeLink;
    }
    
    /**
     * 根据提取码获取节点信息
     */
    @Override
    public INodeLink getLinkByLinkCodeForClient(String linkCode) throws BaseRunException
    {
        // 检查入参
        if (linkCode == null)
        {
            String msg = "linkCode is null";
            throw new BadRequestException(msg);
        }
        // 检查链接是否存在
        INodeLink iNodeLink = checkAndGetLinkById(linkCode);
        
        // 检查链接是否生效
        if (isLinkNotEffective(iNodeLink))
        {
            logger.warn("inode not effective, EffectiveAt:{}, now:{}", iNodeLink.getEffectiveAt(), new Date());
            String msg = "inode not effective, EffectiveAt:" + iNodeLink.getEffectiveAt();
            throw new LinkNotEffectiveException(msg);
        }
        
        // 检查链接是否过期
        if (isLinkExpired(iNodeLink))
        {
            String msg = "inode expired, expireAt:" + iNodeLink.getExpireAt();
            throw new LinkExpiredException(msg);
        }
        
        fillLinkIdentityInfo(iNodeLink);
        
        return iNodeLink;
        
    }
    
    @Override
    public List<INodeLinkDynamic> getLinkDynamicCode(String linkCode) throws BaseRunException
    {
        INodeLinkDynamic iNodeLinkDynamic = new INodeLinkDynamic();
        iNodeLinkDynamic.setId(linkCode);
        List<INodeLinkDynamic> dynamicis = iNodeLinkDynamicDao.list(iNodeLinkDynamic);
        
        for (INodeLinkDynamic dynamici : dynamicis)
        {
            dynamici.setPassword(getDecryptedAccessCode(dynamici));
        }
        
        return dynamicis;
    }
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public FileINodesList listAllLinkNodes(UserToken user, long ownerId, String name, long offset, int limit,
        List<Order> orderList, List<Thumbnail> thumbnailList, Map<String, String> headerCustomMap) throws BaseRunException
    {
    	String enterpriseId = "";
		if(user.getAccountVistor()!=null){
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}
        // 根节点的权限判断，需要兼顾团队空间
        checkOperPermission(ownerId, user.getId(),enterpriseId,user);
        
        FileINodesList fileInodeList = new FileINodesList();
        fileInodeList.setTotalCount(iNodeDAOV2.getCountByLinkStatus(ownerId, name));
        fileInodeList.setLimit(limit);
        fileInodeList.setOffset(offset);
        
        List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        
        buildNodeLinkInfo(user, ownerId, name, offset, limit, orderList, thumbnailList, folderList, fileList, headerCustomMap);
        
        fileInodeList.setFolders(folderList);
        fileInodeList.setFiles(fileList);
        String parentId = String.valueOf(INode.FILES_ROOT);
        String[] logParams = new String[]{String.valueOf(ownerId), parentId};
        String keyword = StringUtils.trimToEmpty("FILES_ROOT");
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.LIST_FOLDER,
            logParams,
            keyword);
        return fileInodeList;
    }
    
    @Override
    public List<INodeLink> listNodeAllLinks(UserToken curUser, long ownerId, long nodeId)
    {
        INode iNodeTemp = checkAndGetNodeInfo(ownerId, nodeId);
        
        // 根节点的权限判断，需要兼顾团队空间
        iNodeACLService.vaildINodeOperACL(curUser, iNodeTemp, AuthorityMethod.GET_ALL.name());
        
        List<INodeLink> list = iNodeLinkReverseDao.listByNode(ownerId, nodeId, null);
        if (!INode.LINKCODE_NEW_SET.equals(iNodeTemp.getLinkCode())
            && StringUtils.isNotBlank(iNodeTemp.getLinkCode()))
        {
            INodeLink link = new INodeLink();
            link.setId(iNodeTemp.getLinkCode());
            list.add(iNodeLinkDao.getV2(link));
        }
        
        for (INodeLink item : list)
        {
            if (isLinkExpired(item))
            {
                deleteLinkByTypeOrId(curUser, iNodeTemp, null, item.getId());
                continue;
            }
            // 需要对提取码进行解密
            decryptPlainAccessCode(item);
            fillLinkIdentityInfo(item);
            fillLinkRoleInfo(item);
        }
        
        return list;
        
    }
    
    @Override
    public List<INodeLink> listNodeAllLinksNoCheck(UserToken curUser, long ownerId, long nodeId)
    {
        INode iNodeTemp = checkAndGetNodeInfo(ownerId, nodeId);
        
        // 根节点的权限判断，需要兼顾团队空间
//        iNodeACLService.vaildINodeOperACL(curUser, iNodeTemp, AuthorityMethod.GET_ALL.name());
        
        List<INodeLink> list = iNodeLinkReverseDao.listByNode(ownerId, nodeId, null);
        if (!INode.LINKCODE_NEW_SET.equals(iNodeTemp.getLinkCode())
            && StringUtils.isNotBlank(iNodeTemp.getLinkCode()))
        {
            INodeLink link = new INodeLink();
            link.setId(iNodeTemp.getLinkCode());
            list.add(iNodeLinkDao.getV2(link));
        }
        
        for (INodeLink item : list)
        {
            if (isLinkExpired(item))
            {
                deleteLinkByTypeOrId(curUser, iNodeTemp, null, item.getId());
                continue;
            }
            // 需要对提取码进行解密
            decryptPlainAccessCode(item);
            fillLinkIdentityInfo(item);
            fillLinkRoleInfo(item);
        }
        
        return list;
        
    }
    
    @Override
    public void updateExpiredAt(INodeLinkDynamic iNodeLinkDynamic) throws BaseRunException
    {
        iNodeLinkDynamicDao.updateExpiredAt(iNodeLinkDynamic);
    }
    
    /**
     * 修改连接
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INodeLink updateLink(UserToken user, INode iNode, INodeLink iNodeLink) throws BaseRunException
    {
        INodeLink link = checkAndGetLinkById(iNodeLink.getId());
        
        if (link.getOwnedBy() != iNode.getOwnedBy() || link.getiNodeId() != iNode.getId())
        {
            throw new ForbiddenException();
        }
        
        // 需要对提取码进行加密
        iNodeLink.setPlainAccessCode(StringUtils.trimToNull(iNodeLink.getPlainAccessCode()));
        link.setPlainAccessCode(iNodeLink.getPlainAccessCode());
        encodeLinkPassword(iNodeLink, link);
        
        link.setAccess(iNodeLink.getAccess());
        
        link.setRole(checkAndGetRoleValid(iNodeLink.getRole()));
        
        // 设置外链失效时间，并判断时间有效性
        checkAndSetLinkDate(link, iNodeLink);
        
        link.setModifiedAt(new Date());
        link.setModifiedBy(user.getId());
        link.setStatus(iNodeLink.getStatus());
        link.setIdentities(iNodeLink.getIdentities());
        link.setNeedLogin(iNodeLink.isNeedLogin());
        transForModifyLinkRole(user, iNode, link, iNodeLink.getRole());
        iNodeLinkReverseDao.updateV2(link);
        
        if (link.getStatus() != LinkAccessCodeMode.TYPE_STATIC_VALUE)
        {
            updateDynamicAttr(link);
        }
        
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
     * 验证操作者ACL
     */
    @Override
    public long vaildLinkOperACL(UserToken access, INode iNode, String oper, boolean valiAccessCode)
        throws BaseRunException
    {
        // 获取外链对应的节点是否存在
        INodeLink dbLink = checkAndGetLinkById(access.getLinkCode());
        INode iNodeRoot = iNodeDao.get(dbLink.getOwnedBy(), dbLink.getiNodeId());
        if (iNodeRoot == null || iNodeRoot.getStatus() != INode.STATUS_NORMAL)
        {
            throw new NoSuchItemsException("inode not exist or abnormal, linkCode:" + access.getLinkCode());
        }
        validAccessCode(access, valiAccessCode, dbLink, iNodeRoot);
        
        if (AuthorityMethod.GET_ALL.contain(AuthorityMethod.valueOf(oper)))
        {
            if (!checkSubFileExist(iNodeRoot, iNode))
            {
                throw new ForbiddenException("inode not a subnode in given linkCode, iNodeRoot:"
                    + iNodeRoot.getId() + ", iNode:" + iNode.getId() + ", linkCode:" + access.getLinkCode());
            }
        }
        
        if (isLinkNotEffective(dbLink))
        {
            logger.error("inode not effective, EffectiveAt:" + dbLink.getEffectiveAt().toString());
            throw new ForbiddenException();
        }
        
        if (isLinkExpired(dbLink))
        {
            throw new ForbiddenException("Link is expired.");
        }
        return access.getId();
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
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void buildNodeLinkInfo(UserToken user, long ownerId, String name, long offset, int limit,
        List<Order> orderList, List<Thumbnail> thumbnailList, List<INode> folderList, List<INode> fileList,
        Map<String, String> headerCustomMap)
    {
        if (CollectionUtils.isNotEmpty(orderList)
            && orderList.contains(new Order("size", "ASC")))
        {
            orderList.add(new Order("NAME", "ASC"));
        }
        
        List<INode> nodeList = iNodeDAOV2.getByLinkStatus(ownerId, name, orderList, offset, limit);
        
        for (INode temp : nodeList)
        {
            setNodeLinkNum(temp);
            
            if (FilesCommonUtils.isFolderType(temp.getType()))
            {
                folderList.add(temp);
            }
            else if (temp.getType() == INode.TYPE_FILE)
            {
                folderServiceV2.setNodeThumbnailUrl(user, thumbnailList, user.getId(), temp, headerCustomMap);
                FilesCommonUtils.setNodeVersionsForV2(temp);
                fileList.add(temp);
            }
        }
    }
    
    private INodeLink checkAndGetLinkById(String linkCode)
    {
        INodeLink link = getINodeLinkInfoV2(linkCode);
        if (null == link)
        {
            String msg = "inodelink not exist, linkCode:" + linkCode;
            throw new NoSuchLinkException(msg);
        }
        return link;
    }
    
    private INode checkAndGetNodeInfo(long ownerId, long id)
    {
        INode iNode = fileBaseService.getINodeInfo(ownerId, id);
        if (iNode == null)
        {
            String msg = "inode not exist, ownerId:" + ownerId + ", inodeid:" + id;
            throw new NoSuchItemsException(msg);
        }
        return iNode;
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

    private void checkAndSetLinkDate(INodeLink link, INodeLink iNodeLink) throws BadRequestException {
        Date dateOfEffective = iNodeLink.getEffectiveAt();
        Date dateOfExpireAt = iNodeLink.getExpireAt();

        if (dateOfEffective == null) {
            dateOfEffective = new Date();
        }
        // 如果生效和失效时间都设置，必须判断参数合法性
        if (dateOfExpireAt != null && dateOfEffective.after(dateOfExpireAt)) {
            String msg = "date setting invalid, effectiveAt:" + dateOfEffective.toString() + ",expireAt:" + dateOfExpireAt;
            throw new BadRequestException(msg);
        }

        link.setEffectiveAt(dateOfEffective);
        link.setExpireAt(dateOfExpireAt);
    }
    
    private INodeLinkDynamic checkDynamicValid(String linkCode, String identity, INodeLink link)
    {
        if (link.getStatus() == LinkAccessCodeMode.TYPE_STATIC_VALUE)
        {
            throw new ForbiddenException("accessCodeMode not allowed");
        }
        
        INodeLinkDynamic dynamic = iNodeLinkDynamicDao.get(linkCode, identity);
        if (dynamic == null)
        {
            throw new ForbiddenException("receiver not find");
        }
        return dynamic;
    }
    
    private void checkMaxLink(INode iNode)
    {
        int total = iNodeLinkReverseDao.getCountByNode(iNode.getOwnedBy(), iNode.getId(), null);
        if (StringUtils.isNotBlank(iNode.getLinkCode())
            && !INode.LINKCODE_NEW_SET.equals(iNode.getLinkCode()))
        {
            total++;
        }
        if (total >= getMaxLinksPerNode())
        {
            throw new ExceedMaxLinkNumException();
        }
    }
    
    private void checkOperPermission(long ownerId, long userId,String enterpriseId,UserToken user) throws ForbiddenException
    {
        if (ownerId == userId)
        {
            return;
        }
        // 是否具有授权权限判断:只需要获取根节点是否具有author权限就可以
        List<INodeACL> aclList = iNodeACLService.getINodeACLSelfAndAnyACLs(ownerId, INode.FILES_ROOT, userId,enterpriseId,user);
        
        if (CollectionUtils.isEmpty(aclList))
        {
            String excepMessage = "Not allowed to operate acl , userId:" + userId;
            throw new ForbiddenException(excepMessage);
        }
        
        boolean isForbidden = true;
        for (INodeACL acl : aclList)
        {
            if (ResourceRole.AUTHER.equals(acl.getResourceRole()))
            {
                isForbidden = false;
                break;
            }
        }
        
        if (isForbidden)
        {
            String excepMessage = "Not allowed to operate acl , userId:" + userId;
            throw new ForbiddenException(excepMessage);
        }
    }
    
    /**
     * 判断节点是否存在父目录下
     * 
     * @param parentNode
     * @param inode
     * @return
     * @throws BaseRunException
     */
    private boolean checkSubFileExist(INode parentNode, INode inode) throws BaseRunException
    {
        // 如果两者就是一个节点，返回
        if (parentNode.getId() == inode.getId() && parentNode.getOwnedBy() == inode.getOwnedBy())
        {
            return true;
        }
        
        // 如果不是目录，返回false
        if (!FilesCommonUtils.isFolderType(parentNode.getType()))
        {
            return false;
        }
        
        long parentId = inode.getParentId();
        
        INode iNodeTemp = null;
        while (parentId != INode.FILES_ROOT)
        {
            if (parentId == parentNode.getId())
            {
                return true;
            }
            
            iNodeTemp = checkAndGetNodeInfo(parentNode.getOwnedBy(), parentId);
            parentId = iNodeTemp.getParentId();
        }
        return false;
    }
    
    private void checkTypeValid(String accessCodeMode) throws InvalidParamException
    {
        if ("all".equals(accessCodeMode))
        {
            return;
        }
        if (!LinkAccessCodeMode.contains(accessCodeMode))
        {
            String errorMsg = "accessCodeMode is not valid:" + accessCodeMode;
            throw new InvalidParamException(errorMsg);
        }
        
    }
    
    private void createDynamicAttr(INodeLink link)
    {
        iNodeLinkDynamicDao.deleteAll(link.getId());
        
        INodeLinkDynamic iNodeLinkDynamic;
        Date date = new Date();
        
        iNodeLinkDynamic = new INodeLinkDynamic();
        iNodeLinkDynamic.setId(link.getId());
        iNodeLinkDynamic.setCreatedAt(date);
        iNodeLinkDynamicDao.create(iNodeLinkDynamic);
       /* if (link.getIdentities() != null)
        {
            for (LinkIdentityInfo identity : link.getIdentities())
            {
                iNodeLinkDynamic = new INodeLinkDynamic();
                iNodeLinkDynamic.setId(link.getId());
                iNodeLinkDynamic.setIdentity(identity.getIdentity());
                iNodeLinkDynamic.setCreatedAt(date);
                iNodeLinkDynamicDao.create(iNodeLinkDynamic);
            }
        }*/
    }
    
    private boolean deleteInvalidLink(UserToken user, INode iNode, INodeLink iNodeLink)
    {
        // 如果外链个数为0，更新flag为空
        int total = iNodeLinkReverseDao.getCountByNode(iNode.getOwnedBy(), iNode.getId(), null);
        if (null == iNodeLink)
        {
            if (total == 0 && StringUtils.isEmpty(iNode.getLinkCode()))
            {
                folderService.updateNodeLinkCode(user, iNode, "");
            }
            return true;
        }
        
        // 检查链接是否超期，如果超期设置状态不可用
        // 该方法供获取外链详情场景使用,故生效日期不做校验
        if (isLinkExpired(iNodeLink))
        {
            iNodeLinkDynamicDao.deleteAll(iNodeLink.getId());
            iNodeLinkReverseDao.deleteV2(iNodeLink);
            iNodeLinkDao.deleteV2(iNodeLink);
            updateNodeLinkCodeForLinkExpired(user, iNode, iNodeLink.getId());
            return true;
        }
        return false;
        
    }
    
    private void deleteLinkById(INode iNode, String linkCode)
    {
        INodeLink linkToDelete = new INodeLink(linkCode);
        linkToDelete = iNodeLinkDao.getV2(linkToDelete);
        if (linkToDelete == null)
        {
            throw new NoSuchLinkException("inodelink not exist, linkCode:" + linkCode);
        }
        if (linkToDelete.getOwnedBy() != iNode.getOwnedBy() || linkToDelete.getiNodeId().longValue() != iNode.getId().longValue())
        {
            throw new ForbiddenException();
        }
        nodeACLService.deleteByResourceAndUser(linkToDelete.getOwnedBy(),
            linkToDelete.getiNodeId(),
            linkToDelete.getId(),
            INodeACL.TYPE_LINK);
        iNodeLinkDynamicDao.deleteAll(linkToDelete.getId());
        iNodeLinkDao.deleteV2(linkToDelete);
        iNodeLinkReverseDao.deleteV2(linkToDelete);
        
    }
    
    private void deleteLinkByType(INode iNode, String type, String linkCode)
    {
        ArrayList<INodeLink> linksToDel = new ArrayList<INodeLink>(BusinessConstants.INITIAL_CAPACITIES);
        
        Byte accessCodeModeValue = LinkAccessCodeMode.transTypeToValue(type);
        List<INodeLink> items = iNodeLinkReverseDao.listByNode(iNode.getOwnedBy(),
            iNode.getId(),
            accessCodeModeValue);
        linksToDel.addAll(items);
        
        for (INodeLink item : linksToDel)
        {
            nodeACLService.deleteByResourceAndUser(item.getOwnedBy(),
                item.getiNodeId(),
                item.getId(),
                INodeACL.TYPE_LINK);
            iNodeLinkDynamicDao.deleteAll(item.getId());
            iNodeLinkDao.deleteV2(item);
        }
        
        iNodeLinkReverseDao.deleteByNode(iNode.getOwnedBy(), iNode.getId(), accessCodeModeValue);
        
        if (LinkAccessCodeMode.TYPE_STATIC_STRING.equals(type))
        {
            if (!INode.LINKCODE_NEW_SET.equals(iNode.getLinkCode())
                && StringUtils.isNotBlank(iNode.getLinkCode()))
            {
                INodeLink linkToDelete = new INodeLink();
                linkToDelete.setId(linkCode);
                nodeACLService.deleteByResourceAndUser(iNode.getOwnedBy(),
                    iNode.getId(),
                    linkCode,
                    INodeACL.TYPE_LINK);
                iNodeLinkDao.deleteV2(linkToDelete);
            }
            
        }
    }
    
    private void doDeleteLink(UserToken userInfo, INode iNode, String type, String linkCode)
    {
        if (StringUtils.isBlank(type))
        {
            if (StringUtils.isBlank(linkCode))
            {
                // 删除节点所有外链
                deleteAllLinkByNode(iNode);
                folderService.updateNodeLinkCode(userInfo, iNode, null);
                linkApproveService.deleteByNodeId(userInfo, iNode);
            }
            else
            {
                // 删掉单个外链
                deleteLinkById(iNode, linkCode);
                // 更新节点链接状态
                updateNodeLinkCodeForDelete(userInfo, iNode, type, linkCode);
                linkApproveService.deleteByLinkCode(linkCode);
            }
        }
        else
        {
            if (StringUtils.isBlank(linkCode))
            {
                checkTypeValid(type);
                if (StringUtils.equals(type, "all"))
                {
                    // 删除节点所有外链
                    deleteAllLinkByNode(iNode);
                    folderService.updateNodeLinkCode(userInfo, iNode, null);
                }
                else
                {
                    // 删除某类外链
                    deleteLinkByType(iNode, type, linkCode);
                    // 更新节点链接状态
                    updateNodeLinkCodeForDelete(userInfo, iNode, type, linkCode);
                }
                
            }
            else
            {
                // 删掉单个外链
                deleteLinkById(iNode, linkCode);
                // 更新节点链接状态
                updateNodeLinkCodeForDelete(userInfo, iNode, type, linkCode);
            }
        }
    }
    
    private void encodeLinkPassword(INodeLink iNodeLink, INodeLink link)
    {
        if (StringUtils.isNotEmpty(iNodeLink.getPlainAccessCode()))
        {
            Map<String, String> encodedKeys = EDToolsEnhance.encode(iNodeLink.getPlainAccessCode());
            link.setPassword(null);
            link.setEncryptedPassword(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
            link.setPasswordKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("change crypt in ufm.LinkServiceV2");
        }
        else
        // 如果未带提取码则表示删除提取码
        {
            link.setPassword(null);
            link.setEncryptedPassword(null);
            link.setPasswordKey(null);
        }
    }
    
    private void fillLinkIdentityInfo(INodeLink item)
    {
        List<INodeLinkDynamic> dynamicis = iNodeLinkDynamicDao.list(new INodeLinkDynamic(item.getId()));
        List<LinkIdentityInfo> identities = new ArrayList<LinkIdentityInfo>(
            BusinessConstants.INITIAL_CAPACITIES);
            
        LinkIdentityInfo linkIdentityInfo;
        for (INodeLinkDynamic dynamici : dynamicis)
        {
            linkIdentityInfo = new LinkIdentityInfo(dynamici.getIdentity());
            identities.add(linkIdentityInfo);
        }
        item.setIdentities(identities);
    }
    
    private void fillLinkRoleInfo(INodeLink link) throws BaseRunException
    {
        INodeACL iNodeACL = nodeACLService.getByResourceAndUser(link.getOwnedBy(),
            link.getiNodeId(),
            link.getId(),
            INodeACL.TYPE_LINK);
            
        if (iNodeACL != null)
        {
            link.setRole(iNodeACL.getResourceRole());
        }
        else
        {
            link.setRole(ResourceRole.VIEWER);
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
    
    private String getLinkSetFlag(INode iNode)
    {
        String linkCodeSet;
        if (StringUtils.isBlank(iNode.getLinkCode()))
        {
            linkCodeSet = INode.LINKCODE_NEW_SET;
        }
        else
        {
            linkCodeSet = iNode.getLinkCode();
        }
        return linkCodeSet;
    }
    
    private int getMaxLinksPerNode()
    {
        try
        {
            SystemConfig config = systemConfigDAO.get("max.links.per.node");
            if (config != null)
            {
                return Integer.parseInt(config.getValue());
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return MAX_LINKS_PER_NODE_DEFAULT;
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
        link.setSubINodes(iNodeLink.getSubINodes());
        
        // 需要对提取码进行加密
        if (StringUtils.isNotEmpty(iNodeLink.getPlainAccessCode()))
        {
            link.setPlainAccessCode(iNodeLink.getPlainAccessCode());
            Map<String, String> encodedKeys = EDToolsEnhance.encode(iNodeLink.getPlainAccessCode());
            link.setEncryptedPassword(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
            link.setPasswordKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("set crypt in ufm.LinkServiceV2");
        }
        
        link.setAccess(iNodeLink.getAccess());
        link.setRole(iNodeLink.getRole());
        
        // 设置外链失效时间，并判断时间有效性
        checkAndSetLinkDate(link, iNodeLink);
        
        link.setCreatedBy(user.getId());
        link.setModifiedBy(user.getId());
        link.setStatus(iNodeLink.getStatus());
        link.setIdentities(iNodeLink.getIdentities());
        link.setNeedLogin(iNodeLink.isNeedLogin());
        link.setRole(checkAndGetRoleValid(iNodeLink.getRole()));
        if(iNodeLink.getIsProgram()!=null){
        	link.setIsProgram(iNodeLink.getIsProgram());
        }
        if(iNodeLink.getDisdump()!=null){
        	link.setDisdump(iNodeLink.getDisdump());
        }
        
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
    
    private void setNodeLinkNum(INode temp)
    {
        int linkCount = 0;
        if (!INode.LINKCODE_NEW_SET.equals(temp.getLinkCode()) && StringUtils.isNotBlank(temp.getLinkCode()))
        {
            linkCount++;
        }
        linkCount += iNodeLinkReverseDao.getCountByNode(temp.getOwnedBy(), temp.getId(), null);
        
        temp.setLinkCount(linkCount);
    }
    
    private void transForModifyLinkRole(UserToken user, INode node, INodeLink link, String role)
        throws BaseRunException
    {
        INodeACL iNodeACL = nodeACLService.getByResourceAndUser(node.getOwnedBy(),
            node.getId(),
            link.getId(),
            INodeACL.TYPE_LINK);
            
        if (StringUtils.isBlank(role))
        {
            if (iNodeACL != null)
            {
                link.setRole(iNodeACL.getResourceRole());
            }
            
            return;
        }
        
        link.setRole(role);
        if (iNodeACL == null)
        {
            addNodeLinkACL(user, node, link.getId(), role);
        }
        else if (!role.equals(iNodeACL.getResourceRole()))
        {
            iNodeACL.setModifiedAt(new Date());
            iNodeACL.setModifiedBy(user.getId());
            
            iNodeACL.setResourceRole(role);
            nodeACLService.modifyINodeACLById(iNodeACL);
        }
        else
        {
            logger.info("role not change, no need to update acL");
        }
        
    }
    
    private void updateDynamicAttr(INodeLink link)
    {
        if (link.getIdentities() != null)
        {
            iNodeLinkDynamicDao.deleteAll(link.getId());
            
            INodeLinkDynamic iNodeLinkDynamic;
            Date date = new Date();
            for (LinkIdentityInfo identity : link.getIdentities())
            {
                iNodeLinkDynamic = new INodeLinkDynamic();
                iNodeLinkDynamic.setId(link.getId());
                iNodeLinkDynamic.setIdentity(identity.getIdentity());
                iNodeLinkDynamic.setCreatedAt(date);
                iNodeLinkDynamicDao.create(iNodeLinkDynamic);
            }
        }
    }
    
    private void updateNodeLinkCodeForDelete(UserToken userInfo, INode iNode, String type, String linkCode)
    {
        boolean hasOldLink = !INode.LINKCODE_NEW_SET.equals(iNode.getLinkCode())
            && StringUtils.isNotBlank(iNode.getLinkCode());
            
        // 更新节点链接状态
        int totalForNew = iNodeLinkReverseDao.getCountByNode(iNode.getOwnedBy(), iNode.getId(), null);
        if (totalForNew > 0)
        {
            if (StringUtils.isNotBlank(linkCode) && StringUtils.equals(linkCode, iNode.getLinkCode()))
            {
                folderService.updateNodeLinkCode(userInfo, iNode, INode.LINKCODE_NEW_SET);
            }
            else if (LinkAccessCodeMode.TYPE_STATIC_STRING.equals(type))
            {
                folderService.updateNodeLinkCode(userInfo, iNode, INode.LINKCODE_NEW_SET);
            }
        }
        else
        {
            if (StringUtils.isNotBlank(linkCode) && StringUtils.equals(linkCode, iNode.getLinkCode()))
            {
                folderService.updateNodeLinkCode(userInfo, iNode, null);
            }
            else if (LinkAccessCodeMode.TYPE_STATIC_STRING.equals(type))
            {
                folderService.updateNodeLinkCode(userInfo, iNode, null);
            }
            else if (StringUtils.isBlank(type) && !hasOldLink)
            {
                folderService.updateNodeLinkCode(userInfo, iNode, null);
            }
            
        }
    }
    
    private void updateNodeLinkCodeForLinkExpired(UserToken userInfo, INode iNode, String linkCode)
    {
        boolean hasOldLink = !INode.LINKCODE_NEW_SET.equals(iNode.getLinkCode())
            && StringUtils.isNotBlank(iNode.getLinkCode());
            
        // 更新节点链接状态
        int totalForNew = iNodeLinkReverseDao.getCountByNode(iNode.getOwnedBy(), iNode.getId(), null);
        if (hasOldLink)
        {
            if (StringUtils.equals(linkCode, iNode.getLinkCode()))
            {
                if (totalForNew > 0)
                {
                    folderService.updateNodeLinkCode(userInfo, iNode, INode.LINKCODE_NEW_SET);
                }
                else
                {
                    folderService.updateNodeLinkCode(userInfo, iNode, null);
                }
            }
        }
        else
        {
            if (totalForNew > 0)
            {
                folderService.updateNodeLinkCode(userInfo, iNode, INode.LINKCODE_NEW_SET);
            }
            else
            {
                folderService.updateNodeLinkCode(userInfo, iNode, null);
            }
        }
        
    }
    
    private void validAccessCode(UserToken access, boolean valiAccessCode, INodeLink dbLink, INode iNodeRoot)
    {
        List<INodeLink> list = iNodeLinkReverseDao.listByNode(iNodeRoot.getOwnedBy(),
            iNodeRoot.getId(),
            null);
            
        boolean isExist = false;
        
        if (StringUtils.equals(access.getLinkCode(), iNodeRoot.getLinkCode()))
        {
            isExist = true;
        }
        else
        {
            for (INodeLink item : list)
            {
                if (StringUtils.equals(access.getLinkCode(), item.getId()))
                {
                    isExist = true;
                    break;
                }
            }
        }
        
        if (!isExist)
        {
            throw new ForbiddenException(
                "the given linkCode is not equal with the linkCode set in iNodeRoot, linkCode in iNodeRoot :"
                    + iNodeRoot.getLinkCode() + ", linkCode:" + access.getLinkCode());
        }
        
        // 判断提取码是否正确
        if (valiAccessCode)
        {
            if (dbLink.getStatus() == LinkAccessCodeMode.TYPE_STATIC_VALUE)
            {
                validStaticAccessCode(access, dbLink);
            }
            else
            {
                validDynamicAccessCode(access);
            }
            
        }
    }
    
    private void validDynamicAccessCode(UserToken access)
    {
        String encode = access.getPlainAccessCode();
        INodeLinkDynamic iNodeLinkDynamic = new INodeLinkDynamic();
        iNodeLinkDynamic.setId(access.getLinkCode());
        List<INodeLinkDynamic> dynamicList = iNodeLinkDynamicDao.list(iNodeLinkDynamic);
        boolean isValid = false;
        String itemCode = null;
        for (INodeLinkDynamic item : dynamicList)
        {
            itemCode = getDecryptedAccessCode(item);
            if (StringUtils.equals(encode, itemCode))
            {
                isValid = true;
                break;
            }
        }
        
        if (!isValid)
        {
            throw new ForbiddenException("Bad plainAccess code");
        }
    }
    
    private String getDecryptedAccessCode(INodeLinkDynamic iNodeLinkDynamic)
    {
        if (StringUtils.isNotEmpty(iNodeLinkDynamic.getEncryptedPassword()))
        {
            return EDToolsEnhance.decode(iNodeLinkDynamic.getEncryptedPassword(),
                iNodeLinkDynamic.getPasswordKey());
        }
        if (StringUtils.isNotEmpty(iNodeLinkDynamic.getPassword()))
        {
            String accessCode = EDTools.decode(iNodeLinkDynamic.getPassword());
            Map<String, String> encodedKeys = EDToolsEnhance.encode(accessCode);
            iNodeLinkDynamic.setEncryptedPassword(encodedKeys.get(EDToolsEnhance.ENCRYPT_CONTENT));
            iNodeLinkDynamic.setPasswordKey(encodedKeys.get(EDToolsEnhance.ENCRYPT_KEY));
            iNodeLinkDynamic.setPassword(null);
            iNodeLinkDynamicDao.upgradePassword(iNodeLinkDynamic);
            return accessCode;
        }
        return null;
    }
    
    private void validStaticAccessCode(UserToken access, INodeLink dbLink)
    {
        if (StringUtils.isNotEmpty(dbLink.getPlainAccessCode())
            && !dbLink.getPlainAccessCode().equals(access.getPlainAccessCode()))
        {
            throw new ForbiddenException("Bad plainAccess code");
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

	@Override
	public void updateLinkStatus(UserToken user, INode iNode, INodeLink iNodeLink) {
		// TODO Auto-generated method stub
		 INodeLink link = checkAndGetLinkById(iNodeLink.getId());
	        
	        if (link.getOwnedBy() != iNode.getOwnedBy() || link.getiNodeId() != iNode.getId())
	        {
	            throw new ForbiddenException();
	        }
	        
	        // 需要对提取码进行加密
	        iNodeLink.setPlainAccessCode(StringUtils.trimToNull(iNodeLink.getPlainAccessCode()));
	        link.setPlainAccessCode(iNodeLink.getPlainAccessCode());
	        encodeLinkPassword(iNodeLink, link);
	        
	        link.setAccess(iNodeLink.getAccess());
	        
	        link.setRole(checkAndGetRoleValid(iNodeLink.getRole()));
	        
	        // 设置外链失效时间，并判断时间有效性
	        checkAndSetLinkDate(link, iNodeLink);
	        
	        link.setModifiedAt(new Date());
	        link.setModifiedBy(user.getId());
	        link.setStatus(iNodeLink.getStatus());
	        link.setIdentities(iNodeLink.getIdentities());
	        link.setNeedLogin(iNodeLink.isNeedLogin());
	        transForModifyLinkRole(user, iNode, link, iNodeLink.getRole());
	        iNodeLinkReverseDao.updateV2(link);
	        
	        if (link.getStatus() != LinkAccessCodeMode.TYPE_STATIC_VALUE)
	        {
	            updateDynamicAttr(link);
	        }
	        
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
	}

	@Override
	public List<String> listAllLinkCodes(UserToken user) {
    	String enterpriseId = "";
		if(user.getAccountVistor()!=null){
			enterpriseId = String.valueOf(user.getAccountVistor().getEnterpriseId());
		}
		List<String> allLinkCodes = iNodeLinkReverseDao.listAllLinkCodes(user.getId());
        return allLinkCodes;
    }
    
}
