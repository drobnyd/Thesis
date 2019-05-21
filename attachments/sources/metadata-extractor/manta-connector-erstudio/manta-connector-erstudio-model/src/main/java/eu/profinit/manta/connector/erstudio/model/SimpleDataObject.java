package eu.profinit.manta.connector.erstudio.model;

/**
 * Depending on its {@link AbstractionLayer} this is a contract of a Column{@link AbstractionLayer#PHYSICAL}
 * or of an Attribute{@link AbstractionLayer#LOGICAL}.
 *
 * @author ddrobny
 */
public interface SimpleDataObject extends DataObject, Mappable<SimpleDataObject> {
}
