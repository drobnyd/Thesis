package eu.profinit.manta.connector.powerdesigner;

import eu.profinit.manta.connector.powerdesigner.model.DataModel;
import eu.profinit.manta.connector.powerdesigner.resolver.build.BuilderFactory;
import eu.profinit.manta.connector.powerdesigner.resolver.build.DataModelBuilder;
import eu.profinit.manta.connector.powerdesigner.sax.TargetSAXHandler;
import eu.profinit.manta.connector.xml.XmlFileReader;
import eu.profinit.manta.platform.automation.AbstractIOBean;
import eu.profinit.manta.platform.automation.InputReader;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reader reading a file or directory creating processing PowerDesigner XML data models.
 * Creates logical components from the given data models and transforms one by one to set of DataModel objects.
 *
 * @author ddrobny
 */
public class PowerDesignerXmlComponentReader extends AbstractIOBean implements InputReader<List<DataModel>> {
    /** SL4J logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerDesignerXmlComponentReader.class);
    /** Filter accepting only supported PowerDesigner data models. */
    private FileFilter filter = new PowerDesignerFileFilter();
    /** XML files reader */
    private final XmlFileReader xmlFileReader = new XmlFileReader();
    /** Directory or file to process. */
    private File inputFile;
    /** File comparator */
    private Comparator<File> comparator;
    /** Iterator on logically grouped data models. */
    private Iterator<List<File>> componentIterator;
    /** Set of data models currently being processed. */
    private List<File> currentFileComponent;

    /**
     * Sets the file filter.
     * @param filter the file filter to set.
     */
    public void setFilter(FileFilter filter){
        this.filter = filter;
    }

    /**
     * Gets the input file.
     * @return the input file.
     */
    public File getInputFile() {
        return inputFile;
    }

