/**
 * 
 */
package com.huawei.sharedrive.app.dataserver.domain.typehandler;

import java.sql.SQLException;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * @author q90003805
 * 
 */
public class ResourceGroupNodeStatusTypeHandler implements TypeHandlerCallback
{
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
    
    @Override
    public Object valueOf(String value)
    {
        return ResourceGroupNode.Status.parseStatus(Integer.parseInt(value));
    }
    
}
