
package com.project.back_end.repository;

import com.project.back_end.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by category
    List<Product> findByCategoryIgnoreCase(String category);

    // Find products belonging to a particular store and category
    @Query("SELECT DISTINCT i.product FROM Inventory i " +
           "WHERE i.store.id = :storeId " +
           "AND LOWER(i.product.category) = LOWER(:category)")
    List<Product> findProductsByStoreIdAndCategory(
            @Param("storeId") Long storeId,
            @Param("category") String category
    );
}
