package eu.profinit.manta.connector.powerdesigner.model.physical;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;

import java.util.Collection;

/**
 * Physical data model that has schemas.
 *
 * @author ddrobny
 */
public interface PhysicalDataModel extends DataModel {

    /**
     * Gets a schema by its name.
     * @param name name of the schema.
     * @return schema with the given name, if no such exists {@code null}.
     */
    PhysicalSchema getSchema(String name);

    /**
     * Gets all schemas of the physical model.
     * @return all schemas of the physical model. Non-null.
     */
    Collection<PhysicalSchema> getAllSchemas();

}
