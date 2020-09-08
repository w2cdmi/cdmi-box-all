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

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.app2dc.FileObject;
import com.huawei.sharedrive.thrift.app2dc.FileObjectThriftService;
import com.huawei.sharedrive.thrift.app2dc.FileObjectThriftService.Iface;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

/**
 * 负责调用appserver提供的thrift接口
 * 
 * @author s90006125
 */
public class FileObjectThriftClient extends AbstractThriftClient implements Iface
{
    
    private FileObjectThriftService.Client client;
    
    protected FileObjectThriftClient(TTransport transport)    {
        super(transport, "FileObjectThriftService");
        client = new FileObjectThriftService.Client(getProtocol());
    }
    
    @Override
    public void updateFileObject(FileObject fileObject, String callBackKey) throws TException
    {
        client.updateFileObject(fileObject, callBackKey);
    }
    
    @Override
    public void abortUpload(String objectID, String callBackKey) throws TException
    {
        client.abortUpload(objectID, callBackKey);
    }
}
