package eu.profinit.manta.connector.powerdesigner.resolver.impl.logical;

import eu.profinit.manta.connector.powerdesigner.model.logical.LogicalAttribute;
import eu.profinit.manta.connector.powerdesigner.model.logical.LogicalEntity;
import eu.profinit.manta.connector.powerdesigner.model.logical.LogicalDataModel;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractSchema;

/**
 * Logical data model.
 *
 * @author ddrobny
 */
public class LogicalDataModelImpl extends AbstractSchema<LogicalEntity, LogicalAttribute> implements LogicalDataModel {
    private String fileName;

    public LogicalDataModelImpl(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
