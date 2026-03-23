package com.smart.service.repository;

import com.smart.service.entity.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Long> {

    // Check for overlapping trips for the same driver
    @Query("SELECT COUNT(t) FROM TripEntity t WHERE t.driver.id = :driverId " +
            "AND t.status = 'AVAILABLE' " +
            "AND t.departureTime BETWEEN :start AND :end")
    long countOverlappingTrips(Long driverId, LocalDateTime start, LocalDateTime end);
    // Use JOIN FETCH to load the driver and category in one go
    @Query("SELECT t FROM TripEntity t " +
            "LEFT JOIN FETCH t.driver " +
            "LEFT JOIN FETCH t.category " +
            "WHERE t.driver.id = :driverId")
    List<TripEntity> findAllByDriverId(@Param("driverId") Long driverId);

    @Query("SELECT t FROM TripEntity t " +
            "LEFT JOIN FETCH t.driver " +
            "LEFT JOIN FETCH t.category " +
            "WHERE t.id = :id")
    Optional<TripEntity> findByIdWithDetails(@Param("id") Long id);
}