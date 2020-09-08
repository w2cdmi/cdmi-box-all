package pw.cdmi.box.disk.product.service;

import java.util.List;

import pw.cdmi.box.disk.product.domain.Product;

public interface ProductService {

	List<Product> list();

	Product get(long productId);

}
