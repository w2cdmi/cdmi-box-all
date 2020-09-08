/**
 * 
 */
package pw.cdmi.box.disk.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;

public interface DistributedCrudService<T, PK extends Serializable>
{
    void delete(Integer ownerId, PK id);
    
    List<T> find(Integer ownerId, Map<String, Object> searchParams);
    
    List<T> findBy(Integer ownerId, String propertyName, Object value);
    
    Page<T> findPage(Integer ownerId, Map<String, Object> searchParams, int offset, int limit, Order order);
    
    T get(Integer ownerId, PK id);
    
    List<T> getAll(Integer ownerId);
    
    void save(Integer ownerId, T entity);
    
    void update(Integer ownerId, T entity);
}
