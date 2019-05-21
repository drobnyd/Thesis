package eu.profinit.manta.connector.powerdesigner.resolver.build;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.resolver.build.desc.HighLevelElementsDescription;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractCompositeModelObject;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractNamedObject;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.conceptual.ConceptualAttributeImpl;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.conceptual.ConceptualDataModelImpl;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.conceptual.ConceptualEntityImpl;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.conceptual.DataItem;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class constructing conceptual data models.
 *
 * @author ddrobny
 */
public class ConceptualDataModelBuilder
        extends AbstractHighLevelDataModelBuilder<ConceptualEntityImpl, ConceptualAttributeImpl> {
    /** Pairs local ID of a data item and the data item itself. */
    private Map<String, DataItem> idToDataItem = new HashMap<>();
    /** The specific conceptual data model resulting from the build process. */
    private ConceptualDataModelImpl result;

    public ConceptualDataModelBuilder(String fileName, Element root) {
        super(fileName, root);
    }

    @Override
    public void buildDataModel() {
        loadDataItems(selectNodes("//o:DataItem[@Id]", model));
        super.buildDataModel();
    }

    @Override
    protected void loadCompositeModelObjects(List<Node> entityNodes) {
        for (Node entityNode : entityNodes) {
            ConceptualEntityImpl entity = createCompositeModelObject();
            setNamedObjectAttributes(entityNode, entity);

            idToComposite.put(getNodeIdAttribute(entityNode), entity);

            // For each attribute listed link with the entity
            List<Node> entityAttributes = selectNodes("c:Attributes/o:EntityAttribute[@Id]", entityNode);

            for (Node attributeNode : entityAttributes) {
                Node dataItemNode = selectSingleNode("c:DataItem/o:DataItem[@Ref]", attributeNode);
                if (dataItemNode != null) {
                    appendAttributeByDataItem(dataItemNode, attributeNode, entity);
                } else { // The attribute defined has its own definition
                    loadChildrenSimpleModelObject(attributeNode, entity);
                }
            }
        }
    }

    /** Puts all the loaded objects to resulting data model object */
    @Override
    protected void buildResultingDataModel() {
        result.setCompositeObjects(idToComposite.values().stream().collect(Collectors.toMap(x -> x.getName(), x -> x)));
    }

    @Override
    protected ConceptualAttributeImpl createSimpleModelObject() {
        return new ConceptualAttributeImpl();
    }

    @Override
    protected ConceptualEntityImpl createCompositeModelObject() {
        return new ConceptualEntityImpl();
    }

    /**
     * Creates the specific resulting data model.
     */
    @Override
    protected void createResultingDataModel() {
        this.result = new ConceptualDataModelImpl(fileName);
    }

    /** Returns the resulting data model */
    @Override
    public DataModel getResult() {
        return result;
    }

    @Override
    protected AbstractNamedObject getNamedDataModel() {
        return result;
    }

    /**
     * Appends an attribute represented by a data item to the entity.
     * @param dataItemNode DOM node representing which data item which the attribute references.
     * @param attributeNode DOM node of the entity's attribute.
     * @param entity the entity to append the new attribute to.
     */
    private void appendAttributeByDataItem(Node dataItemNode, Node attributeNode, AbstractCompositeModelObject entity) {
        String reference = getNodeRefAttribute(dataItemNode);
        String globalId = getXPathValue(attributeNode, "a:ObjectID");
        // If a data item gets referenced from an entity, create corresponding attribute
        ConceptualAttributeImpl attribute = idToDataItem.get(reference).createAttribute(globalId);

        entity.addSubObject(attribute);
        idToSimple.put(getNodeIdAttribute(attributeNode), attribute);

    }

    private void loadDataItems(List<Node> simple) {
        for (Node simpleNode : simple) {
            DataItem toAdd = new DataItem();
            setNamedObjectAttributes(simpleNode, toAdd);

            String id = getNodeIdAttribute(simpleNode);
            idToDataItem.put(id, toAdd);
        }
    }

}
