package com.adidas.tsar.common;

import com.adidas.tsar.dto.DictionaryEntity;
import com.adidas.tsar.exceptions.EntityNotFoundException;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DictionariesCollectionUtils {

    private final Map<String, Map<String, ? extends DictionaryEntity>> dictionariesByNames;
    private final Map<String, Map<Integer, ? extends DictionaryEntity>> dictionariesByIds;

    public DictionariesCollectionUtils(Collection<Pair<Class<?>, List<? extends DictionaryEntity>>> dictionariesByNames) {
        this.dictionariesByNames = dictionariesByNames.stream()
            .map(pair -> Pair.of(pair.getKey().getName(), toMapByNames(pair.getValue())))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        this.dictionariesByIds = dictionariesByNames.stream()
            .map(pair -> Pair.of(pair.getKey().getName(), toMapByIds(pair.getValue())))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public <T> T getOrThrow(Class<T> clazz, String name, String sectionName) {
        return getDictionaryItem(clazz, name)
            .orElseThrow(() -> new EntityNotFoundException(sectionName, clazz.getSimpleName() + " doesn't exists. Name: " + name));
    }

    public <T> T getOrThrow(Class<T> clazz, @Nullable String name, boolean isRequiredField, String sectionName) {
        if(isRequiredField || name != null) {
            return getOrThrow(clazz, name, sectionName);
        } else {
            return null;
        }
    }

    public <T> Map<Integer, T> getDictionaryList(Class<T> clazz) {
        return (Map<Integer, T>) dictionariesByIds.get(clazz.getName());
    }

    public <T> Optional<T> getDictionaryItem(Class<T> clazz, String name) {
        return Optional.ofNullable(clazz.cast(dictionariesByNames.get(clazz.getName()).get(name)));
    }

    public <T> Optional<T> getDictionaryItem(Class<T> clazz, Integer id) {
        return Optional.ofNullable(clazz.cast(dictionariesByIds.get(clazz.getName()).get(id)));
    }

    private <T extends DictionaryEntity> Map<String, T> toMapByNames(List<T> dictionary) {
        return dictionary.stream().collect(Collectors.toMap(DictionaryEntity::getName, it -> it));
    }

    private <T extends DictionaryEntity> Map<Integer, T> toMapByIds(List<T> dictionary) {
        return dictionary.stream().collect(Collectors.toMap(DictionaryEntity::getId, it -> it));
    }

}