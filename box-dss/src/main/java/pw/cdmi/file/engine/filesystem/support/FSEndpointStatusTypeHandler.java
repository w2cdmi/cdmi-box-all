package pw.cdmi.file.engine.filesystem.support;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

import pw.cdmi.core.utils.BaseConvertUtils;
import pw.cdmi.file.engine.filesystem.model.FSEndpointStatus;

public class FSEndpointStatusTypeHandler
  implements TypeHandlerCallback
{
  public Object valueOf(String value)
  {
    return FSEndpointStatus.parseState(BaseConvertUtils.toInt(value, Integer.valueOf(0)).intValue());
  }

  public Object getResult(ResultGetter getter)
    throws SQLException
  {
    return valueOf(getter.getString());
  }

  public void setParameter(ParameterSetter setter, Object parameter)
    throws SQLException
  {
    if (parameter != null)
    {
      setter.setInt(((FSEndpointStatus)parameter).getCode());
    }
  }
}