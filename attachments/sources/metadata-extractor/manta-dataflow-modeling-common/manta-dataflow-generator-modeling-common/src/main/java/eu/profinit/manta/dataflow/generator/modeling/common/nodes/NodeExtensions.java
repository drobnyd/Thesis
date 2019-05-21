package eu.profinit.manta.dataflow.generator.modeling.common.nodes;

import eu.profinit.manta.dataflow.model.Node;

import java.util.Map;

/**
 * Extends {@link Node} of methods that add metadata to an instance of Node.
 *
 * @author ddrobny
 */
public class NodeExtensions {

    /**
     * Attaches metadata as attributes of the given node.
     * @param node the node to append to.
     * @param metadata the metadata to append.
     */
    public static void appendAttributes(Node node, NodeMetadata metadata) {
        if (node == null || metadata == null || metadata.getMetadata() == null) {
            // No attributes to append, or no node to append to
            return;
        }

        for (Map.Entry<String, String> entry : metadata.getMetadata().entrySet()) {
            node.addAttribute(entry.getKey(), entry.getValue());
        }
    }
}
