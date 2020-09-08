package com.huawei.sharedrive.app.utils;

import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.StatementImpl;

@SuppressWarnings("PMD.ExcessiveParameterList")
public class ConnectionWrapper extends ConnectionImpl
{
    
    public static final long serialVersionUID = 5617253585495475648L;
    
    public ConnectionWrapper(String hostToConnectTo, int portToConnectTo, Properties info,
        String databaseToConnectTo, String url) throws SQLException
    {
        super(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
    }
    
    @Override
    public synchronized void close() throws SQLException
    {
        super.close();
    }
    
    @Override
    public synchronized void commit() throws SQLException
    {
        super.commit();
    }
    
    @Override
    public synchronized ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql,
        int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults,
        String catalog, Field[] cachedMetadata, boolean isBatch) throws SQLException
    {
        
        return super.execSQL(callingStatement,
            sql,
            maxRows,
            packet,
            resultSetType,
            resultSetConcurrency,
            streamResults,
            catalog,
            cachedMetadata,
            isBatch);
    }
    
}
