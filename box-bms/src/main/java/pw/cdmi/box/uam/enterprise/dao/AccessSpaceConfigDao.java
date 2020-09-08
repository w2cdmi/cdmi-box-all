package pw.cdmi.box.uam.enterprise.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.enterprise.domain.AccessSpaceConfig;

public interface AccessSpaceConfigDao
{
    
    void create(AccessSpaceConfig netRegion);
    
    void delete(String id);
    
    long getByDomainExclusiveId(AccessSpaceConfig netRegion);
    
    AccessSpaceConfig getById(String id);
    
    List<AccessSpaceConfig> getFilterd(AccessSpaceConfig netRegion, Order order, Limit limit);
    
    int getFilterdCount(AccessSpaceConfig netRegion);
    
    List<AccessSpaceConfig> getListByOperation(long accountId, long intValue);
    
    AccessSpaceConfig getObject(AccessSpaceConfig spaceConfig);
    
    boolean isDuplicateValues(AccessSpaceConfig netRegion);
    
    void update(AccessSpaceConfig netRegion);
    
    void deleteByCondition(AccessSpaceConfig accessSpaceConfig);
    
}
