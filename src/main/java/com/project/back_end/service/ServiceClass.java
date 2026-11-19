
package com.project.back_end.service;

import com.project.back_end.entity.Inventory;
import com.project.back_end.repository.InventoryRepository;

import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository;

    public ServiceClass(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // Checks whether inventory exists for a product in a particular store.
    public boolean validateInventory(Long productId, Long storeId) {

        if (productId == null || storeId == null) {
            return false;
        }

        return inventoryRepository
                .findByProductIdAndStoreId(productId, storeId)
                .isPresent();
    }

    // Retrieves the inventory record using product ID and store ID.
    public Inventory getInventoryId(Long productId, Long storeId) {

        if (productId == null || storeId == null) {
            throw new IllegalArgumentException(
                    "Product ID and Store ID are required"
            );
        }

        return inventoryRepository
                .findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Inventory record not found"
                        ));
    }
}
