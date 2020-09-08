package com.huawei.sharedrive.app.files.dao;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.Shortcut;

public interface ShortcutDao {

	 List<Shortcut> list(long userId);

	void create(Shortcut shortcut);

	void delete(long id);

	Shortcut getByOwnerIdAndNodeId(Shortcut shortcut);
	
	public void deleteByNodeId(long ownerId,long nodeId);

	void deleteShortByOwner(long ownerId);
}
