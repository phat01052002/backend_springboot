package hcmute.it.furnitureshop.Repository;

import hcmute.it.furnitureshop.Entity.Banner;

import hcmute.it.furnitureshop.Entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface BannerRepository extends CrudRepository<Banner, Integer> {
    ArrayList<Banner> findAll();
    Optional<Banner> findByProduct(Product product);
    @Transactional
    @Query(value="SELECT * FROM springserverdb.banner order by banner_id asc limit 5;", nativeQuery = true)
    public Iterable<Banner> findTop5Banner();
}
