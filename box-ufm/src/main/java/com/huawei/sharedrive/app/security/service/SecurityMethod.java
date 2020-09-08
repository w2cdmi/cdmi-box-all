package com.huawei.sharedrive.app.security.service;

public enum SecurityMethod
{
    // 文件操作
    FILE_DOWNLOAD, FILE_UPLOAD, FILE_PREVIEW,  
    
    // 文件夹操作
    FOLDER_LIST, FOLDER_CREATE,
    
    // 通用操作
    NODE_COPY, NODE_RENAME, NODE_DELETE, NODE_INFO,
    
    // 共享外链
    NODE_SETLINK, NODE_SETSHARE;
}
