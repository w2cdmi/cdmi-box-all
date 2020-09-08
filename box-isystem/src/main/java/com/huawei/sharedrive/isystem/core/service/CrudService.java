/*
 * Copyright Huawei Symantec Technologies Co.,Ltd. 2008-2009. All rights reserved.
 * 
 * 
 */

package com.huawei.sharedrive.isystem.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;

/**
 * 通用的manager类，处理常用的CRUD操作
 * 
 * @param <T> 域对象类型
 * @param <PK> 主键类型
 * @author s00108907
 */
public interface CrudService<T, PK extends Serializable>
{
    
    /**
     * 根据ID获取对象
     * 
     * @param id 对象主键
     * @return 获取到的对象
     */
    T get(PK id);
    
    /**
     * 获取该对象的所有列表数据
     * 
     * @return 对象列表
     */
    List<T> getAll();
    
    /**
     * 保存对象实例
     * 
     * @param entity 要保存的对象
     */
    void save(T entity);
    
    /**
     * 更新对象实例
     * 
     * @param entity 要保存的对象
     */
    void update(T entity);
    
    /**
     * 根据对象主键删除对象实例
     * 
     * @param id 要删除的对象主键
     */
    void delete(PK id);
    
    /**
     * 按属性查找对象列表,匹配方式为相等.
     * 
     * @param propertyName
     * @param value
     * @return
     */
    List<T> findBy(String propertyName, Object value);
    
    /**
     * 按属性过滤条件列表查找对象列表.
     * 
     * @param searchParams 过滤条件
     */
    List<T> find(Map<String, Object> searchParams);
    
    /**
     * 按属性过滤条件列表分页查找对象.
     * 
     * @param searchParams 查询条件
     * @param offset 偏移量
     * @param limit 最大返回记录数
     * @param order 排序对象
     * @return 分页对象
     */
    Page<T> findPage(Map<String, Object> searchParams, int offset, int limit, Order order);
}
