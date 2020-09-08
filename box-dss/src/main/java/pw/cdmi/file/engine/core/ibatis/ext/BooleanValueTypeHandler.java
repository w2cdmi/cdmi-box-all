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
package pw.cdmi.file.engine.core.ibatis.ext;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibatis.sqlmap.engine.type.BaseTypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandler;

/**
 * 
 * @author s90006125
 * 
 */
public class BooleanValueTypeHandler extends BaseTypeHandler implements TypeHandler
{
    
    @Override
    public Object getResult(ResultSet arg0, String arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Object getResult(ResultSet arg0, int arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Object getResult(CallableStatement arg0, int arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void setParameter(PreparedStatement arg0, int arg1, Object arg2, String arg3) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Object valueOf(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
