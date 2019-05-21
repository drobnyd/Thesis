package eu.profinit.manta.connector.powerdesigner.resolver.build;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.resolver.build.desc.HighLevelElementsDescription;
import eu.profinit.manta.connector.powerdesigner.resolver.build.desc.PhysicalElementsDescription;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractNamedObject;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.physical.PhysicalColumnImpl;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.physical.PhysicalDataModelImpl;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.physical.PhysicalSchemaImpl;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.physical.PhysicalTableImpl;
import eu.profinit.manta.dataflow.generator.common.query.DataflowQueryService;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class constructing physical data models.
 *
 * @author ddrobny
 */
public class PhysicalDataModelBuilder extends AbstractDataModelBuilder<PhysicalTableImpl, PhysicalColumnImpl> {

    private static final PhysicalElementsDescription elementsDescription = PhysicalElementsDescription.getInstance();
    /** Generally the name of default schema is null, so specific {@link DataflowQueryService} can handle this by their
     * own policies and assign default or so. */
    private static final String DEFAULT_SCHEMA_NAME = null;
    /** The specific physical data model resulting from the build process. */
    PhysicalDataModelImpl result;
    /** Pairs local ID of a schema and the schema instance itself. */
    private Map<String, PhysicalSchemaImpl> idToSchema = new HashMap<>();

    public PhysicalDataModelBuilder(String fileName, Element root) {
        super(fileName, root,  elementsDescription);
    }

    @Override
    protected void buildResultingDataModel() {
        // Create a map where the keys are composite objects' names
        result.setSchemas(idToSchema.values().stream().collect(Collectors.toMap(x -> x.getName(), x -> x)));
    }

    @Override
    protected PhysicalColumnImpl createSimpleModelObject() {
        return new PhysicalColumnImpl();
    }

    @Override
    protected PhysicalTableImpl createCompositeModelObject() {
        return new PhysicalTableImpl();
    }

    @Override
    protected void createResultingDataModel() {
        result = new PhysicalDataModelImpl(fileName);
    }

    @Override
    public DataModel getResult() {
        return result;
    }

    @Override
    protected AbstractNamedObject getNamedDataModel() {
        return result;
    }

    @Override
    protected void loadCompositeModelObjects(List<Node> composite) {
        loadSchemas(selectNodes("//c:Users/o:User[@Id]", model));

        for (Node tableNode : composite) {
            PhysicalTableImpl table = new PhysicalTableImpl();
            setNamedObjectAttributes(tableNode, table);

            idToComposite.put(getNodeIdAttribute(tableNode), table);

            List<Node> owners = selectNodes("c:Owner/o:User", tableNode);
            for (Node owner : owners) {
                idToSchema.get(getNodeRefAttribute(owner)).addCompositeObject(table);
            }
            if (owners.isEmpty()) {
                idToSchema.get(DEFAULT_SCHEMA_NAME).addCompositeObject(table);
            }

            List<Node> columnNodes = selectNodes("c:Columns/o:Column", tableNode);
            loadChildrenSimpleModelObjects(columnNodes, table);
        }
    }

    /**
     * Loads schemas to idToSchema map.
     * @param userNodes DOM nodes where the users, some of them represent schemas, are defined.
     */
    private void loadSchemas(List<Node> userNodes) {
        // Default owner's name will remain if unknown or not set, thus each specific technology
        // node creator can assign its default schema
        idToSchema.put(DEFAULT_SCHEMA_NAME, new PhysicalSchemaImpl());

        for (Node node : userNodes) {
            // A user is a schema in two cases - it listed as its stereotype, or it has not stereotype at all
            if (node.matches(
                    "//c:Users/o:User[@Id and not(a:Stereotype)] | //c:Users/o:User[@Id and a:Stereotype = 'Schema']")) {

                PhysicalSchemaImpl owner = new PhysicalSchemaImpl();
                setNamedObjectAttributes(node, owner);

                idToSchema.put(getNodeIdAttribute(node), owner);
            }
        }
    }
}
