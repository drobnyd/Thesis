package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.connector.erstudio.model.AbstractionLayer;
import eu.profinit.manta.connector.erstudio.model.CompositeDataObject;
import eu.profinit.manta.connector.erstudio.model.Owner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Owener.
 *
 * @author ddrobny
 */
public class OwnerImpl implements Owner {
    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerImpl.class);
    private String name;
    private Map<String, CompositeDataObjectImpl> nameToCompositeObject = new HashMap<>();
    private AbstractionLayer layer;

    public OwnerImpl(String name, AbstractionLayer layer) {
        this.name = name;
        this.layer = layer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends CompositeDataObject> getAllCompositeObjects() {
        return nameToCompositeObject.values();
    }

    @Override
    public CompositeDataObject getCompositeObject(String objectName) {
        return nameToCompositeObject.get(objectName);
    }

    /**
     * Safely adds an object to child CompositeObjects.
     * @param toAdd children object to be added.
     * @return toAdd if no children with the same name existed before, otherwise the same named children
     * that has been there earlier.
     */
    public CompositeDataObjectImpl addCompositeObject(CompositeDataObjectImpl toAdd) {
        // An owner can hold only one object with some name
        if (!nameToCompositeObject.containsKey(toAdd.getName())) {
            nameToCompositeObject.put(toAdd.getName(), toAdd);
        } else {
            LOGGER.warn("The CompositeDataObject with name {} owned by {} will be replaced by another, same-named one",
                    toAdd.getName(), this.name);
        }

        return nameToCompositeObject.get(toAdd.getName());
    }

    /**
     * Method for determining on what {@link AbstractionLayer} is the object implementing this interface modeled.
     * @return On what {@link AbstractionLayer} is the object implementing this interface modeled.
     */
    @Override
    public AbstractionLayer getLayer() {
        return layer;
    }

    @Override
    public Map<String, String> getMetadata() {
        // No additional metadata yet
        return new HashMap<>();
    }
}
