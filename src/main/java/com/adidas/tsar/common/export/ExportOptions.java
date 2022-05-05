package com.adidas.tsar.common.export;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder()
public class ExportOptions<T> {

    private final Class<T> dtoClass;

    private final String sectionName;

}
