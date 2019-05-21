package eu.profinit.manta.connector.erstudio.model;

import java.util.Collection;

/**
 * Depending on its {@link AbstractionLayer} this is a contract of a Table{@link AbstractionLayer#PHYSICAL}
 * or of an Entity{@link AbstractionLayer#LOGICAL}.
 * The object implementing this interface is a parent of {@link SimpleDataObject} objects that are modeled on the same
 * {@link AbstractionLayer}.
 * Every {@link SimpleDataObject} contained in {@link CompositeDataObject} must have a unique name.
 *
 * @author ddrobny
 */
public interface CompositeDataObject extends DataObject, Mappable<CompositeDataObject> {

    /**
     * Method for obtaining underlying {@link SimpleDataObject}s that are,
     * depending on the {@link AbstractionLayer}, Columns(Physical) or Attributes(Logical).
     * @return Underlying {@link SimpleDataObject}s that are, depending on the {@link AbstractionLayer}, Columns(Physical) or Attributes(Logical)
     */
    Collection<SimpleDataObject> getAllSubObjects();

    /**
     * Method for obtaining underlying {@link SimpleDataObject} by its name that is,
     * depending on the {@link AbstractionLayer}, Column(Physical) or Attribute(Logical).
     * @param name Unique identifier of the underlying {@link SimpleDataObject}.
     * @return The {@link SimpleDataObject} with the specified {@code name}
     * or null if no underlying object with the {@code name} exists.
     */
    SimpleDataObject getSubObject(String name);

}
