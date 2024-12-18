package com.swp391.koibe.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EKoiStatus {

    UNVERIFIED("UNVERIFIED"),
    VERIFIED("VERIFIED"),
    REJECTED("REJECTED"),
    SOLD("SOLD"),;

    private final String status;

}
