package com.kael.gastosEmpresa.controller;

import com.kael.gastosEmpresa.model.Group;
import com.kael.gastosEmpresa.model.GroupBalanceDTO;
import com.kael.gastosEmpresa.model.Payment;
import com.kael.gastosEmpresa.model.PaymentInfoDTO;
import com.kael.gastosEmpresa.model.Person;
import com.kael.gastosEmpresa.model.SettlementDTO;
import com.kael.gastosEmpresa.repository.GroupRepository;
import com.kael.gastosEmpresa.repository.PaymentRepository;
import com.kael.gastosEmpresa.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PersonRepository personRepository;

    // GET all groups
    @GetMapping
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }
 
    // POST create a new group
    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        if (group.getName() == null || group.getName().isBlank()) {
            return ResponseEntity.badRequest().build();  // Rechaza si no tiene nombre
        }
        group.setCreationDate(java.time.LocalDateTime.now());
        return ResponseEntity.ok(groupRepository.save(group));
    }

    // PUT update a group
    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long id, @RequestBody Group updatedGroup) {
        return groupRepository.findById(id)
                .map(group -> {
                    if (updatedGroup.getName() != null && !updatedGroup.getName().isBlank()) {
                        group.setName(updatedGroup.getName());
                    }
                    return ResponseEntity.ok(groupRepository.save(group));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{groupId}/payments")
    public ResponseEntity<List<PaymentInfoDTO>> getGroupPayments(@PathVariable Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            return ResponseEntity.notFound().build(); // Si no existe el grupo
        }

        List<PaymentInfoDTO> paymentList = paymentRepository.findByGroupIdOrderByCreationDateDesc(groupId)
                .stream()
                .map(p -> {
                    var person = personRepository.findById(p.getPersonId()).orElse(null);
                    String name = person != null ? person.getName() : "Desconocido";
                    String lastName = person != null ? person.getLastName() : "";
                    return new PaymentInfoDTO(name, lastName, p.getAmount(), p.getDescription(), p.getCreationDate());
                })
                .toList(); 

        return ResponseEntity.ok(paymentList);
    }
    
    @GetMapping("/{groupId}/balance")
    public ResponseEntity<List<GroupBalanceDTO>> getGroupBalance(@PathVariable Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            return ResponseEntity.notFound().build(); // 404 si no existe el grupo
        }

        List<Payment> payments = paymentRepository.findByGroupId(groupId);

        if (payments.isEmpty()) {
            return ResponseEntity.ok(List.of()); // Lista vacía si no hay pagos
        }

        // Suma total de pagos y número de personas distintas
        Map<Long, Double> personPayments = new HashMap<>();
        for (Payment p : payments) {
            personPayments.put(p.getPersonId(),
                    personPayments.getOrDefault(p.getPersonId(), 0.0) + p.getAmount());
        }

        double total = payments.stream().mapToDouble(Payment::getAmount).sum();
        int numPersons = personPayments.size();
        double average = total / numPersons;

        List<GroupBalanceDTO> result = personPayments.entrySet().stream().map(entry -> {
            var person = personRepository.findById(entry.getKey()).orElse(null);
            if (person == null) return null;
            double balance = Math.round((entry.getValue() - average) * 100.0) / 100.0;
            return new GroupBalanceDTO(person.getName(), person.getLastName(), balance);
        }).filter(Objects::nonNull).toList();

        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{groupId}/settlements")
    public ResponseEntity<List<SettlementDTO>> calculateSettlements(@PathVariable Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            return ResponseEntity.notFound().build();  // ✅ 404 si no existe el grupo
        }

        List<Payment> payments = paymentRepository.findByGroupId(groupId);

        if (payments.isEmpty()) {
            return ResponseEntity.ok(List.of());  // ✅ lista vacía si no hay pagos
        }

        // Paso 1: Calcular total pagado por persona
        Map<Long, Double> pagosPorPersona = new HashMap<>();
        for (Payment p : payments) {
            pagosPorPersona.merge(p.getPersonId(), p.getAmount(), Double::sum);
        }

        int totalPersonas = pagosPorPersona.size();
        double totalGastado = pagosPorPersona.values().stream().mapToDouble(Double::doubleValue).sum();
        double media = totalGastado / totalPersonas;

        // Paso 2: Crear lista de balances
        List<PersonBalance> balances = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : pagosPorPersona.entrySet()) {
            Long personId = entry.getKey();
            double balance = Math.round((entry.getValue() - media) * 100.0) / 100.0;
            balances.add(new PersonBalance(personId, balance));
        }

        // Paso 3: Separar deudores y acreedores
        Queue<PersonBalance> deudores = new LinkedList<>();
        Queue<PersonBalance> acreedores = new LinkedList<>();

        for (PersonBalance pb : balances) {
            if (pb.balance < 0) deudores.add(pb);
            else if (pb.balance > 0) acreedores.add(pb);
        }

        List<SettlementDTO> resultado = new ArrayList<>();

        while (!deudores.isEmpty() && !acreedores.isEmpty()) {
            PersonBalance deudor = deudores.poll();
            PersonBalance acreedor = acreedores.poll();

            double cantidad = Math.min(-deudor.balance, acreedor.balance);
            cantidad = Math.round(cantidad * 100.0) / 100.0;

            String from = getPersonFullName(deudor.personId);
            String to = getPersonFullName(acreedor.personId);

            resultado.add(new SettlementDTO(from, to, cantidad));

            deudor.balance += cantidad;
            acreedor.balance -= cantidad;

            if (Math.abs(deudor.balance) > 0.01) deudores.add(deudor);
            if (Math.abs(acreedor.balance) > 0.01) acreedores.add(acreedor);
        }

        return ResponseEntity.ok(resultado);
    }
    
    private String getPersonFullName(Long personId) {
        Optional<Person> personOpt = personRepository.findById(personId);
        if (personOpt.isPresent()) {
            Person p = personOpt.get();
            return p.getName() + " " + p.getLastName();
        }
        return "Desconocido";
    } 
    
    private static class PersonBalance {
        Long personId;
        double balance;

        PersonBalance(Long personId, double balance) {
            this.personId = personId;
            this.balance = balance;
        }
    }
}