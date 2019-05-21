package eu.profinit.manta.connector.powerdesigner.sax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * Handler for obtaining URI of dependencies from a PowerDesigner XML data model.
 *
 * @author ddrobny
 */
public class TargetSAXHandler extends DefaultHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TargetSAXHandler.class);

    /** String value of the listened element */
    private String elementValue;

    /** URIs of dependencies of the scanned data model */
    private Set<URI> targetURIs = new HashSet<>();

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("a:TargetModelURL")) {
            try {
                // For some reason, PowerDesigner transforms ':' in paths to '|', change it back
                String targetPath = elementValue.replace("|", ":");
                targetURIs.add(new URI(targetPath));
            } catch (URISyntaxException e) {
                LOGGER.warn("Could not resolve dependency on file \"{}\"", e.getInput());
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        elementValue = new String(ch, start, length);
    }

    /**
     * Gets URIs of dependencies of the scanned data model.
     * @return URIs of dependencies of the scanned data model. Not-null.
     */
    public Set<URI> getTargetURIs() {
        return targetURIs;
    }
}
