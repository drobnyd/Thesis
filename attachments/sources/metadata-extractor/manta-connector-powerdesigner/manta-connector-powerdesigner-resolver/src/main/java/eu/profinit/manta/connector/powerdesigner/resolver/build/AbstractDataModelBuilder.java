package eu.profinit.manta.connector.powerdesigner.resolver.build;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.resolver.build.desc.AbstractElementsDescription;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractCompositeModelObject;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractMappableNamedObject;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractNamedObject;
import eu.profinit.manta.connector.powerdesigner.resolver.impl.AbstractSimpleModelObject;
import eu.profinit.manta.dataflow.generator.modeling.common.rtf.RtfTools;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class with the common functionality for building every of the data model types.
 * @param <C> type of composite objects of given data model.
 * @param <S> type of simple objects underlying the composite ones.
 *
 * @author ddrobny
 */
public abstract class AbstractDataModelBuilder<C extends AbstractCompositeModelObject<?>, S extends AbstractSimpleModelObject>
        implements DataModelBuilder {
    /** Utility for handling RTF strings */
    private static RtfTools rtfTools = RtfTools.getInstance();
    /** XPath to composite objects */
    private final String compositeObjectsXPath;
    /** Xpath to pairs of composite objects to link */
    private final String compositeMappingTargetXPath;
    /** Xpath to pairs of simple objects to link */
    private final String simpleMappingTargetXPath;
    /** Name of the processed file */
    protected String fileName;
    /** Root of parsed DOM tree */
    protected Element root;
    /** DOM node of the processed model */
    protected Node model;
    /** Internal ID of a composite object is the key, the object itself the value */
    protected Map<String, C> idToComposite = new HashMap<>();
    /** Internal ID of a simple object is the key, the object itself the value */
    protected Map<String, S> idToSimple = new HashMap<>();
    /** Internal ID of a shortcut is the key, the internal ID of the external object in its original model the value.
     * The shortcuts are used for external objects referenced in user defined mappings. */
    private Map<String, String> idToShortcutsGlobalId = new HashMap<>();
    /** Internal ID of a simple object is the key, the key itself the value */
    private Map<String, String> idToKeyword = new HashMap<>();

    public AbstractDataModelBuilder(String fileName, Element root, AbstractElementsDescription elementsDescription) {
        this.root = root;
        this.fileName = fileName;
        this.model = selectSingleNode("//o:Model[@Id]", root);

        this.compositeObjectsXPath = elementsDescription.getCompositeObjectsXPath();
        this.compositeMappingTargetXPath = "c:Classifier/" + elementsDescription.getCompositeObjectElementName();
        this.simpleMappingTargetXPath =
                "c:BaseStructuralFeatureMapping.Feature/" + elementsDescription.getSimpleObjectElementName();

    }

    /**
     * Creates composite objects from DOM ndoes and adds them to the idToComposite map.
     * @param composite the DOM nodes to build the composite objects from.
     */
    abstract protected void loadCompositeModelObjects(List<Node> composite);

    /** Puts all the loaded objects to resulting specific DataModel instance. */
    protected abstract void buildResultingDataModel();

    /**
     * Creates a new specific implementation of a simple object.
     * @return created simple object.
     */
    protected abstract S createSimpleModelObject();

    /**
     * Creates a new specific implementation of a composite object.
     * @return created composite object.
     */
    protected abstract C createCompositeModelObject();

    /**
     * Creates the specific resulting data model.
     */
    protected abstract void createResultingDataModel();

    /** Gets concrete resulting data model.
     * @return concrete resulting data model.
     */
    public abstract DataModel getResult();

    /**
     * Gets resulting data model abstract named object.
     * @return resulting data model abstract named object.
     */
    protected abstract AbstractNamedObject getNamedDataModel();

    /** Sets properties of a data model. */
    protected void setupDataModelAttributes(Node modelNode) {
        setNamedObjectAttributes(modelNode, getNamedDataModel());
    }

    /** Template of the data model object creation. */
    @Override
    public void buildDataModel() {

        // Load all necessary information to reconstruct objects from a data model
        loadKeywords(selectNodes("//o:Keyword[@Id]", model));
        loadCompositeModelObjects(selectNodes(compositeObjectsXPath, model));
        loadExternalShortcuts(selectNodes("//o:Shortcut[@Id]", model));
        loadUserDefinedMappings(selectNodes("//o:DefaultObjectMapping[@Id]", model));

        // Create the resulting the data model, set its metadata and underlying objects
        createResultingDataModel();
        setupDataModelAttributes(model);
        buildResultingDataModel();
    }

    /**
     * Loads objects used for user defined mappings to idToShortcut.
     * @param shortcutNodes the nodes defining the shortcuts.
     */
    protected void loadExternalShortcuts(List<Node> shortcutNodes) {
        for (Node shortcutNode : shortcutNodes) {
            String id = getNodeIdAttribute(shortcutNode);
            String globalId = getXPathValue(shortcutNode, "a:TargetID");
            idToShortcutsGlobalId.put(id, globalId);
        }
    }

    /**
     * Create user defined mappings on the objects.
     * @param mappingDefinitionNodes DOM nodes definitions of mappings.
     */
    protected void loadUserDefinedMappings(List<Node> mappingDefinitionNodes) {
        for (Node mappingDefinitionNode : mappingDefinitionNodes) {
            processCompositeMapping(mappingDefinitionNode);
        }
    }

    /**
     * Gets target and source composite objects taking part in the mapping and links them.
     * @param mappingDefinitionNode the DOM node representing a mapping between composite objects.
     */
    private void processCompositeMapping(Node mappingDefinitionNode) {

        Node targetNode = mappingDefinitionNode.selectSingleNode(compositeMappingTargetXPath);
        // Internal target
        AbstractCompositeModelObject target = idToComposite.get(getNodeRefAttribute(targetNode));
        // External sources
        mapWithExternalObjects(mappingDefinitionNode, "c:SourceClassifiers/o:Shortcut[@Ref]", target);

        // If descendents of the pair are linked as well, process it
        processSimpleMappings(
                selectNodes("c:StructuralFeatureMaps/o:DefaultStructuralFeatureMapping", mappingDefinitionNode));
    }

    /**
     * Gets pairs of simple objects taking part in the mapping and links them.
     * @param mappingDefinitionNodes the DOM node representing a mapping between simple objects.
     */
    private void processSimpleMappings(List<Node> mappingDefinitionNodes) {
        for (Node mappingDefinitionNode : mappingDefinitionNodes) {

            Node targetNode = mappingDefinitionNode.selectSingleNode(simpleMappingTargetXPath);
            // Internal target
            AbstractSimpleModelObject target = idToSimple.get(getNodeRefAttribute(targetNode));
            // External sources
            mapWithExternalObjects(mappingDefinitionNode, "c:SourceFeatures/o:Shortcut[@Ref]", target);
        }
    }

    /**
     * Links the internal object with external counterparts that are mapped to it.
     * @param mappingDefinitionNode the definition of mapping
     * @param externalMappedObjectXPath xpath to external targets
     * @param internalObject the internal object to be mapped with the external objects.
     */
    private void mapWithExternalObjects(Node mappingDefinitionNode, String externalMappedObjectXPath,
            AbstractMappableNamedObject internalObject) {
        List<Node> externalObjectNodes = selectNodes(externalMappedObjectXPath, mappingDefinitionNode);

        for (Node externalObject : externalObjectNodes) {
            // Resolve a shortcut standing for the source
            String sourceGlobalId = idToShortcutsGlobalId.get(getNodeRefAttribute(externalObject));
            internalObject.addMapping(sourceGlobalId);
        }
    }

    /**
     * Appends children to composite objects.
     * @param simpleNodes DOM nodes of children simple objects.
     * @param parent the parent composite objects, predecessor of the children.
     */
    protected void loadChildrenSimpleModelObjects(List<Node> simpleNodes, AbstractCompositeModelObject parent) {

        for (Node node : simpleNodes) {
            loadChildrenSimpleModelObject(node, parent);
        }
    }

    /**
     * Creates child node and appends it to its parent right ahead.
     * @param simpleNode DOM node of the simple object to be created.
     * @param parent the composite object, predecessor of the child.
     */
    protected void loadChildrenSimpleModelObject(Node simpleNode, AbstractCompositeModelObject parent) {

        S simple = createSimpleModelObject();
        setNamedObjectAttributes(simpleNode, simple);
        parent.addSubObject(simple);

        idToSimple.put(getNodeIdAttribute(simpleNode), simple);
    }

    /**
     * Loads all keywords from DOM nodes to idToKeyword.
     * @param keywordNodes DOM nodes defining the keywords.
     */
    private void loadKeywords(List<Node> keywordNodes) {
        for (Node keywordNode : keywordNodes) {
            idToKeyword.put(getNodeIdAttribute(keywordNode), getXPathValue(keywordNode, "a:Name"));
        }
    }

    /**
     * Sets metadata about the named object from its DOM node representation.
     * @param node the DOM node representing the object.
     * @param namedObject the object to be set.
     */
    protected void setNamedObjectAttributes(Node node, AbstractNamedObject namedObject) {
        namedObject.setName(getXPathValue(node, "a:Name"));
        namedObject.setCode(getXPathValue(node, "a:Code"));
        namedObject.setObjectId(getXPathValue(node, "a:ObjectID"));
        namedObject.setComment(getXPathValue(node, "a:Comment"));

        // Attributes and definitions are formatted in RTF. Strip it to plain text.
        namedObject.setAnnotation(rtfTools.rtfToPlain(getXPathValue(node, "a:Annotation")));
        namedObject.setDefinition(rtfTools.rtfToPlain(getXPathValue(node, "a:Description")));

        // Get IDs of external object from which the namedObject was derived.
        namedObject.setHistory(parseHistory(getXPathValue(node, "a:History")));
        namedObject.setKeywords(parseKeywords(selectNodes("c:AttachedKeywords/o:Keyword[@Ref]", node)));
    }

    /**
     * Resolves keywords referenced from the DOM nodes.
     * @param keywordsRefNodes DOM nodes containing references to keywords.
     * @return set of strings that are resolved keywords. Non-null.
     */
    private Set<String> parseKeywords(List<Node> keywordsRefNodes) {
        Set<String> result = new HashSet<>();

        for (Node keywordRefNode : keywordsRefNodes) {
            result.add(idToKeyword.get(getNodeRefAttribute(keywordRefNode)));
        }
        return result;
    }

    /**
     * Retrieves ObjectIDs of the external objects that are referenced in the history string.
     * @param history the text containing, among other, references to external ObjectIDs.
     * @return ObjectIDs of external objects.
     */
    private Set<String> parseHistory(String history) {
        Set<String> result = new HashSet<>();

        if (history != null) {
            // ObjectIDs of the external objects are inside curly brackets separated by comma
            String patternString = "(\\{.+\\})+";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(history);

            while (matcher.find()) {
                // An object may have multiple origins, in the case they are separated by commas
                // As the regex is greedy, it will match them all
                String[] ids = matcher.group().split(",");
                // Strip the brackets from an ID and add it to the history
                Arrays.stream(ids).forEach(x -> result.add(x.substring(1, x.length() - 1)));
            }
        }

        return result;
    }

    /**
     * Get DOM node's attribute containing an ID that is local to a data model file.
     * @param node the node to get the ID attribute from.
     * @return the value of local ID.
     */
    protected String getNodeIdAttribute(Node node) {
        return getXPathValue(node, "@Id");
    }

    /**
     * Get DOM node's attribute containing a reference to local ID
     * @param node the node to get the ID attribute from.
     * @return the value of local ID.
     */
    protected String getNodeRefAttribute(Node node) {
        return getXPathValue(node, "@Ref");
    }

    /**
     * Gets the textual result of the XPath expression on the node.
     * @param node the DOM node to execute the expression on.
     * @param xPath the XPath expression to execute.
     * @return the textual result of the expression.
     */
    protected String getXPathValue(Node node, String xPath) {
        return node.valueOf(xPath);
    }

    /**
     * Retrieves the nodes chosen by the XPath expresion on the given node.
     * @param node the DOM node to execute the expression on.
     * @param xPath the XPath expression to execute.
     * @return the nodes chosen by the XPath expresion on the node.
     */
    protected List<Node> selectNodes(String xPath, Node node) {
        return node.selectNodes(xPath);
    }

    /**
     * Retrieves the DOM node chosen by the XPath expresion on the given node.
     * @param node the DOM node to execute the expression on.
     * @param xPath the XPath expression to execute.
     * @return the node chosen by the XPath expresion on the given node.
     */
    protected Node selectSingleNode(String xPath, Node node) {
        return node.selectSingleNode(xPath);
    }
}
