package eu.profinit.manta.dataflow.generator.powerdesigner.graph.connections;

import eu.profinit.manta.commons.file.utils.ini.IniFileListener;
import eu.profinit.manta.commons.file.utils.ini.IniFileReader;
import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.common.connections.ConnectionType;
import eu.profinit.manta.connector.common.connections.impl.ConnectionImpl;
import eu.profinit.manta.connector.common.utils.CaseInsensitiveComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * Retrieves details about database connections from a .dcp file.
 * TODO Has no use for now, missing the link between data models and dcp files
 *
 * @author ddrobny
 */
public class ConnectionDcpSourceImpl implements IniFileListener {
    public static final String CONNECTION_KEY = "ConnectionProfile";
    public static final String TYPE = "ConnectionType";
    public static final String CONNECTION_STRING = "URL";
    public static final String USER_NAME = "LogId";
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionDcpSourceImpl.class);
    private File connectionFile;
    private String connectionFileEncoding = "UTF-8";
    private boolean optional = true;
    private Map<String, Map<String, String>> connections;

    /**
     * Vrací soubor s nastavením připojení do databáze na základě klíče.
     * @return soubor s nastavením připojení do databáze na základě klíče
     */
    public File getConnectionFile() {
        return connectionFile;
    }

    /**
     * Vrací kódování souboru s nastavením připojení do databáze na základě klíče.
     * @return kódování souboru s nastavením připojení do databáze na základě klíče
     */
    public String getConnectionFileEncoding() {
        return connectionFileEncoding;
    }

    /**
     * Nastavuje kódování souboru s nastavením připojení do databáze na základě klíče.
     * @param connectionFileEncoding kódování souboru s nastavením připojení do databáze na základě klíče
     */
    public void setConnectionFileEncoding(String connectionFileEncoding) {
        this.connectionFileEncoding = connectionFileEncoding;
    }

    /**
     * Vrací, zda je tento zdroj volitelný, nebo vyhazuje chybu, pokud neexistuje.
     * @return zda je tento zdroj volitelný, nebo vyhazuje chybu, pokud neexistuje
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Nastavuje, zda je tento zdroj volitelný, nebo vyhazuje chybu, pokud neexistuje.
     * @param optional zda je tento zdroj volitelný, nebo vyhazuje chybu, pokud neexistuje
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * Ošetření nastalých chyb.
     * @param message zpráva
     * @param cause důvod chyby
     */
    protected void handleError(String message, Throwable cause) {
        LOGGER.error(message, cause);
    }

    /**
     * Vrací načtené nastavení připojení do databáze.
     * @return načtené nastavení připojení do databáze
     */
    protected Map<String, Map<String, String>> getConnections() {
        return connections;
    }

    @Override
    public Map<String, String> sectionChanged(String sectionName) {
        Map<String, String> currentMap = null;
        if (!connections.containsKey(sectionName)) {
            currentMap = new TreeMap<>(CaseInsensitiveComparator.INSTANCE);
            connections.put(sectionName, currentMap);
        }
        return currentMap;
    }

    private void ensureInitialization() {
        if (connections == null) {
            connections = new TreeMap<>(CaseInsensitiveComparator.INSTANCE);
            try {
                if (optional && (connectionFile == null || !connectionFile.isFile())) {
                    return;
                }
                IniFileReader reader = new IniFileReader(this, null);
                reader.proceedFile(connectionFile, connectionFileEncoding);
            } catch (IOException e) {
                if (!optional) {
                    handleError(MessageFormat.format("Unable to read file with connection parameters at path \"{0}\".",
                            connectionFile.getAbsolutePath()), e);
                }
            }
        }
    }

    public Connection getConnection(File connectionFile) {
        this.connectionFile = connectionFile;
        ensureInitialization();
        Map<String, String> connectionMap = getConnections().get(CONNECTION_KEY);

        if (connectionMap == null) {
            return null;
        }
        switch (connectionMap.get(TYPE)) {

        case "JDBC":
            return new ConnectionImpl(ConnectionType.JDBC.getId(), connectionMap.get(CONNECTION_STRING), null, null,
                    null, connectionMap.get(USER_NAME));

        default:
            LOGGER.warn("Unsupported connection type \"{}\" in .dcp file \"{}\"", connectionMap.get(TYPE),
                    connectionFile);
            return null;

        }
    }
}
