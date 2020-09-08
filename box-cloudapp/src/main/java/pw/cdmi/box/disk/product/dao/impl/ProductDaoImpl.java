package pw.cdmi.box.disk.product.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.product.dao.ProductDao;
import pw.cdmi.box.disk.product.domain.Product;

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
		return (Product) sqlMapClientTemplate.queryForObject("Product.get",id);
	}

}
