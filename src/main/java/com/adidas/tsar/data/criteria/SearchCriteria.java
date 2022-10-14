package com.adidas.tsar.data.criteria;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class SearchCriteria {

    private final List<String> keys;
    private final SearchOperation operation;
    private final Object value;

}