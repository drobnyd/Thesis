package eu.profinit.manta.dataflow.generator.powerdesigner.graph.connections;

import eu.profinit.manta.connector.common.connections.Connection;

import java.io.File;

/**
 * Gets connection details about a database corresponding to a physical model.
 *
 * @author ddrobny
 */
public interface ConnectionResolver {

    /**
     * Sets the configuration file containing connection details.
     * @param connectionsConfig the file to set.
     */
    void setConnectionsConfig(File connectionsConfig);

    /**
     * Gets connection details for the given physical model of the corresponding database.
     * @param physicalModel the name of a physical model.
     * @return connection details for the given physical model of the corresponding database.
     */
    Connection getConnection(String physicalModel);
}
