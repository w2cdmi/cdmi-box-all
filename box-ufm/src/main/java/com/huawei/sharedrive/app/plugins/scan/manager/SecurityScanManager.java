package com.huawei.sharedrive.app.plugins.scan.manager;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.manager.PersistentEventManager;
import com.huawei.sharedrive.app.event.service.PersistentEventConsumer;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityType;
import com.huawei.sharedrive.app.plugins.scan.service.SecurityScanService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

@Component
@Lazy(false)
public class SecurityScanManager implements PersistentEventConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityScanManager.class);

	@Autowired
	private SecurityScanTaskProducer securityScanTaskProducer;

	@Autowired
	private SecurityScanService securityScanService;

	@Autowired
	private FileBaseService fileBaseService;

	@Autowired
	private PersistentEventManager persistentEventManager;
	
	@Autowired
	private INodeDAO iNodeDAO;

	// 当前任务数
	private int curTotalTasks = 0;

	// 当前统计时间
	private long curCountDate = 0L;

	// 统计间隔时间600S
	private static final long INTERVAL_TIME = Long
			.parseLong(PropertiesUtils.getProperty("security.scan.interval.time", "600000"));

	@Override
	public void consumeEvent(PersistentEvent event) {

		// 0 .都不扫描 1. 扫描kia,不杀毒 2. 不扫描kia，杀毒 3.扫描kia，杀毒
		int labelInt = 0;
		boolean securityScanEnabled = securityScanService.isSecurityScanEnable();
		if (!securityScanEnabled) {
			return;
		}
		INode node = fileBaseService.getINodeInfo(event.getOwnedBy(), event.getNodeId());
		if (node == null) {
			return;
		}
		labelInt = securityScanService.getSecurityLabel(node.getObjectId());
		SecurityStatus status = SecurityStatus.getSecurityStatus(labelInt);
		SecurityStatus ksoftStatus = SecurityStatus.getKsoftSecurityStatus(labelInt);
		if (securityScanEnabled && (status.getStatus() == SecurityStatus.KIA_UMCOMPLETED.getStatus()
				|| ksoftStatus.getStatus() == SecurityStatus.KSOFT_UMCOMPLETED.getStatus())) {
			// 判断是否已有相同文件的扫描任务存在
			boolean isTaskExist = securityScanService.isSecurityScanTaskExist(node.getObjectId(),
					node.getResourceGroupId(), event.getPriority());
			if (isTaskExist) {
				return;
			}
			// 如果扫描任务表数据量过大, 暂停添加扫描任务, 等待下次触发扫描, 高优先级的任务不受此限制
			if (event.getPriority() != SecurityScanTask.PRIORITY_HIGH && !isAllowedToAddTask()) {
				return;
			}
			securityScanTaskProducer.sendKIAScanTask(node.getId(), node.getName(), node.getObjectId(),
					node.getOwnedBy(), node.getResourceGroupId(), event.getPriority());
		}
	}

	/**
	 * 删除N天前创建的扫描任务
	 * 
	 * @param days
	 */
	public int deleteScanTaskBefore(int days) {
		return securityScanService.deleteCreatedBefore(days);
	}

	@Override
	public EventType[] getInterestedEvent() {
		// 下载请求由于需要将文件状态返回给客户端, 需要同步处理, 不在此处处理
		return new EventType[] { EventType.INODE_PRELOAD_END };
	}

	public SecurityStatus getSecurityStatus(String objectId, SecurityType type) {
		return securityScanService.getSecurityStatus(objectId, type);
	}
	
	public int getSecurityStatus(String objectId) {
		return securityScanService.getSecurityLabel(objectId);
	}

	/**
	 * 查询某一状态的扫描任务总数
	 * 
	 * @param status
	 * @return
	 */
	public int getTotalTasks(byte status) {
		return securityScanService.getTotalTasks(status);
	}

	@PostConstruct
	public void init() {
		persistentEventManager.registerConsumer(this);
	}

	/**
	 * 是否允许继续添加扫描任务
	 * 
	 * @return
	 */
	public boolean isAllowedToAddTask() {

		// 每十分钟统计一次
		if ((System.currentTimeMillis() - curCountDate) > INTERVAL_TIME) {
			curCountDate = System.currentTimeMillis();
			curTotalTasks = securityScanService.getTotalTasks(SecurityScanTask.STATUS_ALL);
		}
		// 10分钟一次代表防抖，不增加额外防抖
		if (curTotalTasks < SecurityScanTask.MAX_TASK_NUM) {
			return true;
		}
		return false;
	}

	public boolean isSecurityScanEnable() {
		return securityScanService.isSecurityScanEnable();
	}

	/**
	 * 发送扫描任务, 返回该文件安全状态
	 * 
	 * @param node
	 * @param type
	 * @param priority
	 * @return
	 */
	public int sendScanTask(INode node, int priority) {
		// 0 .都不扫描 1. 扫描kia,不杀毒 2. 不扫描kia，杀毒 3.扫描kia，杀毒
		int labelInt = 0;
		boolean securityScanMode = securityScanService.isSecurityScanEnable();
		if (!securityScanMode) {
			return labelInt;
		}

		labelInt = securityScanService.getSecurityLabel(node.getObjectId());
		SecurityStatus status = SecurityStatus.getSecurityStatus(labelInt);
		SecurityStatus ksoftStatus = SecurityStatus.getKsoftSecurityStatus(labelInt);
		if (securityScanMode && (status.getStatus() == SecurityStatus.KIA_UMCOMPLETED.getStatus()
				|| ksoftStatus.getStatus() == SecurityStatus.KSOFT_UMCOMPLETED.getStatus())) {
			// 判断是否已有相同文件的扫描任务存在
			boolean isTaskExist = securityScanService.isSecurityScanTaskExist(node.getObjectId(),
					node.getResourceGroupId(), priority);
			if (isTaskExist) {
				return 0;
			}
			// 如果扫描任务表数据量过大, 暂停添加扫描任务, 等待下次触发扫描, 高优先级的任务不受此限制
			if (priority != SecurityScanTask.PRIORITY_HIGH && !isAllowedToAddTask()) {
				return 0;
			}
			securityScanTaskProducer.sendKIAScanTask(node.getId(), node.getName(), node.getObjectId(),
					node.getOwnedBy(), node.getResourceGroupId(), priority);
		}
		return labelInt;
	}
	
	/**
	 * 发送扫描任务, 返回该文件安全状态
	 * 
	 * @param node
	 * @param type
	 * @param priority
	 * @return
	 */
	public void sendScanTask(INode node, int securityLabel, int priority) {
		// 0 .都不扫描 1. 扫描kia,不杀毒 2. 不扫描kia，杀毒 3.扫描kia，杀毒
		boolean secureScanEnabled = securityScanService.isSecurityScanEnable();
		if (!secureScanEnabled) {
			return;
		}
		
		SecurityStatus status = SecurityStatus.getSecurityStatus(securityLabel);
		SecurityStatus ksoftStatus = SecurityStatus.getKsoftSecurityStatus(securityLabel);
		if (secureScanEnabled && 
				(status.getStatus() == SecurityStatus.KIA_UMCOMPLETED.getStatus()
				|| ksoftStatus.getStatus() == SecurityStatus.KSOFT_UMCOMPLETED.getStatus()
				|| 0 == node.getKiaLabel())) {
			// 判断是否已有相同文件的扫描任务存在
			boolean isTaskExist = securityScanService.isSecurityScanTaskExist(node.getObjectId(),
					node.getResourceGroupId(), priority);
			if (isTaskExist) {
				return;
			}
			// 如果扫描任务表数据量过大, 暂停添加扫描任务, 等待下次触发扫描, 高优先级的任务不受此限制
			if (priority != SecurityScanTask.PRIORITY_HIGH && !isAllowedToAddTask()) {
				return;
			}
			securityScanTaskProducer.sendKIAScanTask(node.getId(), node.getName(), node.getObjectId(),
					node.getOwnedBy(), node.getResourceGroupId(), priority);
		}
	}

	/**
	 * 系统管理员触发扫描后, 调用此方法发送扫描任务
	 * 
	 * @param node
	 * @param type
	 * @param priority
	 * @return
	 */
	public int sendSystemScanTask(INode node, int priority) {
		int labelInt = 0;
		boolean securityScanMode = securityScanService.isSecurityScanEnable();
		if (!securityScanMode) {
			return labelInt;
		}

		labelInt = securityScanService.getSecurityLabel(node.getObjectId());
		SecurityStatus status = SecurityStatus.getSecurityStatus(labelInt);
		SecurityStatus ksoftStatus = SecurityStatus.getKsoftSecurityStatus(labelInt);
		if (securityScanMode && (status.getStatus() == SecurityStatus.KIA_UMCOMPLETED.getStatus()
				|| ksoftStatus.getStatus() == SecurityStatus.KSOFT_UMCOMPLETED.getStatus())) {
			// 判断是否已有相同文件的扫描任务存在
			boolean isTaskExist = securityScanService.isSecurityScanTaskExist(node.getObjectId(),
					node.getResourceGroupId(), priority);
			if (isTaskExist) {
				return 0;
			}
			securityScanTaskProducer.sendKIAScanTask(node.getId(), node.getName(), node.getObjectId(),
					node.getOwnedBy(), node.getResourceGroupId(), priority);
		}
		return labelInt;

	}

	/**
	 * 更新文件安全标识
	 * 
	 * @param securityLabel
	 * @param objectId
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSecurityLabel(SecurityType type, int securityLabel, String objectId) {
		// 更新安全标识
		int result = securityScanService.updateSecurityLabel(type, securityLabel, objectId);
		// 更新KIA标示
		updateINodeKIALabel(objectId, securityLabel);
		// 删除安全扫描任务
		securityScanService.deleteScanTaskByObjectId(objectId);

		if (result == 0) {
			LOGGER.error("Object not exist. Object id: {}", objectId);
			throw new NoSuchFileException("File not exist");
		}
	}

	/**
	 * 更新INODE 中的KIA标示
	 * 
	 * @param objectId
	 * @param securityLabel
	 */
	private void updateINodeKIALabel(String objectId, int securityLabel) {
		// 更新INODE标示
		List<Long> lstOwneId = securityScanService.getOwnedByObjectId(objectId);
		if (null == lstOwneId || lstOwneId.isEmpty()) {
			LOGGER.warn("The task  not found  by objectId in the table ,objectId" + objectId);
			return;
		}

		int iRet;
		for (Long ownedBy : lstOwneId) {
			iRet = securityScanService.updateINodeKIAStatus(ownedBy, objectId, securityLabel);
			if (0 == iRet) {
				LOGGER.warn("update failed,ownedBy=" + ownedBy + ",objectId:" + objectId + ",securityLabel:"
						+ securityLabel);
			}

		}
		LOGGER.warn("updateINodeKIALabel end:" + objectId + ",securityLabel:" + securityLabel);

	}
}
