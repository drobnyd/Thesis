package eu.profinit.manta.connector.powerdesigner.model.logical;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.model.Schema;

/**
 * Logical data model. Has no further schemas, is the only one schema itself.
 *
 * @author ddrobny
 */
public interface LogicalDataModel extends DataModel, Schema<LogicalEntity, LogicalAttribute> {

}
