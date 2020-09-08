package com.huawei.sharedrive.app.files.synchronous;

import com.huawei.sharedrive.app.utils.PropertiesUtils;

/**
 * 
 * @author c00110381
 * 
 */
public class SynConstants
{
    
    /**
     * 存放临时文件的地址
     */
    public static final String SYNC_METADATA_TEMP_FILE_PATH = PropertiesUtils.getProperty("synchronous.version.file.temp.path",
        "/opt/ramdisk/container_synchronou_files/");
    
    public static final Character SYNCFILE_SPLIT = '-';
    public static final Character RANDOM_SPLIT = '_';
    
    public static final String ZIP_SURFIX = "#z";
    
    public static final int SUB_FOLDER_MAXNUM = 1000;
    
    /**
     * 用户元数据最大规格：100万
     */
    public static final int USER_DEFAULT_NODE_MAXNUM = 1000000;
    
    public static final String SYNC_USER_MAX_NODENUM = "synchronous.user.max.nodenum";
    
    public static final String HEAD_SYNC_USER_MAX_NODENUM = "x-user-node-limit";
    public static final String HEAD_SYNC_USER_CURR_NODENUM = "x-user-node-current";
}
