package com.huawei.sharedrive.uam.security.domain;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class DeviceTypeHandler implements TypeHandlerCallback
{
    @Override
    public Object valueOf(String value)
    {
        return DeviceType.parseType(Integer.parseInt(value));
    }
    
    @Override
    public Object getResult(ResultGetter getter) throws SQLException
    {
        return valueOf(getter.getString());
    }
    
    @Override
    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException
    {
        if (null != parameter)
        {
            setter.setInt(((DeviceType) parameter).getType());
        }
    }
}
