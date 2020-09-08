package pw.cdmi.box.disk.product.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.product.domain.PaymentInfo;
import pw.cdmi.box.disk.product.service.PaymentService;

@Controller
@RequestMapping(value = "/payment")
public class PaymentController extends CommonController{
	
	@Autowired
	private PaymentService paymentService;
	
	@RequestMapping(value = "/create", method = {RequestMethod.POST})
	public ResponseEntity<?> createPayment(long orderId,byte type,String payAccount,double payAmout,HttpServletRequest request){
		try {
			super.checkToken(request);
			PaymentInfo paymentInfo=new PaymentInfo();
			paymentInfo.setOrderId(orderId);
			paymentInfo.setPayAccount(payAccount);
			paymentInfo.setPayAmout(payAmout);
			paymentInfo.setType(type);
			paymentInfo.setPayDate(new Date());
			paymentService.create(paymentInfo);
			return new ResponseEntity<String>( HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
	}
	
	
	@RequestMapping(value = "/get/{orderId}",method = {RequestMethod.GET})
	public ResponseEntity<?> getPayment( @PathVariable("orderId") long orderId,HttpServletRequest request){
		
		try {
			super.checkToken(request);
			PaymentInfo paymentInfo=paymentService.getPayment(orderId);
			return new ResponseEntity<PaymentInfo>(paymentInfo, HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		
	}

}
