package eu.profinit.manta.dataflow.generator.erstudio.connections;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;

import java.io.File;

/**
 * Resolves {@link Connection} to database that {@link PhysicalDataModel} is resembling.
 *
 * @author ddrobny
 */
public interface ConnectionResolver {
    /**
     * Sets .ini file defining {@link Connection} configuration for all {@link PhysicalDataModel}s in current system.
     * Host name and database name must be provided in each entry.
     * @param connectionsConfig .ini file defining {@link Connection} configuration for all {@link PhysicalDataModel}s
     * in current system.
     */
    void setConnectionsConfig(File connectionsConfig);

    /**
     * Creates {@link Connection} to database that {@code input} is resembling based on its entry in
     * {@code connectionsConfig} file and the model's attributes.
     * @param model which database {@link Connection} is to be obtained.
     * @return {@link Connection} to database that {@code input} is resembling.
     */
    Connection getConnection(PhysicalDataModel model);
}
