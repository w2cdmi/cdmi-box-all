package com.huawei.sharedrive.uam.core.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({"unchecked", "rawtypes"})
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
        if (include != null)
        {
            mapper.setSerializationInclusion(include);
        }
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
    
    public static JsonMapper nonEmptyMapper()
    {
        return new JsonMapper(Include.NON_EMPTY);
    }
    
    public static JsonMapper nonDefaultMapper()
    {
        return new JsonMapper(Include.NON_DEFAULT);
    }

    public static JsonMapper nonEmptyCamelMapper() {
        JsonMapper mapper = new JsonMapper(Include.NON_EMPTY);
        mapper.getMapper().setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy());
        return mapper;
    }

    public String toJson(Object object)
    {
        
        try
        {
            return mapper.writeValueAsString(object);
        }
        catch (IOException e)
        {
            logger.warn("write to json string error:" + object, e);
            return null;
        }
    }
    
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
            logger.warn("parse json string error", e);
            return null;
        }
    }
    
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
            logger.warn("parse json string error", e);
            return null;
        }
    }
    
    public JavaType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass)
    {
        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }
    
    public JavaType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass)
    {
        return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }
    
    public ObjectMapper getMapper()
    {
        return mapper;
    }
}
