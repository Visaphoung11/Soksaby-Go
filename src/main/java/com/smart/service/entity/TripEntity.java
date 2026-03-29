package com.smart.service.entity;

import com.smart.service.enums.TripStatus;
import jakarta.persistence.*;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@SQLDelete(sql = "UPDATE trips SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String origin;
    private String destination;
    private Double pricePerSeat;
    private Integer totalSeats;
    private Integer availableSeats;
    private LocalDateTime departureTime;
    private LocalDateTime createdAt = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.AVAILABLE;

    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private UserEntity driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    private String transportationType;

    private Integer vehicleCapacity;
    private Boolean isWholeVehicleBooking;
    private Double wholeVehiclePrice;
    
    private String scheduleDescription;
    
    private Boolean hasTourGuide;
    @Column(columnDefinition = "TEXT")
    private String tourGuideDescription;
    private String tourGuideImageUrl;
    
    private Boolean mealsIncluded;
    @Column(columnDefinition = "TEXT")
    private String diningDetails;
    
    private String availabilitySchedule;

    @ElementCollection
    @CollectionTable(name = "trip_vehicle_images", joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "image_url")
    private List<String> vehicleImageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItineraryItemEntity> itinerary = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripImageEntity> images = new ArrayList<>();
}