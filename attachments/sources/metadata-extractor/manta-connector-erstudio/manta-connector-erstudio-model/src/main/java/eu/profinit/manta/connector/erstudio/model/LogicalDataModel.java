package eu.profinit.manta.connector.erstudio.model;

/**
 * Interface providing access to a database model created with higher level of abstraction of its objects.
 * Its underlying objects that are of type {@link CompositeDataObject} represent logical Entities.
 *
 * @author ddrobny
 */
public interface LogicalDataModel extends DataModel {

    @Override
    default AbstractionLayer getLayer() {
        return AbstractionLayer.LOGICAL;
    }
}
