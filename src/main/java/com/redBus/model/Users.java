package com.redBus.model;

import com.redBus.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is Required")
    private String name;
    @NotBlank(message = "Mobile is Required")
    @Size(max = 10, min = 10)
    @Column(nullable = false, unique = true, length = 10)
    private String mobile;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @OneToMany(mappedBy = "lockedBy")
    private List<Seat> heldSeats;
}
