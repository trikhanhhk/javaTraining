package com.javateam.mgep.repositories;

import com.javateam.mgep.entity.Employee;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @EntityGraph(attributePaths = {"authorities", "department"})
    Employee findByEmail(String email);

    @EntityGraph(attributePaths = {"authorities", "department"})
    Optional<Employee> findOneByEmailIgnoreCase(String email);
}
