package eu.profinit.manta.dataflow.generator.erstudio;

import eu.profinit.manta.connector.common.connections.Connection;
import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.connector.erstudio.model.ErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.PhysicalDataModel;
import eu.profinit.manta.connector.erstudio.model.SimpleDataObject;
import eu.profinit.manta.dataflow.generator.erstudio.connections.ConnectionResolver;
import eu.profinit.manta.dataflow.generator.erstudio.graph.LogicalGraphBuilder;
import eu.profinit.manta.dataflow.generator.erstudio.graph.LogicalGraphBuilderImpl;
import eu.profinit.manta.dataflow.generator.erstudio.graph.PhysicalGraphBuilder;
import eu.profinit.manta.dataflow.generator.erstudio.graph.PhysicalGraphBuilderImpl;
import eu.profinit.manta.dataflow.generator.modeling.common.connections.DatabaseConnector;
import eu.profinit.manta.dataflow.generator.modelutils.AbstractGraphTask;
import eu.profinit.manta.dataflow.model.Graph;
import eu.profinit.manta.dataflow.model.Node;
import eu.profinit.manta.dataflow.model.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * Task transforming data models from an ER/Studio file to graph.
 *
 * @author ddrobny
 */
public class ErStudioDataflowTask extends AbstractGraphTask<ErStudioFileModel> {
    private String systemId;
    private Resource resource;
    /** A .ini file where connections are set for physical models that are contained in
     * processed {@link ErStudioFileModel}s. Key of an entry is physical model's name, following values are
     * {@link Connection} properties of database corresponding to the model.
     * If two physical models have the same name, it is assumed that they share connection as well */
    private ConnectionResolver connectionResolver;
    private DatabaseConnector databaseConnector;

    /**
     * Sets the connection resolver.
     * @param connectionResolver connection resolver to set.
     */
    public void setConnectionResolver(ConnectionResolver connectionResolver) {
        this.connectionResolver = connectionResolver;
    }

    /**
     * Sets ER/Studio the resource for {@link Graph} components at logical layer.
     * @param resource ER/Studio resource to set.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Sets the database connector
     * @param databaseConnector database connector to set.
     */
    public void setDatabaseConnector(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * Potomci této metody jsou voláni při spuštění úkolu. Je jim předán vstupní
     * a výstupní objekt, o nichž mohou předpokládat, že jsou správně inicializované.
     * Mohou vyhodit libovolnou výjimku.
     * @param input vstup úkolu
     * @param output výstup úkolu
     */
    @Override
    protected void doExecute(ErStudioFileModel input, Graph output) {
        Map<SimpleDataObject, Node> attributeToNode = new HashMap<>();
        // Build all Logical Models. They have to be completed before physical ones so physical objects can refer to
        // the logical then
        attributeToNode.putAll(drawLogicalModel(input.getInternalErStudioSolution(), output));

        for (ErStudioSolution external : input.getAllExternalErStudioSolutions()) {
            attributeToNode.putAll(drawLogicalModel(external, output));
        }

        drawPhysicalModels(input.getInternalErStudioSolution(), output, attributeToNode);

        for (ErStudioSolution external : input.getAllExternalErStudioSolutions()) {
            drawPhysicalModels(external, output, attributeToNode);
        }
    }

    /**
     * Creates {@link Graph} representation of the Logical Model contained in {@code input}.
     * @param input ErStudio model containing the Logical Model to be appended to {@code output}.
     * @param output graph that is being built throughout the task.
     * @return All attributes from {@code input} that may be referenced from other
     * {@link eu.profinit.manta.connector.erstudio.model.DataModel}s
     */
    private Map<SimpleDataObject, Node> drawLogicalModel(ErStudioSolution input, Graph output) {
        LogicalGraphBuilder logicalBuilder = new LogicalGraphBuilderImpl(input, systemId, resource);
        logicalBuilder.buildGraph();
        output.merge(logicalBuilder.getGraph());
        // Get relevant Attributes that will get linked with equivalent Columns on Physical Layer
        return logicalBuilder.getMappedAttributes();
    }

    /**
     * Creates {@link Graph} representation of Physical Models contained in {@code input}.
     * @param input ErStudio model containing the Physical Models to be appended to {@code output}.
     * @param output graph that is being built throughout the task.
     * @param attributeToNode attributes and their node representations from a Logical Model that may get linked
     * with columns that will be created from {@code input}.
     */
    private void drawPhysicalModels(ErStudioSolution input, Graph output, Map<SimpleDataObject, Node> attributeToNode) {
        for (PhysicalDataModel physModel : input.getAllPhysicalModels()) {
            Connection connection = connectionResolver.getConnection(physModel);

            PhysicalGraphBuilder physicalBuilder = new PhysicalGraphBuilderImpl(physModel, databaseConnector);
            physicalBuilder.setMappedAttributes(attributeToNode);
            physicalBuilder.setConnection(connection);

            physicalBuilder.buildGraph();

            output.merge(physicalBuilder.getGraph());
        }
    }
}
