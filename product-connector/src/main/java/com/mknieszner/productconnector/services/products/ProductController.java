package com.mknieszner.productconnector.services.products;

import com.mknieszner.productconnector.common.ProductDto;
import com.mknieszner.productconnector.common.ProductSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController {
    private final ProductSearchService searchService;

    @PostMapping("/search")
    public List<ProductDto> searchProducts(@RequestBody ProductSearchCriteria criteria) {
        return searchService.findAllBy(criteria);
    }
}
