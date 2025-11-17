package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference("order-items")
    private OrderDetails orderDetails;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private Double price;

    public OrderItem() {
    }

    public OrderItem(OrderDetails orderDetails, Product product, Integer quantity, Double price) {
        this.orderDetails = orderDetails;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderDetails getOrderDetail() {
        return orderDetails;
    }

    public void setOrderDetail(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

