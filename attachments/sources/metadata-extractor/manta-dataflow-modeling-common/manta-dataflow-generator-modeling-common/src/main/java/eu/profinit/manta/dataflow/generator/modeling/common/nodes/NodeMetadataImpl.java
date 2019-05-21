package eu.profinit.manta.dataflow.generator.modeling.common.nodes;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of the NodeMetadata interface.
 *
 * @author ddrobny
 */
public class NodeMetadataImpl implements NodeMetadata {
    private String name;
    private Map<String, String> metadata = new HashMap<>();

    public NodeMetadataImpl(String name) {
        this.name = name;
    }

    public NodeMetadataImpl(String name, Map<String, String> metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
