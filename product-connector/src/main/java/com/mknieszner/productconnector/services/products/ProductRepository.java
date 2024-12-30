package com.mknieszner.productconnector.services.products;

import com.mknieszner.productconnector.common.ProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    default void persist(ProductDto productDto) {
        save(Product.from(productDto));
    }

    @Query("SELECT p FROM Product p " +
            "WHERE (:ids IS NULL OR p.id IN :ids) " +
            "AND (:types IS NULL OR p.type IN :types) " +
            "AND (:owners IS NULL OR p.owner IN :owners)")
    List<Product> findAllByCriteria(
            @Param("ids") List<Long> ids,
            @Param("types") List<String> types,
            @Param("owners") List<String> owners
    );
}
