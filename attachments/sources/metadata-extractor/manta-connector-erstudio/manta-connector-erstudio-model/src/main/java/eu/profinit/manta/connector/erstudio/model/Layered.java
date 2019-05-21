package eu.profinit.manta.connector.erstudio.model;

/**
 * An object that can tell on what {@link AbstractionLayer} it is modeled.
 *
 * @author ddrobny
 */
public interface Layered {

    /**
     * Method for determining on what {@link AbstractionLayer} is the object implementing this interface modeled.
     * @return On what {@link AbstractionLayer} is the object implementing this interface modeled.
     */
    AbstractionLayer getLayer();
}
