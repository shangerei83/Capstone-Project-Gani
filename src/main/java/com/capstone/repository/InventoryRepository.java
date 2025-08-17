package com.capstone.repository;

import com.capstone.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Inventory Repository - Data Access Layer
 *
 * This repository handles database operations for Inventory entities.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	/**
	 * Find inventory by product ID
	 */
	Optional<Inventory> findByProductId(Long productId);

	/**
	 * Find low stock products (currentStock <= minimumStock)
	 */
	@Query("SELECT i FROM Inventory i WHERE i.currentStock <= i.minimumStock")
	List<Inventory> findLowStockProducts();

	/**
	 * Find out of stock products (currentStock = 0)
	 */
	@Query("SELECT i FROM Inventory i WHERE i.currentStock = 0")
	List<Inventory> findOutOfStockProducts();

	/**
	 * Find inventories by stock range using currentStock
	 */
	@Query("SELECT i FROM Inventory i WHERE i.currentStock BETWEEN :minStock AND :maxStock")
	List<Inventory> findByStockRange(@Param("minStock") Integer minStock, @Param("maxStock") Integer maxStock);
}
