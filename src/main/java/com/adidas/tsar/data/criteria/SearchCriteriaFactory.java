package com.adidas.tsar.data.criteria;

@FunctionalInterface
public interface SearchCriteriaFactory {
    SearchCriteria produce(String field, String operation, Object value);
}
