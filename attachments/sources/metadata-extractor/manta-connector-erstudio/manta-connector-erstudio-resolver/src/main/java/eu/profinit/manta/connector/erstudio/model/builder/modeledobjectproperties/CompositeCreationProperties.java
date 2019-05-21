package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties;

/**
 * Properties needed to create composite data object.
 *
 * @author ddrobny
 */
public interface CompositeCreationProperties extends NameableByLayer {
    /**
     * Gets the name of the owner.
     * @return the name of the owner.
     */
    String getOwnerName();

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
