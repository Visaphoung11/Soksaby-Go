package com.smart.service.repository;

import com.smart.service.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByPassengerIdOrderByCreatedAtDesc(Long passengerId);

    @Query("SELECT b FROM BookingEntity b JOIN FETCH b.trip t WHERE t.driver.id = :driverId AND b.status = 'PENDING'")
    List<BookingEntity> findPendingRequestsByDriverId(@Param("driverId") Long driverId);
}