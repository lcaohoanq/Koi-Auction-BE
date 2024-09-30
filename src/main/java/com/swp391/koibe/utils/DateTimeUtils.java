package com.swp391.koibe.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    /**
     * Checks if the bid time is within the auction period.
     * @param startTime The start time of the auction.
     * @param endTime The end time of the auction.
     * @param bidTime The time the bid was placed.
     * @return true if the auction is still active (not ended), false otherwise.
     */
    public static boolean isAuctionActive(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime bidTime) {
        return bidTime.isAfter(startTime) && bidTime.isBefore(endTime);
    }

    public static LocalDateTime parseBidTime(String bidTime) {
        // Define the pattern matching your input string format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        return LocalDateTime.parse(bidTime, formatter);
    }

}
