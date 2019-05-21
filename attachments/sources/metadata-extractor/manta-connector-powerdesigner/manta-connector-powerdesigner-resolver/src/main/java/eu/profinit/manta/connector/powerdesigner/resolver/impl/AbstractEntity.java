package eu.profinit.manta.connector.powerdesigner.resolver.impl;

import eu.profinit.manta.connector.powerdesigner.model.Entity;
import eu.profinit.manta.connector.powerdesigner.model.SimpleModelObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation for logical and conceptual entities from enhanced-entity-relationship data model.
 * @param <E> type of entity from which the instance can inherit.
 * @param <S> type of underlying simple objects.
 *
 * @author ddrobny
 */
public abstract class AbstractEntity<E extends Entity<E, S>, S extends SimpleModelObject>
        extends AbstractCompositeModelObject<S> implements Entity<E, S> {
    /** Logger. */
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractEntity.class);
    /** Entity from which the instance inherits attributes. */
    E parent;

    @Override
    public E getParent() {
        return parent;
    }

    public boolean setParent(Object parent) {
        // TODO!!!
        if (this.parent == null) {
            try {
                this.parent = (E) parent;
            } catch (ClassCastException e) {
                LOGGER.warn("Trying to set parent of entity {} that does not match.", this.getName());
                return false;
            }
            return true;
        }
        return false;
    }
}
