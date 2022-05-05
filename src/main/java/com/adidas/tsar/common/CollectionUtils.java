package com.adidas.tsar.common;

import com.adidas.tsar.exceptions.EntityNotFoundException;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Optional;

@UtilityClass
public class CollectionUtils {

    public <K,V> V getOrThrow(Map<K, V> map, K key, String sectionTitle) {
        return Optional.ofNullable(map.get(key)).orElseThrow(() -> new EntityNotFoundException(sectionTitle, "Entity doesn't exists. Key: " + key));
    }

}
