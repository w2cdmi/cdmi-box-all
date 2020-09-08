package com.huawei.sharedrive.app.share.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.huawei.sharedrive.app.share.dao.INodeShareDeleteDao;
import com.huawei.sharedrive.app.share.dao.INodeShareDeleteReserverDao;
import com.huawei.sharedrive.app.share.domain.INodeShareDelete;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository
public class INodeShareDeleteDaoImpl extends AbstractDAOImpl implements INodeShareDeleteDao{
	
	@Autowired
	private INodeShareDeleteReserverDao  shareDeleteReserverDao;

	@Override
	public void addShareDelete(INodeShareDelete iNodeShareDelete) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("INodeShareDelete.create",iNodeShareDelete);
		shareDeleteReserverDao.addShareDelete(iNodeShareDelete);
	}

	@Override
	public void delete(INodeShareDelete iNodeShareDelete) {
		// TODO Auto-generated method stub
		List<INodeShareDelete>   shareDeletelist = shareDeleteReserverDao.list(iNodeShareDelete);
		for(INodeShareDelete shareDelete : shareDeletelist){
			shareDeleteReserverDao.delete(shareDelete);
			sqlMapClientTemplate.delete("INodeShareDelete.delete",shareDelete);
		}
		
	}

}
