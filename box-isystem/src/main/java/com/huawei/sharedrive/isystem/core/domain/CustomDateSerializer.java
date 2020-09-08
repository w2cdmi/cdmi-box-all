/**
 * 
 */
package com.huawei.sharedrive.isystem.core.domain;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * java日期对象经过Jackson库转换成JSON日期格式化自定义类
 * 
 * @author s00108907
 * 
 */
public class CustomDateSerializer extends JsonSerializer<Date>
{
    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
        JsonProcessingException
    {
        // TODO:格式化设置应该缓存起来，当前是每一个日期对象格式化都创建格式化对象，效率低下
        // TODO:缓存到ThreadLocal有问题，未能及时清空
        // 获取用户的日期格式和时区配置信息
    }
}
