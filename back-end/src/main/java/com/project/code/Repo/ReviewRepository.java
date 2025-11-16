package com.project.code.Repo;

import com.example.bookstoreHibernate.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    
    // Find reviews for a specific product in a specific store
    List<Review> findByStoreIdAndProductId(Long storeId, Long productId);

    // Optional: Add other query methods if needed, e.g., by customerId
    List<Review> findByCustomerId(Long customerId);
}