/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 */
package com.huawei.sharedrive.app.share.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huawei.sharedrive.app.plugins.preview.util.PreviewFileUtil;
import com.huawei.sharedrive.app.share.dao.ShareDAO;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 共享者DAO实现类
 * 
 * @author l90003768
 * 
 */
@Service("shareDAO")
@SuppressWarnings("all")
public class ShareDAOImpl extends AbstractDAOImpl implements ShareDAO
{
    static final int TABLE_COUNT = 100;
    
    @Autowired
    private PreviewFileUtil previewFileUtil;
    
    @Override
    public int deleteByInode(long ownerId,long createdBy, long inodeId)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setiNodeId(inodeId);
        iNodeShare.setOwnerId(ownerId);
        iNodeShare.setCreatedBy(createdBy);
        iNodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreatedBy(createdBy));
        return sqlMapClientTemplate.delete("INodeShare.deleteByInode", iNodeShare);
    }
    
    @Override
    public int deleteByInodeAndSharedUser(INodeShare iNodeShare)
    {
        iNodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreateBy(iNodeShare));
        return sqlMapClientTemplate.delete("INodeShare.deleteByInodeAndSharedUser", iNodeShare);
    }
    
    @Override
    public int deleteByCreated(long created)
    {
        INodeShare nodeShare = new INodeShare();
        nodeShare.setCreatedBy(created);
        nodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreateBy(nodeShare));
        return sqlMapClientTemplate.delete("INodeShare.deleteByOwner", nodeShare);
    }
    
    @Override
    public List<INodeShare> getAllByCreated(long createdBy)
    {
        INodeShare nodeShare = new INodeShare();
        nodeShare.setOwnerId(createdBy);
        nodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByOwnerId(createdBy));
        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShare.listAllMyShares", nodeShare);
        for (INodeShare tmp : list)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return list;
    }
    

    
    @Override
    public Long getMySharesTotals(long createdBy,byte shareUserType, String transferStringForSql,String shareType)
    {
        INodeShare filter = new INodeShare();
        
        filter.setSharedUserType(shareUserType);
        filter.setShareType(shareType);
        filter.setCreatedBy(createdBy);
        filter.setTableSuffix(ShareTableCaculator.getTableSuffixByOwnerId(createdBy));
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", filter);
        map.put("searchName", transferStringForSql);
        return (Long) sqlMapClientTemplate.queryForObject("INodeShare.getCountMyShares", map);
    }
    
    @Override
    public int getShareCountForPage(INodeShare inodeShare)
    {
        inodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreatedBy(inodeShare.getCreatedBy()));
        return (Integer) sqlMapClientTemplate.queryForObject("INodeShare.getCountPageList", inodeShare);
    }
    
    @Override
    public int getShareCountForInode(long ownerId,long createdBy, long inodeId)
    {
        INodeShare inodeShare = new INodeShare();
        inodeShare.setOwnerId(ownerId);
        inodeShare.setiNodeId(inodeId);
        inodeShare.setCreatedBy(createdBy);
        inodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreatedBy(createdBy));
        return (Integer) sqlMapClientTemplate.queryForObject("INodeShare.getCountByNode", inodeShare);
    }
    
    @Override
    public List<INodeShare> getShareList(long ownerId,long createdBy ,long inodeId,String linkCode)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setiNodeId(inodeId);
        iNodeShare.setOwnerId(ownerId);
        iNodeShare.setCreatedBy(createdBy);
        if(linkCode!=null){
        	iNodeShare.setLinkCode(linkCode);
        }
        iNodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreateBy(iNodeShare));
        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShare.getsharelist", iNodeShare);
        for (INodeShare tmp : list)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return list;
    }
    
    @Override
    public List<INodeShare> getShareListIgnoreStatus(long ownerId,long createBy, long inodeId)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setiNodeId(inodeId);
        iNodeShare.setOwnerId(ownerId);
        iNodeShare.setCreatedBy(createBy);
        iNodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreatedBy(createBy));
        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShare.getsharelistIgnoreStatus",
            iNodeShare);
        for (INodeShare tmp : list)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<INodeShare> getSharePageList(INodeShare inodeShare, List<Order> orderList, Limit limit)
    {
        inodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreatedBy(inodeShare.getCreatedBy()));
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("inodeShare", inodeShare);
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        map.put("limit", limit);
        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShare.getPageList", map);
        for (INodeShare tmp : list)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return list;
    }
    
    @Override
    public void saveINodeShare(INodeShare nodeShare)
    {
        nodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreateBy(nodeShare));
        try {
        	 sqlMapClientTemplate.insert("INodeShare.insert", nodeShare);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
			// TODO: handle exception
		}
       
    }
    
    @Override
    public int updateNodeInfo(long ownedId,long createdBy, long inodeId, String newName, long size)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setiNodeId(inodeId);
        iNodeShare.setOwnerId(ownedId);
        iNodeShare.setName(newName);
        iNodeShare.setSize(size);
        iNodeShare.setCreatedBy(createdBy);
        iNodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreatedBy(createdBy));
        return sqlMapClientTemplate.delete("INodeShare.updateNameAndSize", iNodeShare);
        
    }
    
    @Override
    public int updateOwnerName(long ownerId,long createdBy, String newName)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setOwnerId(ownerId);
        iNodeShare.setOwnerName(newName);
        iNodeShare.setCreatedBy(createdBy);
        iNodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreatedBy(createdBy));
        return sqlMapClientTemplate.update("INodeShare.updateOwnerName", iNodeShare);
    }
    
    @Override
    public int updateStatus(long ownerId, long createdBy,long inodeId, byte status)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setiNodeId(inodeId);
        iNodeShare.setOwnerId(ownerId);
        iNodeShare.setStatus(status);
        iNodeShare.setCreatedBy(createdBy);
        iNodeShare.setTableSuffix(ShareTableCaculator.getTableSuffixByCreatedBy(createdBy));
        return sqlMapClientTemplate.update("INodeShare.updateStatus", iNodeShare);
    }
    
    /**
     * 获取共享资源V2
     */
    @Override
    public List<INodeShare> getListV2(long createdBy, byte sharedUserType, String searchName, String order,Limit limit,String shareType)
    {
        INodeShare filter = new INodeShare();
        filter.setCreatedBy(createdBy);
        filter.setTableSuffix(ShareTableCaculator.getTableSuffixByOwnerId(createdBy));
        filter.setSharedUserType(sharedUserType);
        filter.setShareType(shareType);
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("searchName", searchName);
        map.put("order", order);
        map.put("limit", limit);
        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShare.listMyShares", map);
        for (INodeShare tmp : list)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return list;
    }
    
    @Override
    public int updateRoleName(INodeShare tempNewShare)
    {
        
        tempNewShare.setTableSuffix(ShareTableCaculator.getTableSuffixByOwnerId(tempNewShare.getOwnerId()));
        return sqlMapClientTemplate.update("INodeShare.updateRole", tempNewShare);
    }
    
    private String getOrderByStr(List<Order> orderList)
    {
        if (null == orderList)
        {
            return "";
        }
        StringBuffer orderBy = new StringBuffer();
        String field = null;
        for (Order order : orderList)
        {
            field = order.getField();
            if ("name".equalsIgnoreCase(field))
            {
                field = "convert(name using gb2312)";
            }
            if ("sharedUserName".equalsIgnoreCase(field))
            {
                field = "convert(sharedUserName using gb2312)";
            }
            orderBy.append(field).append(" ").append(order.getDirection()).append(",");
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }

    @Override
    public List<INodeShare> getForwardRecord(INodeShare share) {
        share.setTableSuffix(ShareTableCaculator.getTableSuffixByCreatedBy(share.getCreatedBy()));
        List<INodeShare> forwardlist= (List<INodeShare>) sqlMapClientTemplate.queryForList("INodeShare.getForwardRecord", share);
        return forwardlist;
    }

}