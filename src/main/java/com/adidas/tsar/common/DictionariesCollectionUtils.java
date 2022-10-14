package com.adidas.tsar.common;

import com.adidas.tsar.dto.DictionaryEntity;
import com.adidas.tsar.exceptions.EntityNotFoundException;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DictionariesCollectionUtils {

    private final Map<String, DictionaryCollection<?>> dictCollectionsMap;

    public DictionariesCollectionUtils() {
        dictCollectionsMap = new HashMap<>();
    }


    public <T> DictionariesCollectionUtils with(Class<T> clazz, List<T> entities, Function<T, Integer> getIdFunc, Function<T, String> getNameFunc, String blankName) {
        dictCollectionsMap.put(clazz.getName(), new DictionaryCollection<>(clazz, entities, getIdFunc, getNameFunc, blankName));
        return this;
    }

    public <T extends DictionaryEntity> DictionariesCollectionUtils with(Class<T> clazz, List<T> entities, String blankName) {
        dictCollectionsMap.put(clazz.getName(), new DictionaryCollection<>(clazz, entities, DictionaryEntity::getId, DictionaryEntity::getName, blankName));
        return this;
    }

    public <T> DictionaryCollection<T> get(Class<T> clazz) {
        return (DictionaryCollection<T>) dictCollectionsMap.get(clazz.getName());
    }

    public <T> Optional<T> getDictionaryItem(Class<T> clazz, String name) {
        return get(clazz).getDictionaryItem(name);
    }

    public <T> Optional<T> getDictionaryItem(Class<T> clazz, Integer id) {
        return get(clazz).getDictionaryItem(id);
    }

    public <T> T getOrThrow(Class<T> clazz, String name, String sectionName) {
        return getDictionaryItem(clazz, name)
            .orElseThrow(() -> new EntityNotFoundException(sectionName, clazz.getSimpleName() + " doesn't exists. Name: " + name));
    }

    public <T> T getOrThrow(Class<T> clazz, @Nullable String name, boolean isRequiredField, String sectionName) {
        if (isRequiredField || name != null) {
            return getOrThrow(clazz, name, sectionName);
        } else {
            return null;
        }
    }

}