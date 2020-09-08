package pw.cdmi.box.disk.favorite.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.FavoriteClient;
import pw.cdmi.box.disk.favorite.common.CreateRoot;
import pw.cdmi.box.disk.favorite.common.FavoriteType;
import pw.cdmi.box.disk.favorite.common.SpellURL;
import pw.cdmi.box.disk.favorite.domain.FavoriteNode;
import pw.cdmi.box.disk.favorite.domain.FavoriteNodeCreateRequest;
import pw.cdmi.box.disk.favorite.domain.FavoriteTreeNode;
import pw.cdmi.box.disk.favorite.domain.Param.Name;
import pw.cdmi.box.disk.favorite.service.FavoriteNodeService;
import pw.cdmi.box.disk.files.service.FileService;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;

@Service("favoriteNodeService")
@Lazy(true)
public class FavoriteNodeServiceImpl implements FavoriteNodeService
{
    private FavoriteClient favoriteClient;
    
    @Autowired
    @Qualifier("messageSource")
    private ResourceBundleMessageSource messageSource;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private FileService fileService;
    
    private static final int INITIAL_SIZE = 10;
    
    @Override
    @SuppressWarnings("unchecked")
    public List<FavoriteTreeNode> getFavoriteNodeList(Locale locale, Long id) throws RestException
    {
        List<FavoriteNode> list = favoriteClient.listFavoriteTreeNode(id);
        
        if (CollectionUtils.isNotEmpty(list))
        {
            FavoriteTreeNode treeNode = null;
            List<FavoriteTreeNode> treeNodes = new ArrayList<FavoriteTreeNode>(list.size());
            for (FavoriteNode node : list)
            {
                treeNode = new FavoriteTreeNode(node);
                treeNode.setExtraAttr(getTip(locale, treeNode));
                treeNode.setName(getInternationName(locale, treeNode));
                
                treeNode.setLinkCode(HtmlUtils.htmlEscape(treeNode.getLinkCode()));
                treeNode.setNodeName(HtmlUtils.htmlEscape(treeNode.getNodeName()));
                treeNode.setNodeType(HtmlUtils.htmlEscape(treeNode.getNodeType()));
                treeNode.setType(HtmlUtils.htmlEscape(treeNode.getType()));
                
                treeNodes.add(treeNode);
            }
            return treeNodes;
        }
        return Collections.EMPTY_LIST;
    }
    
