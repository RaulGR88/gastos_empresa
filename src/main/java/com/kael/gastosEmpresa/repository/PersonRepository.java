package com.kael.gastosEmpresa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kael.gastosEmpresa.model.Person;

@Repository  // Anotación que indica que esta interfaz es un repositorio (componente de acceso a datos)
public interface PersonRepository extends JpaRepository<Person, Long> {
    // Al extender JpaRepository<Person, Long>, Spring genera automáticamente:
    // - métodos como save(), findAll(), findById(), deleteById(), etc.
    // No necesitas implementarlos manualmente: ¡Spring lo hace por ti!
}
