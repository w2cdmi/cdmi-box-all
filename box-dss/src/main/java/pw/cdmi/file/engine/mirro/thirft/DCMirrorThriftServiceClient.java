package pw.cdmi.file.engine.mirro.thirft;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskExeResult;
import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskMinor;
import com.huawei.sharedrive.thrift.mirror.app2dc.MirrorThriftService;
import com.huawei.sharedrive.thrift.mirror.app2dc.ObjectDownloadURL;
import com.huawei.sharedrive.thrift.mirror.app2dc.ReportResultHandleInfo;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

public class DCMirrorThriftServiceClient extends AbstractThriftClient implements MirrorThriftService.Iface
{
    
    private MirrorThriftService.Client client;
    
    public DCMirrorThriftServiceClient(TTransport transport)
    {
        super(transport, "MirrorThriftService");
        this.client = new MirrorThriftService.Client(getProtocol());
    }
    
    @Override
    public void reportCopyTaskExeResult(CopyTaskExeResult result) throws TException
    {
        client.reportCopyTaskExeResult(result);
    }
    
    @Override
    public List<ReportResultHandleInfo> batchReportCopyTaskExeResult(List<CopyTaskExeResult> lstResult)
        throws TException
    {
        return client.batchReportCopyTaskExeResult(lstResult);
    }
    
    @Override
    public ObjectDownloadURL getDownloadUrl(String taskId, String srcObjectId) throws TException
    {
        return client.getDownloadUrl(taskId, srcObjectId);
    }
    
    @Override
    public List<ObjectDownloadURL> batchGetDownloadUrl(List<CopyTaskMinor> tasks) throws TException
    {
        return client.batchGetDownloadUrl(tasks);
    }
}
