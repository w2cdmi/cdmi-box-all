/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.realTime.app2dc.ObjectDownloadURL;
import com.huawei.sharedrive.thrift.realTime.app2dc.RealTimeCopyTaskExeResult;
import com.huawei.sharedrive.thrift.realTime.app2dc.RealTimeThriftService;
import com.huawei.sharedrive.thrift.realTime.app2dc.TBusinessException;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

public class DCRealTimeThriftServiceClient extends AbstractThriftClient implements
    RealTimeThriftService.Iface
{
    private RealTimeThriftService.Client client;
    
    public DCRealTimeThriftServiceClient(TTransport transport)
    {
        super(transport, "RealTimeThriftService");
        this.client = new RealTimeThriftService.Client(getProtocol());
    }
    
    @Override
    public ObjectDownloadURL getDownloadUrl(String taskId, String srcObjectId) throws TBusinessException,
        TException
    {
        return client.getDownloadUrl(taskId, srcObjectId);
    }
    
    @Override
    public void reportCopyTaskExeResult(RealTimeCopyTaskExeResult result) throws TBusinessException, TException
    {
        client.reportCopyTaskExeResult(result);
    }
}
