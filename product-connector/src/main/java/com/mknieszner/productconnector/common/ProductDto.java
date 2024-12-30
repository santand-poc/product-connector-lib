package com.mknieszner.productconnector.common;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id;
    private Long caseId;
    private String type;
    private String owner;
    private BigDecimal margin;
}
