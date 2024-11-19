package com.swp391.koibe.repositories

import com.swp391.koibe.models.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
    fun findByPhoneNumber(phoneNumber: String): Optional<User>
    fun findByRoleName(roleName: String, pageable: Pageable): Page<User>
    @Query("SELECT u FROM User u WHERE u.role.id = 2")
    fun findAllStaff(pageable: Pageable): Page<User>
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role.id = 2")
    fun findStaffById(id: Long): Optional<User>
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role.id = 3")
    fun findBreederById(id: Long): Optional<User>

    @Modifying
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    fun softDeleteUser(id: Long)

    @Modifying
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :id")
    fun restoreUser(id: Long)

    @Modifying
    @Query("UPDATE User u SET u.role.id = :roleId WHERE u.id = :id")
    fun updateRole(id: Long, roleId: Long)
}
