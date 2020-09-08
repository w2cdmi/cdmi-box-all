package pw.cdmi.box.isystem.menu.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.isystem.menu.domain.MenuBean;
import pw.cdmi.box.isystem.menu.domain.QueryCondition;
import pw.cdmi.box.isystem.menu.service.MenuService;

@Controller
@RequestMapping(value = "/menuConfig")
public class MenuController extends AbstractCommonController
{
	@Autowired
	private MenuService menuService;
	
	private static final int DEFAULT_PAGE_SIZE = 10;
	
	@RequestMapping(value = "enterList", method = {RequestMethod.GET})
    public String enterList(Model model)
    {
		QueryCondition condition = new QueryCondition();
	    PageRequest pageRequest = new PageRequest();
	    pageRequest.setSize(DEFAULT_PAGE_SIZE);
	    condition.setPageRequest(pageRequest);
	    Page<MenuBean> menulist = menuService.getMenuBeanList(condition);
		model.addAttribute("menulist", menulist);
	    model.addAttribute("queryCondition", condition);
        return "menu/menuList";
    }
	
	@SuppressWarnings("rawtypes")
    @RequestMapping(value = "display", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> display(String menuKey, HttpServletRequest request)
    {
		try
		{
			menuService.update(menuKey, 0);
			return new ResponseEntity(HttpStatus.OK);
		} catch (Exception e)
		{
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	
	@SuppressWarnings("rawtypes")
    @RequestMapping(value = "notShow", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> notShow(String menuKey, HttpServletRequest request)
    {
		try
		{
			menuService.update(menuKey, 1);
			return new ResponseEntity(HttpStatus.OK);
		} catch (Exception e)
		{
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	
	
}
