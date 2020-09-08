package pw.cdmi.file.engine.manage.datacenter.job;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.InnerLoadBalanceManager;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.manage.datacenter.statistics.ConcDownloadStatistician;
import pw.cdmi.file.engine.manage.datacenter.statistics.ConcUploadStatistician;
import pw.cdmi.file.engine.manage.datacenter.thrift.DCThriftServiceClient;

@Service("reportConcStatusJob")
public class ReportConcStatusJob extends QuartzJobTask
{
    
    private static Logger logger = LoggerFactory.getLogger(ReportConcStatusJob.class);
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    @Autowired
    private ThriftClientProxyFactory dgufmThriftClientProxyFactory;
    
    @Autowired
    private ThriftClientProxyFactory szufmThriftClientProxyFactory;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        String dgServerIp = SystemConfigContainer.getConfigValue(SystemConfigKeys.DG_THRIFT_APPSERVER_SERVER_IP);
        String szServerIp = SystemConfigContainer.getConfigValue(SystemConfigKeys.SZ_THRIFT_APPSERVER_SERVER_IP);
        if (StringUtils.isNotBlank(dgServerIp))
        {
            logger.info("DSS report to dg server ip is " + dgServerIp);
            try
            {
                DCThriftServiceClient client = dgufmThriftClientProxyFactory.getProxy(DCThriftServiceClient.class);
                report(client, record);
            }
            catch (TTransportException e)
            {
                logger.error("ReportStatisticsStatus to dg failed", e);
                record.setSuccess(false);
                record.setOutput(e.getMessage());
            }
        }
        if (StringUtils.isNotBlank(szServerIp))
        {
            logger.info("DSS report to sz server ip is " + szServerIp);
            try
            {
                DCThriftServiceClient client = szufmThriftClientProxyFactory.getProxy(DCThriftServiceClient.class);
                report(client, record);
            }
            catch (TTransportException e)
            {
                logger.error("ReportStatisticsStatus to sz failed", e);
                record.setSuccess(false);
                record.setOutput(e.getMessage());
            }
        }
        if(StringUtils.isBlank(dgServerIp) && StringUtils.isBlank(szServerIp))
        {
            try
            {
                DCThriftServiceClient client = ufmThriftClientProxyFactory.getProxy(DCThriftServiceClient.class);
                report(client, record);
            }
            catch (TTransportException e)
            {
                logger.error("ReportStatisticsStatus Failed", e);
                record.setSuccess(false);
                record.setOutput(e.getMessage());
            }
        }
        
        
    }
    
    private void report(DCThriftServiceClient client, JobExecuteRecord record)
    {
        int counts = InnerLoadBalanceManager.getTryCounts();
        for (int i = 0; i < counts; i++)
        {
            try
            {
                int maxUpload = ConcUploadStatistician.getAndResetMax();
                int maxDownload = ConcDownloadStatistician.getAndResetMax();
                logger.info("[statisticsData] MaxUpload " + maxUpload + ", maxDownload is " + maxDownload);
                String hostName = EnvironmentUtils.getHostName();
                client.reportStatistics(hostName, maxUpload, maxDownload);
                return;
            }
            catch (Exception e)
            {
                logger.error("ReportStatisticsStatus Failed", e);
                record.setSuccess(false);
                record.setOutput(e.getMessage());
            }
        }
    }
    
}
