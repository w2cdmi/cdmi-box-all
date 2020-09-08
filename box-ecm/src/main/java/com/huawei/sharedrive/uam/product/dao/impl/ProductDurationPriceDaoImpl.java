package com.huawei.sharedrive.uam.product.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

import com.huawei.sharedrive.uam.product.dao.ProductDurationPriceDao;
import com.huawei.sharedrive.uam.product.domain.ProductDurationPrice;

@Repository
public class ProductDurationPriceDaoImpl extends CacheableSqlMapClientDAO
		implements ProductDurationPriceDao {

	@SuppressWarnings("deprecation")
	@Override
	public ProductDurationPrice getById(Integer id) {
		// TODO Auto-generated method stub
		return (ProductDurationPrice) sqlMapClientTemplate.queryForObject("productDurationPrice.get", id);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<ProductDurationPrice> getByProductId(
			long productId) {
		return sqlMapClientTemplate.queryForList("productDurationPrice.getByProductId", productId);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ProductDurationPrice getProductDurationPrice(
			ProductDurationPrice productDurationPrice) {
		return (ProductDurationPrice) sqlMapClientTemplate.queryForObject("productDurationPrice.getByProductDurationPrice", productDurationPrice);
	}

}
