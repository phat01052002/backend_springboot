package hcmute.it.furnitureshop.Service.Impl;

import hcmute.it.furnitureshop.DTO.BannerDTO;
import hcmute.it.furnitureshop.Entity.Banner;
import hcmute.it.furnitureshop.Repository.BannerRepository;
import hcmute.it.furnitureshop.Repository.ProductRepository;
import hcmute.it.furnitureshop.Service.BannerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BannerServiceImpl implements BannerService {
    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Override
    public ArrayList<BannerDTO> getAllBanner() {
        ArrayList<Banner> banners = bannerRepository.findAll();
        ArrayList<BannerDTO> bannerDTOS = new ArrayList<>();
        if(banners.size()>0)
        {
            banners.forEach(banner -> {
                BannerDTO bannerDTO = BannerDTO.builder()
                        .bannerId(banner.getBannerId())
                        .productId(banner.getProduct().getProductId())
                        .productName(banner.getProduct().getName())
                        .title(banner.getTitle())
                        .image(banner.getImage())
                        .build();
                bannerDTOS.add(bannerDTO);
            });
        }
        return bannerDTOS;
    }
    @Override
    public String deleteBanner(Integer bannerId) {
        Optional<Banner> Banner = bannerRepository.findById(bannerId);
        if(Banner.isPresent())
        {
            bannerRepository.deleteById(bannerId);
            return "Xóa banner thành công";
        }
        return "Xóa banner thất bại";
    }

    @Override
    public String updateBanner(BannerDTO banner) {
        if(!banner.getTitle().isEmpty()) {
            Optional<Banner> oldBanner = bannerRepository.findById(banner.getBannerId());
            if (oldBanner.isPresent()) {
                oldBanner.get().setImage(banner.getImage());
                oldBanner.get().setTitle(banner.getTitle());
                oldBanner.get().setProduct(productRepository.findById(banner.getProductId()).get());
                return "Cập nhật Banner thành công";
            }
            return "Không tồn tại Banner";
        }
        return "Cập nhật Banner thất bại";
    }

    @Override
    public BannerDTO getById(Integer bannerId) {
        Optional<Banner> banner = bannerRepository.findById(bannerId);
        if(banner.isPresent())
            return BannerDTO.builder().bannerId(banner.get().getBannerId())
                    .image(banner.get().getImage())
                    .title(banner.get().getTitle())
                    .productName(banner.get().getProduct().getName())
                    .productId(banner.get().getProduct().getProductId())
                    .build();
        return null;
    }

    @Override
    public Banner createBanner(BannerDTO bannerDTO) {
        Optional<Banner> validbanner = bannerRepository.findByProduct(productRepository.findById(bannerDTO.getProductId()).get());
        if (validbanner.isEmpty()) {
            Banner banner = Banner.builder().image(bannerDTO.getImage())
                    .title(bannerDTO.getTitle())
                    .product(productRepository.findById(bannerDTO.getProductId()).get())
                    .build();
            bannerRepository.save(banner);
            return banner;
        }
        return null;
    }

    @Override
    public Iterable<Banner> findTop5Banner() {
        return bannerRepository.findTop5Banner();
    }
}
