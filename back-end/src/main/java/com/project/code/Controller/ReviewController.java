package com.project.code.Controller;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            CustomerRepository customerRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/{storeId}/{productId}")
    public List<ReviewResponse> getReviews(
            @PathVariable long storeId,
            @PathVariable long productId) {

        return reviewRepository.findByStoreIdAndProductId(storeId, productId)
                .stream()
                .map(review -> {
                    Optional<Customer> customer = customerRepository.findById(review.getCustomerId());
                    String customerName = (customer.isPresent()) ? customer.get().getName() : "Unknown";

                    return new ReviewResponse(
                            review.getComment(),
                            review.getRating(),
                            customerName
                    );
                })
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // DTO class for clean JSON output
    public static class ReviewResponse {
        private String review;
        private int rating;
        private String customerName;

        public ReviewResponse(String review, int rating, String customerName) {
            this.review = review;
            this.rating = rating;
            this.customerName = customerName;
        }

        public String getReview() { return review; }
        public int getRating() { return rating; }
        public String getCustomerName() { return customerName; }
    }
}
