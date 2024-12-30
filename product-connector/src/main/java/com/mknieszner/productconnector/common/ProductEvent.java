package com.mknieszner.productconnector.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEvent {
    private Long productId;
    private Long caseId;
    private ProductDto message;
    private String status; // "PROCESSED", "ERROR"
}
