package com.kael.gastosEmpresa.model;

import java.time.LocalDateTime;

public class PaymentInfoDTO {
    private String name;
    private String lastName;
    private Double amount;
    private String description;
    private LocalDateTime creationDate;

    public PaymentInfoDTO(String name, String lastName, Double amount, String description, LocalDateTime creationDate) {
        this.name = name;
        this.lastName = lastName;
        this.amount = amount;
        this.description = description;
        this.creationDate = creationDate;
    }

    // Getters
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public Double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getCreationDate() { return creationDate; }
}
