package com.huawei.sharedrive.stub.nodecreate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.huawei.sharedrive.app.files.domain.INode;

public class CreateBackNodesThread implements Runnable
{
    private static int currentFiles = 0;
    
    private static final int MAX_DEEP = 10;
    
    private static Random random = new Random();
    
    private static byte SYNC_STATUS_BACK = 3;

    public static final int getTableSuffix(long ownedBy)
    {
        return 122;
    }
    
    private String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    private long ownerId;
    
    public CreateBackNodesThread(long ownerId)
    {
        this.ownerId = ownerId;
    }
    
    public INode createComputerFolder() throws Exception
    {
        INode tempFolder = INodeFolderBuilder.getBackComputerFolder(ownerId);
        insertDb(tempFolder);
        return tempFolder;
    }
    
    public INode createDiskFolder(INode parentNode) throws Exception
    {
        INode tempFolder = INodeFolderBuilder.getBackDiskFolder(parentNode);
        insertDb(tempFolder);
        return tempFolder;
    }
    
    /**
     * @param sql
     * @param tempFolder
     * @throws SQLException
     */
    public void insertDb(INode tempFolder) throws SQLException
    {
        if(total > 200000)
        {
            return;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("insert into inode_")
            .append(getTableSuffix(ownerId))
            .append(" (`id`,parentId,objectId,`name`,`size`, `type`, `status`, `version`,ownedBy,createdAt, modifiedAt,")
            .append("contentCreatedAt,contentModifiedAt,createdBy,modifiedBy,shareStatus,syncStatus,syncVersion,sha1,securityId,resourceGroupId)")
            .append(" values (");
        sql.append(tempFolder.getId()).append(",");
        sql.append(tempFolder.getParentId()).append(",");
        sql.append("'").append(tempFolder.getObjectId()).append("',");
        sql.append("'").append(tempFolder.getName()).append("',");
        sql.append(tempFolder.getSize()).append(",");
        sql.append(tempFolder.getType()).append(",");
        sql.append(tempFolder.getStatus()).append(",");
        sql.append("'").append(tempFolder.getVersion()).append("',");
        sql.append(tempFolder.getOwnedBy()).append(",");
        sql.append("'").append(now).append("',");
        sql.append("'").append(now).append("',");
        sql.append("'").append(now).append("',");
        sql.append("'").append(now).append("',");
        sql.append(tempFolder.getCreatedBy()).append(",");
        sql.append(tempFolder.getModifiedBy()).append(",");
        sql.append(0).append(",");
        sql.append(tempFolder.getSyncStatus()).append(",");
        sql.append(0).append(",");
        sql.append("'").append(tempFolder.getMd5()).append("',");
        sql.append(0).append(",");
        sql.append(tempFolder.getResourceGroupId());
        sql.append(")");
        
        PreparedStatement ps = DbUtils.getConnection().prepareStatement(sql.toString());
        ps.executeUpdate(sql.toString());
        total++;
        if(total % 10000 == 0)
        {
            System.out.println("total is " + total);
        }
    }
    
    public void recycleCreatedNodes(INode parentNode, int deep) throws SQLException
    {
        List<INode> subFolderList = createMulFolder(parentNode);
        createMulFile(parentNode);
        if(deep >= MAX_DEEP)
        {
            return;
        }
        for(INode tempNode: subFolderList)
        {
            recycleCreatedNodes(tempNode, deep++);
        }
    }
    
    @Override
    public void run()
    {
        try
        {
            INode computerNode = createComputerFolder();
            INode diskNode1 = createDiskFolder(computerNode);
            recycleCreatedNodes(diskNode1, 0);
            INode diskNode2 = createDiskFolder(computerNode);
            recycleCreatedNodes(diskNode2, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private List<INode> createMulFile(INode parentNode) throws SQLException
    {
        Connection con = DbUtils.getConnection();
        int folderNumber = random.nextInt(240);
        List<INode> folderList = new ArrayList<INode>();
        for(int i = 0; i < folderNumber; i++)
        {
            INode tempFolder = INodeFileBuilder.getFile(parentNode, SYNC_STATUS_BACK, true);
            insertDb(tempFolder);
            folderList.add(tempFolder);
        }
        return folderList;
    }
    
    
    private List<INode> createMulFolder(INode parentNode) throws SQLException
    {
        Connection con = DbUtils.getConnection();
        int folderNumber = random.nextInt(20);
        List<INode> folderList = new ArrayList<INode>();
        for(int i = 0; i < folderNumber; i++)
        {
            INode tempFolder = INodeFolderBuilder.getBackFolder(parentNode, SYNC_STATUS_BACK, true);
            insertDb(tempFolder);
            folderList.add(tempFolder);
        }
        return folderList;
    }
    
    private static int total = 0;
    
}
