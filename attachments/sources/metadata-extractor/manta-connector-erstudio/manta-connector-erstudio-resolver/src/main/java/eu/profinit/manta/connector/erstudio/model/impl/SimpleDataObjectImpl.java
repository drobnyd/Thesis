package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.connector.erstudio.model.AbstractionLayer;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;

/**
 * Implementation of SimpleDataObejct.
 *
 * @author ddrobny
 */
public class SimpleDataObjectImpl extends AbstractDataObject<SimpleDataObject> implements SimpleDataObject {

    public SimpleDataObjectImpl(String name, AbstractionLayer layer) {
        super(name, layer);
    }

}
