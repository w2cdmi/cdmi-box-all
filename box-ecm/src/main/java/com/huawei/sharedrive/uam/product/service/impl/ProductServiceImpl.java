package com.huawei.sharedrive.uam.product.service.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.product.dao.ProductDao;
import com.huawei.sharedrive.uam.product.domain.Product;
import com.huawei.sharedrive.uam.product.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	private ProductDao productDao;

	@Override
	public List<Product> list() {
		// TODO Auto-generated method stub
		return productDao.list();
	}

	@Override
	public Product get(long productId) {
		// TODO Auto-generated method stub
		return productDao.get(productId);
	}

	@Override
	public List<Product> getPersonProducts() {
		return productDao.getByType(Product.TYPE_PERSON);
	}
	
	@Override
	public List<Product> getEnterpriseProducts() {
		return productDao.getByType(Product.TYPE_ENTERPRISE);
	}

	@Override
	public List<Product> getByTypeAndLevel(byte type, byte level) {
		Product product = new Product();
		product.setType(type);
//		product.setLevel(level);
		return productDao.getByProduct(product);
	}

}
