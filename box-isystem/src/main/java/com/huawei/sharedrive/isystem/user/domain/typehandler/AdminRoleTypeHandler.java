/**
 * 
 */
package com.huawei.sharedrive.isystem.user.domain.typehandler;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.isystem.user.domain.AdminRole;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * @author q90003805
 * 
 */
public class AdminRoleTypeHandler implements TypeHandlerCallback
{
    
    @Override
    public Object valueOf(String s)
    {
        Set<AdminRole> set = new HashSet<AdminRole>(16);
        String[] roles = s.split(",");
        for (String role : roles)
        {
            if (StringUtils.isNotBlank(role))
            {
                AdminRole adminRole = AdminRole.valueOf(role);
                set.add(adminRole);
            }
        }
        return set;
    }
    
    @Override
    public Object getResult(ResultGetter getter) throws SQLException
    {
        String s = getter.getString();
        return valueOf(s);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException
    {
        if (parameter == null)
        {
            setter.setString("");
        }
        else
        {
            Set<AdminRole> set = (Set<AdminRole>) parameter;
            StringBuffer str = new StringBuffer("");
            for (AdminRole adminRole : set)
            {
                str.append(',').append(adminRole.name());
            }
            if (str.length() > 0)
            {
                str.deleteCharAt(0);
            }
            setter.setString(str.toString());
        }
    }
    
}
