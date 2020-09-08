package com.huawei.sharedrive.app.openapi.restv2.favorite;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchLinkException;
import com.huawei.sharedrive.app.favorite.domain.FavoriteNode;
import com.huawei.sharedrive.app.favorite.service.FavoriteNodeService;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.NodeCreateRequest;
import com.huawei.sharedrive.app.openapi.domain.node.favor.FavoriteNodeCreateRequest;
import com.huawei.sharedrive.app.openapi.domain.node.favor.FavoriteNodeList;
import com.huawei.sharedrive.app.openapi.domain.node.favor.FavoriteNodeResponse;
import com.huawei.sharedrive.app.openapi.domain.node.favor.ListFavoriteRequest;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.service.LinkServiceV2;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.utils.JsonUtils;


@SuppressWarnings("rawtypes")
@Controller
@RequestMapping(value = "/api/v2/favorites")
public class FavoriteApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteApi.class);
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private LinkServiceV2 linkServiceV2;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FavoriteNodeService favoriteNodeService;
    
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addFavoriteNode(@RequestBody FavoriteNodeCreateRequest theRequest,
        @RequestHeader("Authorization") String authorization, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        theRequest.checkParamter();
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        userToken = userTokenHelper.checkTokenAndGetUserForV2(authorization, headerCustomMap);
        userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
        String[] description = new String[]{String.valueOf(theRequest.getLinkCode())};
        FavoriteNodeResponse nodeResponse = null;
        if (theRequest.getType().equals(FavoriteNode.LINK))
        {
            try
            {
                INodeLink iLink = linkServiceV2.getLinkByLinkCodeForClient(theRequest.getLinkCode());
                if (null == iLink)
                {
                    throw new InvalidParamException("INodeLink can not be null");
                }
                NodeCreateRequest node = new NodeCreateRequest(iLink.getOwnedBy(), iLink.getiNodeId());
                theRequest.setNode(node);
            }
            catch (NoSuchLinkException e)
            {
                LOGGER.warn("Linkcode[" + theRequest.getLinkCode() + "]Link not exist:", e);
                throw new NoSuchLinkException("link does not exist", e);
            }
            catch (Exception e)
            {
                
                LOGGER.warn("Linkcode[" + theRequest.getLinkCode() + "]Link not exist:", e);
                throw new InvalidParamException("iLink not exist", e);
            }
        }
        try
        {
            FavoriteNode favoriteNode = theRequest.builderFavoriteNode();
            favoriteNode.setOwnedBy(userToken.getId());
            FavoriteNode node = favoriteNodeService.createFavoriteNode(favoriteNode, theRequest.getLinkCode());
            nodeResponse = new FavoriteNodeResponse(node);
        }
        catch (RuntimeException e)
        {
            // TODO Auto-generated catch block
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.ADD_FAVORITE_NODE_ERR,
                description,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.ADD_FAVORITE_NODE,
            description,
            null);
        return new ResponseEntity<String>(JsonUtils.toJson(nodeResponse), HttpStatus.CREATED);
        
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteFavoriteNode(@PathVariable Long id,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        FilesCommonUtils.checkNonNegativeIntegers(id);
        
        // Token 验证
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
        
        userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
        String[] description = new String[]{String.valueOf(id)};
        try
        {
            favoriteNodeService.deleteFavoriteNode(id, userToken.getId());
        }
        catch (RuntimeException e)
        {
            LOGGER.error("delete failed:" + id);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.DELETE_FAVORITE_NODE_ERR,
                description,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.DELETE_FAVORITE_NODE,
            description,
            null);
        return new ResponseEntity(HttpStatus.OK);
        
    }
    
    @RequestMapping(value = "/{id}/items", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listFavorite(@PathVariable Long id,
        @RequestBody(required = false) ListFavoriteRequest listFavoriteRequest,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        FilesCommonUtils.checkNonNegativeIntegers(id);
        
        // Token 验证
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
        
        userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
        
        if (null != listFavoriteRequest)
        {
            listFavoriteRequest.checkParameter();
        }
        else
        {
            listFavoriteRequest = new ListFavoriteRequest();
        }
        List<Order> orderList = listFavoriteRequest.getOrder();
        Integer length = listFavoriteRequest.getLimit();
        Long offset = listFavoriteRequest.getOffset();
        String keyword = listFavoriteRequest.getKeyword();
        FavoriteNode filter = new FavoriteNode();
        filter.setOwnedBy(userToken.getId());
        filter.setId(id);
        Limit limit = new Limit(offset, length);
        FavoriteNodeList favoriteNodeList;
        try
        {
            favoriteNodeList = favoriteNodeService.listNodesbyParent(filter, orderList, limit, keyword);
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.DELETE_FAVORITE_NODE_ERR,
                null,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.DELETE_FAVORITE_NODE,
            null,
            null);
        return new ResponseEntity<String>(JsonUtils.toJson(favoriteNodeList), HttpStatus.OK);
        
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getFavorite(@PathVariable Long id,
    
    @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        FilesCommonUtils.checkNonNegativeIntegers(id);
        
        // Token 验证
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
        
        userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getId());
        String[] description = {String.valueOf(userToken.getId()), String.valueOf(id)};
        FavoriteNode favoriteNode = null;
        try
        {
            favoriteNode = favoriteNodeService.getFavoriteNode(userToken.getId(), id);
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_FAVORITE_NODE_ERR,
                description,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_FAVORITE_NODE,
            description,
            null);
        FavoriteNodeResponse reNodeResponse = new FavoriteNodeResponse(favoriteNode);
        return new ResponseEntity<String>(JsonUtils.toJson(reNodeResponse), HttpStatus.OK);
        
    }
    
}
