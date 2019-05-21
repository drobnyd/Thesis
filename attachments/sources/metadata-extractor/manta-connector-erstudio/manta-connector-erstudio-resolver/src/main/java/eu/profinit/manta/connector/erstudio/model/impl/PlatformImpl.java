package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.connector.erstudio.model.ConnectionType;
import eu.profinit.manta.connector.erstudio.model.Platform;

/**
 * Implementation of database Platform.
 *
 * @author ddrobny
 */
public class PlatformImpl implements Platform {
    String fullName;
    ConnectionType connectionType;

    public PlatformImpl(String fullName, ConnectionType connectionType) {
        this.fullName = fullName;
        this.connectionType = connectionType;
    }

    @Override
    public String getFullName() {
        return this.fullName;
    }

    @Override
    public ConnectionType getPlatformType() {
        return this.connectionType;
    }
}
