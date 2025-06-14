package com.kael.gastosEmpresa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "group_person", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_id", "person_id"})
})
public class GroupPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    public GroupPerson() {}

    public GroupPerson(Long groupId, Long personId) {
        this.groupId = groupId;
        this.personId = personId;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}