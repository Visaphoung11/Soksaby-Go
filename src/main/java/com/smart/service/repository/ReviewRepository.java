package com.smart.service.repository;

import com.smart.service.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    
    @Query("SELECT r FROM ReviewEntity r JOIN FETCH r.passenger p LEFT JOIN FETCH r.imageUrls i WHERE r.trip.id = :tripId ORDER BY r.createdAt DESC")
    List<ReviewEntity> findAllByTripIdOrderByCreatedAtDesc(@Param("tripId") Long tripId);

    @Query("SELECT r FROM ReviewEntity r JOIN FETCH r.passenger p LEFT JOIN FETCH r.imageUrls i WHERE r.driver.id = :driverId ORDER BY r.createdAt DESC")
    List<ReviewEntity> findAllByDriverIdOrderByCreatedAtDesc(@Param("driverId") Long driverId);

    long countByDriverId(Long driverId);

    boolean existsByPassengerIdAndTripId(Long passengerId, Long tripId);
}
