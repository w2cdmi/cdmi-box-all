package pw.cdmi.box.disk.product.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.product.domain.OrderBill;
import pw.cdmi.box.disk.product.domain.Product;
import pw.cdmi.box.disk.product.service.OrderBillService;
import pw.cdmi.box.disk.product.service.ProductService;
import pw.cdmi.box.disk.utils.QRCodeUtil;


@Controller
@RequestMapping(value = "/order")
public class OrderBillController extends CommonController{
	
	@Autowired
	private OrderBillService orderBillService;

	@Autowired
	private ProductService productService;
	
	@RequestMapping(value = "/create", method = {RequestMethod.POST})
	public ResponseEntity<?> createOrder(long productId,long duration,byte type,HttpServletRequest request,HttpServletResponse response){
		try {
			
			OrderBill orderBill=new OrderBill();
			orderBill.setProductId(productId);
			orderBill.setType(type);
			orderBill.setUserType(OrderBill.USERTYPE_COMPANY);
			orderBill.setDuration(duration);
			Map<String, Object> result=orderBillService.create(orderBill,getToken());
			return new ResponseEntity<Map<String, Object>>(result,HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	
	}
	
	
	@RequestMapping(value = "/getPayCode", method = {RequestMethod.GET})
	public ResponseEntity<?> getPayCode(String url,HttpServletRequest request,HttpServletResponse response){
		try {
			QRCodeUtil.createQrCode(response.getOutputStream(),url, 160, "JPEG");
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return null;
	
	}
	
	

	@RequestMapping(value = "/getOrderStatus", method = {RequestMethod.GET})
	@ResponseBody
	public  ResponseEntity<?> getOrderStatus(String orderId,HttpServletRequest request,HttpServletResponse response){
			try {
				byte status=orderBillService.getOrderStatus(orderId);
				return  new ResponseEntity<Byte>(status, HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
	}
	
	
	
	@RequestMapping(value = "/list", method = {RequestMethod.GET})
	public ResponseEntity<?> listOrder(OrderBill orderBill, HttpServletRequest request){
		
		try {
			UserToken user=getCurrentUser();
			orderBill.setEnterpriseId(user.getEnterpriseId());
			orderBill.setEnterpriseUserId(user.getId());
			List<OrderBill> orderBillList=orderBillService.list(orderBill);
			return new ResponseEntity<List<OrderBill>>(orderBillList, HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}
