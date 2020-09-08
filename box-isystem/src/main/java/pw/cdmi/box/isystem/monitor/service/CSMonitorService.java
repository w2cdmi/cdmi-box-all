package pw.cdmi.box.isystem.monitor.service;

import java.util.List;

import com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo;

import pw.cdmi.box.isystem.monitor.domain.CSNodeRunningInfo;

/**
 * ConvertService的web页面监控服务类
 * @author Administrator
 *
 */
public interface CSMonitorService
{
	/**
	 * 获取所有的convertservice的系统名称和状态
	 * @return 系统列表
	 */
	List<SystemClusterInfo> getAllCSSystemName();
	
	/**
	 * 通过系统名称获取系统状态
	 * @param csSystemName 系统名称
	 * @return  系统状态
	 */
	int getCurrentSystemStatus(String csSystemName);

	/**
	 * 获取某一系统下所有节点的状态
	 * @param csSystemName 系统名称
	 * @return 所有节点的状态
	 */
	List<CSNodeRunningInfo> getCSClusterNodes(String csSystemName);
}
