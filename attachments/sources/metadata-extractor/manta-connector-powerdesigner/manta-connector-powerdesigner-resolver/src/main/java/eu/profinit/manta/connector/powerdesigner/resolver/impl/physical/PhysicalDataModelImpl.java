package eu.profinit.manta.connector.powerdesigner.resolver.impl.physical;

import eu.profinit.manta.connector.powerdesigner.model.physical.PhysicalDataModel;
import eu.profinit.manta.connector.powerdesigner.model.physical.PhysicalSchema;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractDataModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Physical data model.
 *
 * @author ddrobny
 */
public class PhysicalDataModelImpl extends AbstractDataModel implements PhysicalDataModel {
    /** Pairs of schema name and the instance itself. */
    private Map<String, PhysicalSchema> nameToSchema = new HashMap<>();

    public PhysicalDataModelImpl(String fileName) {
        super(fileName);
    }

    @Override
    public PhysicalSchema getSchema(String name) {
        return nameToSchema.get(name);
    }

    @Override
    public Collection<PhysicalSchema> getAllSchemas() {
        return nameToSchema.values();
    }

    /**
     * Adds all the schemas from nameToOwner to the model.
     * @param nameToOwner the schemas to add.
     */
    public void setSchemas(Map<String, ? extends PhysicalSchema> nameToOwner) {
        nameToSchema.putAll(nameToOwner);
    }
}
