package eu.profinit.manta.connector.erstudio.model.builder;

import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml.XmlComposite;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml.XmlErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml.XmlModel;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml.XmlSimple;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;

/**
 * SAX Parser of objects saved in an external solution.
 * Stateless singleton.
 *
 * @author ddrobny
 */
public class XmlObjectParser {
    /* Attributes of object element, that is XML representation of external object */
    /* For XmlErStudioModel */
    private static final String ERSTUDIOMODEL_NAME_XML_ATTRIBUTE = "Name";
    private static final String ERSTUDIOMODEL_FILE_NAME_XML_ATTRIBUTE = "File_Name";
    /* For XmlModel */
    private static final String MODEL_NAME_XML_ATTRIBUTE = "Name";
    private static final String MODEL_TYPE_XML_ATTRIBUTE = "Model_Type";
    private static final String MODEL_PLATFORM_XML_ATTRIBUTE = "Platform";
    /* For XmlComposite */
    private static final String COMPOSITE_ENTITY_NAME_XML_ATTRIBUTE = "Name";
    private static final String COMPOSITE_TABLE_NAME_XML_ATTRIBUTE = "Table_Name";
    private static final String COMPOSITE_OWNER_XML_ATTRIBUTE = "Owner";
    private static final String COMPOSITE_DEFINITION_XML_ATTRIBUTE = "Definition";
    private static final String COMPOSITE_ENTITY_TYPE_XML_ATTRIBUTE = "Entity_Type";
    /* For XmlSimple */
    private static final String SIMPLE_ATTRIBUTE_NAME_XML_ATTRIBUTE = "Name";
    private static final String SIMPLE_COLUMN_NAME_XML_ATTRIBUTE = "Column_Name";
    private static final String SIMPLE_DEFINITION_XML_ATTRIBUTE = "Definition";
    private static final String SIMPLE_PRIMARY_KEY_XML_ATTRIBUTE = "Primary_Key";
    /** XPATH of an object in XML structure of externally mapped objects */
    private static final String XPATH_OBJECT = "/objects/object";
    private static Logger LOGGER = LoggerFactory.getLogger(XmlObjectParser.class);
    private static XmlObjectParser ourInstance = new XmlObjectParser();

    private XmlObjectParser() {
    }

    public static XmlObjectParser getInstance() {
        return ourInstance;
    }

    /**
     * Gets attributes of an external solution based on XML.
     * @param xmlDefinition the XML description of a solution.
     * @return the attributes of an external solution.
     */
    public XmlErStudioSolution parseXmlErStudioSolution(String xmlDefinition) {
        SAXReader saxReader = new SAXReader();
        XmlErStudioSolution result = new XmlErStudioSolution();
        saxReader.addHandler(XPATH_OBJECT, new ElementHandler() {
            @Override
            public void onStart(ElementPath elementPath) {
                Element current = elementPath.getCurrent();
                result.setName(current.attributeValue(ERSTUDIOMODEL_NAME_XML_ATTRIBUTE));
                result.setFileName(current.attributeValue(ERSTUDIOMODEL_FILE_NAME_XML_ATTRIBUTE));
            }

            @Override
            public void onEnd(ElementPath elementPath) {
                elementPath.getCurrent().detach();
            }
        });
        try {
            saxReader.read(new StringReader(xmlDefinition));
        } catch (DocumentException e) {
            LOGGER.error("Couldn't parse properly the ErStudio File defined on line {}", xmlDefinition);
        }
        return result;
    }

    /**
     * Gets the attributes of a data model based on XML.
     * @param xmlModelDefinition the XML representing a data model.
     * @return the attributes of a data model.
     */
    public XmlModel parseXmlModel(String xmlModelDefinition) {
        SAXReader saxReader = new SAXReader();
        XmlModel result = new XmlModel();
        saxReader.addHandler(XPATH_OBJECT, new ElementHandler() {
            @Override
            public void onStart(ElementPath elementPath) {
                Element current = elementPath.getCurrent();
                result.setModelType(Integer.parseInt(current.attributeValue(MODEL_TYPE_XML_ATTRIBUTE)));
                result.setName(current.attributeValue(MODEL_NAME_XML_ATTRIBUTE));
                result.setPlatform(Integer.parseInt(current.attributeValue(MODEL_PLATFORM_XML_ATTRIBUTE)));
            }

            @Override
            public void onEnd(ElementPath elementPath) {
                elementPath.getCurrent().detach();
            }
        });
        try {
            saxReader.read(new StringReader(xmlModelDefinition));
        } catch (DocumentException e) {
            LOGGER.error("Couldn't parse properly the DataModel defined on line {}", xmlModelDefinition);
        }
        return result;
    }

    /**
     * Gets the attributes of a composite object based on XML.
     * @param xmlCompositeObjectDefinition the XML representing a composite object.
     * @return the attributes of composite object.
     */
    public XmlComposite parseXmlComposite(String xmlCompositeObjectDefinition) {
        SAXReader saxReader = new SAXReader();
        XmlComposite result = new XmlComposite();
        saxReader.addHandler(XPATH_OBJECT, new ElementHandler() {
            @Override
            public void onStart(ElementPath elementPath) {
                Element current = elementPath.getCurrent();
                result.setDefinition(current.attributeValue(COMPOSITE_DEFINITION_XML_ATTRIBUTE));
                result.setEntityName(current.attributeValue(COMPOSITE_ENTITY_NAME_XML_ATTRIBUTE));
                result.setTableName(current.attributeValue(COMPOSITE_TABLE_NAME_XML_ATTRIBUTE));
                result.setEntityType(current.attributeValue(COMPOSITE_ENTITY_TYPE_XML_ATTRIBUTE));
                result.setOwnerName(current.attributeValue(COMPOSITE_OWNER_XML_ATTRIBUTE));
            }

            @Override
            public void onEnd(ElementPath elementPath) {
                elementPath.getCurrent().detach();
            }
        });
        try {
            saxReader.read(new StringReader(xmlCompositeObjectDefinition));
        } catch (DocumentException e) {
            LOGGER.error("Couldn't parse properly the CompositeDataObject defined on line {}",
                    xmlCompositeObjectDefinition);
        }
        return result;
    }

    /**
     * Gets the attributes of a simple object based on XML.
     * @param xmlCSimpleObjectDefinition the XML representing a simple object.
     * @return the attributes of a simple object.
     */
    public XmlSimple parseXmlSimple(String xmlCSimpleObjectDefinition) {
        SAXReader saxReader = new SAXReader();
        XmlSimple result = new XmlSimple();
        saxReader.addHandler(XPATH_OBJECT, new ElementHandler() {
            @Override
            public void onStart(ElementPath elementPath) {
                Element current = elementPath.getCurrent();
                result.setDefinition(current.attributeValue(SIMPLE_DEFINITION_XML_ATTRIBUTE));
                result.setAttributeName(current.attributeValue(SIMPLE_ATTRIBUTE_NAME_XML_ATTRIBUTE));
                result.setColumnName(current.attributeValue(SIMPLE_COLUMN_NAME_XML_ATTRIBUTE));
                result.setPrimaryKey(current.attributeValue(SIMPLE_PRIMARY_KEY_XML_ATTRIBUTE));
            }

            @Override
            public void onEnd(ElementPath elementPath) {
                elementPath.getCurrent().detach();
            }
        });
        try {
            saxReader.read(new StringReader(xmlCSimpleObjectDefinition));
        } catch (DocumentException e) {
            LOGGER.error("Couldn't parse properly the SimpleDataObject defined on line {}", xmlCSimpleObjectDefinition);
        }
        return result;
    }
}
