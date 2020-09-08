package com.huawei.sharedrive.app.favorite.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.exception.NoSuchParentException;
import com.huawei.sharedrive.app.exception.SameFavoriteNodeConflictException;
import com.huawei.sharedrive.app.favorite.dao.FavoriteNodeDao;
import com.huawei.sharedrive.app.favorite.domain.FavoriteNode;
import com.huawei.sharedrive.app.favorite.domain.Node;
import com.huawei.sharedrive.app.favorite.service.FavoriteIdGenerateService;
import com.huawei.sharedrive.app.favorite.service.FavoriteNodeService;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.node.favor.FavoriteNodeList;
import com.huawei.sharedrive.app.openapi.domain.node.favor.FavoriteNodeResponse;
import com.huawei.sharedrive.app.openapi.domain.node.favor.Param;
import com.huawei.sharedrive.app.openapi.domain.node.favor.Param.Name;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.service.LinkService;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.utils.JsonUtils;

@Service(value = "FavoriteNodeService")
public class FavoriteNodeServiceImpl implements FavoriteNodeService
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteNodeServiceImpl.class);
    
    @Autowired
    private LinkService linkService;
    
    @Autowired
    private FavoriteNodeDao favoriteNodeDao;
    
    @Autowired
    private TeamSpaceDAO teamSpaceDAO;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private UserDAOV2 userDAO;
    
    @Autowired
    private FavoriteIdGenerateService favoriteIdGenerateService;
    
    @Override
    public FavoriteNode createFavoriteNode(FavoriteNode favoriteNode, String linkCode)
        throws BaseRunException
    {
        initFavoriteNode(favoriteNode, linkCode);
        FavoriteNode fNode = favoriteNodeDao.getFavoriteNodeBy(favoriteNode);
        
        if (null != fNode)
        {
            throw new SameFavoriteNodeConflictException(
                "this favorite node is already exits and in db id is " + fNode.getId());
        }
        if (!(favoriteNode.getParent()).equals(FavoriteNode.TREE_ROOT_ID))
        {
            FavoriteNode tempNode = new FavoriteNode();
            tempNode.setOwnedBy(favoriteNode.getOwnedBy());
            tempNode.setId(favoriteNode.getParent());
            FavoriteNode parentNode = favoriteNodeDao.getFavoriteNodeBy(tempNode);
            if (null == parentNode)
            {
                throw new NoSuchParentException("Favorite node parent is not exits; parent id is "
                    + favoriteNode.getParent());
            }
            if (parentNode.getType() != (FavoriteNode.typeStringToInt(FavoriteNode.CONTAINOR)))
            {
                throw new NoSuchParentException("Favorite node parent type lagel, parent id is "
                    + favoriteNode.getParent() + "type is  " + parentNode.getType());
            }
        }
        favoriteNode.setId(favoriteIdGenerateService.getNextId(favoriteNode.getOwnedBy()));
        favoriteNodeDao.create(favoriteNode);
        return favoriteNode;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteFavoriteNode(Long id, Long userId) throws BaseRunException
    {
        FavoriteNode favoriteNode = favoriteNodeDao.getFavortieNode(id, userId);
        if (favoriteNode == null)
        {
            throw new NoSuchItemsException("no such FavoriteNode id is " + id);
        }
        if (!favoriteNode.getOwnedBy().equals(userId))
        {
            throw new ForbiddenException("userId  and ownedBy not equale :" + "userid is " + userId
                + ", ownedBy is " + favoriteNode.getOwnedBy());
        }
        deleteChridren(favoriteNode);
        favoriteNodeDao.delete(favoriteNode);
        return true;
    }
    
    public void deleteChridren(FavoriteNode favoriteNode) throws BaseRunException
    {
        List<FavoriteNode> nodes = favoriteNodeDao.getFavoriteNodeByParent(favoriteNode, null, null, null);
        if (nodes == null || nodes.isEmpty())
        {
            return;
        }
        for (FavoriteNode node : nodes)
        {
            deleteChridren(node);
            favoriteNodeDao.delete(node);
        }
    }
    
    @Override
    public FavoriteNodeList listNodesbyParent(FavoriteNode filter, List<Order> orderList, Limit limit,
        String keyword) throws BaseRunException
    {
        if (null == filter)
        {
            LOGGER.error(" node is null");
            throw new InvalidParameterException("favorite node id and user id can not be null");
        }
        if (!filter.getId().equals(FavoriteNode.TREE_ROOT_ID))
        {
            FavoriteNode node = favoriteNodeDao.getFavoriteNodeBy(filter);
            if (null == node)
            {
                throw new NoSuchParentException("No parent power");
            }
        }
        
        int total = favoriteNodeDao.getSubFavortieNodeCount(filter, keyword);
        
        FavoriteNodeList favoriteNodeLists = new FavoriteNodeList();
        favoriteNodeLists.setTotalCount(total);
        favoriteNodeLists.setLimit(limit.getLength());
        favoriteNodeLists.setOffset(limit.getOffset());
        
        List<FavoriteNode> favoriteNodes = favoriteNodeDao.getFavoriteNodeByParent(filter,
            orderList,
            limit,
            keyword);
        int len = favoriteNodes.size();
        List<FavoriteNodeResponse> responses = new ArrayList<FavoriteNodeResponse>(len);
        FavoriteNodeResponse response = null;
        for (int i = 0; i < len; i++)
        {
            response = new FavoriteNodeResponse(favoriteNodes.get(i));
            responses.add(response);
        }
        favoriteNodeLists.setContents(responses);
        
        return favoriteNodeLists;
        
    }
    
    @Override
    public FavoriteNode getFavoriteNode(Long userId, Long id) throws BaseRunException
    {
        if (null == userId || null == id)
        {
            LOGGER.error(" userId  or  id is null");
            throw new InvalidParameterException("favorite node id and user id can not be null");
        }
        FavoriteNode favoriteNode = favoriteNodeDao.getFavortieNode(id, userId);
        if (favoriteNode == null)
        {
            throw new NoSuchItemsException("no such FavoriteNode id is " + id);
        }
        if (!favoriteNode.getOwnedBy().equals(userId))
        {
            throw new ForbiddenException("userId not equal!");
        }
        return favoriteNode;
    }
    
    private void initFavoriteNode(FavoriteNode favoriteNode, String linkCode) throws BaseRunException
    {
        Node node = favoriteNode.getNode();
        if (null != node && (favoriteNode.getType() != FavoriteNode.typeStringToInt(FavoriteNode.CONTAINOR)))
        {
            LOGGER.info("node.getOwnedBy():[" + node.getOwnedBy() + "]node.getId(): " + node.getId());
            INode iNode = iNodeDAO.get(node.getOwnedBy(), node.getId());
            if (iNode == null)
            {
                throw new InvalidParamException("INode can not be null");
            }
            
            checkNode(favoriteNode, iNode);
            node.setType(iNode.getType());
            favoriteNode.setNode(node);
            Param param = new Param(Name.ORGINNAME.getName(), iNode.getName());
            List<Param> params = new ArrayList<Param>(3);
            params.add(param);
            favoriteNode.setParams(initParams(favoriteNode, params, linkCode));
        }
        Date date = new Date();
        favoriteNode.setCreatedAt(date);
        favoriteNode.setModifiedAt(date);
    }
    
    private void checkNode(FavoriteNode favoriteNode, INode iNode)
    {
        Node node = favoriteNode.getNode();
        if (iNode == null || iNode.getStatus() == INode.STATUS_DELETE
            || iNode.getStatus() == INode.STATUS_TRASH || iNode.getStatus() == INode.STATUS_TRASH_DELETE)
        {
            LOGGER.error("no such iNode; id is " + node.getId());
            throw new NoSuchItemsException("no such iNode; id is " + node.getId());
        }
        
        if (FilesCommonUtils.isBackupFolderType(iNode.getType()))
        {
            throw new ForbiddenException("Backup FolderType is prohibited, type:" + iNode.getType());
        }
    }
    
    private String initParams(FavoriteNode favoriteNode, List<Param> params, String linkCode)
        throws BaseRunException
    {
        if (null == favoriteNode)
        {
            return null;
        }
        String type = FavoriteNode.typeIntToString(favoriteNode.getType());
        if (type.equals(FavoriteNode.MYSPACE))
        {
            params.add(new Param(Name.PATH.getName(), getFullPath(favoriteNode)));
        }
        else if (type.equals(FavoriteNode.TEAMSPACE))
        {
            TeamSpace space = teamSpaceDAO.get(favoriteNode.getNode().getOwnedBy());
            if (space == null)
            {
                throw new NoSuchItemsException("no such teamSpace " + favoriteNode.getOwnedBy());
            }
            params.add(new Param(Name.TEAMSPACENAME.getName(), space.getName()));
            params.add(new Param(Name.PATH.getName(), getFullPath(favoriteNode)));
        }
        else if (type.equals(FavoriteNode.SHARE))
        {
            User user = userDAO.get(favoriteNode.getNode().getOwnedBy());
            if (null == user)
            {
                throw new NoSuchItemsException("no such scender  " + favoriteNode.getOwnedBy());
            }
            params.add(new Param(Name.SENDER.getName(), user.getName()));
        }
        else if (type.equals(FavoriteNode.LINK))
        {
            params.add(new Param(Name.LINKCODE.getName(), linkCode));
            INodeLink iLink = linkService.getLinkByLinkCodeForClientV2(linkCode);
            if (null == iLink)
            {
                throw new NoSuchItemsException("no such iLink  ");
            }
            User user = userDAO.get(iLink.getCreatedBy());
            if (null == user)
            {
                throw new NoSuchItemsException("no such scender  " + favoriteNode.getOwnedBy());
            }
            
            params.add(new Param(Name.SENDER.getName(), user.getName()));
        }
        
        return JsonUtils.toJson(params);
    }
    
    public String getFullPath(FavoriteNode favoriteNode)
    {
        if (favoriteNode == null || favoriteNode.getNode() == null
            || favoriteNode.getNode().getOwnedBy() == null || favoriteNode.getNode().getId() == null)
        {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        INode iNode = iNodeDAO.get(favoriteNode.getNode().getOwnedBy(), favoriteNode.getNode().getId());
        while (iNode != null)
        {
            sb.insert(0, iNode.getName());
            sb.insert(0, "/");
            if (iNode.getParentId() == 0)
            {
                break;
            }
            iNode = iNodeDAO.get(favoriteNode.getOwnedBy(), iNode.getParentId());
        }
        if (sb.length() > 0)
        {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }
}
