package pw.cdmi.file.engine.filesystem.manage;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.utils.BaseConvertUtils;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.filesystem.exception.NoAvailableFileSystemException;
import pw.cdmi.file.engine.filesystem.manage.cache.FSEndpointCache;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public final class FSEndpointSelector {
	private static final Logger LOGGER = LoggerFactory.getLogger(FSEndpointSelector.class);
	private static SelectAlgorithm selectAlgorithm;

	public static FSEndpoint assignWriteAbleStorage(Object condition) throws NoAvailableFileSystemException {
		if (selectAlgorithm == null) {
			initSelectAlgorithm();
		}

		List<FSEndpoint> allWriteAble = FSEndpointCache.getAllWriteAbleEndpoints();
		if (CollectionUtils.isEmpty(allWriteAble)) {
			LOGGER.warn("no writeable filesystem for object [ {} ]", ReflectionToStringBuilder.toString(condition));

			allWriteAble = FSEndpointCache.getAllEnableEndpoints();
		}

		if (CollectionUtils.isEmpty(allWriteAble)) {
			String message = "no enable filesystem for object [ " + ReflectionToStringBuilder.toString(condition)
					+ " ]";
			LOGGER.warn(message);
			throw new NoAvailableFileSystemException(message);
		}

		return selectAlgorithm.select(allWriteAble, condition);
	}

	private static void initSelectAlgorithm() {
		selectAlgorithm = (SelectAlgorithm) BaseConvertUtils.toObjectInstance(
				SystemConfigContainer.getConfigValue("fs.endpoint.select.algorithm"), new RandomSelectAlgorithm());
	}
}