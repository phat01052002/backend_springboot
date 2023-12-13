package hcmute.it.furnitureshop.Service.Impl;

import hcmute.it.furnitureshop.Entity.ImageProduct;
import hcmute.it.furnitureshop.Entity.Product;
import hcmute.it.furnitureshop.Repository.ImageProductRepository;
import hcmute.it.furnitureshop.Service.ImageProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageProductServiceImpl implements ImageProductService {
    @Autowired
    ImageProductRepository imageProductRepository;
    @Override
    public Iterable<ImageProduct> getImageProductByProduct(Product product){
     return imageProductRepository.findImageProductByProduct(product);
    }
}


