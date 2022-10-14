package com.adidas.tsar.common;

import com.adidas.tsar.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DictionaryCollection<T> {

    private final Class<T> clazz;
    private final String blankName;
    private final Map<String, T> dictionariesByNames;
    private final Map<Integer, T> dictionariesByIds;

    public DictionaryCollection(Class<T> clazz, List<T> dictionaryEntries, Function<T, Integer> getIdFunc, Function<T, String> getNameFunc, String blankName) {
        this.clazz = clazz;
        this.blankName = blankName;
        this.dictionariesByNames = dictionaryEntries.stream()
            .collect(Collectors.toMap(getNameFunc, Function.identity()));
        this.dictionariesByIds = dictionaryEntries.stream()
            .collect(Collectors.toMap(getIdFunc, Function.identity()));
    }

    public Optional<T> getDictionaryItem(String name) {
        return Optional.ofNullable(dictionariesByNames.get(name));
    }

    public Optional<T> getDictionaryItem(Integer id) {
        return Optional.ofNullable(dictionariesByIds.get(id));
    }

    public T getDictionaryItemOrBlank(Integer id) {
        return getDictionaryItem(id).orElse(getBlankItem());
    }

    public T getBlankItem() {
        return getDictionaryItem(blankName).orElseThrow(() -> new EntityNotFoundException("Blank entry [" + blankName + "] for [" + clazz.getSimpleName() + "] is not found"));
    }

}
