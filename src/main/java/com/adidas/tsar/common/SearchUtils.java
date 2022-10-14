package com.adidas.tsar.common;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.experimental.UtilityClass;
import org.springframework.http.converter.json.MappingJacksonValue;

import javax.annotation.Nullable;
import java.util.List;

@UtilityClass
public class SearchUtils {

    public static final String SEARCH_FILTER_NAME = "SearchFilter";

    public <T> MappingJacksonValue filterFields(T response, @Nullable List<String> fields) {
        MappingJacksonValue mapping = new MappingJacksonValue(response);
        SimpleFilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false);
        if (fields != null) {
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(fields.toArray(new String[0]));
            filters.addFilter(SEARCH_FILTER_NAME, filter);
        }
        mapping.setFilters(filters);
        return mapping;
    }

}
