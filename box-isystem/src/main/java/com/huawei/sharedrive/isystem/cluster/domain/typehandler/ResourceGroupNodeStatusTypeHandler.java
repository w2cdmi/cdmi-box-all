/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.cluster.domain.typehandler;

import java.sql.SQLException;

import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 * @author s90006125
 * 
 */
public class ResourceGroupNodeStatusTypeHandler implements TypeHandlerCallback
{
    @Override
    public Object valueOf(String value)
    {
        return ResourceGroupNode.Status.parseStatus(Integer.parseInt(value));
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
            setter.setInt(((ResourceGroupNode.Status) parameter).getCode());
        }
    }
}
