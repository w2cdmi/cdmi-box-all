package com.huawei.sharedrive.app.share.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.huawei.sharedrive.app.share.dao.INodeLinkApproveDao;
import com.huawei.sharedrive.app.share.domain.INodeLinkApprove;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
@Component("iNodeLinkApproveDao")
public class INodeLinkApproveDaoImpl extends CacheableSqlMapClientDAO implements INodeLinkApproveDao {

	 private static final int TABLE_COUNT = 100;

	@Override
	public void create(INodeLinkApprove linkApprove) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("INodeLinkApprove.insert",linkApprove);
	}
	
	@Override
	public void update(INodeLinkApprove linkApprove) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("INodeLinkApprove.update",linkApprove);
	}

	@Override
	public List<INodeLinkApprove> listLinkApprove(long approveBy,Byte status,long accountId) {
		// TODO Auto-generated method stub
		 Map<String, Object> prameter=new HashMap<>();
		 prameter.put("approveBy", approveBy);
		 prameter.put("status", status);
		 prameter.put("accountId", accountId);
		 return sqlMapClientTemplate.queryForList("INodeLinkApprove.list",prameter);
	}

	@Override
	public void updateStatus(INodeLinkApprove linkApprove) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("INodeLinkApprove.updateStatus",linkApprove);
	}

	@Override
	public INodeLinkApprove getApproveByLinkCode(String linkCode) {
		// TODO Auto-generated method stub
		return (INodeLinkApprove) sqlMapClientTemplate.queryForObject("INodeLinkApprove.getApproveByLinkCode",linkCode);
	}

	@Override
	public void deleteByLinkCode(String linkCode) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.delete("INodeLinkApprove.deleteByLinkCode",linkCode);
	}
	
	@Override
	public List<INodeLinkApprove> listLinkApprove(INodeLinkApprove linkApprove, Map<String, Object> filter) {
		// TODO Auto-generated method stub
		 Map<String, Object> prameter=new HashMap<>();
		 prameter.put("linkApprove", linkApprove);
		 prameter.put("filter", filter);
		 return sqlMapClientTemplate.queryForList("INodeLinkApprove.list",prameter);
	}
	@Override
	public int listCount(INodeLinkApprove linkApprove, Map<String, Object> filter) {
		// TODO Auto-generated method stub
		 Map<String, Object> prameter=new HashMap<>();
		 prameter.put("linkApprove", linkApprove);
		 prameter.put("filter", filter);
		 return (int) sqlMapClientTemplate.queryForObject("INodeLinkApprove.listCount",prameter);
	}

	@Override
	public void deleteByNodeId(long cloudUserId, long inodeId) {
		// TODO Auto-generated method stub 2
		 Map<String, Object> prameter=new HashMap<>();
		 prameter.put("linkOwner", cloudUserId);
		 prameter.put("nodeId", inodeId);
		 sqlMapClientTemplate.delete("INodeLinkApprove.deleteByNodeId",prameter);
	}

	@Override
	public void updateLinkStatus(long cloudUserId, long inodeId) {
		// TODO Auto-generated method stub
		 Map<String, Object> prameter=new HashMap<>();
		 prameter.put("linkOwner", cloudUserId);
		 prameter.put("nodeId", inodeId);
		 prameter.put("linkStatus", INodeLinkApprove.LINK_STATUS_DELETE);
		sqlMapClientTemplate.update("INodeLinkApprove.updateLinkStatus",prameter);
	}

	@Override
	public void updateStatusByDuplicateTo(INodeLinkApprove linkApprove) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("INodeLinkApprove.updateStatusByDuplicateTo",linkApprove);
	}

	@Override
	public List<INodeLinkApprove> listAllLinkApprove(INodeLinkApprove linkApprove, Map<String, Object> filter) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		 Map<String, Object> prameter=new HashMap<>();
		 prameter.put("linkApprove", linkApprove);
		 prameter.put("filter", filter);
		 return sqlMapClientTemplate.queryForList("INodeLinkApprove.listAll",prameter);
	}

	@Override
	public int listAllCount(INodeLinkApprove linkApprove, Map<String, Object> filter) {
		// TODO Auto-generated method stub
		 Map<String, Object> prameter=new HashMap<>();
		 prameter.put("linkApprove", linkApprove);
		 prameter.put("filter", filter);
		 return (int) sqlMapClientTemplate.queryForObject("INodeLinkApprove.listAllCount",prameter);
	}

	@Override
	public List<INodeLinkApprove> listUserApprove(INodeLinkApprove linkApprove, Map<String, Object> filter) {
		// TODO Auto-generated method stub
		 Map<String, Object> prameter=new HashMap<>();
		 prameter.put("linkApprove", linkApprove);
		 prameter.put("filter", filter);
		 return sqlMapClientTemplate.queryForList("INodeLinkApprove.listUserApprove",prameter);
	}

	@Override
	public int listCountUserApprove(INodeLinkApprove linkApprove, Map<String, Object> filter) {
		// TODO Auto-generated method stub
		 Map<String, Object> prameter=new HashMap<>();
		 prameter.put("linkApprove", linkApprove);
		 prameter.put("filter", filter);
		 return (int) sqlMapClientTemplate.queryForObject("INodeLinkApprove.listCountUserApprove",prameter);
	}
}
