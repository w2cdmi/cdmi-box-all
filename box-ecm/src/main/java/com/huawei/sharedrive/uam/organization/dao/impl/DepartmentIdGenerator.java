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

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.organization.dao.DepartmentDao;

import pw.cdmi.core.exception.ZookeeperException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * 
 */
@Service("departmentIdGenerator")
public class DepartmentIdGenerator implements SeedInitializer {
	private static final String BASE_PATH = "/department_id";

	private CuratorFramework client;

	@Autowired
	private DepartmentDao departmentDao;

	private SequenceGenerator sequenceGenerator;

	@Autowired
	private ZookeeperServer zookeeperServer;

	public CuratorFramework getClient() {
		return client;
	}

	public long getNextId() {
		return sequenceGenerator.getSequence("maxId");
	}

	public DepartmentDao getDepartmentDao() {
		return departmentDao;
	}

	public void setDepartmentDao(DepartmentDao departmentDao) {
		this.departmentDao = departmentDao;
	}

	@Override
	public long getSeed(String subPath) {
		return departmentDao.queryMaxExecuteRecordId();
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
