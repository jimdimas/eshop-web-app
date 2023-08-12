package com.jimdimas.api.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {

    @Query("SELECT r FROM Review r WHERE r.reviewId=?1")
    Optional<Review> findByPublicId(UUID publicId);

    @Query("SELECT r FROM Review r where r.product.productId=?1")
    List<Review> findByProductId(UUID productId);

    @Query("SELECT r FROM Review r WHERE r.product.productId=:productId AND r.user.username=:username")
    Optional<Review> findUserReviewOnProduct(@Param("username") String username,@Param("productId") UUID productId);
}
