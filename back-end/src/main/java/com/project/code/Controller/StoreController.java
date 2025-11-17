package com.project.code.Controller;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Model.ApiResponse;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreRepository storeRepository;
    private final OrderService orderService;

    public StoreController(StoreRepository storeRepository, OrderService orderService) {
        this.storeRepository = storeRepository;
        this.orderService = orderService;
    }

    // --------------------------
    // ADD STORE
    // --------------------------
    @PostMapping
    public ApiResponse<Void> addStore(@RequestBody Store store) {
        Store savedStore = storeRepository.save(store);
        return new ApiResponse<>("Store added successfully with id " + savedStore.getId(), null);
    }

    // --------------------------
    // VALIDATE STORE
    // --------------------------
    @GetMapping("/validate/{storeId}")
    public ApiResponse<Boolean> validateStore(@PathVariable Long storeId) {
        boolean exists = storeRepository.existsById(storeId);
        return new ApiResponse<>(null, exists);
    }

    // --------------------------
    // PLACE ORDER
    // --------------------------
    @PostMapping("/placeOrder")
    public ApiResponse<Void> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequest) {
        try {
            orderService.saveOrder(placeOrderRequest);
            return new ApiResponse<>("Order placed successfully", null);
        } catch (Exception e) {
            return new ApiResponse<>("Error: " + e.getMessage(), null);
        }
    }
}
