package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.connector.erstudio.model.DataModel;
import eu.profinit.manta.connector.erstudio.model.Owner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation for all DataModels.
 *
 * @author ddrobny
 */
public abstract class AbstractDataModel extends AbstractObject implements DataModel {
    /** Default owner is {@code null}, no other can have the value */
    private final Map<String, OwnerImpl> nameToOwner = new HashMap<>();

    public AbstractDataModel(String name) {
        super(name);
    }

    @Override
    public Collection<? extends Owner> getOwners() {
        return nameToOwner.values();
    }

    @Override
    public OwnerImpl getOwner(String ownerName) {
        // If an owner with such name is not present add him
        if (!nameToOwner.containsKey(ownerName)) {
            addOwner(ownerName);
        }

        return nameToOwner.get(ownerName);
    }

    /**
     * Adds the given owner to the data model.
     * @param ownerName the owner to add.
     * @return the previous owner with the same name that was stored in the model.
     */
    public OwnerImpl addOwner(String ownerName) {
        return nameToOwner.put(ownerName, new OwnerImpl(ownerName, this.getLayer()));
    }

    @Override
    public OwnerImpl getDefaultOwner() {
        return nameToOwner.get(null);
    }

}
