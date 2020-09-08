package com.huawei.sharedrive.app.share.dao;

import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeShareDelete;

public interface INodeShareDeleteReserverDao {

	void addShareDelete(INodeShareDelete iNodeShareDelete);

	void delete(INodeShareDelete iNodeShareDelete);

	List<INodeShareDelete> list(INodeShareDelete iNodeShareDelete);
	
	
	

}
