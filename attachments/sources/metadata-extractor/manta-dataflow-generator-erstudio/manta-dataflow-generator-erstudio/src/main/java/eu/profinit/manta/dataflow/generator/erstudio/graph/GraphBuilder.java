package eu.profinit.manta.dataflow.generator.erstudio.graph;

import eu.profinit.manta.dataflow.model.Graph;

/**
 * General scenario of building a graph out of data model.
 *
 * @author ddrobny
 */
public interface GraphBuilder {

    /**
     * Builds {@link Graph} from a {@link eu.profinit.manta.connector.erstudio.model.DataModel}.
     * After construction can be accessed via {@link #getGraph()}
     */
    void buildGraph();

    /**
     * Accesses {@link Graph} that has been created by {@link #buildGraph()}.
     * Method {@link #buildGraph()} must be called before. If it wasn't no {@link Graph} to be returned exists.
     * @return {@link Graph} representation of a {@link eu.profinit.manta.connector.erstudio.model.DataModel}.
     * Method {@link #buildGraph()} must be called before. If it wasn't built before, null is returned.
     */
    Graph getGraph();
}
