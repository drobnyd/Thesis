package eu.profinit.manta.connector.erstudio.model;

/**
 * Database platform.
 *
 * @author ddrobny
 */
public interface Platform {

    /**
     * Gets descriptive name of the DB platform.
     * @return descriptive name of the DB platform.
     */
    String getFullName();

    /**
     * Gets the connection type of database platform.
     * @return the connection type of database platform.
     */
    ConnectionType getPlatformType();

}
