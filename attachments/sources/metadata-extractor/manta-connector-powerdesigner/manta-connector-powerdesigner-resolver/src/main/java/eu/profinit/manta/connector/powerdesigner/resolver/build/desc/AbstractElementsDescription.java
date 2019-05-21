package eu.profinit.manta.connector.powerdesigner.resolver.build.desc;

/**
 * Container for names of selected elements of an XML data model. These are the elements that are being used
 * commonly in general algorithms.
 *
 * @author ddrobny
 */
public abstract class AbstractElementsDescription {
    private final String simpleObjectElementName;
    private final String compositeObjectElementName;
    private final String compositeObjectsXPath;

    public AbstractElementsDescription(String simpleObjectElementName,
            String compositeObjectElementName, String compositeObjectsXPath) {
        this.simpleObjectElementName = simpleObjectElementName;
        this.compositeObjectsXPath = compositeObjectsXPath;
        this.compositeObjectElementName = compositeObjectElementName;
    }

    /**
     * Gets the name of the XML element representing a simple object.
     * @return the name of the XML element representing a simple object.
     */
    public String getSimpleObjectElementName() {
        return simpleObjectElementName;
    }

    /**
     * Gets the XPath from model root to composite object element.
     * @return the XPath from model root to composite object element.
     */
    public String getCompositeObjectsXPath() {
        return compositeObjectsXPath;
    }

    /**
     * Gets the name of the XML element representing a composite object.
     * @return the name of the XML element representing a composite object.
     */
    public String getCompositeObjectElementName() {
        return compositeObjectElementName;
    }
}
