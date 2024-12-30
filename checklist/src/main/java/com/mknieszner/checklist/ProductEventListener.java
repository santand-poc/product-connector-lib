package com.mknieszner.checklist;

import com.mknieszner.productconnector.common.ProductEvent;
import com.mknieszner.productconnector.common.ProductSearchCriteria;
import com.mknieszner.productconnector.services.products.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductEventListener {
    private final ProductSearchService productSearchService;

    @EventListener
    public void handleProductEvent(ProductEvent event) {
        System.out.println("Received ProductEvent: " + event);
        System.out.println("Products: " + productSearchService.findAllBy(ProductSearchCriteria.builder().build()));
    }
}
