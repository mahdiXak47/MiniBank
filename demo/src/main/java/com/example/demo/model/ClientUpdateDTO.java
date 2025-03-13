package com.example.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientUpdateDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Client.ClientType clientType;

    @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

    private String address;

    @Pattern(regexp = "^\\d{5,10}$", message = "Postal code must be between 5 and 10 digits")
    private String postalCode;
} 