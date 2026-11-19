
package com.project.back_end.controller;

import com.project.back_end.entity.Inventory;
import com.project.back_end.repository.InventoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // Filter inventory by category, product name, and store ID.
    @GetMapping("/filter/{category}/{name}/{storeId}")
    public ResponseEntity<?> filterInventory(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable Long storeId) {

        if (category == null || category.isBlank()
                || name == null || name.isBlank()
                || storeId == null || storeId <= 0) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid filter parameters")
            );
        }

        List<Inventory> storeInventory =
                inventoryRepository.findAllByStore_Id(storeId);

        List<Inventory> filteredInventory = storeInventory.stream()
                .filter(item -> {
                    String productCategory =
                            item.getProduct().getCategory();
                    String productName =
                            item.getProduct().getName();

                    boolean categoryMatches =
                            category.equalsIgnoreCase("all")
                            || (productCategory != null
                            && productCategory.toLowerCase()
                                    .contains(category.toLowerCase()));

                    boolean nameMatches =
                            name.equalsIgnoreCase("all")
                            || (productName != null
                            && productName.toLowerCase()
                                    .contains(name.toLowerCase()));

                    return categoryMatches && nameMatches;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                Map.of(
                        "storeId", storeId,
                        "count", filteredInventory.size(),
                        "inventory", filteredInventory
                )
        );
    }

    // Validate whether the requested quantity is available.
    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public ResponseEntity<Map<String, Object>> validateQuantity(
            @PathVariable Integer quantity,
            @PathVariable Long storeId,
            @PathVariable Long productId) {

        if (quantity == null || quantity <= 0
                || storeId == null || storeId <= 0
                || productId == null || productId <= 0) {

            return ResponseEntity.badRequest().body(
                    Map.of("valid", false,
                           "message", "Invalid quantity or ID")
            );
        }

        return inventoryRepository
                .findByProductIdAndStoreId(productId, storeId)
                .map(inventory -> {
                    boolean available =
                            inventory.getQuantity() >= quantity;

                    return ResponseEntity.ok(
                            Map.<String, Object>of(
                                    "valid", available,
                                    "requestedQuantity", quantity,
                                    "availableQuantity",
                                    inventory.getQuantity(),
                                    "message", available
                                            ? "Sufficient stock available"
                                            : "Insufficient stock"
                            )
                    );
                })
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                Map.of(
                                        "valid", false,
                                        "message", "Inventory record not found"
                                )
                        )
                );
    }
}
