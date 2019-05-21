package eu.profinit.manta.connector.powerdesigner.resolver;

import eu.profinit.manta.connector.xml.XmlFileReader;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConceptualDataModelCreationTest {
    private Element root;

    public ConceptualDataModelCreationTest() throws IOException {
        XmlFileReader xmlFileReader = new XmlFileReader();
        root = (Element) xmlFileReader.readFile(new File("src/test/resources/project.cdm"));
    }

    @Test
    public void loadDiagrams() {
        List<Node> diagrams = root.selectNodes("//c:ConceptualDiagrams");
        diagrams.size();
    }

    @Test
    public void loadCompositeModelObjects() {
        List<Node> entities = root.selectNodes("/Model/o:RootObject/c:Children/o:Model/c:Entities/o:Entity");
    }

    @Test
    public void loadSimpleModelObjects() {
    }

    @Test
    public void loadGenerationEdges() {
    }
}