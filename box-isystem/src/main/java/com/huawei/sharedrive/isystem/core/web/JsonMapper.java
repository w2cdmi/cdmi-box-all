package com.huawei.sharedrive.isystem.core.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * 
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 * 
 */
public class JsonMapper
{
    
    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);
    
    private ObjectMapper mapper;
    
    public JsonMapper()
    {
        this(null);
    }
    
    public JsonMapper(Include include)
    {
        mapper = new ObjectMapper();
        // 设置输出时包含属性的风格
        if (include != null)
        {
            mapper.setSerializationInclusion(include);
        }
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
    
    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     */
    public static JsonMapper nonEmptyMapper()
    {
        return new JsonMapper(Include.NON_EMPTY);
    }
    
    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public static JsonMapper nonDefaultMapper()
    {
        return new JsonMapper(Include.NON_DEFAULT);
    }
    
    /**
     * Object可以是POJO，也可以是Collection或数组。 如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
     */
    public String toJson(Object object)
    {
        
        try
        {
            return mapper.writeValueAsString(object);
        }
        catch (IOException e)
        {
            logger.warn("write to json string error:" + object);
            return null;
        }
    }
    
    /**
     * 反序列化POJO或简单Collection如List<String>.
     * 
     * 如果JSON字符串为Null或"null"字符串, 返回Null. 如果JSON字符串为"[]", 返回空集合.
     * 
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String, JavaType)
     * 
     * @see #fromJson(String, JavaType)
     */
    public <T> T fromJson(String jsonString, Class<T> clazz)
    {
        if (StringUtils.isEmpty(jsonString))
        {
            return null;
        }
        
        try
        {
            return mapper.readValue(jsonString, clazz);
        }
        catch (IOException e)
        {
            logger.warn("parse json string error");
            return null;
        }
    }
    
    /**
     * 反序列化复杂Collection如List<Bean>, 先使用createCollectionType()或contructMapType()构造类型,
     * 然后调用本函数.
     * 
     * @see #createCollectionType(Class, Class...)
     */
    public <T> T fromJson(String jsonString, JavaType javaType)
    {
        if (StringUtils.isEmpty(jsonString))
        {
            return null;
        }
        
        try
        {
            return (T) mapper.readValue(jsonString, javaType);
        }
        catch (IOException e)
        {
            logger.warn("parse json string error");
            return null;
        }
    }
    
    /**
     * 构造Collection类型.
     */
    public JavaType contructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass)
    {
        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }
    
    /**
     * 构造Map类型.
     */
    public JavaType contructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass)
    {
        return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }
    
    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public ObjectMapper getMapper()
    {
        return mapper;
    }
}
