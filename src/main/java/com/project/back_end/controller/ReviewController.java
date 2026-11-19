
package com.project.back_end.controller;

import com.project.back_end.entity.Review;
import com.project.back_end.repository.ReviewRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // Retrieve reviews for a particular store and product.
    @GetMapping("/{storeId}/{productId}")
    public ResponseEntity<?> getReviews(
            @PathVariable Long storeId,
            @PathVariable Long productId) {

        if (storeId == null || storeId <= 0
                || productId == null || productId <= 0) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid store ID or product ID")
            );
        }

        // Fetch all reviews from the database.
        List<Review> allReviews = reviewRepository.findAll();

        // Filter reviews using store ID and product ID.
        List<Map<String, Object>> filteredReviews = allReviews.stream()
                .filter(review ->
                        review.getStore() != null
                        && review.getProduct() != null
                        && review.getStore().getId().equals(storeId)
                        && review.getProduct().getId().equals(productId)
                )
                .map(review -> Map.<String, Object>of(
                        "reviewId", review.getId(),
                        "customerName",
                        review.getCustomer() != null
                                ? review.getCustomer().getName()
                                : "Unknown",
                        "rating", review.getRating() != null
                                ? review.getRating() : 0,
                        "comment", review.getComment() != null
                                ? review.getComment() : ""
                ))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "storeId", storeId,
                        "productId", productId,
                        "count", filteredReviews.size(),
                        "reviews", filteredReviews
                )
        );
    }
}
