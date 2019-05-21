package eu.profinit.manta.dataflow.generator.erstudio.graph;

import eu.profinit.manta.connector.erstudio.model.DataObject;
import eu.profinit.manta.connector.erstudio.model.LogicalDataModel;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;

import java.util.Map;

/**
 * Extension of the general graph building scenario specific for logical data models.
 *
 * @author ddrobny
 */
public interface LogicalGraphBuilder extends GraphBuilder {

    /**
     * Gets pairs of every logical attribute represented as {@link DataObject} from {@link LogicalDataModel}
     * and its actual {@link Node} representation in created {@link Graph}.
     * Method {@link #buildGraph()} must be called before. If it wasn't no {@link Graph} to be returned exists.
     * @return Pairs of every logical {@link DataObject} from {@link LogicalDataModel}
     * and its actual {@link Node} representation in created {@link Graph}.
     * If the {@link Graph} wasn't built before, null is returned.
     */
    Map<SimpleDataObject, Node> getMappedAttributes();
}
