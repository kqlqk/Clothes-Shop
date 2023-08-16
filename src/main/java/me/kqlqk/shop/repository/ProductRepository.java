package me.kqlqk.shop.repository;

import me.kqlqk.shop.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    boolean existsByName(String name);

    boolean existsByPath(String path);

    @Query("from Product p where p.discount > 0")
    List<Product> findSales();

    @Query(value = "select * from Product p " +
            "order by p.id desc " +
            "limit :limit",
            nativeQuery = true)
    List<Product> findLastProducts(@Param("limit") int limit);
}
