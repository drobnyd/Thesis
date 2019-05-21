package eu.profinit.manta.connector.powerdesigner.model;

/**
 * A data model is a representation of the information consumed and produced by a system, which lets you analyze
 * the data objects present in the system and the relationships between them. PowerDesigner provides conceptual,
 * logical, and physical data models to allow you to analyze and model your system at all levels of abstraction.
 *
 * A data model in PowerDesigner is mapped one to one with a file where it is stored. Every specific data model
 * has to extend this so it can be casted to this common predecessor.
 *
 * @author ddrobny
 */
public interface DataModel extends NamedObject {

    /**
     * Gets the location of the model file.
     * @return the location of the model file.
     */
    String getFileName();

}
