package pw.cdmi.file.engine.filesystem.manage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.spring.ext.DoAfterSpringLoadComplete;
import pw.cdmi.core.spring.ext.DoAfterSpringLoadExecutor;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.core.job.ThreadPool;
import pw.cdmi.file.engine.filesystem.FileSystemFactory;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.filesystem.exception.RuntimeFSException;
import pw.cdmi.file.engine.filesystem.exception.UnknownFSException;
import pw.cdmi.file.engine.filesystem.exception.WrongFileSystemArgsException;
import pw.cdmi.file.engine.filesystem.manage.cache.FSEndpointCache;
import pw.cdmi.file.engine.filesystem.model.FSDefinition;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.filesystem.model.FSEndpointStatus;
import pw.cdmi.file.engine.filesystem.support.service.FSEndpointService;

public class FSEndpointManager implements ConfigListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(FSEndpointManager.class);
	private static final String CONFIG_ZOOKEEPER_KEY_FSENDPOINT = "config.zookeeper.key.fsendpoint";

	@Autowired
	private FSEndpointService fsEndpointService;

	@Autowired
	private ConfigManager configManager;

	@PostConstruct
	public void init() {
		DoAfterSpringLoadComplete.regiestExecutor(new DoAfterSpringLoadExecutor("Load FSEndpoint") {
			public void execute() {
				FSEndpointManager.this.refreshFSEndpointInfo();
			}
		});
	}

	public void configChanged(String key, Object value) {
		LoggerUtil.regiestThreadLocalLogWithPrefix("Refresh-FSEndpoint-Notify-");
		if (!CONFIG_ZOOKEEPER_KEY_FSENDPOINT.equals(key)) {
			return;
		}
		LOGGER.info("Refresh FSEndpoint By Cluster Notify.");

		final String logId = LoggerUtil.getCurrentLogID();

		new Thread(new Runnable() {
			public void run() {
				LoggerUtil.regiestThreadLocalLog();
				LoggerUtil.regiestThreadLocalLog(logId);
				FSEndpointManager.this.refreshFSEndpointInfo();
			}
		}).start();
	}

	public FSEndpoint addFSEndpoint(FSEndpoint endpoint) throws FSException {
		if (!checkEndpoint(endpoint)) {
			String message = "Endpoint is Null Or Unsupported";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
			}
			throw new WrongFileSystemArgsException(message,
					new String[] { endpoint != null ? endpoint.toString() : null });
		}

		try {
			FSEndpoint fsEndpoint = this.fsEndpointService.addFSEndpoint(createEndpoint(endpoint));

			notifyCluster();

			return fsEndpoint;
		} catch (DuplicateKeyException e) {
			String errorMessage = "Failed To create addStorageResource. Endpoint Already Userd by another Tenant.";
			LOGGER.error(errorMessage);

			throw new UnknownFSException(errorMessage, e);
		}
	}

	public List<FSEndpoint> getAllFSEndpoint() {
		refreshFSEndpointInfo();

		List<FSEndpoint> endpoints = FSEndpointCache.getAllEndpoints();

		if (endpoints.isEmpty()) {
			String message = "No Endpoint On This DataService, Please Add FSEndpoint For This DataService.";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
			}
		}

		return endpoints;
	}

	public FSEndpoint getFSEndpoint(String endpointID) {
		return this.fsEndpointService.getFSEndpoint(endpointID);
	}

	public void disableFSEndpoint(String endpointID) {
		LOGGER.info("Start Disabe FSEndpoint [ " + endpointID + " ] ");

		FSEndpoint endpoint = this.fsEndpointService.getFSEndpoint(endpointID);
		if (endpoint == null) {
			return;
		}

		this.fsEndpointService.updateFSEndpointStatus(endpoint, FSEndpointStatus.DISABLED);

		notifyCluster();
	}

	public void enableStorageResource(String endpointID) {
		FSEndpoint endpoint = this.fsEndpointService.getFSEndpoint(endpointID);
		if (endpoint == null) {
			return;
		}

		this.fsEndpointService.updateFSEndpointStatus(endpoint, FSEndpointStatus.ENABLE);

		notifyCluster();
	}

	public void updateFSEndpoint(FSEndpoint endpoint) throws FSException {
		FSEndpoint temp = this.fsEndpointService.getFSEndpoint(endpoint.getId());

		if (temp == null) {
			String message = "Endpoint Is Null [ " + endpoint.toString() + " ]";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
			}
			throw new WrongFileSystemArgsException(message);
		}

		endpoint.setFsType(temp.getFsType());
		if (endpoint.getMaxUtilization() == null) {
			endpoint.setMaxUtilization(temp.getMaxUtilization());
		}

		if (endpoint.getRetrieval() == null) {
			endpoint.setRetrieval(temp.getRetrieval());
		}

		if (StringUtils.isBlank(endpoint.getEndpoint())) {
			endpoint.setEndpoint(temp.getEndpoint());
		}

		FSDefinition fsDefinition = FSDefinition.findFSDefinition(endpoint.getFsType());
		endpoint = FileSystemFactory.getInstance(fsDefinition).getFSManager().updateFSEndpoint(endpoint);

		this.fsEndpointService.updateFSEndpoint(endpoint);

		notifyCluster();
	}

	public void updateFSEndpointExtAttribute(FSEndpoint endpoint) throws FSException {
		FSEndpoint temp = this.fsEndpointService.getFSEndpoint(endpoint.getId());

		if (temp == null) {
			String message = "Endpoint Is Null [ " + endpoint.toString() + " ]";
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn(message);
			}
			throw new WrongFileSystemArgsException(message);
		}

		if (endpoint.getBestStartRange() == null) {
			endpoint.setBestStartRange(temp.getBestStartRange());
		}
		if (endpoint.getBestEndRange() == null) {
			endpoint.setBestEndRange(temp.getBestEndRange());
		}
		if (endpoint.getMultipartFirst() == null) {
			endpoint.setMultipartFirst(temp.getMultipartFirst());
		}

		this.fsEndpointService.updateFSEndpointExtAttribute(endpoint);
	}

	public void removeFSEndpoint(String endpointID) throws FSException {
		FSEndpoint endpoint = this.fsEndpointService.getFSEndpoint(endpointID);
		if (endpoint == null) {
			return;
		}

		if (FSEndpointStatus.NOT_ENABLED != endpoint.getStatus()) {
			String message = "Cann't Remove An Enable FSEndpoint.";
			LOGGER.warn(message);
			throw new FSException(message);
		}

		this.fsEndpointService.deleteFSEndpoint(endpoint);

		notifyCluster();
	}

	public void refreshFSEndpointInfo() {
		final String logId = LoggerUtil.getCurrentLogID();

		final CountDownLatch latch = new CountDownLatch(1);

		ThreadPool.execute(new Runnable() {
			public void run() {
				LoggerUtil.regiestThreadLocalLog(logId);
				try {
					FSEndpointManager.this.doRefresh();
				} catch (FSException e) {
					FSEndpointManager.LOGGER.error("DoRefresh Failed.", e);
					throw new RuntimeFSException(e);
				}
				latch.countDown();
			}

		});
		try {
			if (!latch.await(
					SystemConfigContainer.getInteger("fs.storage.check.timeout", Integer.valueOf(55)).intValue(),
					TimeUnit.SECONDS)) {
				LOGGER.warn("refresh endpoint info timeout.");
			}
		} catch (InterruptedException e) {
			String message = "RefreshStorageResourceInfo State Failed.";
			LOGGER.error(message, e);
			throw new UnknownFSException(message, e);
		}
	}

	private void doRefresh() throws FSException {
		List<FSEndpoint> allEndpoints = this.fsEndpointService.getAllFSEndpointsForCurrentDevice();
		if ((allEndpoints == null) || (allEndpoints.isEmpty())) {
			FSEndpointCache.clearAll();
			return;
		}

		Map<String, FSEndpoint> result = checkAllFSEndpointState(allEndpoints);
		if (result.isEmpty()) {
			LOGGER.warn("refresh result is empty.");
			return;
		}

		List<FSEndpoint> cacheList = new ArrayList<FSEndpoint>(allEndpoints.size());

		FSEndpoint temp = null;
		for (FSEndpoint endpoint : allEndpoints) {
			temp = (FSEndpoint) result.get(endpoint.getId());
			if (temp == null) {
				temp = FSEndpointCache.getFSEndpoint(endpoint.getFsType(), endpoint.getId());
			}

			if (temp != null) {
				cacheList.add(temp);
			}
		}

		FSEndpointCache.refreshLocalCache(cacheList);

		for (FSEndpoint endpoint : result.values()) {
			this.fsEndpointService.updateFSEndpointsForCurrentDevice(endpoint);
		}
	}

	private static FSEndpoint createEndpoint(FSEndpoint endpoint) throws FSException {
		FSDefinition fsDefinition = FSDefinition.findFSDefinition(endpoint.getFsType());

		endpoint.setFsType(fsDefinition.getType());

		return FileSystemFactory.getInstance(fsDefinition).getFSManager().createFSEndpoint(endpoint);
	}

	private static boolean checkEndpoint(FSEndpoint endpoint) {
		if (endpoint == null) {
			LOGGER.warn("endpoint is empty.");
			return false;
		}

		List<String> allSupportFSType = getAllSupportFSType();

		FSDefinition definition = FSDefinition.findFSDefinition(endpoint.getFsType());
		if (definition == null) {
			LOGGER.warn("Unsupport fileSystem Type [" + endpoint + " ] ");
			return false;
		}

		if (!allSupportFSType.contains(definition.getType())) {
			LOGGER.warn("Unsupport fileSystem Type [" + endpoint + " ] ");
			return false;
		}

		endpoint.setFsType(definition.getType());

		return true;
	}

	private static List<String> getAllSupportFSType() {
		String temp = SystemConfigContainer.getConfigValue("support.filesystem.types");
		return Arrays.asList(StringUtils.lowerCase(temp).split(";"));
	}

	private static Map<String, FSEndpoint> checkAllFSEndpointState(List<FSEndpoint> allEndpoints) throws FSException {
		CountDownLatch latch = new CountDownLatch(allEndpoints.size());

		List<RefreshFuture> refreshResult = new ArrayList<RefreshFuture>(allEndpoints.size());

		RefreshFuture refreshFuture = null;
		for (FSEndpoint endpoint : allEndpoints) {
			refreshFuture = new RefreshFuture(endpoint);
			refreshResult.add(refreshFuture);
			startNewRefreshThread(latch, refreshFuture);
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			String message = "checkAllFSEndpointState State Failed.";
			LOGGER.error(message, e);
			throw new UnknownFSException(message, e);
		}

		Map<String, FSEndpoint> result = new HashMap<String, FSEndpoint>(allEndpoints.size());

		for (RefreshFuture future : refreshResult) {
			if (future.getAfterObj() != null) {
				result.put(future.getAfterObj().getId(), future.getAfterObj());
			}
		}

		return result;
	}

	private static void startNewRefreshThread(final CountDownLatch latch, final RefreshFuture refreshFuture) {
		final String logId = LoggerUtil.getCurrentLogID();

		ThreadPool.execute(new Runnable() {
			public void run() {
				LoggerUtil.regiestThreadLocalLog(logId);
				try {
					refreshFuture.setAfterObj(FileSystemFactory.getInstance(refreshFuture.getBeforeObj()).getFSManager()
							.refreshFSEndpoint(refreshFuture.getBeforeObj()));
				} catch (Exception e) {
					FSEndpointManager.LOGGER.error("refreshFSEndpoint Failed", e);
				} finally {
					latch.countDown();
				}
			}
		});
	}

	private void notifyCluster() {
		try {
			this.configManager.setConfig(CONFIG_ZOOKEEPER_KEY_FSENDPOINT, "");
		} catch (Exception e) {
			LOGGER.error("Notify to Cluster Failed.", e);
			throw new RuntimeFSException(e);
		}
	}
}