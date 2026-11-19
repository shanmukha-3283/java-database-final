
package com.project.back_end.controller;

import com.project.back_end.entity.Product;
import com.project.back_end.repository.InventoryRepository;
import com.project.back_end.repository.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductController(
            ProductRepository productRepository,
            InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // Retrieve a product using its ID.
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid product ID")
            );
        }

        return productRepository.findById(id)
                .<ResponseEntity<?>>map(product ->
                        ResponseEntity.ok(
                                Map.of(
                                        "message", "Product found",
                                        "product", product
                                )
                        )
                )
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                Map.of("error", "Product not found")
                        )
                );
    }

    // Delete a product and all its inventory records.
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable Long id) {

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid product ID")
            );
        }

        try {
            Product product = productRepository.findById(id)
                    .orElse(null);

            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "Product not found")
                );
            }

            inventoryRepository.deleteByProduct_Id(id);
            productRepository.delete(product);

            return ResponseEntity.ok(
                    Map.of("message",
                            "Product and its inventory deleted successfully")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to delete product"));
        }
    }
}
