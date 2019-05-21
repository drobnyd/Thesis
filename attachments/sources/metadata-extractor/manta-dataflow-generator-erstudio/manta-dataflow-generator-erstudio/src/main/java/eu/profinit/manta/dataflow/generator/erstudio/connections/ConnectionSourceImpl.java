package eu.profinit.manta.dataflow.generator.erstudio.connections;

import eu.profinit.manta.connector.common.connections.impl.AbstractConnectionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Implementation of ConnectionSource retrieving connection details from .ini file based on a key.
 *
 * @author ddrobny
 */
public class ConnectionSourceImpl extends AbstractConnectionSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSourceImpl.class);

    /**
     * Ošetření nastalých chyb.
     * @param message zpráva
     * @param cause důvod chyby
     */
    @Override
    protected void handleError(String message, Throwable cause) {
        LOGGER.error(message, cause);
    }

    /**
     * Vratí nastavení připojení do databáze na základě klíče.
     * @param key klíč
     * @return nastavení připojení do databáze na základě klíče nebo <code>null</code> či výjimka, pokud není nalezeno
     */
    @Override
    protected Map<String, String> getConnectionMap(String key) {
        return getConnections().get(key);
    }
}
