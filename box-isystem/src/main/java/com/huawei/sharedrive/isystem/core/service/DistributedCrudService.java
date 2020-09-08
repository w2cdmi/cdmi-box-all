/**
 * 
 */
package com.huawei.sharedrive.isystem.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;

/**
 * @author s00108907
 * 
 */
public interface DistributedCrudService<T, PK extends Serializable>
{
    
    /**
     * 根据ID获取对象
     * 
     * @param ownerId 所属用户ID，分库字段
     * @param id 对象主键
     * @return 获取到的对象
     */
    T get(Integer ownerId, PK id);
    
    /**
     * 获取该对象的所有列表数据
     * 
     * @param ownerId 所属用户ID，分库字段
     * @return 对象列表
     */
    List<T> getAll(Integer ownerId);
    
    /**
     * 保存对象实例
     * 
     * @param ownerId 所属用户ID，分库字段
     * @param entity 要保存的对象
     */
    void save(Integer ownerId, T entity);
    
    /**
     * 更新对象实例
     * 
     * @param ownerId 所属用户ID，分库字段
     * @param entity 要保存的对象
     */
    void update(Integer ownerId, T entity);
    
    /**
     * 根据对象主键删除对象实例
     * 
     * @param ownerId 所属用户ID，分库字段
     * @param id 要删除的对象主键
     */
    void delete(Integer ownerId, PK id);
    
    /**
     * 按属性查找对象列表,匹配方式为相等.
     * 
     * @param ownerId 所属用户ID，分库字段
     * @param propertyName
     * @param value
     * @return
     */
    List<T> findBy(Integer ownerId, String propertyName, Object value);
    
    /**
     * 按属性过滤条件列表查找对象列表.
     * 
     * @param ownerId 所属用户ID，分库字段
     * @param searchParams 过滤条件
     */
    List<T> find(Integer ownerId, Map<String, Object> searchParams);
    
    /**
     * 按属性过滤条件列表分页查找对象.
     * 
     * @param ownerId 所属用户ID，分库字段
     * @param searchParams 查询条件
     * @param offset 偏移量
     * @param limit 最大返回记录数
     * @param order 排序对象
     * @return 分页对象
     */
    Page<T> findPage(Integer ownerId, Map<String, Object> searchParams, int offset, int limit, Order order);
}
