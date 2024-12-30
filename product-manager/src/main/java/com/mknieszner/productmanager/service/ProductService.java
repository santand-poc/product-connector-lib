package com.mknieszner.productmanager.service;

import com.mknieszner.productmanager.domain.Product;
import com.mknieszner.productmanager.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductEventPublisher productEventPublisher;

    @Transactional
    public Product addProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        productEventPublisher.publishProductEvent(savedProduct);
        return savedProduct;
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
}
