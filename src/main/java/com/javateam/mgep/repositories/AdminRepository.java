package com.javateam.mgep.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.javateam.mgep.entity.Employee;

public interface AdminRepository extends JpaRepository<Employee, Long> {
	@Query(value="Select * from employee t where t.email LIKE " + "%"+":email"+"%", nativeQuery=true)
    List<Employee> findByEmail(@Param("email") String email);

}
