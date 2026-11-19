
package com.project.back_end.controller;

import com.project.back_end.entity.OrderDetails;
import com.project.back_end.repository.StoreRepository;
import com.project.back_end.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreRepository storeRepository;
    private final OrderService orderService;

    public StoreController(
            StoreRepository storeRepository,
            OrderService orderService) {
        this.storeRepository = storeRepository;
        this.orderService = orderService;
    }

    // GET endpoint to validate whether a store exists.
    @GetMapping("/validate/{storeId}")
    public ResponseEntity<Map<String, Object>> validateStore(
            @PathVariable Long storeId) {

        if (storeId == null || storeId <= 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Invalid store ID")
            );
        }

        if (!storeRepository.existsById(storeId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message", "Store not found")
            );
        }

        return ResponseEntity.ok(
                Map.of("message", "Store exists", "storeId", storeId)
        );
    }

    // Places an order and handles possible exceptions.
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> placeOrder(
            @RequestBody OrderDetails order) {

        try {
            OrderDetails savedOrder = orderService.placeOrder(order);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Order placed successfully",
                            "orderId", savedOrder.getId(),
                            "order", savedOrder
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to place order"));
        }
    }
}
