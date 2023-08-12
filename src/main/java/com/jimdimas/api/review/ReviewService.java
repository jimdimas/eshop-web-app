package com.jimdimas.api.review;

import com.jimdimas.api.product.Product;
import com.jimdimas.api.product.ProductService;
import com.jimdimas.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    public List<Review> getAllReviews(){
        return reviewRepository.findAll();
    }

    public List<Review> getProductReviews(UUID productId){
        return reviewRepository.findByProductId(productId);
    }

    public void addReview(User user,UUID productId,Review review) {
        Optional<Review> reviewExists = reviewRepository.findUserReviewOnProduct(user.getUsername(),productId);
        if (reviewExists.isPresent()){
            throw new IllegalStateException("Review on product for given user already exists");
        }
        Optional<Product> productExists = productService.getProductById(productId);
        if (!productExists.isPresent()){
            throw new IllegalStateException("Product with given id does not exist");
        }
        checkReviewFields(review);
        Review endReview = Review.builder()
                .reviewId(UUID.randomUUID())
                .user(user)
                .rating(review.getRating())
                .text(review.getText())
                .product(productExists.get())
                .build();
        reviewRepository.save(endReview);
    }

    private void checkReviewFields(Review review){
        List<Integer> ratings = List.of(1,2,3,4,5);
        if (!ratings.contains(review.getRating())){
            throw new IllegalStateException("Invalid rating given");
        }
        if (review.getText()==null || review.getText().length()<3){
            throw new IllegalStateException("Invalid review text provided");
        }
    }

    public Optional<Review> getReviewById(UUID reviewId) {
        return reviewRepository.findByPublicId(reviewId);
    }
}