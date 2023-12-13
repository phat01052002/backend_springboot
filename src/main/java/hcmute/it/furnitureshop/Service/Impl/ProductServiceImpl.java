package hcmute.it.furnitureshop.Service.Impl;

import hcmute.it.furnitureshop.DTO.ProductDTO;
import hcmute.it.furnitureshop.DTO.ProductDetailDTO;
import hcmute.it.furnitureshop.Entity.*;
import hcmute.it.furnitureshop.Repository.*;
import hcmute.it.furnitureshop.Service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private ImageProductRepository imageProductRepository;
    @Override
    public Iterable<Product> getTop8Product() {
        return productRepository.findTop8ByProductSold();
    }

    @Override
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Iterable<Product> getProductByNameContaining(String name) {
        return productRepository.findProductsByNameContaining(name);
    }

    public  Iterable<Product> getProductsByCategory(Integer categoryId){
        Optional<Category> category=categoryRepository.findById(categoryId);
        return productRepository.findProductsByCategory(category.get());
    }

    @Override
    public Iterable<Product> getProductByCategoryAndPriceDesc(Category category) {
        return productRepository.findProductsByCategoryOrderByPriceDesc(category);
    }

    @Override
    public Iterable<Product> getProductByCategoryAndPriceAsc(Category category) {
        return productRepository.findProductsByCategoryOrderByPriceAsc(category);
    }

    @Override
    public Iterable<Product> findProductByRoomDesc(Integer roomId) {
        return productRepository.findProductsByCategory_Room_RoomIdOrderByPriceDesc(roomId);
    }

    @Override
    public Iterable<Product> findProductByRoomAsc(Integer roomId) {
        return productRepository.findProductsByCategory_Room_RoomIdOrderByPriceAsc(roomId);
    }

    @Override
    public Optional<Product> findById(Integer productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Iterable<Product> getAll() {
        return productRepository.findProductsByStatus("inactive");
    }

    @Override
    public Optional<Product> findProductByOrderId(Integer orderId) {
        return productRepository.findProductByOrder_OrderId(orderId);
    }

    @Override
    public <S extends Product> void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public Iterable<Product> getProductByRoom(Integer roomId) {
        Optional<Room> roomById= roomRepository.findById(roomId);
        Iterable<Category> categories=categoryRepository.findCategoriesByRoom(roomById);
        ArrayList<Product> products = new ArrayList<>();
        categories.forEach(category -> {
            products.addAll((Collection<? extends Product>) productRepository.findProductsByCategory(category));
        });
        return products;
    }

    @Override
    public Iterable<Product> getProductsByCategoryAndDiscount(Integer categoryId) {
        Iterable<Product> products= getProductsByCategory(categoryId);
        ArrayList<Product> productsHaveDisCount = new ArrayList<>();
        products.forEach(product -> {
            if(product.getDiscount()!=null){
                productsHaveDisCount.add(product);
            }
        });
        return productsHaveDisCount;
    }

    @Override
    public Iterable<Product> getProductSaleByRoom(Integer roomId) {
        Iterable<Product> products= productRepository.findProductsByCategory_Room_RoomId(roomId);
        ArrayList<Product> productsHaveDisCount = new ArrayList<>();
        products.forEach(product -> {
            if(product.getDiscount()!=null){
                productsHaveDisCount.add(product);
            }
        });
        return productsHaveDisCount;
    }
    public List<ProductDetailDTO> getAllProductsWithCategoryName() {
        List<ProductDetailDTO> detailDTOList = new ArrayList<>();
        productRepository.findAllProductsWithCategoryName().forEach(productJoinCate -> {
            ProductDetailDTO productDetail = ProductDetailDTO.builder()
                    .productId(productJoinCate.getProductId())
                    .name(productJoinCate.getName())
                    .price(productJoinCate.getPrice())
                    .size(productJoinCate.getSize())
                    .categoryName(productJoinCate.getCategory().getName())
                    .numberProductSold(productJoinCate.getNumberProductSold())
                    .description(productJoinCate.getDescription())
                    .material(productJoinCate.getMaterial())
                    .quantity(productJoinCate.getQuantity())
                    .status(productJoinCate.getStatus())
                    .build();
            List<ImageProduct> listImage = productJoinCate.getImageProducts();
            if (listImage.size() > 0)
                productDetail.setImage1(productJoinCate.getImageProducts().get(0).getImage());
            if (listImage.size() > 1)
                productDetail.setImage2(productJoinCate.getImageProducts().get(0).getImage());
            if (listImage.size() > 2)
                productDetail.setImage3(productJoinCate.getImageProducts().get(0).getImage());
            detailDTOList.add(productDetail);
        });
        return detailDTOList;
    }
    @Override
    public String updateProduct(ProductDetailDTO productDTO) {
        Optional<Product> product = productRepository.findById(productDTO.getProductId());
        if(product.isPresent())
        {
            product.get().setName(productDTO.getName());
            product.get().setPrice(productDTO.getPrice());
            product.get().setSize(productDTO.getSize());
            product.get().setCategory(categoryRepository.findByName(productDTO.getCategoryName()));
            product.get().setMaterial(productDTO.getMaterial());
            product.get().setQuantity(productDTO.getQuantity());
            if(productDTO.getDiscountName() != null)
                product.get().setDiscount(discountRepository.findByDiscountName(productDTO.getDiscountName()).get());
            product.get().setStatus(productDTO.getStatus());
            List<ImageProduct> listImageProduct = new ArrayList<>();
            if(!productDTO.getImage1().isEmpty())
            {
                var image = ImageProduct.builder().image(productDTO.getImage1())
                        .product(product.get()).build();
                listImageProduct.add(image);

            }
            if(!productDTO.getImage2().isEmpty())
            {
                var image = ImageProduct.builder().image(productDTO.getImage2())
                        .product(product.get()).build();
                listImageProduct.add(image);
            }
            if(!productDTO.getImage3().isEmpty())
            {
                var image = ImageProduct.builder().image(productDTO.getImage3())
                        .product(product.get()).build();
                listImageProduct.add(image);
            }
            imageProductRepository.findImageProductByProduct(product.get()).forEach(imageProduct -> imageProductRepository.delete(imageProduct));
            product.get().setImageProducts(listImageProduct);
            return "Đã cập nhật sản phẩm thành công";
        }
        return "Không tồn tại sản phẩm trong hệ thống";
    }

    @Override
    public String deleteProduct(Integer productId) {
        if(productRepository.findById(productId).isPresent())
        {
            productRepository.deleteById(productId);
            return "Đã xóa sản phẩm thành công";
        }
        return "Không tồn tại sản phẩm trong hệ thống";
    }

    @Override
    public Product createProduct(ProductDetailDTO createProductDTO) {
        if(productRepository.findByName(createProductDTO.getName()).isEmpty())
        {
            var product = Product.builder()
                    .dateImport(new Date())
                    .productId(createProductDTO.getProductId())
                    .name(createProductDTO.getName())
                    .price(createProductDTO.getPrice())
                    .size(createProductDTO.getSize())
                    .category(categoryRepository.findByName(createProductDTO.getCategoryName()))
                    .numberProductSold(createProductDTO.getNumberProductSold())
                    .description(createProductDTO.getDescription())
                    .material(createProductDTO.getMaterial())
                    .quantity(createProductDTO.getQuantity())
                    .status("active")
                    .build();
            List<ImageProduct> listImageProduct = new ArrayList<>();
            if(!createProductDTO.getImage1().isEmpty())
            {
                var image = ImageProduct.builder().image(createProductDTO.getImage1())
                        .product(product).build();
                listImageProduct.add(image);
            }
            if(!createProductDTO.getImage2().isEmpty())
            {
                var image = ImageProduct.builder().image(createProductDTO.getImage2())
                        .product(product).build();
                listImageProduct.add(image);
            }
            if(!createProductDTO.getImage3().isEmpty())
            {
                var image = ImageProduct.builder().image(createProductDTO.getImage3())
                        .product(product).build();
                listImageProduct.add(image);
            }
            product.setImageProducts(listImageProduct);
            if(createProductDTO.getDiscountName() != null)
                product.setDiscount(discountRepository.findByDiscountName(createProductDTO.getDiscountName()).get());
            productRepository.save(product);
            return product;
        }
        else return null;
    }

    @Override
    public Iterable<Product> findByDiscountIsNotNull() {
        return productRepository.findByDiscountIsNotNull();
    }

    @Override
    public Iterable<Product> findProductNearProduct(Integer productId) {
        Optional<Product> productById=productRepository.findById(productId);
        Optional<Category> category=categoryRepository.findCategoryByProduct(productById.get());
        ArrayList<Product> productsReturn=new ArrayList<>();
        Iterable<Product> products= productRepository.findProductsByCategory(category.get());
        products.forEach(product ->{
            if(product.getProductId()==productById.get().getProductId()){

            }else{
                productsReturn.add(product);
            }
        });
        return productsReturn;
    }

    @Override
    public ProductDetailDTO getById(Integer productId) {
        Optional<Product> product = productRepository.findById(productId);
        Discount discount = product.get().getDiscount();
        Double percentDiscount;
        String discountName;
        if(discount != null) {
            percentDiscount = discount.getPercentDiscount();
            discountName = discount.getDiscountName();
        }
        else {
            percentDiscount = null;
            discountName = null;
        }
        return product.map(value -> {
                var productDetail = ProductDetailDTO.builder()
                .productId(value.getProductId())
                .name(value.getName())
                .price(value.getPrice())
                .size(value.getSize())
                .categoryName(value.getCategory().getName())
                .numberProductSold(value.getNumberProductSold())
                .material(value.getMaterial())
                .quantity(value.getQuantity())
                .description(value.getDescription())
                .status(value.getStatus())
                .numberFavorite(value.getFavorites().size())
                .numberRating(value.getRatings().size())
                .percentDiscount(percentDiscount).discountName(discountName)
                .build();
                List<ImageProduct> listImage = value.getImageProducts();
                if (listImage.size() > 0)
                    productDetail.setImage1(value.getImageProducts().get(0).getImage());
                if (listImage.size() > 1)
                    productDetail.setImage2(value.getImageProducts().get(1).getImage());
                if (listImage.size() > 2)
                    productDetail.setImage3(value.getImageProducts().get(2).getImage());
                return productDetail;
        }).orElse(null);
    }
}
