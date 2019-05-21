package eu.profinit.manta.connector.powerdesigner.model;

import java.util.Collection;

/**
 * Schema is a container for composite objects.
 *
 * @param <C> type of stored composite objects
 * @param <S> type of simple objects that composite objects hold own.
 *
 * @author ddrobny
 */
public interface Schema<C extends CompositeModelObject<S>, S extends SimpleModelObject> extends NamedObject {
    /**
     * Gets the tables by the name that belongs to the owner.
     * @param name the name to search by.
     * @return the entity of the owner.
     */
    C getCompositeModelObject(String name);

    /**
     * Gets the tables belonging to the owner.
     * @return the entities of the owner.
     */
    Collection<C> getCompositeModelObjects();
}
