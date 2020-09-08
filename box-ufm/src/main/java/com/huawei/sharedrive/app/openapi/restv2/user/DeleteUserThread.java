package com.huawei.sharedrive.app.openapi.restv2.user;

import com.huawei.sharedrive.app.user.service.UserService;

public class DeleteUserThread implements Runnable
{
    private long accountId;
    
    private long userId;
    
    private UserService userService;
    
    public DeleteUserThread(long accountId, long userId, UserService userService)
    {
        this.accountId = accountId;
        this.userId = userId;
        this.userService = userService;
    }
    
    @Override
    public void run()
    {
        userService.deleteUser(accountId, userId);
    }
    
}
