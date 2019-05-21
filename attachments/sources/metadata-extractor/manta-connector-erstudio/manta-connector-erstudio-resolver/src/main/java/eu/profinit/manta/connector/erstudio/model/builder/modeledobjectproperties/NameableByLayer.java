package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties;

/**
 * Interface for objects that have both physical and logical name and what they use is decided by on what layer they are.
 *
 * @author ddrobny
 */
public interface NameableByLayer {

    /**
     * Gets the name used on the logical level.
     * @return the logical name.
     */
    String getLogicalName();

    /**
     * Gets the name used on the physical level.
     * @return the physical name.
     */
    String getPhysicalName();
}
