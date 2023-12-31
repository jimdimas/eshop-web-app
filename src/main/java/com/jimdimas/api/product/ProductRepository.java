package com.jimdimas.api.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value="SELECT p FROM Product p where p.user.username=?1")
    Optional<List<Product>> findProductsByUserUsername(String username);

    @Query(value="SELECT p FROM Product p where p.productId=?1")
    Optional<Product> findByPublicId(UUID productId);

    List<ProductProjection> findAllProjectedBy();

    @Query(value="SELECT p FROM Product p where p.user.username=?1")
    List<ProductProjection> findProjectionByUsername(String username);

    @Query(value = "SELECT p FROM Product  p where p.category=?1")
    List<ProductProjection> findProjectionByCategory(Category category);
}
