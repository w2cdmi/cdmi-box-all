package com.huawei.sharedrive.uam.util;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StringUtils;

public class ConnectionWrapper extends ConnectionImpl
{
    private static final long serialVersionUID = 5617253585495475648L;
    
    private static final String[] IGNORE_KEYS = {"session", "token", "pass", "secretkey", "auth"};
    
    private static Logger logger = LoggerFactory.getLogger(ConnectionWrapper.class);
    
    public ConnectionWrapper(String hostToConnectTo, int portToConnectTo, Properties info,
        String databaseToConnectTo, String url) throws SQLException
    {
        super(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public synchronized ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql,
        int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults,
        String catalog, Field[] cachedMetadata, boolean isBatch) throws SQLException
    {
        if (!StringUtils.isNullOrEmpty(sql))
        {
            printSQL(sql);
        }
        else if (packet != null)
        {
            byte[] buf = packet.getByteBuffer();
            String prepareSql;
            try
            {
                prepareSql = new String(Arrays.copyOfRange(buf, 5, packet.getPosition()), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                throw new SQLException(e);
            }
            printSQL(prepareSql);
        }
        
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
    
    private void printSQL(String sql)
    {
        String trimedSql = sql.trim();
        if ("SELECT 1".equalsIgnoreCase(trimedSql))
        {
            return;
        }
        if (sql.startsWith("/*") || sql.startsWith("SHOW") || sql.startsWith("COMMIT")
            || sql.startsWith("SET") || sql.startsWith("SELECT @@"))
        {
            return;
        }
        
        if (logger.isDebugEnabled() && !isInRange(sql))
        {
            logger.debug("Execute sql:" + sql);
        }
    }
    
    private boolean isInRange(String input)
    {
        String temp = input.toLowerCase(Locale.ENGLISH);
        for (String item : IGNORE_KEYS)
        {
            if (temp.indexOf(item) != -1)
            {
                return true;
            }
        }
        return false;
    }
}
