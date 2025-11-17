package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final ProductRepository productRepository;

    private final InventoryRepository inventoryRepository;

    private final CustomerRepository customerRepository;

    private final StoreRepository storeRepository;

    private final OrderDetailsRepository orderDetailsRepository;

    private final OrderItemRepository orderItemRepository;

    public OrderService(ProductRepository productRepository, CustomerRepository customerRepository,
                        StoreRepository storeRepository, OrderDetailsRepository orderDetailsRepository,
                        OrderItemRepository orderItemRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.storeRepository = storeRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
    }


    // 1. **saveOrder Method**:
//    - Processes a customer's order, including saving the order details and associated items.
//    - Parameters: `PlaceOrderRequestDTO placeOrderRequest` (Request data for placing an order)
//    - Return Type: `void` (This method doesn't return anything, it just processes the order)
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        //1. Retrieve or Create the Customer
        Customer existingCustomer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        Customer customer = new Customer(placeOrderRequest.getCustomerName(), placeOrderRequest.getCustomerEmail(), placeOrderRequest.getCustomerPhone());
        if (existingCustomer == null) {

            customerRepository.save(customer);
        } else {
            customer = existingCustomer;
        }

        //2. Retrieve the Store
        Store store = retrieveStore(placeOrderRequest.getStoreId());

        //3. Create OrderDetails

        OrderDetails orderDetails = createOrderDetails(customer, store, placeOrderRequest.getTotalPrice());

        //4. Create and Save OrderItem

        saveOrderItems(placeOrderRequest.getPurchaseProduct(), placeOrderRequest.getStoreId(), orderDetails);
    }

    // 2. **Retrieve the Store**:
//    - Fetch the store by ID from `storeRepository`.
//    - If the store doesn't exist, throw an exception. Use `storeRepository.findById()`.
    public Store retrieveStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found with ID: " + storeId));
    }

    // 3. **Create OrderDetails**:
//    - Create a new `OrderDetails` object and set customer, store, total price, and the current timestamp.
//    - Set the order date using `java.time.LocalDateTime.now()` and save the order with `orderDetailsRepository.save()`.
    public OrderDetails createOrderDetails(Customer customer, Store store, Double totalPrice) {
        OrderDetails orderDetails = new OrderDetails(customer, store, totalPrice, LocalDateTime.now());
        return orderDetailsRepository.save(orderDetails);

    }
// 4. **Create and Save OrderItems**:
//    - For each product purchased, find the corresponding inventory, update stock levels, and save the changes using `inventoryRepository.save()`.
//    - Create and save `OrderItem` for each product and associate it with the `OrderDetails` using `orderItemRepository.save()`.

    public void saveOrderItems(List<PurchaseProductDTO> products, Long storeId, OrderDetails orderDetails) {

        for (PurchaseProductDTO productDTO : products) {
            Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productDTO.getId(), storeId);
            inventory.setStockLevel(inventory.getStockLevel() - productDTO.getQuantity());
            inventoryRepository.save(inventory);


            Product product = productRepository.findById(productDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productDTO.getId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(orderDetails);
            orderItem.setProduct(product);
            orderItem.setQuantity(productDTO.getQuantity());
            orderItem.setPrice(productDTO.getPrice() * productDTO.getQuantity());
            orderItemRepository.save(orderItem);
        }
    }
}