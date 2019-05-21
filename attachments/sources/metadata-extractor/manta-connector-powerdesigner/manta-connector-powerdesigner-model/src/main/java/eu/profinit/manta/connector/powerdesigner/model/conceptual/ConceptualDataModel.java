package eu.profinit.manta.connector.powerdesigner.model.conceptual;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.model.Schema;

/**
 * Conceptual data model. Has no further schemas, is the only one schema itself.
 *
 * @author ddrobny
 */
public interface ConceptualDataModel extends DataModel, Schema<ConceptualEntity, ConceptualAttribute> {

}
