package com.huawei.sharedrive.uam.product.dao;


import java.util.List;

import com.huawei.sharedrive.uam.product.domain.Product;


public interface ProductDao {

	List<Product> list();

	Product get(long id);

	List<Product> getByType(byte type);
	
	List<Product> getByProduct(Product product);
}
