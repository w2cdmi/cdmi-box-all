package com.huawei.sharedrive.uam.product.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import com.huawei.sharedrive.uam.product.dao.ProductDao;
import com.huawei.sharedrive.uam.product.domain.Product;


@Repository
public class ProductDaoImpl extends CacheableSqlMapClientDAO implements ProductDao{

	@Override
	public List<Product> list() {
		// TODO Auto-generated method stub
		return sqlMapClientTemplate.queryForList("Product.list");
	}

	@Override
	public Product get(long id) {
		// TODO Auto-generated method stub
		return (Product) sqlMapClientTemplate.queryForObject("Product.get", id);
	}

	@Override
	public List<Product> getByType(byte type) {
		return sqlMapClientTemplate.queryForList("Product.getByType", type);
	}

	@Override
	public List<Product> getByProduct(Product product) {
		return sqlMapClientTemplate.queryForList("Product.getByProduct", product);
	}
	
	

}
