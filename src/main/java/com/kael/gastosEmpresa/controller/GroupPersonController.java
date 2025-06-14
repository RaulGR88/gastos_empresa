package com.kael.gastosEmpresa.controller;

import com.kael.gastosEmpresa.model.GroupPerson;
import com.kael.gastosEmpresa.repository.GroupPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-person")
public class GroupPersonController {

    @Autowired
    private GroupPersonRepository groupPersonRepository;

    @PostMapping  // este método responde a POST /api/group-person
    public ResponseEntity<?> assignPersonToGroup(@RequestBody GroupPerson relation) {
        boolean exists = groupPersonRepository
                .findByGroupIdAndPersonId(relation.getGroupId(), relation.getPersonId())
                .isPresent();

        if (exists) {
            return ResponseEntity.unprocessableEntity().body("La persona ya está asignada al grupo.");
        }

        groupPersonRepository.save(relation);
        return ResponseEntity.ok("Persona asignada correctamente al grupo.");
    }
}