package com.huawei.sharedrive.uam.rest.test.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.sharedrive.uam.httpclient.exception.ClientException;

/**
 * JSON工具类，可以将JSON转化为Object，或者将Object转化为JSON
 */
public class JsonUtils
{
    
    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    
    /**
     * 将JSON数据转化为对象
     * 
     * @param content JSON字符串
     * @param clz 转化后的对象
     * @return 制定的泛型对象
     * @throws ClientException
     */
    public static <T> T stringToObject(String content, Class<T> clz)
    {
        T result = null;
        if (null == content || "".equals(content))
        {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try
        {
            result = mapper.readValue(content, clz);
        }
        catch (Exception e)
        {
            logger.error("parse json string error:", e);
        }
        return result;
    }
    
    /**
     * 将JSONArray格式的字符串，转化为集合对象
     * 
     * @param content 需要转化的JSONArray字符串
     * @param collectionClz 转化为什么样的集合，比如ArrayList.class
     * @param elementClz 集合元素类型，需要有默认的无参构造函数
     * @return collectionClz类型的结合
     * @throws ClientException
     */
    public static List<?> stringToList(String content, Class<?> collectionClz, Class<?>... elementClz)
    {
        if (null == content)
        {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        List<?> list = null;
        JavaType javaType = getCollectionType(collectionClz, elementClz);
        try
        {
            list = (List<?>) mapper.readValue(content, javaType);
        }
        catch (Exception e)
        {
            logger.error("parse json string error:", e);
        }
        return list;
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
    public static <T> T stringToCollection(String jsonString, Class<T> clazz)
    {
        if (StringUtils.isEmpty(jsonString))
        {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try
        {
            return mapper.readValue(jsonString, clazz);
        }
        catch (IOException e)
        {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }
    
    public static Map<String, Object> stringToMap(String jsonString)
    {
        if (StringUtils.isEmpty(jsonString))
        {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try
        {
            return mapper.readValue(jsonString, Map.class);
        }
        catch (IOException e)
        {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }
    
    /**
     * 将Object对象 转化为JSON数据
     * 
     * @param obj 需要转化的Object对象
     * @return JSON数据
     * @throws ClientException
     */
    public static <T> String toJson(T obj)
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        StringWriter sw = new StringWriter();
        
        try
        {
            mapper.writeValue(sw, obj);
        }
        catch (Exception e)
        {
            logger.error("parse json string error:", e);
        }
        String result = sw.toString();
        return result;
    }
    
    /**
     * 获取转化类型
     * 
     * @param collectionClz
     * @param elementClz
     * @return
     */
    private static JavaType getCollectionType(Class<?> collectionClz, Class<?>... elementClz)
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.getTypeFactory().constructParametricType(collectionClz, elementClz);
    }
    
}
