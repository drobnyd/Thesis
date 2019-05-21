package eu.profinit.manta.connector.erstudio.model;

/**
 * Representation of database technology specific model.
 * Its underlying objects that are of type {@link CompositeDataObject} represent tables in the particular database.
 *
 * @author ddrobny
 */
public interface PhysicalDataModel extends DataModel {

    @Override
    default AbstractionLayer getLayer() {
        return AbstractionLayer.PHYSICAL;
    }

    /**
     * Find out what database technology is the physical model aimed/used on
     * @return Name of the database technology the physical model aimed/used on.
     */
    Platform getPlatform();
}
