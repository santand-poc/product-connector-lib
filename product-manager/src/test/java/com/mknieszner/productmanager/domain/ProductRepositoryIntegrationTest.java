package com.mknieszner.productmanager.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void whenSaveProduct_thenFindByIdShouldReturnProduct() {
        // Given
        Product product = new Product();
        product.setOwner("Test Owner");
        product.setType("Test Product");
        product.setCaseId(1L);
        product.setMargin(new BigDecimal("19.99"));

        // When
        Product savedProduct = productRepository.save(product);

        // Then
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getOwner()).isEqualTo("Test Owner");
        assertThat(foundProduct.get().getType()).isEqualTo("Test Product");
        assertThat(foundProduct.get().getCaseId()).isEqualTo(1L);
        assertThat(foundProduct.get().getMargin()).isEqualByComparingTo(new BigDecimal("19.99"));
    }
}
