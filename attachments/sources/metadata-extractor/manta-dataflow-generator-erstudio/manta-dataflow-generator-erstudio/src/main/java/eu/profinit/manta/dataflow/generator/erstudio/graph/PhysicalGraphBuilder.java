package eu.profinit.manta.dataflow.generator.erstudio.graph;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;

import java.util.Map;

/**
 * Extension of the general graph building scenario specific for physical data models.
 *
 * @author ddrobny
 */
public interface PhysicalGraphBuilder extends GraphBuilder {

    /**
     * Sets the connection.
     * @param connection connection to set.
     */
    void setConnection(Connection connection);

    /**
     * Sets logical attributes that may have mappings to column in physical model being built by the PhysicalGraphBuilder,
     * together with their {@link Graph} representation.
     * @param attributeToNode attributes with their representation to be set.
     */
    void setMappedAttributes(Map<SimpleDataObject, Node> attributeToNode);
}
