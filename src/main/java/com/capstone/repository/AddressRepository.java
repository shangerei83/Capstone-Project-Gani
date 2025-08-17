package com.capstone.repository;

import com.capstone.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Address Repository - Data Access Layer
 *
 * This repository handles database operations for Address entities.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

	/**
	 * Find addresses by user ID
	 */
	@Query("SELECT a FROM Address a WHERE a.user.id = :userId")
	List<Address> findByUserId(@Param("userId") Long userId);

	/**
	 * Find addresses by user ID and type
	 */
	@Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.addressType = :type")
	List<Address> findByUserIdAndType(@Param("userId") Long userId, @Param("type") Address.AddressType type);

	/**
	 * Find default address by user ID and type
	 */
	@Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.addressType = :type AND a.isDefault = true")
	Optional<Address> findDefaultByUserIdAndType(@Param("userId") Long userId, @Param("type") Address.AddressType type);

	/**
	 * Find addresses by city
	 */
	@Query("SELECT a FROM Address a WHERE a.city = :city")
	List<Address> findByCity(@Param("city") String city);
}
