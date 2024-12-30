package com.mknieszner.productmanager.services;

import com.mknieszner.productmanager.domain.Product;
import com.mknieszner.productmanager.domain.ProductRepository;
import com.mknieszner.productmanager.service.ProductEventPublisher;
import com.mknieszner.productmanager.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEventPublisher productEventPublisher;

    @InjectMocks
    private ProductService productService;

    public ProductServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenAddProduct_thenSaveProduct() {
        Product product = new Product();
        when(productRepository.save(product)).thenReturn(product);

        productService.addProduct(product);

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void whenAddProduct_thenPublishEvent() {
        Product product = new Product();
        when(productRepository.save(product)).thenReturn(product);

        productService.addProduct(product);

        verify(productEventPublisher, times(1)).publishProductEvent(product);
    }
}
