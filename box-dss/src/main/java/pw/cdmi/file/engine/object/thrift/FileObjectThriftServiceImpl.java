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
package pw.cdmi.file.engine.object.thrift;

import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.thrift.dc2app.FileObjectThriftService.Iface;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.object.manager.FileObjectManager;

/**
 * 负责对外提供thrift服务
 * 
 * @author s90006125
 * 
 */
public class FileObjectThriftServiceImpl implements Iface
{
    @Autowired
    private FileObjectManager fileObjectManager;
    
    
    @Override
    @MethodLogAble(value=Level.INFO, newLogId=true)
    public boolean deleteFileObject(String objectID)
        throws com.huawei.sharedrive.thrift.dc2app.TBusinessException
    {
        fileObjectManager.deleteFileObject(objectID);
        return true;
    }
}
