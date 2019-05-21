package eu.profinit.manta.connector.erstudio.model;

import java.util.Collection;

/**
 * Interface for getting information about mappings of objects that can be mapped to
 * zero or more {@link DataObject}s.
 *
 * @author ddrobny
 */
public interface Mappable<T extends DataObject> {
    /**
     * Gets every {@link DataObject} that the instance is mapped to.
     * @return Every {@link DataObject} that the instance is mapped to. If there's none an empty {@link Collection} is returned.
     */
    Collection<T> getAllMappedObjects();

    /**
     * Gets every {@link DataObject} that the instance is mapped to and its {@link DataObject#getName()} is equal to {@code objectName}
     * @param objectName Name that to search the mapped objects to be added to result by.
     * @return Every {@link DataObject} that the instance is mapped to and its {@link DataObject#getName()} is equal to {@code objectName}.
     * If there's none an empty {@link Collection} is returned.
     */
    Collection<T> getMappedObjects(String objectName);

    /**
     * Decides whether the instance is mapped to {@code object} or not.
     * @param object object to find in the instance's mappings.
     * @return {@code true} if the {@code object} is mapped to the instance, otherwise {@code false}.
     */
    boolean isMappedTo(T object);

    /**
     * Decides whether the instance is mapped to at least one {@link DataObject}.
     * @return {@code true} if the instance is mapped to at least one {@link DataObject}, otherwise {@code false}.
     */
    boolean hasMappings();
}
