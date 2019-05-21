package eu.profinit.manta.dataflow.generator.modeling.common.connections;

import eu.profinit.manta.connector.common.connections.impl.AbstractConnectionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Implementation of AbstractConnectionSource. Looks for connection details from an .ini file based on keys.
 *
 * @author ddrobny
 */
public class ConnectionSourceImpl extends AbstractConnectionSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSourceImpl.class);

    /**
     * Handles an error.
     * @param message the message.
     * @param cause the cause of the error.
     */
    @Override
    protected void handleError(String message, Throwable cause) {
        LOGGER.error(message, cause);
    }

    /**
     * Gets database connection details based on the key.
     * @param key the key.
     * @return database connection details based on the key. {@code null} if the key is not present.
     */
    @Override
    protected Map<String, String> getConnectionMap(String key) {
        return getConnections().get(key);
    }
}
