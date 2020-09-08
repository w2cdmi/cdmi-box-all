package pw.cdmi.box.disk.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.product.domain.EnterpriseVip;
import pw.cdmi.box.disk.product.service.OrderBillService;

@Controller
@RequestMapping(value = "/buy")
public class BuyVipController extends CommonController{
	
	@Autowired
	private OrderBillService orderBillService;
	
	@RequestMapping(value = "vip", method = RequestMethod.GET)
	public String enterVipPage( Model model) {
		
		EnterpriseVip enterpriseVip= orderBillService.getEnterpriseVip(getToken());
		model.addAttribute("enterpriseVip",enterpriseVip);
		return "/buy/buyVip";
	}
}
