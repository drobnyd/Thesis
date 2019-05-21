package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;
import eu.profinit.manta.connector.erstudio.model.Platform;

/**
 * Implementation of PhysicalDataModel.
 *
 * @author ddrobny
 */
public class PhysicalDataModelImpl extends AbstractDataModel implements PhysicalDataModel {
    private Platform platform;

    public PhysicalDataModelImpl(String name, Platform platform) {
        super(name);
        this.platform = platform;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }
}
