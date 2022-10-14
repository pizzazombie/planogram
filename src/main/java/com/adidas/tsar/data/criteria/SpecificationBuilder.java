package com.adidas.tsar.data.criteria;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpecificationBuilder<T> {

    private final Pattern pattern = Pattern.compile("(.+?)\\.(\\w+?)\\=(.+?);");
    private final List<ImmutableTriple<String, String, Object>> args;
    private SearchCriteriaFactory factory = (fields, operation, value) -> {
        final var searchOperation = SearchOperation.getSearchOperation(operation.toLowerCase());
        if (searchOperation == null)
            throw new IllegalArgumentException("The filter operation " + operation + " has incorrect value");
        return new SearchCriteria(Arrays.asList(StringUtils.split(fields, '.')), searchOperation, value);
    };

    public SpecificationBuilder() {
        args = new ArrayList<>();
    }

    public SpecificationBuilder<T> with(SearchCriteriaFactory factory) {
        this.factory = factory;
        return this;
    }

    public SpecificationBuilder<T> withSearchString(String searchString) {
        final var matcher = pattern.matcher(searchString);
        while (matcher.find()) {
            with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        return this;
    }

    public SpecificationBuilder<T> with(String field, String operation, Object value) {
        args.add(ImmutableTriple.of(field, operation, value));
        return this;
    }

    public Specification<T> build() {
        if (args.isEmpty()) {
            return null;
        }

        List<Specification<T>> specs = args.stream()
            .map(it -> factory.produce(it.getLeft(), it.getMiddle(), it.getRight()))
            .filter(Objects::nonNull)
            .map(criteria -> (Specification<T>) (root, query, builder) -> {
                switch (criteria.getOperation()) {
                    case EQUALITY:
                        return builder.equal(joinTables(root, criteria.getKeys()), criteria.getValue());
                    case CONTAINS:
                        return builder.like(joinTables(root, criteria.getKeys()), "%" + criteria.getValue() + "%");
                    case GREATER_THAN:
                        return builder.greaterThan(joinTables(root, criteria.getKeys()), criteria.getValue().toString());
                    case LESS_THAN:
                        return builder.lessThan(joinTables(root, criteria.getKeys()), criteria.getValue().toString());
                    default:
                        return null;
                }
            })
            .collect(Collectors.toCollection(ArrayList::new));

        Specification<T> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }

        return result;
    }

    private Path<String> joinTables(Root<T> root, List<String> keys) {
        Path<String> path = root.get(keys.get(0));
        for (int i = 1; i < keys.size(); i++) {
            path = path.get(keys.get(i));
        }
        return path;
    }

}
