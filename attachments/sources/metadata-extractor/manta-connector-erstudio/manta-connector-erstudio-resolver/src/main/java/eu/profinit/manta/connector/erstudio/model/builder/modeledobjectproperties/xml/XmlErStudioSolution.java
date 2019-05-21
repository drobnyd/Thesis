package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml;

/**
 * Set of properties that an ER/Studio solution has if represented as XML.
 *
 * @author ddrobny
 */
public class XmlErStudioSolution {
    /** Name of the file the model is stored in */
    private String fileName;
    /** Name of the ErStudioSolution */
    private String name;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
