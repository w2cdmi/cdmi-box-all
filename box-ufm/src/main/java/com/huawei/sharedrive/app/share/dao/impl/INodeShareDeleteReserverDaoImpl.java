package com.huawei.sharedrive.app.share.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.huawei.sharedrive.app.share.dao.INodeShareDeleteReserverDao;
import com.huawei.sharedrive.app.share.domain.INodeShareDelete;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository
public class INodeShareDeleteReserverDaoImpl extends AbstractDAOImpl implements INodeShareDeleteReserverDao{

	@Override
	public void addShareDelete(INodeShareDelete iNodeShareDelete) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("INodeShareDeleteReverse.create",iNodeShareDelete);
	}

	@Override
	public void delete(INodeShareDelete iNodeShareDelete) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.delete("INodeShareDeleteReverse.delete",iNodeShareDelete);
	}
	
	@Override
	public List<INodeShareDelete> list(INodeShareDelete iNodeShareDelete) {
		// TODO Auto-generated method stub
	    return 	sqlMapClientTemplate.queryForList("INodeShareDeleteReverse.list",iNodeShareDelete);
	}

}
