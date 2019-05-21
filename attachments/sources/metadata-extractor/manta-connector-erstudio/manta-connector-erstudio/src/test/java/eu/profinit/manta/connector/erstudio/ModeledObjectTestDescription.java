package eu.profinit.manta.connector.erstudio;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class capturing common tested aspects of a modeled object.
 *
 * @author ddrobny
 */
public abstract class ModeledObjectTestDescription {
    /* Simplified, Cannot be mapped to two different objects with same name */
    private Map<String, ModeledObjectTestDescription> nameToMappedObject = new HashMap<>();
    private String name, definition, note;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Collection<ModeledObjectTestDescription> getAllMappedObjectDescriptions() {
        return nameToMappedObject.values();
    }

    public void addMappedObjectDescription(ModeledObjectTestDescription toAdd) {
        assert !nameToMappedObject
                .containsKey(toAdd.getName()); // Adding object with the same name again -> is considered to be the same
        nameToMappedObject.put(toAdd.getName(), toAdd);
    }
}
