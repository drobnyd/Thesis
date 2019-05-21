package eu.profinit.manta.dataflow.generator.powerdesigner.graph.connections;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.common.connections.ConnectionType;
import eu.profinit.manta.connector.common.connections.impl.ConnectionImpl;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.ConnectionSourceImpl;

import java.io.File;

/**
 * Gets connection details about a database corresponding to a physical model.
 *
 * @author ddrobny
 */
public class ConnectionResolverImpl implements ConnectionResolver {
    /** The service for obtaining connection detail from a .ini file. */
    ConnectionSourceImpl connectionSource = new ConnectionSourceImpl();
    // TODO extend of support of other possible connection definitions such as .dcp, .dsn.

    public ConnectionResolverImpl() {
        // Don't fail if connection is not specified, try to do as much as possible
        connectionSource.setOptional(true);
    }

    @Override
    public void setConnectionsConfig(File connectionsConfig) {
        connectionSource.setConnectionFile(connectionsConfig);
    }

    @Override
    public Connection getConnection(String physicalModel) {

        // Try to obtain the connection details from a file
        return connectionSource.getConnection(physicalModel) != null ? connectionSource.getConnection(physicalModel) :
               // If has not bee found, set the default. TODO the default
               new ConnectionImpl(ConnectionType.UNKNOWN.getId(), "", "UNKNOWN_SERVER", "UNKNOWN_DATABASE",
                       "UNKNOWN_SCHEMA", "UNKNOWN_USER");
    }

}
