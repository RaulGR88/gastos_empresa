package com.kael.gastosEmpresa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kael.gastosEmpresa.model.Person;
import com.kael.gastosEmpresa.repository.PersonRepository;

import java.util.List;

@RestController  // Le indica a Spring que esta clase va a manejar peticiones HTTP y devolver datos (JSON)
@RequestMapping("/api/persons")  // Ruta base para todas las URLs de este controlador
public class PersonController {

    @Autowired  // Inyección automática del repositorio
    private PersonRepository personRepository;

    // GET /api/persons
    @GetMapping
    public List<Person> getAllPersons() {
    	return personRepository.findAll().stream()
                .sorted((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()))
                .map(p -> new Person(p.getId(), p.getName(), p.getLastName()))
                .toList();
    }

    // POST /api/persons
    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
    	if (person.getName() == null || person.getName().isBlank()) {
            return ResponseEntity.badRequest().build();  // Rechaza si no tiene nombre
        }
    	person.setCreationDate(java.time.LocalDateTime.now());
        return ResponseEntity.ok(personRepository.save(person));
    }


    // PUT /api/persons/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person updatedData) {
        return personRepository.findById(id)
                .map(person -> {
                    person.setName(updatedData.getName());
                    person.setLastName(updatedData.getLastName());
                    return ResponseEntity.ok(personRepository.save(person));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/persons/order
    @GetMapping("/order")
    public List<Person> getPersonsOrderedByLastName() {
        return personRepository.findAll()
                .stream()
                .sorted((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()))
                .toList();
    }
}