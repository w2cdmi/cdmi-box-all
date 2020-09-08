package pw.cdmi.box.isystem.menu.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.isystem.menu.dao.MenuDao;
import pw.cdmi.box.isystem.menu.domain.MenuBean;
import pw.cdmi.box.isystem.menu.domain.QueryCondition;
import pw.cdmi.box.isystem.menu.service.MenuService;

@Service("menuService")
public class MenuServiceImpl implements MenuService
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MenuServiceImpl.class);
	
	@Autowired
    private MenuDao menuDao;
	
	@Override
	public Page<MenuBean> getMenuBeanList(QueryCondition condition)
	{
		LOGGER.info("Enter getMenuBeanList");
		int total = menuDao.getTotals();
		List<MenuBean> content = menuDao.getMenuBeanList();
        Page<MenuBean> page = new PageImpl<MenuBean>(content, condition.getPageRequest(), total);
        LOGGER.info("End getMenuBeanList");
        return page;
	}
	
	@Override
	public void update(String menuKey,int menuValue)
	{
		menuDao.update(menuKey, menuValue);
	}
}
