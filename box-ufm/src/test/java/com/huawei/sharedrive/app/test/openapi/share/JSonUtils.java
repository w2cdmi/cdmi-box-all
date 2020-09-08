package com.huawei.sharedrive.app.test.openapi.share;


import java.io.StringWriter;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON工具类，可以将JSON转化为Object，或者将Object转化为JSON
 */
public class JSonUtils
{
    
    /**
     * 将JSON数据转化为对象
     * 
     * @param content JSON字符串
     * @param clz 转化后的对象
     * @return 制定的泛型对象
     * @throws ClientException
     */
    public static <T> T stringToObject(String content, Class<T> clz) throws Exception
    {
        T result = null;
        if (null == content || "".equals(content))
        {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        result = mapper.readValue(content, clz);
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
        throws Exception
    {
        if (null == content)
        {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        List<?> list = null;
        JavaType javaType = getCollectionType(collectionClz, elementClz);
        list = (List<?>) mapper.readValue(content, javaType);
        return list;
    }
    
    /**
     * 将Object对象 转化为JSON数据
     * 
     * @param obj 需要转化的Object对象
     * @return JSON数据
     * @throws ClientException
     */
    public static <T> String toJson(T obj) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        mapper.writeValue(sw, obj);
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
