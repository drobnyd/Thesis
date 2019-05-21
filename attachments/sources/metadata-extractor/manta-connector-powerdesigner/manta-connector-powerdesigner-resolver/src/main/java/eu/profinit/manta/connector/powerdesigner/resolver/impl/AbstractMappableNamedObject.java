package eu.profinit.manta.connector.powerdesigner.resolver.impl;

import eu.profinit.manta.connector.powerdesigner.model.Mappable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Base implementation for the usual case when a NamedObject is Mappable as well.
 *
 * @author ddrobny
 */
public abstract class AbstractMappableNamedObject extends AbstractNamedObject implements Mappable {
    /** All ObjectIDs of the external objects the instance is mapped to. */
    private Set<String> mappedObjects = new HashSet<>();

    @Override
    public Collection<String> getAllMappedObjects() {
        return mappedObjects;
    }

    @Override
    public boolean isMappedTo(String id) {
        return mappedObjects.contains(id);
    }

    @Override
    public boolean hasMappings() {
        return !mappedObjects.isEmpty();
    }

    /**
     * Adds mapping of the instance to the external object with by mappedId .
     * @param externalId the ObjectID of an external object to be mapped with the instance.
     * @return {@code true} if this set did not already contain the specified element.
     */
    public boolean addMapping(String externalId) {
        return mappedObjects.add(externalId);
    }

    /**
     * Adds all the ObjectIDs of external objects from the externalObjects set as mappings.
     * @param externaObjects the ObjectIDs of externals objects to be mapped to the entity.
     * @return {@code true} if this at least od ObjectID from externalObjects was not present before.
     */
    public boolean addAllMappings(Collection<? extends String> externaObjects) {
        return mappedObjects.addAll(externaObjects);
    }

    @Override
    public void setHistory(Set<String> history) {
        super.setHistory(history);
        addAllMappings(history);
    }

}
