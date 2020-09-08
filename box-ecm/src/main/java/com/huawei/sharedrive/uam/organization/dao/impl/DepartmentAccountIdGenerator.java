/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.uam.organization.dao.impl;

import com.huawei.sharedrive.uam.organization.dao.DepartmentAccountDao;
import com.huawei.sharedrive.uam.organization.dao.DepartmentDao;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.core.exception.ZookeeperException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

import javax.annotation.PostConstruct;

/**
 * 
 */
@Service("departmentAccountIdGenerator")
public class DepartmentAccountIdGenerator implements SeedInitializer {
	private static final String BASE_PATH = "/department_account_id";

	private CuratorFramework client;

	@Autowired
	private DepartmentAccountDao departmentAccountDao;

	private SequenceGenerator sequenceGenerator;

	@Autowired
	private ZookeeperServer zookeeperServer;

	public CuratorFramework getClient() {
		return client;
	}

	public long getNextId() {
		return sequenceGenerator.getSequence("maxId") + 1;
	}

	public DepartmentAccountDao getDepartmentAccountDao() {
		return departmentAccountDao;
	}

	public void setDepartmentAccountDao(DepartmentAccountDao departmentAccountDao) {
		this.departmentAccountDao = departmentAccountDao;
	}

	@Override
	public long getSeed(String subPath) {
		return departmentAccountDao.getMaxId();
	}

	public SequenceGenerator getSequenceGenerator() {
		return sequenceGenerator;
	}

	public ZookeeperServer getZookeeperServer() {
		return zookeeperServer;
	}

	@PostConstruct
	public void init() throws ZookeeperException {
		client = zookeeperServer.getClient();
		sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
	}

	public void setClient(CuratorFramework client) {
		this.client = client;
	}

	public void setSequenceGenerator(SequenceGenerator sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	public void setZookeeperServer(ZookeeperServer zookeeperServer) {
		this.zookeeperServer = zookeeperServer;
	}
}
