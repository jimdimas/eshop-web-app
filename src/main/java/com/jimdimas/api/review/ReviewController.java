package com.jimdimas.api.review;

import com.jimdimas.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/review"})
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<Review> getReviews(@RequestParam(name="productId",required = false) Optional<UUID> productId){
        if (productId.isPresent()){
            return reviewService.getProductReviews(productId.get());
        }
        return reviewService.getAllReviews();
    }

    @GetMapping(path="{reviewId}")
    public Optional<Review> getReviewById(@PathVariable(name="reviewId") UUID reviewId){
        return reviewService.getReviewById(reviewId);
    }

    @PostMapping
    public void addReview(
            @RequestAttribute(name="user") User user,
            @RequestParam(name="productId") UUID productId,
            @RequestBody Review review){
        reviewService.addReview(user,productId,review);
    }
}
