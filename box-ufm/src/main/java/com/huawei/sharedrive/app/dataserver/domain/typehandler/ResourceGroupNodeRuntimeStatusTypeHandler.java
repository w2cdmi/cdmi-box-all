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
package com.huawei.sharedrive.app.dataserver.domain.typehandler;

import java.sql.SQLException;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 * @author s90006125
 * 
 */
public class ResourceGroupNodeRuntimeStatusTypeHandler implements TypeHandlerCallback
{
    @Override
    public Object getResult(ResultGetter getter) throws SQLException
    {
        String s = getter.getString();
        return valueOf(s);
    }
    
    @Override
    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException
    {
        if (null != parameter)
        {
            setter.setInt(((ResourceGroupNode.RuntimeStatus) parameter).getCode());
        }
    }
    
    @Override
    public Object valueOf(String value)
    {
        return ResourceGroupNode.RuntimeStatus.parseStatus(Integer.parseInt(value));
    }
}
