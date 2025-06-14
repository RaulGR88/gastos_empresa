package com.kael.gastosEmpresa.controller;

import com.kael.gastosEmpresa.model.Payment;
import com.kael.gastosEmpresa.model.PaymentInfoDTO;
import com.kael.gastosEmpresa.model.Person;
import com.kael.gastosEmpresa.repository.GroupRepository;
import com.kael.gastosEmpresa.repository.PaymentRepository;
import com.kael.gastosEmpresa.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private GroupRepository groupRepository;
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<PaymentInfoDTO>> getPaymentsByGroup(@PathVariable Long groupId) {

        if (!groupRepository.existsById(groupId)) {
            return ResponseEntity.notFound().build();  // 404 si no existe el grupo
        }

        List<Payment> payments = paymentRepository.findByGroupIdOrderByCreationDateDesc(groupId);

        List<PaymentInfoDTO> result = payments.stream()
                .map(payment -> {
                    Optional<Person> personOpt = personRepository.findById(payment.getPersonId());
                    if (personOpt.isPresent()) {
                        Person person = personOpt.get();
                        return new PaymentInfoDTO(
                                person.getName(),
                                person.getLastName(),
                                payment.getAmount(),
                                payment.getDescription(),
                                payment.getCreationDate()
                        );
                    } else {
                        return null;
                    }
                })
                .filter(p -> p != null)
                .toList();

        return ResponseEntity.ok(result);
    }

    // POST: Crear pago
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        // Validaciones
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            return ResponseEntity.badRequest().body("El importe debe ser mayor que cero.");
        }

        if (payment.getPersonId() == null || payment.getGroupId() == null) {
            return ResponseEntity.badRequest().body("Los campos personId y groupId son obligatorios.");
        }

        payment.setCreationDate(java.time.LocalDateTime.now());
        return ResponseEntity.ok(paymentRepository.save(payment));
    }

    // DELETE: Eliminar pago por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        if (payment.isPresent()) {
            paymentRepository.deleteById(id);
            return ResponseEntity.ok("Pago eliminado correctamente.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/test")
    public String test() {
        return "Controlador activo";
    }
}
