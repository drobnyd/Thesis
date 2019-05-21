package eu.profinit.manta.connector.powerdesigner.model;

import java.util.Collection;

/**
 * Composite object in a data model has metadata, can be mapped to composite objects and contains simple objects.
 * @param <T> type of underlying simple objects.
 *
 * @author ddrobny
 */
public interface CompositeModelObject<T extends SimpleModelObject> extends NamedObject, Mappable {

    /**
     * Gets an simple object by its name from the object.
     * @param simpleObjectName name to search by.
     * @return the simple objects with {@code simpleObjectName} or {@code null} if not found.
     */
    T getSimpleModelObject(String simpleObjectName);

    /**
     * Gets the simple objects associated with the object.
     * @return the simple objects associated with the object.
     */
    Collection<T> getSimpleModelObjects();
}
