package pw.cdmi.box.uam.adminlog.domain;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class OperateTypeHandler implements TypeHandlerCallback
{
    @Override
    public Object valueOf(String value)
    {
        return OperateType.parseType(Integer.parseInt(value));
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
            setter.setInt(((OperateType) parameter).getCode());
        }
    }
}
