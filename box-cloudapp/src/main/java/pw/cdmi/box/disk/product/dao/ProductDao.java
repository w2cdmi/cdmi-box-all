package pw.cdmi.box.disk.product.dao;

import java.util.List;

import pw.cdmi.box.disk.product.domain.Product;

public interface ProductDao {

	List<Product> list();

	Product get(long id);

}