    /**
     * Sets the input file
     * @param inputFile the input file to set.
     */
    public void setInputFile(final File inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Gets the file filter.
     * @return the file filter.
     */
    public FileFilter getFilter() {
        return filter;
    }

    /**
     * Gets the file comparator.
     * @return the file comparator.
     */
    public Comparator<File> getComparator() {
        return comparator;
    }

    /**
     * Sets the file comparator.
     * @param comparator the file comparator.
     */
    public void setComparator(final Comparator<File> comparator) {
        this.comparator = comparator;
    }

    /**
     * If  {@code file} is a directory, collects the files in it recursively.
     * If it is a file, collects it as far as it is not filtered out and adds it with its dependencies to a dependency graph.
     * @param file the file to inspect.
     * @param dependencyGraph graph of model dependencies with bidirectional edges.
     * @return current state of the graph of model dependencies
     */
    protected Map<File, Set<File>> collectFiles(final File file, Map<File, Set<File>> dependencyGraph) {
        if (file.isFile()) {
            dependencyGraph.putIfAbsent(file, new HashSet<>());
            Set<File> linkedFiles = getTargetFiles(file);
            // Link with all its targets
            dependencyGraph.get(file).addAll(linkedFiles);
            // Create links in the other direction - link every target to this file
            for (File linkedFile : linkedFiles) {
                dependencyGraph.putIfAbsent(linkedFile, new HashSet<>());
                dependencyGraph.get(linkedFile).add(file);
            }
            return dependencyGraph;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles(filter);
            if (files != null) {
                if (comparator != null) {
                    Arrays.sort(files, comparator);
                }
                for (File f : files) {
                    collectFiles(f, dependencyGraph);
                }
            } else {
                throw new IllegalArgumentException(MessageFormat
                        .format("A reading error occurred during listing directory \"{0}\"", file.getAbsolutePath()));
            }
        } else {
            LOGGER.warn(MessageFormat
                    .format("Path \"{0}\" does not contain existing file nor directory.", file.getAbsoluteFile()));
        }

        return dependencyGraph;
    }

    /**
     * Gets dependencies of the given file.
     * @param file file to inspect.
     * @return dependencies of the given file. Non-null.
     */
    private Set<File> getTargetFiles(File file) {
        Set<File> result = new HashSet<>();
        try {
            // Scan quickly to find the dependencies with a SAX reader
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            TargetSAXHandler saxHandler = new TargetSAXHandler();
            saxParser.parse(file, saxHandler);

            for (URI linkedFileURI : saxHandler.getTargetURIs()) {
                File toAdd = resolveFile(linkedFileURI);
                if (toAdd != null) {
                    result.add(toAdd);
                }
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            LOGGER.warn("Could not parse the input file. \"{}\"", e.getMessage());
        }
        return result;
    }

    /**
     * Tries to find the file described by URI in the input directory.
     * @param searchedFileURI the uri to data model that is being searched for.
     * @return the searched file or the most similar one from input, must have at least the same name.
     * If none is found, {@code null}
     */
    private File resolveFile(URI searchedFileURI) {
        Path inputBase = inputFile.toPath();
        Path searchedPath = Paths.get(searchedFileURI);

        // The path is correct and searched file is contained in the input dir
        if (searchedPath.startsWith(inputBase) && searchedPath.toFile().exists()) {
            return searchedPath.toFile();
        }

        // Target file path is not in the input, may be obsolete try to find it in the input
        for (int i = 0; i < searchedPath.getNameCount(); i++) {
            // Trying to match the longest possible sequence
            Path subPath = searchedPath.subpath(i, searchedPath.getNameCount());
            Path candidatePath = inputBase.resolve(subPath);

            if (candidatePath.toFile().exists()) {
                return candidatePath.toFile();
            }
        }

        LOGGER.warn("File \"{}\" that is being referenced is not reachable, external objects won't be loaded.",
                searchedPath);

        return null;
    }

    /**
     * Creates connected components from a graph of dependencies.
     * @param dependencyGraph the graph of dependencies - bidirectional/undirected/
     * @return connected components of data models based on dependencies.
     */
    private List<List<File>> createGraphComponents(Map<File, Set<File>> dependencyGraph) {
        List<List<File>> result = new ArrayList<>();

        Set<File> unvisited = new HashSet<>(dependencyGraph.keySet());

        while (!unvisited.isEmpty()) {
            File file = unvisited.iterator().next();
            result.add(createComponent(file, unvisited, dependencyGraph));
        }

        return result;
    }

    /**
     * Creates a connected component of the {@code currentFile} by doing a graph DFS.
     * @param currentFile node of which component will be created.
     * @param unvisited nodes waiting to be discovered.
     * @param dependencyGraph full graph of dependencies between data model file from input.
     * @return
     */
    private List<File> createComponent(File currentFile, Set<File> unvisited, Map<File, Set<File>> dependencyGraph) {
        List<File> component = new ArrayList<>();
        // Visit all reachable files recursively, these have to be in component.
        extendComponent(currentFile, component, unvisited, dependencyGraph);

        return component;
    }

    /**
     * Discovers the rest of component by DFS.
     * Adds the files seen to the {@code component} while removes them from {@code unvisited}.
     * @param file current node through which doing DFS.
     * @param component component being created.
     * @param unvisited nodes waiting to be discovered.
     * @param dependencyGraph full graph of dependencies between data model file from input.
     */
    private void extendComponent(File file, List<File> component, Set<File> unvisited,
            Map<File, Set<File>> dependencyGraph) {
        // Mark visited
        unvisited.remove(file);
        // Add to current component
        component.add(file);

        for (File neighbor : dependencyGraph.get(file)) {
            if (unvisited.contains(neighbor)) {
                // DFS and add to the came component
                extendComponent(neighbor, component, unvisited, dependencyGraph);
            }
        }
    }

    /**
     * Loads a single data model file and creates a DataModel instance out of it.
     * @param file the file to be scanned.
     * @return instance of DataModel created from the file
     * @throws IOException
     */
    protected DataModel readFile(File file) throws IOException {
        Element root = (Element) xmlFileReader.readFile(file);
        BuilderFactory factory = new BuilderFactory();
        DataModelBuilder builder = factory.getBuilder(file.getName(), root);
        if (builder == null) {
            return null;
        }

        builder.buildDataModel();
        return builder.getResult();
    }

    @Override
    public boolean canRead() {
        open();
        return componentIterator.hasNext();
    }

    @Override
    public List<DataModel> read() {
        open();
        List<DataModel> result = new ArrayList<>();
        currentFileComponent = componentIterator.next();
        for (File file : currentFileComponent) {
            try {
                result.add(readFile(file));
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return result;
    }

    private void open() {
        if (componentIterator == null) {
            if (inputFile.exists()) {
                componentIterator = createGraphComponents(collectFiles(inputFile, new HashMap<>())).iterator();
            } else {
                LOGGER.warn("Input file {} doesn't exist.", inputFile.getAbsolutePath());
                componentIterator = Collections.<List<File>>emptyList().iterator();
            }
        }
    }

    @Override
    public void close() {
        componentIterator = null;
        currentFileComponent = null;
    }

    @Override
    public String getInputName() {
        if (inputFile != null) {
            // Get name of each file in current component separated by comma
            return currentFileComponent.stream().map(x -> x.getName()).sorted().collect(Collectors.joining(", "));
        } else {
            throw new IllegalStateException(
                    "It is not possible to get the current" + "input name before the first reading.");
        }
    }

}

