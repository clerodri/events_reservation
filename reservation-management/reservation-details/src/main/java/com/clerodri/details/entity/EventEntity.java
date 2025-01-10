package com.clerodri.details.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
@ToString
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "eventDateTime")
    private LocalDateTime eventDateTime;

    @Column(name = "location" )
    private String location;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "availability")
    private int availability;

    @ToString.Exclude
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationEntity> reservations;

}
