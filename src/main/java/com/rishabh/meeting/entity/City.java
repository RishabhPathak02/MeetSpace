package com.rishabh.meeting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cities")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String country = "India";

    @Column(name = "is_active")
    private Boolean isActive = true;
}
