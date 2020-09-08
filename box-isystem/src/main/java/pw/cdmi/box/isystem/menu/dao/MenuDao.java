package pw.cdmi.box.isystem.menu.dao;

import java.util.List;

import pw.cdmi.box.isystem.menu.domain.MenuBean;

public interface MenuDao
{
	List<MenuBean> getMenuBeanList();
	
	int getTotals();
	
	void update(String menuKey,int menuValue);
}
