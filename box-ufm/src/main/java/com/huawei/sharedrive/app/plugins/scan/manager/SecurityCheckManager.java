package com.huawei.sharedrive.app.plugins.scan.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.exception.FileScanningException;
import com.huawei.sharedrive.app.exception.ScannedForbiddenException;
import com.huawei.sharedrive.app.exception.VirusForbiddenException;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.service.SecurityScanService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

@Component
@Lazy(false)
public class SecurityCheckManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityCheckManager.class);

	private static final boolean IGNORE_SCAN_RESULT = Boolean
			.parseBoolean(PropertiesUtils.getProperty("security.scan.ignore.result", "true"));

	@Autowired
	private SecurityScanManager securityScanManager;

	@Autowired
	private SecurityScanService securityScanService;

	@Autowired
	private INodeDAOV2 iNodeDAOV2;

	public void checkSecurityStatus(INode node, boolean checkKia, boolean checkKsoft) {

		if (FilesCommonUtils.isFolderType(node.getType())) {
			List<INode> lst = iNodeDAOV2.getAllNormalINodeByParent(node);
			for (INode tmpNode : lst) {
				checkSecurityStatus(tmpNode, checkKia, checkKsoft);
			}
		} else if (node.getType() == INode.TYPE_FILE) {
			int secLabel = securityScanService.getSecurityLabel(node.getObjectId());
			doCheckSecurityStatus(node, secLabel, checkKia, checkKsoft);
		}

	}

	private void doCheckSecurityStatus(INode node, int secLabel, boolean checkKia, boolean checkKsoft) {
		// 触发安全扫描
		LOGGER.info("checkSecurityStatus: node name is {} ;", node.getName());
		securityScanManager.sendScanTask(node, secLabel, SecurityScanTask.PRIORITY_HIGH);

		// 不做安全检查，忽略
		if (IGNORE_SCAN_RESULT) {
			return;
		}

		if (checkKia) {
			SecurityStatus status = SecurityStatus.getSecurityStatus(secLabel);
			switch (status) {
//			case KIA_UMCOMPLETED:
//				throw new FileScanningException("File is not ready");
			case KIA_COMPLETED_INSECURE:
				throw new ScannedForbiddenException("This file is not allowed to be downloaded");
			default:
				break;
			}
		}
		if (checkKsoft) {
			SecurityStatus status = SecurityStatus.getKsoftSecurityStatus(secLabel);
			switch (status) {
			case KSOFT_COMPLETED_INSECURE:
				throw new VirusForbiddenException("This file is not allowed to be downloaded");
			default:
				break;
			}
		}
	}
}
