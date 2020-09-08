package com.huawei.sharedrive.app.files.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.ShortcutDao;
import com.huawei.sharedrive.app.files.domain.Shortcut;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
@Service
public class ShortcutDaoImpl extends AbstractDAOImpl implements ShortcutDao {

	@Override
	public List<Shortcut> list(long userId) {
		 List<Shortcut> list=sqlMapClientTemplate.queryForList("Shortcut.list",userId);
		if(list.size()>20){
			for(int i=20;i<list.size();i++){
				delete(list.get(i).getId());
			}
		}
		return list;
	}
	

	@Override
	public void create(Shortcut shortcut) {
		if(shortcut.getCreateAt()==null){
			shortcut.setCreateAt(new Date());
		}
		sqlMapClientTemplate.insert("Shortcut.insert",shortcut);
	}
	
	@Override
	public void delete(long id) {
		sqlMapClientTemplate.delete("Shortcut.delete",id);
	}
	
	@Override
	public void deleteByNodeId(long ownerId,long nodeId) {
		Map<String, Long> parameter=new HashMap<String, Long>();
		parameter.put("ownerId", ownerId);
		parameter.put("nodeId", nodeId);
		sqlMapClientTemplate.delete("Shortcut.deleteByNodeId",parameter);
	}


	@Override
	public Shortcut getByOwnerIdAndNodeId(Shortcut shortcut) {
		// TODO Auto-generated method stub
		return (Shortcut) sqlMapClientTemplate.queryForObject("Shortcut.getByOwnerIdAndNodeId",shortcut);
	}


	@Override
	public void deleteShortByOwner(long ownerId) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.delete("Shortcut.deleteShortByOwner",ownerId);
	}
}
