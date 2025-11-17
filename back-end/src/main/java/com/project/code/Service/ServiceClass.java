package com.project.code.Service;


import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Model.Store;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class ServiceClass {

    public InventoryRepository inventoryRepository;

    public ProductRepository productRepository;

    public ServiceClass(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }
    
    public boolean validateInventory(Inventory inventory) {
        Inventory foundInventory = inventoryRepository.findByProductIdAndStoreId(inventory.getProduct().getId(), inventory.getStore().getId());

        return foundInventory != null;
    }
    
    public boolean validateProductByName(Product product) {
        Optional<Product> repoProd = productRepository.findByName(product.getName());

        return repoProd.isPresent();
    }
    
    public boolean validateProductId(Long id) {
        Optional<Product> product = productRepository.findById(id);

        return product.isPresent();
    }
    
    public Inventory getInventory(Inventory inventory) {
        Inventory foundInventoryRecord = inventoryRepository.findByProductIdAndStoreId(inventory.getProduct().getId(), inventory.getStore().getId());
        return foundInventoryRecord;
    }
}
