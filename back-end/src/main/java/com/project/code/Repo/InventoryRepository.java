package com.project.code.Repo;


import com.project.code.Model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.store.id = :storeId")
    public Inventory findByProductIdAndStoreId(Long productId, Long storeId);

    List<Inventory> findByStoreId(Long storeId);

    @Modifying
    @Transactional
    @Query("Delete From Inventory i where i.product.id = :productId")
    void deleteByProductId(Long productId);
}
