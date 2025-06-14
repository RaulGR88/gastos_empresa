package com.kael.gastosEmpresa.repository;

import com.kael.gastosEmpresa.model.Payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByGroupId(Long groupId);
    List<Payment> findByGroupIdOrderByCreationDateDesc(Long groupId);
}