package com.kael.gastosEmpresa.repository;

import com.kael.gastosEmpresa.model.GroupPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupPersonRepository extends JpaRepository<GroupPerson, Long> {
	Optional<GroupPerson> findByGroupIdAndPersonId(Long groupId, Long personId);
}
