package eu.profinit.manta.connector.erstudio.model.impl;

import eu.profinit.manta.connector.erstudio.model.ErStudioFileModel;
import eu.profinit.manta.connector.erstudio.model.ErStudioSolution;
import eu.profinit.manta.connector.erstudio.model.csv.CsvTable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Implementation of ErStudioFileModel
 *
 * @author ddrobny
 */
public class ErStudioFileModelImpl implements ErStudioFileModel {
    private String fileName;
    private Map<String, CsvTable> csvTables;
    private ErStudioSolution internalErStudioSolution;
    private Map<String, ErStudioSolution> externalErStudioModels;

    public ErStudioFileModelImpl(String fileName) {
        this.fileName = fileName;
    }

    public void setCsvTables(Map<String, CsvTable> csvTables) {
        this.csvTables = csvTables;
    }

    @Override
    public CsvTable getCsvTable(String tableName) {
        return csvTables.get(tableName);
    }

    @Override
    public Map<String, CsvTable> getAllCsvTables() {
        return Collections.unmodifiableMap(csvTables);
    }

    @Override
    public Collection<ErStudioSolution> getAllExternalErStudioSolutions() {
        return Collections.unmodifiableCollection(externalErStudioModels.values());
    }

    @Override
    public ErStudioSolution getExternalErStudioSolution(String fileName) {
        return externalErStudioModels.get(fileName);
    }

    public void setExternalErStudioModels(Map<String, ErStudioSolution> externalErStudioModels) {
        this.externalErStudioModels = externalErStudioModels;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public ErStudioSolution getInternalErStudioSolution() {
        return internalErStudioSolution;
    }

    public void setInternalErStudioSolution(ErStudioSolution internalErStudioSolution) {
        this.internalErStudioSolution = internalErStudioSolution;
    }
}
