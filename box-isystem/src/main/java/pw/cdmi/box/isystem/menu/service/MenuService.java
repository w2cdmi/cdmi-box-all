package pw.cdmi.box.isystem.menu.service;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.isystem.menu.domain.MenuBean;
import pw.cdmi.box.isystem.menu.domain.QueryCondition;

public interface MenuService
{
	Page<MenuBean> getMenuBeanList(QueryCondition condition);
	
	void update(String menuKey,int menuValue);
}
