package pw.cdmi.box.disk.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.dao.CrudDao;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;

@Transactional
public abstract class AbstractCrudService<T, PK extends Serializable> implements CrudService<T, PK>
{
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(PK id)
    {
        getGenericDao().delete(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> find(final Map<String, Object> searchParams)
    {
        return this.getGenericDao().find(searchParams);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> findBy(final String propertyName, final Object value)
    {
        return this.getGenericDao().findBy(propertyName, value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<T> findPage(Map<String, Object> searchParams, int offset, int limit, Order order)
    {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public T get(PK id)
    {
        return getGenericDao().get(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> getAll()
    {
        return getGenericDao().getAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void save(T entity)
    {
        getGenericDao().save(entity);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(T entity)
    {
        getGenericDao().update(entity);
    }
    
    /**
     * GenericDao instance, set by constructor of this class
     */
    protected abstract CrudDao<T, PK> getGenericDao();
}
