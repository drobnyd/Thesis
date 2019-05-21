package eu.profinit.manta.connector.erstudio;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class capturing tested aspects of a composite object.
 *
 * @author ddrobny
 */
public class CompositeObjectTestDescription extends ModeledObjectTestDescription {
    private final Map<String, SimpleObjectTestDescription> nameToSubObject = new HashMap<>();
    private long subObjectCount;
    private String modelName;
    private long mappingCount;
    private String owner;

    public long getSubObjectCount() {
        return subObjectCount;
    }

    public void setSubObjectCount(long subObjectCount) {
        this.subObjectCount = subObjectCount;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(java.lang.String modelName) {
        this.modelName = modelName;
    }

    public long getMappingCount() {
        return mappingCount;
    }

    public void setMappingCount(long mappingCount) {
        this.mappingCount = mappingCount;
    }

    public String getOwnerName() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void addSubObjectDescription(SimpleObjectTestDescription simpleObject) {
        assert !nameToSubObject.containsKey(simpleObject.getName());
        nameToSubObject.put(simpleObject.getName(), simpleObject);
    }

    public Collection<SimpleObjectTestDescription> getSubObjectDescription() {
        return nameToSubObject.values();
    }

    public SimpleObjectTestDescription getSubObjectDescription(String name) {
        return nameToSubObject.get(name);
    }
}