package com.huawei.sharedrive.app.plugins.scan.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.mirror.manager.MirrorObjectManager;
import com.huawei.sharedrive.app.plugins.scan.dao.SecurityScanTaskDAO;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityType;
import com.huawei.sharedrive.app.plugins.scan.service.SecurityScanService;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.utils.DateUtils;

@Service("securityScanService")
public class SecurityScanServiceImpl implements SecurityScanService {
	private static final int BASE_MASK = 15;

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityScanServiceImpl.class);

	private static final int DEFAULT_TIME_OUT = 300;

	// 安全扫描引擎版本配置key
	private static final String SECURITY_SCAN_ENGINE_VERSION_KEY = "security.scan.engine.version";

	// 是否开启安全扫描功能配置key
	private static final String SECURITY_SCAN_ENABLE_KEY = "security.scan.enable";

	// 安全扫描超时时间配置key
	private static final String SECURITY_SCAN_TIMEOUT = "security.scan.timeout.seconds";

	@Autowired
	private ObjectReferenceDAO objectReferenceDAO;

	@Autowired
	private SecurityScanTaskDAO securityScanTaskDAO;

	@Autowired
	private SystemConfigDAO systemConfigDAO;

	@Autowired
	private INodeDAOV2 iNodeDAOV2;

	@Autowired
	private MirrorObjectManager mirrorObjectManager;

	@Override
	public void createScanTask(SecurityScanTask task) {
		securityScanTaskDAO.create(task);

	}

	@Override
	public int deleteCreatedBefore(int days) {
		Date now = new Date();
		Date before = DateUtils.getDateBefore(now, days);
		return securityScanTaskDAO.deleteCreatedBefore(before);
	}

	@Override
	public int deleteScanTask(String taskId) {
		return securityScanTaskDAO.delete(taskId);
	}

	@Override
	public int deleteScanTaskByObjectId(String objectId) {
		return securityScanTaskDAO.deleteByObjectId(objectId);
	}

	@Override
	public SecurityStatus getSecurityStatus(String objectId, SecurityType type) {
		ObjectReference objectReference = objectReferenceDAO.get(objectId);
		if (objectReference == null) {
			String message = "File not exist, objectId id:" + objectId;
			throw new NoSuchFileException(message);
		}
		if (SecurityType.CONFIDENTIAL == type) {
			return getKIASecurityStatus(objectReference);
		}
		if (SecurityType.KSOFT == type) {
			return getKsoftSecurityStatus(objectReference);
		}
		throw new InvalidParamException("Unsupported security type " + type.getType());
	}

	@Override
	public int getSecurityLabel(String objectId) {
		ObjectReference objectReference = objectReferenceDAO.get(objectId);
		if (objectReference == null) {
			String message = "File not exist, objectId id:" + objectId;
			throw new NoSuchFileException(message);
		}
		return getSecurityLabelHelper(objectReference);
	}

	@Override
	public int getTotalTasks(byte status) {
		return securityScanTaskDAO.getTotalTasks(status);
	}

	@Override
	public boolean isSecurityScanEnable()
    {
        SystemConfig systemConfig = systemConfigDAO.get(SECURITY_SCAN_ENABLE_KEY);
        
        //兼容数据库中的值
        if (systemConfig == null || StringUtils.isBlank(systemConfig.getValue())
				|| "0".equals(systemConfig.getValue()) 
				) {
			return false;
		}
        if (systemConfig.getValue().length() == 1) {
			return true;
		}
        
        return Boolean.parseBoolean(systemConfig.getValue());
    }
	
	@Override
	public int getSecurityScanMode() {
		SystemConfig systemConfig = systemConfigDAO.get(SECURITY_SCAN_ENABLE_KEY);
		if (systemConfig == null || StringUtils.isBlank(systemConfig.getValue())
				|| "0".equals(systemConfig.getValue()) 
				) {
			return 0;
		}
		//兼容旧版本中，kia配置为true或false的情形。
		if("false".equals(systemConfig.getValue())){
			return 0;
		}
		if("true".equals(systemConfig.getValue()))
		{
			return 1;
		}
		
		return Integer.parseInt(systemConfig.getValue());
	}
	
	@Override
	public boolean isSecurityScanTaskExist(String objectId, int dssId, int priority) {
		List<SecurityScanTask> list = securityScanTaskDAO.getByObjectIdAndDSSId(objectId, dssId);
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		SystemConfig systemConfig = systemConfigDAO.get(SECURITY_SCAN_TIMEOUT);
		int timeout = systemConfig != null ? Integer.parseInt(systemConfig.getValue()) : DEFAULT_TIME_OUT;
		Date timeoutDate = null;
		for (SecurityScanTask task : list) {
			// 扫描超时时间
			timeoutDate = DateUtils.getDateAfterSeconds(task.getModifiedAt(), timeout);

			// 已存在的扫描任务还未超时且任务优先级不低于当前任务
			if (timeoutDate.after(new Date()) && task.getPriority() >= priority) {
				LOGGER.info(
						"A same task exist. Task id: {}, object id: {}, dss id: {}, priority: {}; Current task priority: {}",
						task.getTaskId(), task.getObjectId(), task.getDssId(), task.getPriority(), priority);
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional
	public int updateSecurityLabel(SecurityType securityType, int securityLabel, String objectId) {
		Integer currentLabel = objectReferenceDAO.getSecurityLabelForUpdate(objectId);
		currentLabel = currentLabel == null ? Integer.valueOf(0) : currentLabel;

		// 计算公式为((~mask) & currentLabel) | (mask & label)
		int mask = getMask(securityType);
		int newLabel = ((~mask) & currentLabel) | (mask & securityLabel);

		// 当前版本号
		SystemConfig systemConfig = systemConfigDAO.get(SECURITY_SCAN_ENGINE_VERSION_KEY);

		updateMirrorSecurityLabel(newLabel, systemConfig.getValue(), objectId);
		return objectReferenceDAO.updateSecurityLabel(newLabel, systemConfig.getValue(), objectId);
	}

	private void updateMirrorSecurityLabel(int securityLabel, String securityVersion, String parentObjectId) {
		List<String> lstDestObjectId = mirrorObjectManager.getDestObjectIdBySrcObjectId(parentObjectId);
		if (null == lstDestObjectId || lstDestObjectId.isEmpty()) {
			return;
		}
		for (String destObjectId : lstDestObjectId) {
			if (1 != objectReferenceDAO.updateSecurityLabel(securityLabel, securityVersion, destObjectId)) {
				LOGGER.error("copy SercurityLabel error from " + parentObjectId + " to " + destObjectId);
			}
		}
	}

	private SecurityStatus getKIASecurityStatus(ObjectReference objectReference) {
		String version = objectReference.getSecurityVersion();
		Integer label = objectReference.getSecurityLabel();

		// 安全标识或安全引擎版本为空, 则还未执行扫描
		if (StringUtils.isBlank(version) || label == null) {
			return SecurityStatus.KIA_UMCOMPLETED;
		}

		// 安全引擎版本与系统安全引擎版本不符, 则还未执行扫描
		SystemConfig systemConfig = systemConfigDAO.get(SECURITY_SCAN_ENGINE_VERSION_KEY);
		if (!version.equals(systemConfig.getValue())) {
			return SecurityStatus.KIA_UMCOMPLETED;
		}

		return SecurityStatus.getSecurityStatus(label);
	}

	private int getSecurityLabelHelper(ObjectReference objectReference) {
		String version = objectReference.getSecurityVersion();
		Integer label = objectReference.getSecurityLabel();

		// 安全标识或安全引擎版本为空, 则还未执行扫描
		if (StringUtils.isBlank(version) || label == null) {
			return 0;
		}

		// 安全引擎版本与系统安全引擎版本不符, 则还未执行扫描
		SystemConfig systemConfig = systemConfigDAO.get(SECURITY_SCAN_ENGINE_VERSION_KEY);
		if (!version.equals(systemConfig.getValue())) {
			return 0;
		}

		return label;
	}

	private SecurityStatus getKsoftSecurityStatus(ObjectReference objectReference) {
		String version = objectReference.getSecurityVersion();
		Integer label = objectReference.getSecurityLabel();

		// 安全标识或安全引擎版本为空, 则还未执行扫描
		if (StringUtils.isBlank(version) || label == null) {
			return SecurityStatus.KSOFT_UMCOMPLETED;
		}

		// 安全引擎版本与系统安全引擎版本不符, 则还未执行扫描
		SystemConfig systemConfig = systemConfigDAO.get(SECURITY_SCAN_ENGINE_VERSION_KEY);
		if (!version.equals(systemConfig.getValue())) {
			return SecurityStatus.KSOFT_UMCOMPLETED;
		}

		return SecurityStatus.getKsoftSecurityStatus(label);
	}

	private int getMask(SecurityType securityType) {
		// CONFIDENTIAL mask 为 00000011, 新增的检测项依次左移两位
		if (SecurityType.CONFIDENTIAL == securityType) {
			return BASE_MASK;
		}
		throw new InvalidParamException("Invalid attribute " + securityType.getType());
	}

	@Override
	public List<Long> getOwnedByObjectId(String objectId) {

		return securityScanTaskDAO.getOwnedByByObjectId(objectId);
	}

	@Override
	@Transactional
	public int updateINodeKIAStatus(long ownedBy, String objectId, int kiaStatus) {
		SystemConfig systemConfig = systemConfigDAO.get(SECURITY_SCAN_ENGINE_VERSION_KEY);
		String securityVersion = systemConfig.getValue();
		try {
			int ver = Integer.parseInt(securityVersion);
			return iNodeDAOV2.updateKiaLabel(ownedBy, objectId, INode.buildKIALabel(ver, kiaStatus));

		} catch (Exception e) {
			LOGGER.warn("update inode KIALabel error,msg" + e.getMessage(), e);
			return 0;
		}

	}
}
