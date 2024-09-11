package com.swp391_09.Koi_BE.repositories;

import com.swp391_09.Koi_BE.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
