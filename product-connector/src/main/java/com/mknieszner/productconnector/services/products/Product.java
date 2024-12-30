package com.mknieszner.productconnector.services.products;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mknieszner.productconnector.common.ProductDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
class Product {

    @Id
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long caseId;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String json;

    public static Product from(ProductDto source) {
        Product product = new Product();
        product.id = source.getId();
        product.caseId = source.getCaseId();
        product.owner = source.getOwner();
        product.type = source.getType();
        product.json = convertToJson(source);
        return product;
    }

    public ProductDto getValue() {
        return convertFromJson();
    }

    public static String convertToJson(ProductDto productDto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(productDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting ProductDto to JSON", e);
        }
    }

    public ProductDto convertFromJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, ProductDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting ProductDto to JSON", e);
        }
    }
}
