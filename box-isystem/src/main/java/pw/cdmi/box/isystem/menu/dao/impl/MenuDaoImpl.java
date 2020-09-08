package pw.cdmi.box.isystem.menu.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.isystem.menu.dao.MenuDao;
import pw.cdmi.box.isystem.menu.domain.MenuBean;

@SuppressWarnings("deprecation")
@Service("menuDao")
public class MenuDaoImpl extends AbstractDAOImpl implements MenuDao
{
	@SuppressWarnings("unchecked")
	@Override
	public List<MenuBean> getMenuBeanList()
	{
		return sqlMapClientTemplate.queryForList("MenuBean.getMenuBeanList");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int getTotals()
	{
		return (Integer)sqlMapClientTemplate.queryForObject("MenuBean.getTotals");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void update(String menuKey,int menuValue)
	{
		Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("menuKey", menuKey);
        map.put("menuValue", menuValue);
		sqlMapClientTemplate.update("MenuBean.update",map);
	}
}
