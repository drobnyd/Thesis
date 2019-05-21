package eu.profinit.manta.connector.powerdesigner.resolver.build;

import eu.profinit.manta.connector.powerdesigner.model.Layer;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class selecting a specific data model builder.
 *
 * @author ddrobny
 */
public class BuilderFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(BuilderFactory.class);

    /**
     * Creates a corresponding data model builder for the given data model file.
     * @param filename path to data model whose builder is requested.
     * @param root XML root element.
     * @return the specific data model builder.
     */
    public DataModelBuilder getBuilder(String filename, Element root) {
        switch (Layer.getLayer(filename)) {
        case CONCEPTUAL:
            return new ConceptualDataModelBuilder(filename, root);
        case LOGICAL:
            return new LogicalDataModelBuilder(filename, root);
        case PHYSICAL:
            return new PhysicalDataModelBuilder(filename, root);
        default:
            LOGGER.error( "File extension {} not supported.",FilenameUtils.getExtension(filename));
            return null;
        }
    }
}
