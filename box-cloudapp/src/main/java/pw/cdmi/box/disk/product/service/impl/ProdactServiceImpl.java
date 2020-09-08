package pw.cdmi.box.disk.product.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.box.disk.product.dao.ProductDao;
import pw.cdmi.box.disk.product.domain.Product;
import pw.cdmi.box.disk.product.service.ProductService;

@Service
public class ProdactServiceImpl implements ProductService{
	
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

}
