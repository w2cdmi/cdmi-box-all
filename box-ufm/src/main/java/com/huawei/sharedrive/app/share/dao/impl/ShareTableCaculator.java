/**
 * 
 */
package com.huawei.sharedrive.app.share.dao.impl;

import com.huawei.sharedrive.app.share.domain.INodeShare;

import pw.cdmi.core.utils.HashTool;

/**
 * 共享表计算工具
 * 
 * @author l90003768
 * 
 */
public final class ShareTableCaculator
{
    private ShareTableCaculator()
    {
        
    }
    
    /**
     * 获取根据SharedUserId分表的后缀
     * 
     * @param iNodeShare
     * @return
     */
    public static int getSuffixBySharedUserId(INodeShare iNodeShare)
    {
        long sharedUserId = iNodeShare.getSharedUserId();
        if (sharedUserId < 0)
        {
            throw new IllegalArgumentException("illegal sharedUserId " + sharedUserId);
        }
        return ShareTableCaculator.getTableSuffix(sharedUserId);
    }
    
    /**
     * 获取根据SharedUserId分表的后缀
     * 
     * @param iNodeShare
     * @return
     */
    public static int getSuffixBySharedUserId(long sharedUserId)
    {
        if (sharedUserId <= 0)
        {
            throw new IllegalArgumentException("illegal sharedUserId " + sharedUserId);
        }
        return ShareTableCaculator.getTableSuffix(sharedUserId);
    }
    
    /**
     * 获取根据OwnerID分表的后缀
     * 
     * @param iNodeShare
     * @return
     */
    public static int getTableSuffixByOwnerId(INodeShare iNodeShare)
    {
        long ownerId = iNodeShare.getOwnerId();
        if (ownerId <= 0)
        {
            throw new IllegalArgumentException("illegal ownerId " + ownerId);
        }
        return ShareTableCaculator.getTableSuffix(ownerId);
    }
    
    /**
     * 获取根据OwnerID分表的后缀
     * 
     * @param iNodeShare
     * @return
     */
    public static int getTableSuffixByOwnerId(long ownerId)
    {
        if (ownerId <= 0)
        {
            throw new IllegalArgumentException("illegal ownerId " + ownerId);
        }
        return ShareTableCaculator.getTableSuffix(ownerId);
    }
    
    

    /**
     * 获取根据OwnerID分表的后缀
     * 
     * @param iNodeShare
     * @return
     */
    public static int getTableSuffixByCreatedBy(long createdBy)
    {
        if (createdBy <= 0)
        {
            throw new IllegalArgumentException("illegal createdBy " + createdBy);
        }
        return ShareTableCaculator.getTableSuffix(createdBy);
    }
    
    /**
     * 获取后缀
     * 
     * @param dataId
     * @return
     */
    private static int getTableSuffix(long dataId)
    {
        int database = (int) (HashTool.apply(dataId) % ShareDAOImpl.TABLE_COUNT);
        return database;
    }

	public static int getTableSuffixByCreateBy(INodeShare nodeShare) {
		// TODO Auto-generated method stub
		  long createdBy = nodeShare.getCreatedBy();
	        if (createdBy <= 0)
	        {
	            throw new IllegalArgumentException("illegal createdBy " + createdBy);
	        }
	        return ShareTableCaculator.getTableSuffix(createdBy);
	}
}
