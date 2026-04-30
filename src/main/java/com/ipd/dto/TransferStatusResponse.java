package com.ipd.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferStatusResponse {
    private String status;
    private String currentLocation;
}