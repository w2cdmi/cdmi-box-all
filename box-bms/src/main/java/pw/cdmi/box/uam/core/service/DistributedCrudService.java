package pw.cdmi.box.uam.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;

public interface DistributedCrudService<T, PK extends Serializable>
{
    
    /**
     * 
     * @param ownerId
     * @param id
     * @return
     */
    T get(Integer ownerId, PK id);
    
    /**
     * 
     * @param ownerId
     * @return
     */
    List<T> getAll(Integer ownerId);
    
    /**
     * 
     * @param ownerId
     * @param entity
     */
    void save(Integer ownerId, T entity);
    
    /**
     * 
     * @param ownerId
     * @param entity
     */
    void update(Integer ownerId, T entity);
    
    /**
     * 
     * @param ownerId
     * @param id
     */
    void delete(Integer ownerId, PK id);
    
    /**
     * 
     * @param ownerId
     * @param propertyName
     * @param value
     * @return
     */
    List<T> findBy(Integer ownerId, String propertyName, Object value);
    
    /**
     * 
     * @param ownerId
     * @param searchParams
     */
    List<T> find(Integer ownerId, Map<String, Object> searchParams);
    
    /**
     * 
     * @param ownerId
     * @param searchParams
     * @param offset
     * @param limit
     * @param order
     * @return
     */
    Page<T> findPage(Integer ownerId, Map<String, Object> searchParams, int offset, int limit, Order order);
}