    @SuppressWarnings("unchecked")
    public List<FavoriteTreeNode> getFavoriteNodeListForOld(Long id) throws RestException
    {
        List<FavoriteNode> list = favoriteClient.listFavoriteTreeNode(id);
        
        if (list != null && !list.isEmpty())
        {
            FavoriteTreeNode treeNode = null;
            List<FavoriteTreeNode> treeNodes = new ArrayList<FavoriteTreeNode>(list.size());
            for (FavoriteNode node : list)
            {
                treeNode = new FavoriteTreeNode(node);
                treeNodes.add(treeNode);
            }
            return treeNodes;
        }
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public void create(FavoriteNodeCreateRequest favoriteNode, Long userid)
    {
        Long parentId = getParentId(favoriteNode, userid);
        favoriteNode.setParentId(parentId);
        favoriteClient.createFavoriteNode(favoriteNode, userid);
    }
    
    private Long getParentId(FavoriteNodeCreateRequest favoriteNode, Long userid)
    {
        List<FavoriteTreeNode> list = getFavoriteNodeListForOld(FavoriteNode.TREE_ROOT_ID);
        if (null == list || list.isEmpty())
        {
            List<FavoriteNodeCreateRequest> roots = CreateRoot.getFavoriteNodeCreateRequest(userid);
            for (FavoriteNodeCreateRequest favoriteNodeCreateRequest : roots)
            {
                favoriteClient.createFavoriteNode(favoriteNodeCreateRequest, userid);
            }
            list = getFavoriteNodeListForOld(FavoriteNode.TREE_ROOT_ID);
        }
        Long parentId = CreateRoot.getId(list, favoriteNode);
        if (parentId == null)
        {
            String name = CreateRoot.typeToName(favoriteNode.getType());
            FavoriteNodeCreateRequest root = CreateRoot.getRootNode(userid, name);
            favoriteClient.createFavoriteNode(root, userid);
            list = getFavoriteNodeListForOld(FavoriteNode.TREE_ROOT_ID);
            parentId = CreateRoot.getId(list, favoriteNode);
        }
        return parentId;
    }
    
    @Override
    public boolean deleteTreeNode(Long id)
    {
        return favoriteClient.deleteFavoriteTreeNode(id);
    }
    
    @Override
    public String getParentUrl(UserToken user, FavoriteTreeNode node)
    {
        String url = SpellURL.getURL(node.getType(),
            node.getOwnedBy(),
            fileService.getFileInfo(user, node.getOwnedBy(), node.getiNodeId()).getParent(),
            null,
            node.getNodeType());
        return url;
        
    }
    
    @Override
    public String locationUrL(FavoriteTreeNode node)
    {
        String url = SpellURL.getURL(node.getType(),
            node.getOwnedBy(),
            node.getiNodeId(),
            node.getLinkCode(),
            node.getNodeType());
        return url;
    }
    
    public String getTip(Locale locale, FavoriteTreeNode node)
    {
        if (node == null)
        {
            return null;
        }
        Map<String, String> params = node.getParams();
        if (null == params)
        {
            params = new HashMap<String, String>(INITIAL_SIZE);
        }
        StringBuffer sb = new StringBuffer();
        if (node.getType().equals(FavoriteNode.MYSPACE))
        {
            sb.append(SpellURL.SEPARATOR);
            sb.append(getText(locale, FavoriteType.MYFILETIP, null));
            sb.append(SpellURL.SEPARATOR);
            sb.append(getFilePath(params));
        }
        else if (node.getType().equals(FavoriteNode.TEAMSPACE))
        {
            sb.append(SpellURL.SEPARATOR);
            String[] s = new String[]{params.get(Name.TEAMSPACENAME.getName())};
            sb.append(getText(locale, FavoriteType.TEAMSPACETIP, s));
            sb.append(SpellURL.SEPARATOR);
            sb.append(getFilePath(params));
        }
        else if (node.getType().equals(FavoriteNode.SHARE))
        {
            sb.append(SpellURL.SEPARATOR);
            String[] s = new String[]{params.get(Name.SENDER.getName())};
            sb.append(getText(locale, FavoriteType.SHARETIP, s));
            sb.append(SpellURL.SEPARATOR);
            sb.append(params.get(Name.ORGINNAME.getName()));
        }
        else if (node.getType().equals(FavoriteNode.LINK))
        {
            sb.append(SpellURL.SEPARATOR);
            String[] s = new String[]{params.get(Name.SENDER.getName())};
            sb.append(getText(locale, FavoriteType.LINKTIP, s));
            sb.append(SpellURL.SEPARATOR);
            sb.append(params.get(Name.ORGINNAME.getName()));
        }
        return sb.toString();
    }
    
    private String getFilePath(Map<String, String> params)
    {
        String path = params.get(Name.PATH.getName());
        if (StringUtils.isNotBlank(path))
        {
            return path;
        }
        return params.get(Name.ORGINNAME.getName());
    }
    
    public String getInternationName(Locale locale, FavoriteTreeNode node)
    {
        if (node != null && node.getName() != null)
        {
            return messageSource.getMessage(node.getName(), null, locale);
        }
        return null;
    }
    
    public String getText(Locale locale, FavoriteType favoriteType, String[] params)
    {
        if (favoriteType != null && favoriteType.getName() != null)
        {
            return messageSource.getMessage(favoriteType.getName(), params, locale);
        }
        return null;
    }
    
    @PostConstruct
    void init()
    {
        this.favoriteClient = new FavoriteClient(ufmClientService);
    }
    
}
