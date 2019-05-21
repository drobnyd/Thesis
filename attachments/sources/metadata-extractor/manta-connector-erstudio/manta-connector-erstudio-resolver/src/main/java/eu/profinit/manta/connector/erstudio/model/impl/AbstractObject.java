package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.dataflow.generator.modeling.common.nodes.NodeMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation for every AbstractObject.
 *
 * @author ddrobny
 */
public abstract class AbstractObject implements NodeMetadata {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataModel.class);
    private final String name;
    private Map<String, String> nodeAttributes = new HashMap<>();

    public AbstractObject(String name) {
        this.name = name;
        if (name == null) {
            LOGGER.warn("The created object has no name assigned");
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<String, String> getMetadata() {
        return nodeAttributes;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
