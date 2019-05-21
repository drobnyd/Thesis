package eu.profinit.manta.connector.erstudio.utils;

import eu.profinit.manta.connector.erstudio.model.DataObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic operations on maps of lists.
 *
 * @author ddrobny
 */
public class MultiMapExtensions {

    /**
     * Transforms a collection to map.
     * @param fromSet the collection to transform
     * @param <T> the type of input and output.
     * @return the map where keys are names of the elements.
     */
    public static <T extends DataObject> Map<String, List<T>> collectionToMultiMap(Collection<T> fromSet) {
        // Make entry's name key in resulting map pair and the entry itself the value
        Map<String, List<T>> result = new HashMap<>();
        fromSet.forEach(x -> {
            if (x != null) {
                addToMultiMap(result, x.getName(), x);
            }
        });
        return result;
    }

    /**
     * Transforms a collection to map.
     * @param fromSet the collection to transform
     * @param keySelector the method choosing keys.
     * @param <T> the type of input and output.
     * @return the map where keys are selected by the selector of the elements.
     */
    public static <T> Map<String, List<T>> collectionToMultiMap(Collection<T> fromSet,
            Function<T, String> keySelector) {
        // Make entry's name key in resulting map pair and the entry itself the value
        Map<String, List<T>> result = new HashMap<>();
        fromSet.forEach(x -> {
            if (x != null) {
                addToMultiMap(result, keySelector.apply(x), x);
            }
        });
        return result;
    }

    /**
     * Transform a multimap to list
     * @param multiMap the multimap to flatten.
     * @param <K> type of key.
     * @param <V> type of value.
     * @return elements from the given multimap in sequential form.
     */
    public static <K, V> List<V> flattenMultiMap(Map<K, List<V>> multiMap) {
        return multiMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * Adds an element to multimap.
     * @param multiMap the multi map to add to.
     * @param key the key of the added element.
     * @param element the element to add.
     * @param <K> type of key.
     * @param <V> type of eleement.
     */
    public static <K, V> void addToMultiMap(Map<K, List<V>> multiMap, K key, V element) {
        List<V> sameNamed;
        if ((sameNamed = multiMap.get(key)) == null) {
            sameNamed = new ArrayList<>();
            sameNamed.add(element);
            multiMap.put(key, sameNamed);
        } else {
            sameNamed.add(element);
        }
    }
}
