package com.huawei.sharedrive.app.files.synchronous;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.huawei.sharedrive.app.files.domain.INode;
import com.ibatis.sqlmap.client.event.RowHandler;

import pw.cdmi.core.exception.InnerException;

public class INodeRowHandle implements RowHandler
{
    private List<INodeMetadata> synDatas = new LinkedList<INodeMetadata>();
    
    private List<INode> folderLst = new LinkedList<INode>();
    
    private Connection con = null;
    
    private PreparedStatement prep = null;
    
    private long total = 0L;
    
    private int count = 0;
    
    public List<INodeMetadata> getSynDatas()
    {
        return synDatas;
    }
    
    public INodeRowHandle(Connection con, PreparedStatement prep)
    {
        this.con = con;
        this.prep = prep;
    }
    
    @Override
    public void handleRow(Object arg0)
    {
        INode tmpNode = (INode) arg0;
        if (tmpNode.getType() == INode.TYPE_FOLDER)
        {
            folderLst.add(tmpNode);
        }
        synDatas.add(new INodeMetadata(tmpNode));
        total++;
        count++;
        if (count >= 500)
        {
            try
            {
                SQLiteUtils.insertSQLiteData(con, prep, synDatas);
            }
            catch (SQLException e)
            {
                throw new InnerException("createSQLiteFile failed", e);
            }
            count = 0;
        }
        
    }
    
    public List<INode> getFolderLst()
    {
        return folderLst;
    }
    
    public void setFolderLst(List<INode> folderLst)
    {
        this.folderLst = folderLst;
    }
    
    public Connection getCon()
    {
        return con;
    }
    
    public PreparedStatement getPrep()
    {
        return prep;
    }
    
    public long getTotal()
    {
        return total;
    }
    
    public void setTotal(long total)
    {
        this.total = total;
    }
}
