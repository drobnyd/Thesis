package eu.profinit.manta.connector.powerdesigner.resolver.build;

import eu.profinit.manta.connector.powerdesigner.resolver.build.desc.HighLevelElementsDescription;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractEntity;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractSimpleModelObject;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common base class for construction of conceptual and logical data models.
 * @param <E> type of underlying entities
 * @param <S> type of simple object under entities.
 *
 * @author ddrobny
 */
public abstract class AbstractHighLevelDataModelBuilder<E extends AbstractEntity<?, ?>, S extends AbstractSimpleModelObject>
        extends AbstractDataModelBuilder<E, S> {
    private final static HighLevelElementsDescription entityDescription = HighLevelElementsDescription.getInstance();

    /** Pairs parentId and entity that may appear as a parent. In inheritances to parent entities is referred via parentId */
    private Map<String, E> parentIdToEntity = new HashMap<>();

    public AbstractHighLevelDataModelBuilder(String fileName, Element root) {

        super(fileName, root, entityDescription);
    }

    @Override
    public void buildDataModel() {
        // Construct all objects from data model as usual
        super.buildDataModel();
        // Load inheritances
        loadInheritances();
    }

    /**
     * Loads entities that are predecessor of another entities to parentIdToEntity.
     */
    private void loadParents() {
        List<Node> inheritanceNodes = selectNodes("//o:Inheritance[@Id]", model);

        for (Node inheritanceNode : inheritanceNodes) {

            Node parentEntity = selectSingleNode("c:ParentEntity/o:Entity[@Ref]", inheritanceNode);
            parentIdToEntity
                    .put(getNodeIdAttribute(inheritanceNode), idToComposite.get(getNodeRefAttribute(parentEntity)));
        }
    }

    /**
     * Links children to their parent entities.
     */
    private void loadInheritances() {
        // Firstly load the parents that may be referenced form the individual mappings, they are referenced using
        // specific parent ID from inheritances
        loadParents();

        // Then link the children to their parents
        List<Node> inheritanceLinksNodes = selectNodes("//o:InheritanceLink[@Id]", model);

        for (Node inheritanceLinkNode : inheritanceLinksNodes) {

            Node parentRefNode = selectSingleNode("c:Object1/o:Inheritance", inheritanceLinkNode);
            E parent = parentIdToEntity.get(getNodeRefAttribute(parentRefNode));

            Node childRefNode = selectSingleNode("c:Object2/o:Entity", inheritanceLinkNode);
            E child = idToComposite.get(getNodeRefAttribute(childRefNode));

            child.setParent(parent);
        }

    }
}
