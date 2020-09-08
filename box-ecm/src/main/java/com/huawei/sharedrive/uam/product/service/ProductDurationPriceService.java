package com.huawei.sharedrive.uam.product.service;

import java.util.List;

import com.huawei.sharedrive.uam.product.domain.ProductDurationPrice;

public interface ProductDurationPriceService {
	
	ProductDurationPrice get(Integer id);
	
	ProductDurationPrice getByProductIdAndDuration(long productId, Integer duration);
	
	List<ProductDurationPrice> getProductDurationPriceByProductId(long productId);
}
