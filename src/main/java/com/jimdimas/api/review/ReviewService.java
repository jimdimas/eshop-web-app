package com.jimdimas.api.review;

import com.jimdimas.api.exception.BadRequestException;
import com.jimdimas.api.exception.ConflictException;
import com.jimdimas.api.exception.NotFoundException;
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

    public void addReview(User user,UUID productId,Review review) throws ConflictException, NotFoundException, BadRequestException {
        Optional<Review> reviewExists = reviewRepository.findUserReviewOnProduct(user.getUsername(),productId);
        if (reviewExists.isPresent()){
            throw new ConflictException("Review on product for given user already exists");
        }
        Optional<Product> productExists = productService.getProductById(productId);
        if (!productExists.isPresent()){
            throw new NotFoundException("Product with given id does not exist");
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

    private void checkReviewFields(Review review) throws BadRequestException {
        List<Integer> ratings = List.of(1,2,3,4,5);
        if (!ratings.contains(review.getRating())){
            throw new BadRequestException("Invalid rating given");
        }
        if (review.getText()==null || review.getText().length()<3){
            throw new BadRequestException("Invalid review text provided");
        }
    }

    public Optional<Review> getReviewById(UUID reviewId) {
        return reviewRepository.findByPublicId(reviewId);
    }

    public void updateReview(User user, UUID reviewId, Review review) throws NotFoundException {
        Optional<Review> reviewExists = reviewRepository.findByPublicId(reviewId);
        if (!reviewExists.isPresent()){
            throw new NotFoundException("No review with given id exists");
        }
        Review updatedReview=reviewExists.get();
        if (!user.getUsername().equals(updatedReview.getUser().getUsername())){
            throw new NotFoundException("No reviews by given user for given product");
        }

        if (review.getText()!=null && !(review.getText().length()<3)){
            updatedReview.setText(review.getText());
        }
        if (review.getRating()!=null && List.of(1,2,3,4,5).contains(review.getRating())){
            updatedReview.setRating(review.getRating());
        }
        reviewRepository.save(updatedReview);
    }

    public void deleteReview(User user, UUID reviewId) throws NotFoundException {
        Optional<Review> reviewExists = reviewRepository.findByPublicId(reviewId);
        if (!reviewExists.isPresent()){
            throw new NotFoundException("No review with given id exists");
        }
        Review deletedReview = reviewExists.get();
        if (!deletedReview.getUser().getUsername().equals(user.getUsername())){
            throw new NotFoundException("No reviews by given user for given product");
        }
        reviewRepository.delete(deletedReview);
    }
}
