package eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.xml;

import eu.profinit.manta.connector.erstudio.model.DataModel;
import eu.profinit.manta.connector.erstudio.model.builder.modeledobjectproperties.ModelCreationProperties;

import static eu.profinit.manta.connector.erstudio.utils.StringTranslator.processXmlString;

/**
 * Description of XML object that represents a {@link DataModel} coming from another .DM1 file.
 *
 * @author ddrobny
 */
public class XmlModel implements ModelCreationProperties {
    private String name;
    private int modelType;
    private int platform;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = processXmlString(name);
    }

    @Override
    public int getModelType() {
        return modelType;
    }

    public void setModelType(int modelType) {
        this.modelType = modelType;
    }

    @Override
    public int getPlatformId() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }
}
