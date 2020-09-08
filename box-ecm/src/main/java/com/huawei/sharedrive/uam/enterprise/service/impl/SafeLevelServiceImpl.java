package com.huawei.sharedrive.uam.enterprise.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterprise.dao.AccessConfigDao;
import com.huawei.sharedrive.uam.enterprise.dao.ResourceStrategyDao;
import com.huawei.sharedrive.uam.enterprise.dao.SafeLevelDao;
import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.service.SafeLevelService;
import com.huawei.sharedrive.uam.exception.ForbiddenException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;

@Component
public class SafeLevelServiceImpl implements SafeLevelService {

	@Autowired
	private ResourceStrategyDao resourceStrategyDao;

	@Autowired
	private SafeLevelDao safeLevelDao;

	@Autowired
	private AccessConfigDao accessConfigDao;

	@Override
	public long create(SafeLevel safeLevel, List<ResourceStrategy> listStr) {
		Date now = new Date();
		safeLevel.setCreatedAt(now);
		safeLevel.setModifiedAt(now);
		return safeLevelDao.create(safeLevel);
	}

	@Override
	public boolean isDuplicateValues(SafeLevel safeLevel) {
		return safeLevelDao.isDuplicateValues(safeLevel);
	}

	@Override
	public List<SafeLevel> getFilterdList(SafeLevel safeLevel) {

		List<SafeLevel> content = safeLevelDao.getFilterd(safeLevel, null, null);
		return content;

	}

	@Override
	public Page<SafeLevel> getFilterd(SafeLevel safeLevel, PageRequest pageRequest) {

		int total = safeLevelDao.getFilterdCount(safeLevel);
		List<SafeLevel> content = safeLevelDao.getFilterd(safeLevel, pageRequest.getOrder(), pageRequest.getLimit());

		for (SafeLevel sr : content) {
			sr.setSafeLevelName(HtmlUtils.htmlEscape(sr.getSafeLevelName()));
			sr.setSafeLevelDesc(HtmlUtils.htmlEscape(sr.getSafeLevelDesc()));
		}
		Page<SafeLevel> page = new PageImpl<SafeLevel>(content, pageRequest, total);
		return page;

	}

	@Override
	public SafeLevel getById(long id) {

		return safeLevelDao.getById(id);
	}

	@Override
	public void updateSecurityRole(SafeLevel safeLevel) {
		Date now = new Date();
		safeLevel.setModifiedAt(now);
		safeLevelDao.updateEnterpriseInfo(safeLevel);
	}

	@Override
	public long getByDomainExclusiveId(SafeLevel safeLevel) {
		return safeLevelDao.getByDomainExclusiveId(safeLevel);
	}

	@Override
	public boolean isDuplicateNetConfigValues(SafeLevel safeLevel) {
		return safeLevelDao.isDuplicateValues(safeLevel);
	}

	@Override
	public void delete(long id) {
		safeLevelDao.delete(id);
	}

	@Override
	public List<ResourceStrategy> getFilterdList(ResourceStrategy safeLevel) {

		List<ResourceStrategy> content = resourceStrategyDao.getFilterd(safeLevel, null, null);
		return content;
	}

	@Override
	public ResourceStrategy getResourceStrategyByCondition(ResourceStrategy resourceStrategy) {
		List<ResourceStrategy> list = resourceStrategyDao.getResourceStrategyByCondition(resourceStrategy);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public void deleteByCondition(ResourceStrategy resourceStrategy) {
		resourceStrategyDao.deleteByContidion(resourceStrategy);

	}

	@Override
	public void delete(SafeLevel safeLevel) {
		long accountId = safeLevel.getAccountId();
		long safeLevelId = safeLevel.getId();

		checkAccessConfigIsConfigured(accountId, safeLevelId);

		checkResourceStrategyIsConfigured(accountId, safeLevelId);

		safeLevelDao.delete(safeLevel.getId());

	}

	private void checkResourceStrategyIsConfigured(long accountId, long safeLevelId) {
		ResourceStrategy resourceStrategy = new ResourceStrategy();
		resourceStrategy.setAccountId(accountId);
		resourceStrategy.setResourceSecurityLevelId(safeLevelId);

		int resourceStrategyCount = resourceStrategyDao.getFilterdCount(resourceStrategy);
		if (resourceStrategyCount > 0) {
			resourceStrategyDao.deleteStrategy(safeLevelId);
			// throw new ForbiddenException();
		}
	}

	private void checkAccessConfigIsConfigured(long accountId, long safeLevelId) {
		List<AccessConfig> listAccessConfig = accessConfigDao.getAccessConfigList(accountId);
		String downLoadResrouceTypeIds;
		String previewResourceTypeIds;
		for (AccessConfig acc : listAccessConfig) {
			downLoadResrouceTypeIds = acc.getDownLoadResrouceTypeIds();
			previewResourceTypeIds = acc.getPreviewResourceTypeIds();
			checkIsConfiguredDownload(safeLevelId, downLoadResrouceTypeIds);
			checkIsConfiguredPreview(safeLevelId, previewResourceTypeIds);
		}
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	private void checkIsConfiguredPreview(long safeLevelId, String previewResourceTypeIds) {
		if (StringUtils.isBlank(previewResourceTypeIds)) {
			return;
		}
		String[] strPreviewResourceTypeIds = previewResourceTypeIds.split(",");
		boolean isPreviewConfigured = false;
		try {
			for (String sd : strPreviewResourceTypeIds) {
				if (Long.parseLong(sd) == safeLevelId) {
					isPreviewConfigured = true;
					break;
				}
			}
		} catch (NumberFormatException e) {
			throw new InvalidParamterException(e.getMessage());
		}
		if (isPreviewConfigured) {
			throw new ForbiddenException();
		}
	}

	private void checkIsConfiguredDownload(long safeLevelId, String downLoadResrouceTypeIds) {
		if (StringUtils.isBlank(downLoadResrouceTypeIds)) {
			return;
		}
		boolean isDownloadConfigured = false;
		String[] strDownLoadResrouceTypeIds = downLoadResrouceTypeIds.split(",");
		for (String sd : strDownLoadResrouceTypeIds) {
			if (Long.parseLong(sd) == safeLevelId) {
				isDownloadConfigured = true;
				break;
			}
		}
		if (isDownloadConfigured) {
			throw new ForbiddenException();
		}
	}
}
