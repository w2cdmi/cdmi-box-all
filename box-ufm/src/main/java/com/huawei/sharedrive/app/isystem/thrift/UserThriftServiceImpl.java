/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.isystem.thrift;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.thrift.app2isystem.UserThriftService;

/**
 * 提供给iSystem的Thrift接口
 * 
 * @author d00199602
 * 
 */
public class UserThriftServiceImpl implements UserThriftService.Iface
{
    @Autowired
    private FileService fileService;
    
    @Override
    public long getUsedSpace(long userId) throws TException
    {
        return fileService.getUserTotalSpace(userId);
    }
}
