package hcmute.it.furnitureshop.Service.Impl;

import hcmute.it.furnitureshop.DTO.CategoryDTO;
import hcmute.it.furnitureshop.DTO.DiscountDTO;
import hcmute.it.furnitureshop.Entity.Category;
import hcmute.it.furnitureshop.Entity.Discount;
import hcmute.it.furnitureshop.Repository.DiscountRepository;
import hcmute.it.furnitureshop.Service.DiscountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DiscountServiceImpl implements DiscountService {
    @Autowired
    private DiscountRepository discountRepository;
    @Override
    public List<DiscountDTO> getListDiscount() {
        List<DiscountDTO> discountDTOS= new ArrayList<>();
        discountRepository.findAll().forEach(discount -> {
            discountDTOS.add(DiscountDTO.builder().discountId(discount.getDiscountId())
                    .discountName(discount.getDiscountName())
                    .percentDiscount(discount.getPercentDiscount())
                    .build());

        });
        return discountDTOS;
    }
    @Override
    public Iterable<Discount> getAll(){
        return discountRepository.findAll();
    }

    @Override
    public String deleteDiscount(Integer discountId) {
        Optional<Discount> discount = discountRepository.findById(discountId);
        if(discount.isPresent())
        {
            discountRepository.deleteById(discountId);
            return "Xóa giảm giá thành công";
        }
        return "Xóa giảm giá thất bại";
    }

    @Override
    public String updateDiscount(DiscountDTO discountDTO) {
        if(!discountDTO.getDiscountName().isEmpty()) {
            Optional<Discount> cate = discountRepository.findById(discountDTO.getDiscountId());
            if (cate.isPresent()) {
                cate.get().setDiscountName(discountDTO.getDiscountName());
                cate.get().setPercentDiscount(discountDTO.getPercentDiscount());
                return "Cập nhật giảm giá thành công";
            }
            return "Không tồn tại giảm giá này";
        }
        return "Cập nhật giảm giá thất bại";
    }

    @Override
    public Discount createDiscount(DiscountDTO discountDTO) {
        if(!discountDTO.getDiscountName().isEmpty()) {
            Optional<Discount> dis = discountRepository.findByDiscountName(discountDTO.getDiscountName());
            if (dis.isEmpty()) {
                Discount discount = Discount.builder().discountName(discountDTO.getDiscountName())
                        .percentDiscount(discountDTO.getPercentDiscount())
                        .build();
                discountRepository.save(discount);
                return discount;
            }
        }
        return null;
    }

    @Override
    public DiscountDTO getById(Integer discountId) {
        Optional<Discount> discount = discountRepository.findById(discountId);
        if(discount.isPresent())
            return DiscountDTO.builder().discountId(discount.get().getDiscountId())
                    .discountName(discount.get().getDiscountName())
                    .percentDiscount(discount.get().getPercentDiscount())
                    .numberProduct(discount.get().getProducts().size())
                    .build();
        return null;
    }
}
