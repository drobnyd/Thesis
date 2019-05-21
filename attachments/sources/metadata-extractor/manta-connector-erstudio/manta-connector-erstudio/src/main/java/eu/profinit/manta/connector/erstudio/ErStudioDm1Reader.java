package eu.profinit.manta.connector.erstudio;

import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.connector.erstudio.model.builder.ErStudioFileModelBuilder;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;
import eu.profinit.manta.connector.erstudio.parser.ErStudioFileFormatParser;
import eu.profinit.manta.platform.automation.AbstractFileInputReader;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Reader of ER/Studio data models.
 *
 * @author ddrobny
 */
public class ErStudioDm1Reader extends AbstractFileInputReader<ErStudioFileModel> {

    private String encoding = "UTF-8";

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    protected ErStudioFileModel readFile(File file) throws IOException {
        ErStudioFileFormatParser parser = new ErStudioFileFormatParser(encoding);
        Map<String, CsvTable> tables = parser.readFile(file);
        ErStudioFileModelBuilder builder = new ErStudioFileModelBuilder(tables, file.getName());
        builder.build();
        return builder.getErStudioFileModel();
    }
}