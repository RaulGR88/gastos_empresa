package com.kael.gastosEmpresa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long personId;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Double amount;

    private String description;

    private LocalDateTime creationDate;

    public Payment() {
    }

    public Payment(Long personId, Long groupId, Double amount, String description) {
        this.personId = personId;
        this.groupId = groupId;
        this.amount = amount;
        this.description = description;
        this.creationDate = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}