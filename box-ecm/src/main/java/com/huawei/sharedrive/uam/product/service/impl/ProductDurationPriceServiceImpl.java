package com.huawei.sharedrive.uam.product.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.product.dao.ProductDurationPriceDao;
import com.huawei.sharedrive.uam.product.domain.ProductDurationPrice;
import com.huawei.sharedrive.uam.product.service.ProductDurationPriceService;

@Service
public class ProductDurationPriceServiceImpl implements ProductDurationPriceService{
	
	@Autowired
	private ProductDurationPriceDao producDurationPriceDao;

	@Override
	public ProductDurationPrice get(Integer id) {
		// TODO Auto-generated method stub
		return producDurationPriceDao.getById(id);
	}

	@Override
	public List<ProductDurationPrice> getProductDurationPriceByProductId(
			long productId) {
		// TODO Auto-generated method stub
		return producDurationPriceDao.getByProductId(productId);
	}

	@Override
	public ProductDurationPrice getByProductIdAndDuration(long productId,
			Integer duration) {
		ProductDurationPrice productDurationPrice= new ProductDurationPrice();
		productDurationPrice.setProductId(productId);
		productDurationPrice.setDuration(duration);
		return producDurationPriceDao.getProductDurationPrice(productDurationPrice);
	}
	
	
	
}
