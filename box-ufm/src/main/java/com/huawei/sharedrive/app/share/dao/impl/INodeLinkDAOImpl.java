package com.huawei.sharedrive.app.share.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.share.dao.INodeLinkDAO;
import com.huawei.sharedrive.app.share.domain.INodeLink;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.core.utils.HashTool;

@Service("iNodeLinkDAO")
@SuppressWarnings("deprecation")
public class INodeLinkDAOImpl extends CacheableSqlMapClientDAO implements INodeLinkDAO
{
    private static Logger logger = LoggerFactory.getLogger(INodeLinkDAOImpl.class);
    
    static final int TABLE_COUNT = 100;
    
    private static final String CACHE_KEY_PREFIX_ID = "inode_link_";
    
    private static final long CACHE_EXPIRETIME = 120 * 1000L;
    
    @Value("${link.cache.supported}")
    private boolean linkCacheSupported;
    
    @Override
    public boolean checkINodeLinkExist(INodeLink iNodeLink)
    {
        iNodeLink.setTableSuffix(getTableSuffix(iNodeLink));
        Long id = (Long) sqlMapClientTemplate.queryForObject("INodeLink.check", iNodeLink);
        if (null != id)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public void create(INodeLink iNodeLink)
    {
        iNodeLink.setTableSuffix(getTableSuffix(iNodeLink));
        sqlMapClientTemplate.insert("INodeLink.insert", iNodeLink);
        setCache(iNodeLink);
    }
    
    @Override
    public void createV2(INodeLink iNodeLink)
    {
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        sqlMapClientTemplate.insert("INodeLink.insert", iNodeLink);
        setCache(iNodeLink);
    }
    
    @Override
    public void delete(INodeLink iNodeLink)
    {
        deleteCache(iNodeLink.getId());
        iNodeLink.setTableSuffix(getTableSuffix(iNodeLink));
        sqlMapClientTemplate.delete("INodeLink.delete", iNodeLink);
    }
    
    @Override
    public int deleteByOwner(long ownerId)
    {
        if(isCacheSupported())
        {
            String linkOwnerKey = CACHE_KEY_PREFIX_ID +ownerId;
            getCacheClient().deleteCache(linkOwnerKey);
        }
        
        int total = 0;
        INodeLink nodeLink = null;
        for (int i = 0; i < 100; i++)
        {
            nodeLink = new INodeLink();
            nodeLink.setTableSuffix(i);
            nodeLink.setOwnedBy(ownerId);
            total += sqlMapClientTemplate.delete("INodeLink.deleteByOwner", nodeLink);
        }
        return total;
    }
    
    @Override
    public void deleteV2(INodeLink iNodeLink)
    {
    	try {
    		  deleteCache(iNodeLink.getId());
		} catch (Exception e) {
			// TODO: handle exception
		}
      
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        sqlMapClientTemplate.delete("INodeLink.delete", iNodeLink);
    }
    
    @Override
    public INodeLink get(INodeLink iNodeLink)
    {
        INodeLink temp = getCache(iNodeLink.getId());
        if(null != temp)
        {
            return temp;
        }
        iNodeLink.setTableSuffix(getTableSuffix(iNodeLink));
        temp = (INodeLink) sqlMapClientTemplate.queryForObject("INodeLink.get", iNodeLink);
        
        if(null != temp)
        {
            setCache(temp);
        }
        
        return temp;
    }
    
    @Override
    public INodeLink getV2(INodeLink iNodeLink)
    {
        INodeLink temp = getCache(iNodeLink.getId());
        if(null != temp)
        {
            return temp;
        }
        
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        temp = (INodeLink) sqlMapClientTemplate.queryForObject("INodeLink.get", iNodeLink);
        
        if(null != temp)
        {
            setCache(temp);
        }
        
        return temp;
    }
    
    @Override
    public void update(INodeLink iNodeLink)
    {
        deleteCache(iNodeLink.getId());
        iNodeLink.setTableSuffix(getTableSuffix(iNodeLink));
        sqlMapClientTemplate.update("INodeLink.update", iNodeLink);
    }
    
    @Override
    public void updateV2(INodeLink iNodeLink)
    {
        deleteCache(iNodeLink.getId());
        iNodeLink.setTableSuffix(getTableSuffixV2(iNodeLink));
        sqlMapClientTemplate.update("INodeLink.update", iNodeLink);
    }
    
    @Override
    public void upgradePassword(INodeLink iNodeLink)
    {
        deleteCache(iNodeLink.getId());
        iNodeLink.setTableSuffix(getTableSuffix(iNodeLink));
        sqlMapClientTemplate.update("INodeLink.upgradePassword", iNodeLink);
    }
    
    /**
     * 根据linkCode获取分表名称
     * 
     * @param linkCode
     * @return
     */
    private int getTableSuffix(INodeLink iNodeLink)
    {
        String linkCode = iNodeLink.getId();
        if (StringUtils.isBlank(linkCode))
        {
            throw new IllegalArgumentException("illegal linkCode " + linkCode);
        }
        return getTableSuffix(linkCode);
    }
    
    /**
     * 根据linkCode获取分表名称
     * 
     * @param linkCode
     * @return
     */
    private int getTableSuffix(String linkCode)
    {
        logger.debug(HashTool.applySuffux(linkCode) % TABLE_COUNT + "");
        
        return (int) (HashTool.applySuffux(linkCode) % TABLE_COUNT);
    }
    
    /**
     * 根据linkCode获取分表名称
     * 
     * @param linkCode
     * @return
     */
    private int getTableSuffixV2(INodeLink iNodeLink)
    {
        String linkCode = String.valueOf(iNodeLink.getId());
        if (StringUtils.isBlank(linkCode))
        {
            throw new IllegalArgumentException("illegal linkCode " + linkCode);
        }
        return getTableSuffix(linkCode);
    }
    
    /**
     * 存放到cache，因为有一个根据owner删除外链的方法，因此在存放cache的时候，先放一个ownerId，获取的时候，如果能获取到该ownerId，再去获取相应的inodeLink
     * @param iNodeLink
     */
    private void setCache(INodeLink iNodeLink)
    {
        if(null == iNodeLink)
        {
            return;
        }
        
        if(isCacheSupported())
        {
            String linkOwnerKey = CACHE_KEY_PREFIX_ID + iNodeLink.getOwnedBy();
            String linkKey =CACHE_KEY_PREFIX_ID + iNodeLink.getId();
            getCacheClient().setCache(linkOwnerKey, iNodeLink.getOwnedBy(), CACHE_EXPIRETIME);
            getCacheClient().setCache(linkKey, iNodeLink, CACHE_EXPIRETIME);
        }
    }
    
    private INodeLink getCache(String linkCode)
    {
        if(StringUtils.isBlank(linkCode))
        {
            return null;
        }
        
        if(isCacheSupported())
        {
            String linkKey =CACHE_KEY_PREFIX_ID + linkCode;
            
            INodeLink temp = (INodeLink)getCacheClient().getCache(linkKey);
            if(null == temp)
            {
                logger.info("get link : {} from cache failed.", linkCode);
                return null;
            }
            
            // 再判断是否执行过根据ownerId删除外链
            String linkOwnerKey = CACHE_KEY_PREFIX_ID + temp.getOwnedBy();
            if(null == getCacheClient().getCache(linkOwnerKey))
            {
                // 如果为空，表示很可能执行过根据ownerId删除外链
                deleteCache(linkCode);
                logger.info("get link : {} from cache failed.", linkCode);
                return null;
            }
            
            logger.info("get link : {} from cache success.", linkCode);
            return temp;
        }
        
        return null;
    }
    
    private void deleteCache(String linkCode)
    {
        logger.info("delete link : {} from cache.", linkCode);
        
        if(StringUtils.isBlank(linkCode))
        {
            return;
        }
        
        if(isCacheSupported())
        {
            String linkKey =CACHE_KEY_PREFIX_ID + linkCode;
            INodeLink temp = (INodeLink)getCacheClient().getCache(linkKey);
            if(null == temp)
            {
                // 无法获取，则直接返回，表示已经删除
                return;
            }
            
            String linkOwnerKey = CACHE_KEY_PREFIX_ID + temp.getOwnedBy();
            getCacheClient().deleteCache(linkOwnerKey);
            getCacheClient().deleteCache(linkKey);
        }
    }
    
    @Override
    public boolean isCacheSupported()
    {
        if(super.isCacheSupported())
        {
            return linkCacheSupported;
        }
        
        return false;
    }
    

}
