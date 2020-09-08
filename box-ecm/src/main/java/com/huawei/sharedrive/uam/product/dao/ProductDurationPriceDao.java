package com.huawei.sharedrive.uam.product.dao;

import java.util.List;

import com.huawei.sharedrive.uam.product.domain.ProductDurationPrice;

public interface ProductDurationPriceDao {
	
	public ProductDurationPrice getById(Integer id);
	
	//获取价格
	public ProductDurationPrice getProductDurationPrice(ProductDurationPrice productDurationPrice);
	
	//获取购买存储价格
	List<ProductDurationPrice> getByProductId(long productId);
	
}
