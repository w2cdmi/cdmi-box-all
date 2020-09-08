package pw.cdmi.box.uam.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;

public interface CrudService<T, PK extends Serializable>
{
    
    T get(PK id);
    
    List<T> getAll();
    
    void save(T entity);
    
    void update(T entity);
    
    void delete(PK id);
    
    /**
     * 
     * @param propertyName
     * @param value
     * @return
     */
    List<T> findBy(String propertyName, Object value);
    
    List<T> find(Map<String, Object> searchParams);
    
    /**
     * 
     * @param searchParams
     * @param offset
     * @param limit
     * @param order
     * @return
     */
    Page<T> findPage(Map<String, Object> searchParams, int offset, int limit, Order order);
}
