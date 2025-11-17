package com.project.code.Model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "address cannot be null")
    @NotBlank(message = "address cannot be blank")
    private String address;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("inventory-store")
    private List<Inventory> inventoryEntries;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @JsonManagedReference("store-orders")
    private List<OrderDetails> orders = new ArrayList<>();

    public Store() {
    }

    public Store(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Inventory> getInventoryEntries() {
        return inventoryEntries;
    }

    public void setInventoryEntries(List<Inventory> inventoryEntries) {
        this.inventoryEntries = inventoryEntries;
    }

    public List<OrderDetails> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDetails> orders) {
        this.orders = orders;
    }
}
