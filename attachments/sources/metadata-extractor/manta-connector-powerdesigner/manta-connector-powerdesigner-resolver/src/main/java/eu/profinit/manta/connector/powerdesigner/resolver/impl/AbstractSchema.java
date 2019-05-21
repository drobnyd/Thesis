package eu.profinit.manta.connector.powerdesigner.resolver.impl;

import eu.profinit.manta.connector.powerdesigner.model.CompositeModelObject;
import eu.profinit.manta.connector.powerdesigner.model.Schema;
import eu.profinit.manta.connector.powerdesigner.model.SimpleModelObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation for schemas.
 * @param <C> type of underlying composite objects.
 * @param <S> type of simple objects the composite ones consist from.
 *
 * @author ddrobny
 */
public abstract class AbstractSchema<C extends CompositeModelObject<S>, S extends SimpleModelObject>
        extends AbstractNamedObject implements Schema<C, S> {
    /** Pairs name of composite object and the instance itself. */
    Map<String, C> idToComposite = new HashMap<>();

    public CompositeModelObject addCompositeObject(C toAdd) {
        return idToComposite.put(toAdd.getName(), toAdd);
    }

    /**
     * Adds all composite objects to the schema.
     * @param compositeObjects pairs name-composite object to add to the schema.
     */
    public void setCompositeObjects(Map<String, ? extends C> compositeObjects) {
        idToComposite.putAll(compositeObjects);
    }

    @Override
    public C getCompositeModelObject(String name) {
        return idToComposite.get(name);
    }

    @Override
    public Collection<C> getCompositeModelObjects() {
        return idToComposite.values();
    }
}
