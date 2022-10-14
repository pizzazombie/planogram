package com.adidas.tsar.data.criteria;

public enum SearchOperation {
    EQUALITY, CONTAINS, GREATER_THAN, LESS_THAN;

    public static SearchOperation getSearchOperation(final String input) {
        switch (input) {
            case "equal":
                return EQUALITY;
            case "contains":
                return CONTAINS;
            case "gt":
                return GREATER_THAN;
            case "lt":
                return LESS_THAN;
            default:
                return null;
        }
    }
}
