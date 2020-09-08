package com.huawei.sharedrive.uam.openapi.rest.product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.core.exception.InvalidParamException;

import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.product.domain.Product;
import com.huawei.sharedrive.uam.product.domain.ProductDurationPrice;
import com.huawei.sharedrive.uam.product.service.ProductDurationPriceService;
import com.huawei.sharedrive.uam.product.service.ProductService;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;


@Controller
@RequestMapping(value = "/api/v2/products")
public class ProductApiController {
	
	@Autowired
	private UserTokenHelper userTokenHelper;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductDurationPriceService productDurationPriceService;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getProducts(@RequestHeader("Authorization") String authorization){
		
		UserToken token = userTokenHelper.checkTokenAndGetUser(authorization);
		
		List<Product> productList = null;
		if(token.getType() == WxUser.TYPE_TEMPORARY || token.getType() >= WxUser.TYPE_TEMPORARY_VIP1){
			productList = productService.getPersonProducts();
		}else{
			productList = productService.getEnterpriseProducts();
		}
		
		if(productList != null){
			return new ResponseEntity<>(productList, HttpStatus.OK);
		}
		
		return new ResponseEntity<>("The user type product is empty", HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value = "/{productId}/durationAndPrice", method = RequestMethod.GET)
	public ResponseEntity<?> getProductDurationPrices(@RequestHeader("Authorization") String authorization, @PathVariable long productId){
		userTokenHelper.checkTokenAndGetUser(authorization);
		
		if(productId == 0){
			throw new InvalidParamException();
		}
		
		List<ProductDurationPrice> productDurationPriceList = null;
		productDurationPriceList = productDurationPriceService.getProductDurationPriceByProductId(productId);
		if(productDurationPriceList != null){
			return new ResponseEntity<>(productDurationPriceList, HttpStatus.OK);
		}
		
		return new ResponseEntity<>("get product price is empty", HttpStatus.NO_CONTENT);
	}
}
