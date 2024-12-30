package com.mknieszner.productconnector.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {
    List<Long> ids;
    List<String> types;
    List<String> owners;
}
