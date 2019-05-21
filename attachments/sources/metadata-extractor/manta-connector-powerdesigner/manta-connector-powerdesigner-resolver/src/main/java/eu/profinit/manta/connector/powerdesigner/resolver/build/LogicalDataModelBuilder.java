package eu.profinit.manta.connector.powerdesigner.resolver.build;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractNamedObject;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.logical.LogicalAttributeImpl;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.logical.LogicalDataModelImpl;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.logical.LogicalEntityImpl;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class constructing logical data models.
 *
 * @author ddrobny
 */
public class LogicalDataModelBuilder
        extends AbstractHighLevelDataModelBuilder<LogicalEntityImpl, LogicalAttributeImpl> {

    /** The specific conceptual data model resulting from the build process. */
    private LogicalDataModelImpl result;

    public LogicalDataModelBuilder(String fileName, Element root) {
        super(fileName, root);

    }

    @Override
    protected void loadCompositeModelObjects(List<Node> composite) {
        for (Node entityNode : composite) {
            LogicalEntityImpl entity = createCompositeModelObject();
            setNamedObjectAttributes(entityNode, entity);

            idToComposite.put(getNodeIdAttribute(entityNode), entity);

            List<Node> attributeNodes = selectNodes("c:Attributes/o:EntityAttribute[@Id]", entityNode);
            loadChildrenSimpleModelObjects(attributeNodes, entity);
        }
    }

    @Override
    protected void buildResultingDataModel() {
        result.setCompositeObjects(idToComposite.values().stream().collect(Collectors.toMap(x -> x.getName(), x -> x)));
    }

    @Override
    protected LogicalAttributeImpl createSimpleModelObject() {
        return new LogicalAttributeImpl();
    }

    @Override
    protected LogicalEntityImpl createCompositeModelObject() {
        return new LogicalEntityImpl();
    }

    @Override
    protected void createResultingDataModel() {
        result = new LogicalDataModelImpl(fileName);
    }

    @Override
    public DataModel getResult() {
        return result;
    }

    @Override
    protected AbstractNamedObject getNamedDataModel() {
        return result;
    }
}
