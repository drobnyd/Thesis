package eu.profinit.manta.dataflow.generator.modeling.common.nodes;

import eu.profinit.manta.dataflow.model.Node;

import java.util.Map;

/**
 * Objects implementing this interfaces can be translated into {@link Node} representation.
 *
 * @author ddrobny
 */
public interface NodeMetadata {
    /**
     * Gets a name that can be used as node's name.
     * @return name that can be used as name of a node.
     */
    String getName();

    /**
     * Gets a set of important textual attributes that will be attached to node representation of the object.
     * @return a set of textual attributes. May be {@code null}.
     */
    Map<String, String> getMetadata();
}
