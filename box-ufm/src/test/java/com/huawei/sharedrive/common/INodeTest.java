package com.huawei.sharedrive.common;

import pw.cdmi.core.utils.HashTool;

public class INodeTest
{
    
    private static long ownerId = 70022L;
    
//    private static long nodeId = 0L;
    
    private static final int TABLE_NODES = 500;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        int table = (int) (HashTool.apply(String.valueOf(ownerId)) % TABLE_NODES);
        System.out.println("table name is inode_" + table);
        long db = ownerId % 1024 / 128 + 1;
        System.out.println("user db(1-8)  is userdb_" + db);
        
    }
    
}
