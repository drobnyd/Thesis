package eu.profinit.manta.connector.powerdesigner.model;

import java.util.Collection;

/**
 * Interface for getting information about mappings of an object. A mapped target is represented by its global ID.
 *
 * @author ddrobny
 */
public interface Mappable {
    /**
     * Gets every object ID the the instance is mapped to.
     * @return all objects the instance is mapped to. If there's none an empty structure is returned.
     */
    Collection<String> getAllMappedObjects();

    /**
     * Decides whether the instance is mapped to {@code object} or not.
     * @param object object to find between the instance's mappings.
     * @return {@code true} if the {@code object} is mapped to the instance, otherwise {@code false}.
     */
    boolean isMappedTo(String object);

    /**
     * Decides whether the instance is mapped to at least one object.
     * @return {@code true} if the instance is mapped to at least one object, otherwise {@code false}.
     */
    boolean hasMappings();
}