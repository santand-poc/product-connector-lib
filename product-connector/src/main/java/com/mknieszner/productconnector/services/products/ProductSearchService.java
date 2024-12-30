package com.mknieszner.productconnector.services.products;

import com.mknieszner.productconnector.common.ProductDto;
import com.mknieszner.productconnector.common.ProductSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductSearchService {
    private final ProductRepository repository;

    public List<ProductDto> findAllBy(ProductSearchCriteria criteria) {
        return find(criteria).stream()
                .map(Product::getValue)
                .collect(Collectors.toList());
    }

    private List<Product> find(ProductSearchCriteria criteria) {
        if ((criteria.getIds() == null || criteria.getIds().isEmpty()) &&
                (criteria.getTypes() == null || criteria.getTypes().isEmpty()) &&
                (criteria.getOwners() == null || criteria.getOwners().isEmpty())) {

            return repository.findAll();
        }

        return repository.findAllByCriteria(
                criteria.getIds(),
                criteria.getTypes(),
                criteria.getOwners()
        );
    }
}
