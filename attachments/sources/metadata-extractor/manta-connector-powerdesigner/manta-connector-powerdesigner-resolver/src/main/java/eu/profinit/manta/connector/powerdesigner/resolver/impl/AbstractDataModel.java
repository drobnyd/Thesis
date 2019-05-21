package eu.profinit.manta.connector.powerdesigner.resolver.impl;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;

/**
 * Base implementation of DataModel.
 *
 * @author ddrobny
 */
public abstract class AbstractDataModel extends AbstractNamedObject implements DataModel {
    /** Relative name of the file where the model is stored. */
    private String fileName;

    public AbstractDataModel(String filename) {
        this.fileName = filename;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
