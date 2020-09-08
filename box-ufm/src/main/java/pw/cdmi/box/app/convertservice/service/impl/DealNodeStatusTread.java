package pw.cdmi.box.app.convertservice.service.impl;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinasoft.sharedrive.thrift.convertService.heartbeat.ConertServiceHartBeat;

import pw.cdmi.box.app.convertservice.domain.NodeRunningInfo;
import pw.cdmi.box.app.convertservice.service.CSMonitorService;
import pw.cdmi.box.app.convertservice.service.ConvertService;
import pw.cdmi.box.app.convertservice.util.ConvertPropertiesUtils;
import pw.cdmi.box.app.core.alarm.CSNodeAlarm;
import pw.cdmi.box.app.system.service.CSAlarmService;

public class DealNodeStatusTread implements Runnable
{
    private static Logger                       logger      = LoggerFactory.getLogger(DealNodeStatusTread.class);
    private NodeRunningInfo                     nodeRunningInfo;
    private  CSAlarmService                      cSAlarmService;
    private  ConvertService                      convertService;
    private CSMonitorService                    cSMonitorService;
    private int                                 servicePort = 16627;
    private String                              nodeName;
    private int                                 countLimit  = 3;
	
	public DealNodeStatusTread(NodeRunningInfo nodeRunningInfo) {
		this.nodeRunningInfo = nodeRunningInfo;
		nodeName = nodeRunningInfo.getHostName() + nodeRunningInfo.getResourceGroupID();
		
		try {
			countLimit = Integer.valueOf(ConvertPropertiesUtils.getProperty("convertservice.server.alarm.count", "3", ConvertPropertiesUtils.BundleName.CONVERT));
			servicePort = Integer.valueOf(ConvertPropertiesUtils.getProperty("convertservice.server.port", "16627", ConvertPropertiesUtils.BundleName.CONVERT));
		} catch (NumberFormatException e) {
			logger.error("failed to parse configvalue to int", e);
		}
	}

    @Override
    public void run()
    {
        int status = 1;
        try
        {
            for(int i = 0; i < countLimit; i++)
            {	
            	/*
            	 *  這里的的狀態是監控預覽轉換服務器的，與dss的服務狀態無關。
            	 *  但原部署方式是dss與預覽轉換服務器合布在同一臺機器上的，在預覽轉換服務器中又對dss進行了狀態監控。
            	 *  這里處理的方式就是查看同一臺機器上dss與預覽轉換服務器是否都是活的，如果是那么狀態設置為0
            	 *  這里不應該設置為boolean方式，并真實監聽dss嗎？此次合并不優化以后再說。
            	 */
            	status = getStatus();
                if(status == 0)
                {
                    break;
                }
                Thread.sleep(5000); 
            }
            nodeRunningInfo.setStatus(status);
            cSMonitorService.setOrUpdateNodeStatus(nodeRunningInfo);
        }
        catch(Exception e)
        {
            logger.error("get status error", e);
        }
       
        if(status == 1)
        {

            {
                sendAlarm(nodeRunningInfo.getHostName(), 0);
                nodeRunningInfo.setSendAlarm(true);
                convertService.cleanTaskIp(nodeRunningInfo);
            }
        }
        else
        {
            if(nodeRunningInfo.isSendAlarm())
            {
                sendAlarm(nodeRunningInfo.getHostName(), 2);
	            convertService.renewTask(nodeRunningInfo);
            }
        }
    }

    private int getStatus()
    {
        int status = 1;
        TTransport transport = null;
        try
        {
            transport = new TSocket(nodeRunningInfo.getHostIP(), servicePort);
            TProtocol protocol = new TBinaryProtocol(transport);
            logger.info("convertService  servicePort:" + servicePort + " serviceIP:" + nodeRunningInfo.getHostIP() + " hostname:"
                    + nodeRunningInfo.getHostName());
            ConertServiceHartBeat.Client client = new ConertServiceHartBeat.Client(protocol);
            transport.open();
            String statusStr = client.getStatus();
            logger.info("the response of convertservice is :" + statusStr);
            if("SUCCESS".equals(statusStr))
            {
                status = 0;
            }
            else
            {
                status = 1;
            }
        }
        catch(Exception e)
        {
            status = 1;
            //logger.error("failed to get status.", e);
            logger.error("status error: perview service not start ! 狀態錯誤:預覽服務未啟動,如無預覽功能，此報錯可以忽略。");
        }
        finally
        {
            if(null != transport)
            {
                transport.close();
            }
        }
        logger.info("the host ip is : " + nodeRunningInfo.getHostIP() + " and the status is : " + status);
        return status;
    }

    private void sendAlarm(String hostname, int alarmType)
    {
        String alarmId = ConvertPropertiesUtils
                .getProperty("convertservice.server.alarmid", "1", ConvertPropertiesUtils.BundleName.CONVERT);
        String serviceName = ConvertPropertiesUtils
                .getProperty("convertservice.server.servicename", "convert service alarm", ConvertPropertiesUtils.BundleName.CONVERT);
        // 告警类型，0: 故障1: 事件2: 恢复3：操作日志4：运行
        // int alarmType = 0;
        // 告警级别2: 提示3: 警告5: 重要6: 紧急
        int alarmLevel = 6;
        CSNodeAlarm alarm = new CSNodeAlarm(alarmId, alarmType, alarmLevel, serviceName);
        cSAlarmService.sendUfm2CSAlarm(alarm, hostname);
    }

   
    
    public CSAlarmService getcSAlarmService()
    {
        return cSAlarmService;
    }

    public void setcSAlarmService(CSAlarmService cSAlarmService)
    {
        this.cSAlarmService = cSAlarmService;
    }

    public ConvertService getConvertService()
    {
        return convertService;
    }

    public void setConvertService(ConvertService convertService)
    {
        this.convertService = convertService;
    }

    public CSMonitorService getcSMonitorService()
    {
        return cSMonitorService;
    }

    public void setcSMonitorService(CSMonitorService cSMonitorService)
    {
        this.cSMonitorService = cSMonitorService;
    }
}
