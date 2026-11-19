
package com.project.back_end.service;

import com.project.back_end.entity.*;
import com.project.back_end.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public OrderService(
        OrderDetailsRepository orderDetailsRepository,
        InventoryRepository inventoryRepository,
        CustomerRepository customerRepository,
        StoreRepository storeRepository,
        ProductRepository productRepository) {

    this.orderDetailsRepository = orderDetailsRepository;
    this.inventoryRepository = inventoryRepository;
    this.customerRepository = customerRepository;
    this.storeRepository = storeRepository;
    this.productRepository = productRepository;
}

    @Transactional
    public OrderDetails placeOrder(OrderDetails order) {

        if (order == null || order.getCustomer() == null
                || order.getStore() == null
                || order.getProduct() == null
                || order.getCustomer().getId() == null
                || order.getStore().getId() == null
                || order.getProduct().getId() == null
                || order.getQuantity() == null
                || order.getQuantity() <= 0) {

            throw new IllegalArgumentException(
                    "Invalid order details");
        }

        // Fetch existing records from the database.
        Customer customer = customerRepository.findById(
                order.getCustomer().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found"));

        Store store = storeRepository.findById(
                order.getStore().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Store not found"));

        Product product = productRepository.findById(
                order.getProduct().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found"));

        // Find inventory for this product and store.
        Inventory inventory = inventoryRepository
                .findByProductIdAndStoreId(
                        product.getId(), store.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Inventory not found"));

        if (inventory.getQuantity() < order.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        // Prepare the order.
        order.setCustomer(customer);
        order.setStore(store);
        order.setProduct(product);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(product.getPrice() * order.getQuantity());

        // Save the order.
        OrderDetails savedOrder = orderDetailsRepository.save(order);

        // Reduce and save inventory.
        inventory.setQuantity(
                inventory.getQuantity() - savedOrder.getQuantity());

        inventoryRepository.save(inventory);

        return savedOrder;
    }
}
