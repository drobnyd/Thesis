package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties;

/**
 * Properties needed for crating a data model.
 *
 * @author ddrobny
 */
public interface ModelCreationProperties {
    /**
     * Gets the type of the data model.
     * @return the type of the data model.
     */
    int getModelType();

    /**
     * Gets the name of the model.
     * @return the name of the model.
     */
    String getName();

    /**
     * Gets ID of the DB platform the model is targeted on.
     * @return ID of the DB platform the model is targeted on.
     */
    int getPlatformId();
}
