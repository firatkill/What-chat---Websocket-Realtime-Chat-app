package com.whatchat.backend.dto.ws;

import lombok.Data;

import java.util.UUID;

@Data
public class DeliveryStatus {
    private UUID messageId;
    private UUID userId;
}