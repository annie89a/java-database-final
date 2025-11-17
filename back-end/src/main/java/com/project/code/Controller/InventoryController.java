package com.project.code.Controller;

import com.project.code.Model.ApiResponse;
import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ServiceClass serviceClass;

    public InventoryController(ProductRepository productRepository,
                               InventoryRepository inventoryRepository,
                               ServiceClass serviceClass) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.serviceClass = serviceClass;
    }

    // ----------------------
    // UPDATE INVENTORY
    // ----------------------
    @PutMapping("/updateInventory")
    public ResponseEntity<ApiResponse<Void>> updateInventory(@RequestBody CombinedRequest request) {

        Product product = request.getProduct();
        Inventory inventory = request.getInventory();

        if (!serviceClass.validateProductId(product.getId())) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("Product ID not found: " + product.getId(), null));
        }

        productRepository.save(product);

        try {
            Inventory existing = serviceClass.getInventory(inventory);
            if (existing == null) {
                return ResponseEntity.status(404)
                        .body(new ApiResponse<>("No inventory found for this product/store", null));
            }

            inventory.setId(existing.getId());
            inventoryRepository.save(inventory);

            return ResponseEntity.ok(new ApiResponse<>("Inventory updated successfully", null));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Error: " + e.getMessage(), null));
        }
    }


    // ----------------------
    // SAVE NEW INVENTORY
    // ----------------------
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Void>> saveInventory(@RequestBody Inventory inventory) {

        try {
            if (!serviceClass.validateInventory(inventory)) {
                return ResponseEntity.status(409)
                        .body(new ApiResponse<>("Inventory already exists", null));
            }

            inventoryRepository.save(inventory);
            return ResponseEntity.ok(new ApiResponse<>("Inventory saved successfully", null));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Error: " + e.getMessage(), null));
        }
    }


    // ----------------------
    // GET ALL PRODUCTS FOR STORE
    // ----------------------
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts(@PathVariable Long storeId) {

        List<Product> result = productRepository.findProductsByStoreId(storeId);
        return ResponseEntity.ok(new ApiResponse<>("Products retrieved", result));
    }


    // ----------------------
    // FILTER PRODUCTS
    // ----------------------
    @GetMapping("/filter/{category}/{name}/{storeId}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductName(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable Long storeId) {

        List<Product> result;

        if (category.equals("null")) {
            result = productRepository.findByNameLike(storeId, name);
        } else if (name.equals("null")) {
            result = productRepository.findByCategoryAndStoreId(storeId, category);
        } else {
            result = productRepository.findByNameAndCategory(storeId, name, category);
        }

        return ResponseEntity.ok(new ApiResponse<>("Filtered products", result));
    }


    // ----------------------
    // SEARCH PRODUCTS
    // ----------------------
    @GetMapping("/search/{name}/{storeId}")
    public ResponseEntity<ApiResponse<List<Product>>> searchProduct(
            @PathVariable String name,
            @PathVariable Long storeId) {

        List<Product> result = productRepository.findByNameLike(storeId, name);
        return ResponseEntity.ok(new ApiResponse<>("Search results", result));
    }


    // ----------------------
    // DELETE PRODUCT
    // ----------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeProduct(@PathVariable Long id) {

        if (!serviceClass.validateProductId(id)) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("Product ID not found: " + id, null));
        }

        inventoryRepository.deleteByProductId(id);

        return ResponseEntity.ok(new ApiResponse<>("Product deleted successfully", null));
    }


    // ----------------------
    // VALIDATE QUANTITY
    // ----------------------
    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> validateQuantity(
            @PathVariable int quantity,
            @PathVariable long storeId,
            @PathVariable long productId) {

        Inventory result = inventoryRepository.findByProductIdAndStoreId(productId, storeId);

        boolean available = result.getStockLevel() >= quantity;

        return ResponseEntity.ok(new ApiResponse<>("Quantity validation result", available));
    }
}
