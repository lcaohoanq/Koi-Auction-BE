package com.swp391.koibe.repositories

import com.swp391.koibe.models.Auction
import org.springframework.data.jpa.repository.JpaRepository

interface AuctionRepository : JpaRepository<Auction, Long> {
    fun findAllByStatus(status: String): List<Auction>
    fun existsByTitle(title: String): Boolean
}
