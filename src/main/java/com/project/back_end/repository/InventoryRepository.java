
package com.project.back_end.repository;

import com.project.back_end.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find inventory using both product ID and store ID
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.store.id = :storeId")
    Optional<Inventory> findByProductIdAndStoreId(
            @Param("productId") Long productId,
            @Param("storeId") Long storeId
    );
    // Delete all inventory records associated with a product.
    void deleteByProduct_Id(Long productId);
    // Find all inventory records belonging to a particular store
    List<Inventory> findAllByStore_Id(Long storeId);
}
