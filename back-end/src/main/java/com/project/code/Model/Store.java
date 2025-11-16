package com.project.code.Model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @NotBlank
    private String address;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("inventory-store")
    private List<Inventory> inventoryEntries;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @JsonManagedReference("store-orders")
    private List<OrderDetails> orders;

    public Store() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
