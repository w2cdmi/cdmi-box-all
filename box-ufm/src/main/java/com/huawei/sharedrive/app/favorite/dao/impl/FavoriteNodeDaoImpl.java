package com.huawei.sharedrive.app.favorite.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.favorite.dao.FavoriteNodeDao;
import com.huawei.sharedrive.app.favorite.domain.FavoriteNode;
import com.huawei.sharedrive.app.plugins.preview.util.PreviewFileUtil;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service("favoriteNodeDao")
@SuppressWarnings("deprecation")
public class FavoriteNodeDaoImpl extends AbstractDAOImpl implements FavoriteNodeDao
{
    public final static Logger LOGGER = LoggerFactory.getLogger(FavoriteNodeDaoImpl.class);
    
    private static final long BASE_NODE_ID = 1;
    
    @Autowired
    private PreviewFileUtil previewFileUtil;
    
    @SuppressWarnings("unchecked")
    @Override
    public List<FavoriteNode> getFavoriteNodeFilterd(FavoriteNode filter, List<Order> orderList, Limit limit)
    {
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", getOrderByStr(orderList));
        map.put("limit", limit);
        List<FavoriteNode> list = sqlMapClientTemplate.queryForList("FavoriteNode.getFilterd", map);
        for (FavoriteNode node : list)
        {
            node.setPreviewable(previewFileUtil.isPreviewable(node));
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<FavoriteNode> getFavoriteNodeByParent(FavoriteNode filter, List<Order> orderList,
        Limit limit, String keyword)
    {
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        filter.setName(keyword);
        map.put("filter", filter);
        map.put("orderBy", getOrderByStr(orderList));
        map.put("limit", limit);
        List<FavoriteNode> list = sqlMapClientTemplate.queryForList("FavoriteNode.getbyparent", map);
        for (FavoriteNode node : list)
        {
            node.setPreviewable(previewFileUtil.isPreviewable(node));
        }
        return list;
    }
    
    private String getOrderByStr(List<Order> orderList)
    {
        if (null == orderList)
        {
            return null;
        }
        StringBuffer orderBy = new StringBuffer();
        String field = null;
        for (Order order : orderList)
        {
            field = order.getField();
            
            // 解决中文名称排序问题
            if ("name".equalsIgnoreCase(field))
            {
                field = "convert(name using gb2312)";
            }
            orderBy.append(field).append(' ').append(order.getDirection()).append(',');
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
    
    @Override
    public int delete(FavoriteNode node)
    {
        return sqlMapClientTemplate.delete("FavoriteNode.delete", node);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long create(FavoriteNode node)
    {
        return (Long) sqlMapClientTemplate.insert("FavoriteNode.insert", node);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public FavoriteNode getFavoriteNodeBy(FavoriteNode favoriteNode)
    {
        List<FavoriteNode> favoriteNodes = sqlMapClientTemplate.queryForList("FavoriteNode.getFavoriteNodeBy",
            favoriteNode);
        if (favoriteNodes.size() > 1)
        {
            LOGGER.debug(favoriteNode.toString());
            for (FavoriteNode favoriteNode2 : favoriteNodes)
            {
                LOGGER.debug(favoriteNode2.toString());
            }
            throw new InternalServerErrorException("DB server has many result  exception");
        }
        else if (favoriteNodes.size() == 1)
        {
            for (FavoriteNode node : favoriteNodes)
            {
                node.setPreviewable(previewFileUtil.isPreviewable(node));
            }
            return favoriteNodes.get(0);
        }
        return null;
        
    }
    
    @Override
    public int update(FavoriteNode node)
    {
        return sqlMapClientTemplate.update("FavoriteNode.update", node);
    }
    
    @Override
    public int getSubFavortieNodeCount(FavoriteNode node, String keyword)
    {
        node.setName(keyword);
        return (int) sqlMapClientTemplate.queryForObject("FavoriteNode.getSubCount", node);
    }
    
    @Override
    public FavoriteNode getFavortieNode(Long id, Long ownedBy)
    {
        FavoriteNode node = new FavoriteNode();
        node.setId(id);
        node.setOwnedBy(ownedBy);
        node = (FavoriteNode) sqlMapClientTemplate.queryForObject("FavoriteNode.get", node);
        if (node != null)
        {
            node.setPreviewable(previewFileUtil.isPreviewable(node));
        }
        return node;
    }
    
    @Override
    public long getMaxId(long ownedBy)
    {
        FavoriteNode node = new FavoriteNode();
        node.setOwnedBy(ownedBy);
        
        Object maxId = sqlMapClientTemplate.queryForObject("FavoriteNode.getMaxId", node);
        if (maxId == null)
        {
            return BASE_NODE_ID;
        }
        return (Long) maxId;
    }
}
