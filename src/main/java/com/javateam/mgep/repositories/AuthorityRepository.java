package com.javateam.mgep.repositories;

import com.javateam.mgep.entity.Authoritty;
import com.javateam.mgep.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authoritty, String> {
}
