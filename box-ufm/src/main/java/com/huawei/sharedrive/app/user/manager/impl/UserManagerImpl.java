package com.huawei.sharedrive.app.user.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.service.AccountService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ExceedQuotaException;
import com.huawei.sharedrive.app.exception.NoSuchAccountException;
import com.huawei.sharedrive.app.exception.NoSuchUserException;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.manager.UserManager;
import com.huawei.sharedrive.app.user.service.UserService;

@Component
public class UserManagerImpl implements UserManager
{
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    @Override
    public User deleteUserById(long userId, String[] akArr) throws BaseRunException
    {
        User user = userService.get(userId);
        if (null == user)
        {
            throw new NoSuchUserException("cant not found user: " + userId);
        }
        Account account = accountService.getById(user.getAccountId());
        if (null == account)
        {
            throw new NoSuchAccountException("cant not found account: " + user.getAccountId());
        }
        user = userService.deleteUser(user, akArr, userService);
        accountService.updateCurrentMember(account);
        return user;
    }
    
    @Override
    public RestUserCreateRequest addUser(RestUserCreateRequest requestUser, String[] akArr, Account account)
        throws BaseRunException
    {
        
        if (accountService.isUserExceed(account))
        {
            throw new ExceedQuotaException("user exceed in account: " + account.getId());
        }
        userService.setDefaultValue(requestUser);
        userService.checkNewUserRegion(requestUser);
        User user = userService.initUser(account, requestUser, akArr);
        userService.saveUser(user);
        requestUser = userService.update(requestUser, user);
        accountService.updateCurrentMember(account);
        return requestUser;
    }
    
    
    @Override
    public RestUserCreateRequest addWxUser(RestUserCreateRequest requestUser,String appId, String[] akArr)
        throws BaseRunException
    {
    	Account account=new Account();
    	account.setId(0);
    	account.setEnterpriseId(0L);
    	account.setAppId(appId);
        userService.setDefaultValue(requestUser);
        userService.checkNewUserRegion(requestUser);
        User user = userService.initUser(account, requestUser, akArr);
        userService.saveUser(user);
        requestUser = userService.update(requestUser, user);
        accountService.updateCurrentMember(account);
        return requestUser;
    }
}
