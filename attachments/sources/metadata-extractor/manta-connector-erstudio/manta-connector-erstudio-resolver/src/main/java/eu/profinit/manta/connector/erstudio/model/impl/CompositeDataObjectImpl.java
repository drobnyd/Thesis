package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.connector.erstudio.model.AbstractionLayer;
import eu.profinit.manta.connector.erstudio.model.CompositeDataObject;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of CompositeDataObject.
 *
 * @author ddrobny
 */
public class CompositeDataObjectImpl extends AbstractDataObject<CompositeDataObject> implements CompositeDataObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeDataObjectImpl.class);

    private final Map<String, SimpleDataObjectImpl> nameToSubObject = new HashMap<>();

    public CompositeDataObjectImpl(String name, AbstractionLayer layer) {
        super(name, layer);
    }

    @Override
    public Collection<SimpleDataObject> getAllSubObjects() {
        return Collections.unmodifiableCollection(nameToSubObject.values());
    }

    @Override
    public SimpleDataObject getSubObject(String name) {
        return nameToSubObject.get(name);
    }

    /**
     * Safely adds an object to child SimpleObjects.
     * @param toAdd children object to be added.
     * @return toAdd if no children with the same name existed before, otherwise the same named children
     * that has been there earlier.
     */
    public SimpleDataObjectImpl addSubObject(SimpleDataObjectImpl toAdd) {
        if (!nameToSubObject.containsKey(toAdd.getName())) {
            nameToSubObject.put(toAdd.getName(), toAdd);
        } else {
            LOGGER.debug("{} SimpleDataObject {} has multiple definitions in CompositeDataObject {}", this.getLayer(),
                    toAdd.getName(), this.getName());
        }

        return nameToSubObject.get(toAdd.getName());

    }
}
