package eu.profinit.manta.connector.powerdesigner.resolver.build;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;

/**
 * General way of building a data model, no matter on what specific layer of abstraction.
 *
 * @author ddrobny
 */
public interface DataModelBuilder {

    /**
     * Build a data model.
     */
    void buildDataModel();

    /**
     * Get the data model that has been built.
     * @return the built data model.
     */
    DataModel getResult();
}
