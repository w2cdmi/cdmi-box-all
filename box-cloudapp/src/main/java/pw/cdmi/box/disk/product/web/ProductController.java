package pw.cdmi.box.disk.product.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.product.domain.PaymentInfo;
import pw.cdmi.box.disk.product.domain.Product;
import pw.cdmi.box.disk.product.service.ProductService;

@Controller
@RequestMapping(value = "/product")
public class ProductController extends CommonController{

	@Autowired
	private ProductService productService;
	

	@RequestMapping(value = "/list")
	public ResponseEntity<?> listProduct( HttpServletRequest request){
		
		try {
//			super.checkToken(request);
			List<Product> productList=productService.list();
			return new ResponseEntity<List<Product>>(productList, HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
	}
	
	
}
