package eu.profinit.manta.connector.powerdesigner.resolver.impl.conceptual;

import eu.profinit.manta.connector.powerdesigner.model.conceptual.ConceptualAttribute;
import eu.profinit.manta.connector.powerdesigner.model.conceptual.ConceptualDataModel;
import eu.profinit.manta.connector.powerdesigner.model.conceptual.ConceptualEntity;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractSchema;

/**
 * Conceptual data model.
 *
 * @author ddrobny
 */
public class ConceptualDataModelImpl extends AbstractSchema<ConceptualEntity, ConceptualAttribute> implements ConceptualDataModel {

    private String fileName;

    public ConceptualDataModelImpl(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
