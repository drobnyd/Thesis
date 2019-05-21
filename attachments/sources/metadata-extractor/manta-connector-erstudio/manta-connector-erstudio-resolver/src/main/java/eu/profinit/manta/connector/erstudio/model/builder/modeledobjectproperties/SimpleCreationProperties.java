package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties;

/**
 * Set of properties needed for creation of a simple object.
 *
 * @author ddrobny
 */
public interface SimpleCreationProperties extends NameableByLayer {

    /**
     * Gets the definition of the object.
     * @return the definition of the object.
     */
    String getDefinition();

    /**
     * Gets the note of the object.
     * @return the note of the object.
     */
    String getNote();
}
