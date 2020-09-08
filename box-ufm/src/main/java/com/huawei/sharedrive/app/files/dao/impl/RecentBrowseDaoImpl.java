package com.huawei.sharedrive.app.files.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.RecentBrowseDao;
import com.huawei.sharedrive.app.files.domain.RecentBrowse;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
@Service("recentBrowseDao")
public class RecentBrowseDaoImpl extends AbstractDAOImpl implements RecentBrowseDao{

	@Override
	public List<RecentBrowse> list(long userId,long accountId ,long offset, long limit) {
		// TODO Auto-generated method stub
		Map<String, Object> prameter=new HashMap<>();
		prameter.put("userId", userId);
		prameter.put("accountId", accountId);
		prameter.put("offset", offset);
		prameter.put("limit", limit);
		return sqlMapClientTemplate.queryForList("RecentBrowse.list",prameter);
	}

	@Override
	public void deleteByTime(Date dateTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create(RecentBrowse recentBrowse) {
		// TODO Auto-generated method stub
		RecentBrowse old=get(recentBrowse);
		if(old==null){
			sqlMapClientTemplate.insert("RecentBrowse.insert",recentBrowse);	
		}else{
			recentBrowse.setLastBrowseTime(new Date());
			sqlMapClientTemplate.update("RecentBrowse.updateCreateAt",recentBrowse);
		}
        //现在最近浏览最大条数
		List<RecentBrowse> recentBrowseList=list(recentBrowse.getUserId(),recentBrowse.getAccountId() ,0,200);
		if(recentBrowseList.size()>40){
			for(int i=40;i<recentBrowseList.size();i++){
				delete(recentBrowseList.get(i));
			}
		}
		
	}
	
	@Override
	public RecentBrowse get(RecentBrowse recentBrowse) {
		// TODO Auto-generated method stub
		return (RecentBrowse) sqlMapClientTemplate.queryForObject("RecentBrowse.get",recentBrowse);
	}

	@Override
	public void delete(RecentBrowse recentBrowse) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.delete("RecentBrowse.deleteRecent",recentBrowse);
		
	}
	
	@Override
	public void deleteRecentByNode(long ownerId, long nodeId) {
		// TODO Auto-generated method stub
		RecentBrowse recentBrowse=new RecentBrowse();
		recentBrowse.setOwnedBy(ownerId);
		recentBrowse.setInodeId(nodeId);
		sqlMapClientTemplate.delete("RecentBrowse.deleteRecentByNode",recentBrowse);
	}

	@Override
	public void updateCreateAt(RecentBrowse recentBrowse) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("RecentBrowse.updateCreateAt",recentBrowse);
	}

	@Override
	public void deleteRecentByUserId(RecentBrowse recentBrowse) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("RecentBrowse.deleteRecentByUserId",recentBrowse);
	}
	

	

}
