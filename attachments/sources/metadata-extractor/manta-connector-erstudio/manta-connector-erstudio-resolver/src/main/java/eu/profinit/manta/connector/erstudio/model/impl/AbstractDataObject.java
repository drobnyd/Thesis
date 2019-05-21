package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.connector.erstudio.model.AbstractionLayer;
import eu.profinit.manta.connector.erstudio.model.DataObject;
import eu.profinit.manta.connector.erstudio.model.Mappable;
import eu.profinit.manta.connector.erstudio.utils.MultiMapExtensions;
import eu.profinit.manta.dataflow.generator.modeling.common.rtf.RtfTools;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation for every DataObject.
 *
 * @author ddrobny
 */
public abstract class AbstractDataObject<T extends DataObject> extends AbstractObject
        implements DataObject, Mappable<T> {

    private final Map<String, List<T>> mappedObjects = new HashMap<>();
    private RtfTools rtfTools = RtfTools.getInstance();
    private String note;
    private String definition;
    private AbstractionLayer layer;

    public AbstractDataObject(String name, AbstractionLayer layer) {
        super(name);
        this.layer = layer;
    }

    @Override
    public String getNote() {
        return note;
    }

    /**
     * Sets the note of the object.
     * @param note the note to set.
     */
    public void setNote(String note) {
        this.note = rtfTools.rtfToPlain(note);
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the definition of the object.
     * @param definition the definition to set.
     */
    public void setDefinition(String definition) {
        this.definition = rtfTools.rtfToPlain(definition);
    }

    @Override
    public AbstractionLayer getLayer() {
        return layer;
    }

    @Override
    public List<T> getAllMappedObjects() {
        return Collections.unmodifiableList(MultiMapExtensions.flattenMultiMap(mappedObjects));
    }

    @Override
    public List<T> getMappedObjects(String objectName) {
        return Collections.unmodifiableList(mappedObjects.get(objectName));
    }

    @Override
    public boolean hasMappings() {
        return mappedObjects.size() > 0;
    }

    /**
     * Maps the instance to the given object.
     * @param toAdd the object to create the mapping to.
     */
    public void addMapping(T toAdd) {
        if (isMappedTo(toAdd)) {
            return;
        }
        MultiMapExtensions.addToMultiMap(mappedObjects, toAdd.getName(), toAdd);
    }

    @Override
    public boolean isMappedTo(T object) {
        List<T> list = mappedObjects.get(object.getName());
        if (list == null) {
            return false;
        }
        return list.contains(object);
    }
}
