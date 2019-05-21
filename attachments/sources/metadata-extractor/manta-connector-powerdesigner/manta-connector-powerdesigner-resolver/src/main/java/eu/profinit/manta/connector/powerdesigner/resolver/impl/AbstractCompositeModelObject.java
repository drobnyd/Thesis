package eu.profinit.manta.connector.powerdesigner.resolver.impl;

import eu.profinit.manta.connector.powerdesigner.model.CompositeModelObject;
import eu.profinit.manta.connector.powerdesigner.model.SimpleModelObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation for all CompositeModelObjects.
 * @param <S> type of underlying simple objects.
 *
 * @author ddrobny
 */
public abstract class AbstractCompositeModelObject<S extends SimpleModelObject> extends AbstractMappableNamedObject
        implements CompositeModelObject<S> {
    /** Pairs name of simple object and the instance itself. */
    private Map<String, S> nameToSimple = new HashMap<>();

    @Override
    public S getSimpleModelObject(String simpleObjectName) {
        return nameToSimple.get(simpleObjectName);
    }

    @Override
    public Collection<S> getSimpleModelObjects() {
        return nameToSimple.values();
    }

    /**
     * Adds the simple object to children.
     * @param simpleModelObject the child to add.
     * @return the previous value assigned with the key.
     */
    public SimpleModelObject addSubObject(S simpleModelObject) {
        return nameToSimple.put(simpleModelObject.getName(),simpleModelObject);
    }
}
