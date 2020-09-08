package pw.cdmi.box.disk.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;

public interface CrudService<T, PK extends Serializable>
{
    
    void delete(PK id);
    
    List<T> find(Map<String, Object> searchParams);
    
    List<T> findBy(String propertyName, Object value);
    
    Page<T> findPage(Map<String, Object> searchParams, int offset, int limit, Order order);
    
    T get(PK id);
    
    List<T> getAll();
    
    void save(T entity);
    
    void update(T entity);
}
