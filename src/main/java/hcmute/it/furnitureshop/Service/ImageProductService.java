package hcmute.it.furnitureshop.Service;

import hcmute.it.furnitureshop.Entity.ImageProduct;
import hcmute.it.furnitureshop.Entity.Product;

public interface ImageProductService {
    Iterable<ImageProduct> getImageProductByProduct(Product product);
}
