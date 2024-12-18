package com.swp391.koibe.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swp391.koibe.models.listener.BidListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="auction_kois_details")
//@EntityListeners(BidListener.class)
public class Bid {

    @Id
    @SequenceGenerator(name = "bid_seq", sequenceName = "bid_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bid_seq")
    @Column(name="id", unique=true, nullable=false)
    @JsonProperty("id")
    private Long id;

    @Column(name = "bid_amount")
    private long bidAmount; // Optional, if you want to enforce bid increments

    @Column(name = "bid_time")
    private LocalDateTime bidTime; // Track when the bid was placed

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User bidder; // Track who placed the bid

    @ManyToOne
    @JoinColumn(name = "auction_koi_id")
    private AuctionKoi auctionKoi;

}
