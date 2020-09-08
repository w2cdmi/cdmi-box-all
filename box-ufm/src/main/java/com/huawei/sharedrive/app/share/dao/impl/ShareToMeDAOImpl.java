/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 */
package com.huawei.sharedrive.app.share.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.plugins.preview.util.PreviewFileUtil;
import com.huawei.sharedrive.app.share.dao.ShareToMeDAO;
import com.huawei.sharedrive.app.share.domain.INodeShare;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 被共享者DAO实现类
 * 
 * @author l90003768
 */
@Service("shareToMeDAO")
@SuppressWarnings("all")
public class ShareToMeDAOImpl extends AbstractDAOImpl implements ShareToMeDAO
{
    @Autowired
    private PreviewFileUtil previewFileUtil;
    
    @Override
    public int delete(long ownerId, long nodeId, long sharedUserId, byte sharedUserType)
    {
        INodeShare nodeShare = new INodeShare();
        nodeShare.setOwnerId(ownerId);
        nodeShare.setiNodeId(nodeId);
        nodeShare.setSharedUserId(sharedUserId);
        nodeShare.setSharedUserType(sharedUserType);
        return deleteByInode(nodeShare);
    }
    
    @Override
    public int deleteByInode(INodeShare iNodeShare)
    {
        iNodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(iNodeShare));
        return sqlMapClientTemplate.delete("INodeShareReverse.deleteByInode", iNodeShare);
    }
    
    @Override
    public List<Long> getInodeIdListFromOwner(long ownerId, long sharedUserId, byte sharedUserType)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setSharedUserId(sharedUserId);
        iNodeShare.setSharedUserType(sharedUserType);
        iNodeShare.setOwnerId(ownerId);
        iNodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(sharedUserId));
        return sqlMapClientTemplate.queryForList("INodeShareReverse.listShareToMeIdListByOwner", iNodeShare);
    }
    
    @Override
    public INodeShare getINodeShare(long ownerId, long nodeId, long sharedUserId, byte sharedUserType,String linkCode)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setSharedUserId(sharedUserId);
        iNodeShare.setSharedUserType(sharedUserType);
        iNodeShare.setOwnerId(ownerId);
        iNodeShare.setiNodeId(nodeId);
        if(linkCode!=null){
        	iNodeShare.setLinkCode(linkCode);
        }
        iNodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(sharedUserId));
        INodeShare tmp = (INodeShare) sqlMapClientTemplate.queryForObject("INodeShareReverse.getINodeShare",
            iNodeShare);
        if (tmp != null)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return tmp;
    }
    
    @Override
    public List<INodeShare> getList(long sharedUserId, byte sharedUserType)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setSharedUserId(sharedUserId);
        iNodeShare.setSharedUserType(sharedUserType);
        iNodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(sharedUserId));
        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShareReverse.listShareToMe", iNodeShare);
        for (INodeShare tmp : list)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return list;
    }
    
    @Override
    public List<INodeShare> getList(long sharedUserId, byte sharedUserType, String searchName, OrderV1 order,
        Limit limit)
    {
        INodeShare filter = new INodeShare();
        filter.setSharedUserId(sharedUserId);
        filter.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(filter));
        filter.setSharedUserType(sharedUserType);
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("searchName", searchName);
        map.put("order", order);
        map.put("limit", limit);
        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShareReverse.listShareToMeByPage", map);
        for (INodeShare tmp : list)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return list;
    }
    
    @Override
    public List<INodeShare> getListIgnoreStatus(long sharedId)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setSharedUserId(sharedId);
        iNodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(sharedId));
        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShareReverse.listShareToMeIgnoreStatus", iNodeShare);
        for (INodeShare tmp : list)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return list;
    }
    
    /**
     * 获取共享资源V2
     */
    @Override
    public List<INodeShare> getListV2(long sharedUserId, byte sharedUserType, String searchName,
        String order, Limit limit)
    {
        INodeShare filter = new INodeShare();
        filter.setSharedUserId(sharedUserId);
        filter.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(filter));
        filter.setSharedUserType(sharedUserType);
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("searchName", searchName);
        map.put("order", order);
        map.put("limit", limit);
        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShareReverse.listShareToMeByPageV2", map);
        for (INodeShare tmp : list)
        {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }
        return list;
    }
    
    @Override
    public int getShareToMeTotals(long sharedUserId, byte sharedUserType, String searchName)
    {
        INodeShare filter = new INodeShare();
        filter.setSharedUserId(sharedUserId);
        filter.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(filter));
        filter.setSharedUserType(sharedUserType);
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", filter);
        map.put("searchName", searchName);
        return (Integer) sqlMapClientTemplate.queryForObject("INodeShareReverse.getShareToMeCountBySearchName",
            map);
    }
    
    @Override
    public void saveINodeShare(INodeShare nodeShare)
    {
    	nodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(nodeShare));
	    sqlMapClientTemplate.insert("INodeShareReverse.insert", nodeShare);
     
    }
    
    @Override
    public int updateFileSize(long sharedUserId, long ownerId, long nodeId, long size)
    {
        INodeShare nodeShare = new INodeShare();
        nodeShare.setSharedUserId(sharedUserId);
        nodeShare.setOwnerId(ownerId);
        nodeShare.setiNodeId(nodeId);
        nodeShare.setSize(size);
        nodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(sharedUserId));
        return sqlMapClientTemplate.update("INodeShareReverse.updateSize", nodeShare);
        
    }
    
    @Override
    public int updateNodeInfo(long sharedUserId, long ownerId, long nodeId, String newName, long size)
    {
        INodeShare nodeShare = new INodeShare();
        nodeShare.setSharedUserId(sharedUserId);
        nodeShare.setOwnerId(ownerId);
        nodeShare.setiNodeId(nodeId);
        nodeShare.setName(newName);
        nodeShare.setSize(size);
        nodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(sharedUserId));
        return sqlMapClientTemplate.update("INodeShareReverse.updateNameAndSize", nodeShare);
    }
    
    @Override
    public int updateNodeName(INodeShare iNodeShare)
    {
        iNodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(iNodeShare));
        return sqlMapClientTemplate.update("INodeShareReverse.renameNode", iNodeShare);
    }
    
    @Override
    public int updateSharedUserName(long sharedUserId, String sharedUserName)
    {
        INodeShare iNodeShare = new INodeShare();
        iNodeShare.setSharedUserId(sharedUserId);
        iNodeShare.setSharedUserName(sharedUserName);
        iNodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(sharedUserId));
        return sqlMapClientTemplate.update("INodeShareReverse.updateSharedUserName", iNodeShare);
    }
    
    @Override
    public int updateStatus(INodeShare iNodeShare, byte status)
    {
        iNodeShare.setStatus(status);
        iNodeShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(iNodeShare));
        return sqlMapClientTemplate.update("INodeShareReverse.updateStatus", iNodeShare);
    }
    
    @Override
    public int updateRoleName(INodeShare tempNewShare)
    {
        tempNewShare.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(tempNewShare.getSharedUserId()));
        return sqlMapClientTemplate.update("INodeShareReverse.updateRole", tempNewShare);
    }

    @Override
    public INodeShare getForwardRecord(INodeShare share) {
        share.setTableSuffix(ShareTableCaculator.getTableSuffixByOwnerId(share.getSharedUserId()));
        INodeShare tmp = (INodeShare) sqlMapClientTemplate.queryForObject("INodeShareReverse.getForwardRecord", share);
        if (tmp != null) {
            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
        }

        return tmp;
    }

	@Override
	public List<INodeShare> getListWithDelete(long deleteUserId, long sharedUserId, byte sharedUserType, String searchName, Order order, Limit limit) {
		// TODO Auto-generated method stub
		  INodeShare filter = new INodeShare();
	        filter.setSharedUserId(sharedUserId);
	        filter.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(filter));
	        filter.setSharedUserType(sharedUserType);
	        Map<String, Object> map = new HashMap<String, Object>(3);
	        map.put("filter", filter);
	        map.put("searchName", searchName);
	        map.put("order", order);
	        map.put("limit", limit);
	        map.put("deleteUserId", deleteUserId);
	        List<INodeShare> list = sqlMapClientTemplate.queryForList("INodeShareReverse.getListWithDelete", map);
	        for (INodeShare tmp : list)
	        {
	            tmp.setPreviewable(previewFileUtil.isPreviewable(tmp));
	        }
	        return list;
	}

	@Override
	public int getListWithDeleteTotal(long deleteUserId, long sharedUserId, byte sharedUserType, String keyword) {
		// TODO Auto-generated method stub
		 INodeShare filter = new INodeShare();
	        filter.setSharedUserId(sharedUserId);
	        filter.setTableSuffix(ShareTableCaculator.getSuffixBySharedUserId(filter));
	        filter.setSharedUserType(sharedUserType);
	        Map<String, Object> map = new HashMap<String, Object>(2);
	        map.put("filter", filter);
	        map.put("searchName", keyword);
	        map.put("deleteUserId", deleteUserId);
	        return (Integer) sqlMapClientTemplate.queryForObject("INodeShareReverse.getListWithDeleteTotal",
	            map);
	}
}
