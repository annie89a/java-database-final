package com.project.code.Service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public void saveOrder(PlaceOrderRequestDTO request) {
        // 1: retrieve/create customer
        Customer customer = createOrRetrieveCustomer(request.getEmail());

        // 2: retrieve store
        Store store = retrieveStore(request.getStoreId());

        // 3: create and save OrderDetails
        OrderDetails order = createOrderDetails(customer, store, request.getTotalPrice());

        // 4: process and save each OrderItem
        saveOrderItems(order, store, request.getItems());
    }

    public Customer createOrRetrieveCustomer(String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer != null) {
            return customer;
        }

        Customer newCustomer = new Customer();
        newCustomer.setEmail(email);
        return customerRepository.save(newCustomer);
    }

    public Store retrieveStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found with ID: " + storeId));
    }

    public OrderDetails createOrderDetails(Customer customer, Store store, Double totalPrice) {
        OrderDetails order = new OrderDetails();
        order.setCustomer(customer);
        order.setStore(store);
        order.setTotalPrice(totalPrice);
        order.setDate(LocalDateTime.now());
        return orderDetailsRepository.save(order);
    }

    public void saveOrderItems(OrderDetails order, Store store, List<OrderItemDTO> items) {
        for (OrderItemDTO itemDTO : items) {
            // retrieve inventory
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(itemDTO.getProductId(), store.getId());
            if (inventory == null || inventory.getQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product ID " + itemDTO.getProductId());
            }

            // update inventory
            inventory.setQuantity(inventory.getQuantity() - itemDTO.getQuantity());
            inventoryRepository.save(inventory);

            // create and save OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(inventory.getProduct());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(inventory.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }
    }
}
