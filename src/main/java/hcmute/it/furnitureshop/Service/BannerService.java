package hcmute.it.furnitureshop.Service;

import hcmute.it.furnitureshop.DTO.BannerDTO;
import hcmute.it.furnitureshop.Entity.Banner;

import java.util.ArrayList;

public interface BannerService {
    public ArrayList<BannerDTO> getAllBanner();
    public String deleteBanner(Integer bannerId);
    public BannerDTO getById(Integer bannerId);
    public String updateBanner(BannerDTO banner);
    public Banner createBanner(BannerDTO bannerDTO);
    public Iterable<Banner> findTop5Banner();
}
