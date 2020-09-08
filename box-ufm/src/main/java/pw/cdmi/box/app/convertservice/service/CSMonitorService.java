package pw.cdmi.box.app.convertservice.service;

import java.util.List;

import pw.cdmi.box.app.convertservice.domain.NodeRunningInfo;

public interface CSMonitorService
{
	/**
	 * 从本地数据库中查出所有的dss节点信息(DSS和convertservice节点合设)
	 * @return 所有节点信息
	 */
	List<NodeRunningInfo> getAllCSNodeInfo();
	
	/**
	 * 设置或者更新节点状态
	 * @param nodeRunningInfos 节点信息，含最新的状态
	 */
	void setOrUpdateNodeStatus(NodeRunningInfo nodeRunningInfo);
	
	/**
	 * 从convertservice监控节点的状态
	 * @param nodeRunningInfos  需要监控的节点信息
	 * @return 节点信息，含最新的状态
	 */
	List<NodeRunningInfo> getNormalNodes(String resourceGroupID);

	/**
	 * 由于是通过负载启动了n个节点，所以会启动n个定时任务，此处主要是要保证同时只有一个定时任务在运行。
	 * 各个节点会先去获取定时任务运行权限，谁先获取到，先将信息记录到zookeeper中，然后定时更新信息，
	 * 其他的节点每隔一定时间，去查询信息，如果线程被占用，继续等待，如果没有则占用定时任务线程，并更新信息。
	 */
	void getTask();
	
}
