package hcmute.it.furnitureshop.Repository;

import hcmute.it.furnitureshop.Entity.ImageProduct;
import hcmute.it.furnitureshop.Entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageProductRepository extends CrudRepository<ImageProduct,Integer> {
    Iterable<ImageProduct> findImageProductByProduct(Product product);
}
