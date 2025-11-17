package com.project.code.Controller;

import com.project.code.Model.ApiResponse;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ServiceClass serviceClass;
    private final InventoryRepository inventoryRepository;

    public ProductController(ProductRepository productRepository,
                             OrderItemRepository orderItemRepository,
                             ServiceClass serviceClass,
                             InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.serviceClass = serviceClass;
        this.inventoryRepository = inventoryRepository;
    }

    // --------------------------
    // ADD PRODUCT
    // --------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addProduct(@RequestBody Product product) {

        if (!serviceClass.validateProductId(product.getId())) {
            return ResponseEntity.status(409)
                    .body(new ApiResponse<>("Product already present in database", null));
        }

        try {
            productRepository.save(product);
            return ResponseEntity.ok(new ApiResponse<>("Product added successfully", null));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>("SKU must be unique", null));
        }
    }

    // --------------------------
    // GET PRODUCT BY ID
    // --------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Optional<Product>>> getProductById(@PathVariable Long id) {

        Optional<Product> result = productRepository.findById(id);

        if (result.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("Product not found", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("Product retrieved", result));
    }

    // --------------------------
    // UPDATE PRODUCT
    // --------------------------
    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updateProduct(@RequestBody Product product) {

        try {
            productRepository.save(product);
            return ResponseEntity.ok(new ApiResponse<>("Product updated successfully", null));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Error occurred while updating product", null));
        }
    }

    // --------------------------
    // FILTER BY NAME & CATEGORY
    // --------------------------
    @GetMapping("/filter/{name}/{category}")
    public ResponseEntity<ApiResponse<List<Product>>> filterByProductCategory(
            @PathVariable String name,
            @PathVariable String category) {

        List<Product> result;

        if (name.equals("null")) {
            result = productRepository.findByCategory(category);
        } else if (category.equals("null")) {
            result = productRepository.findProductBySubName(name);
        } else {
            result = productRepository.findProductBySubNameAndCategory(name, category);
        }

        return ResponseEntity.ok(new ApiResponse<>("Products filtered", result));
    }

    // --------------------------
    // LIST ALL PRODUCTS
    // --------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> listProduct() {

        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(new ApiResponse<>("All products retrieved", products));
    }

    // --------------------------
    // FILTER BY CATEGORY + STORE ID
    // --------------------------
    @GetMapping("/filter/{category}/{storeId}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductByCategoryAndStoreId(
            @PathVariable String category,
            @PathVariable long storeId) {

        List<Product> result = productRepository.findProductByCategory(category, storeId);

        return ResponseEntity.ok(new ApiResponse<>("Products retrieved", result));
    }

    // --------------------------
    // DELETE PRODUCT
    // --------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {

        if (!serviceClass.validateProductId(id)) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("Product ID not present in database", null));
        }

        inventoryRepository.deleteByProductId(id);
        orderItemRepository.deleteByProductId(id);
        productRepository.deleteById(id);

        return ResponseEntity.ok(new ApiResponse<>("Product deleted successfully", null));
    }

    // --------------------------
    // SEARCH PRODUCT BY NAME
    // --------------------------
    @GetMapping("/search/{name}")
    public ResponseEntity<ApiResponse<List<Product>>> searchProduct(@PathVariable String name) {

        List<Product> products = productRepository.findProductBySubName(name);

        return ResponseEntity.ok(new ApiResponse<>("Search results", products));
    }
}
