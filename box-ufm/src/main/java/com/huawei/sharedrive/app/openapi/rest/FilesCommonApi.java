package com.huawei.sharedrive.app.openapi.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderLists;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;

@Controller
public class FilesCommonApi
{
    @Autowired
    private UserService userService;
    
    protected void fillListUserInfo(RestFolderLists reList)
    {
        fillNodesUserInfo(reList.getFiles());
        filllFolderNodeUserInfo(reList.getFolders());
    }
    
    private void fillNodesUserInfo(List<RestFileInfo> files)
    {
        if (null == files)
        {
            return;
        }
        
        for (RestFileInfo inode : files)
        {
            inode.setMender(getUserLoginName(inode.getModifiedBy()));
            inode.setMenderName(getUserName(inode.getModifiedBy()));
        }
    }
    
    private void filllFolderNodeUserInfo(List<RestFolderInfo> folders)
    {
        if (null == folders)
        {
            return;
        }
        for (RestFolderInfo inode : folders)
        {
            inode.setMender(getUserLoginName(inode.getModifiedBy()));
            inode.setMenderName(getUserName(inode.getModifiedBy()));
        }
    }
    
    private String getUserLoginName(Long userId)
    {
        if (userId == null || userId <= 0)
        {
            return null;
        }
        User user = userService.get(null, userId);
        if (user != null)
        {
            return user.getLoginName();
        }
        
        return "unknown";
    }
    
    private String getUserName(Long userId)
    {
        if (userId == null || userId <= 0)
        {
            return null;
        }
        User user = userService.get(null, userId);
        if (user != null)
        {
            return user.getName();
        }
        
        return "unknown";
    }
}
