package cellsociety.controller;

import cellsociety.Model.Grid.ViewGrid;
import cellsociety.config.ConfigLoader;
import cellsociety.config.ConfigSaver;
import cellsociety.config.SimulationConfig;
import cellsociety.config.SimulationLoader;
import cellsociety.config.SimulationPreferences;
import cellsociety.config.SimulationSaver;
import cellsociety.config.SimulationState;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Service-layer class for simulation persistence workflows (load/save config and full app state).
 */
public class SimulationPersistenceService {

  private final ConfigLoader myConfigLoader;
  private final ConfigSaver myConfigSaver;
  private final SimulationLoader mySimulationLoader;
  private final SimulationSaver mySimulationSaver;

  public SimulationPersistenceService() {
    myConfigLoader = new ConfigLoader();
    myConfigSaver = new ConfigSaver();
    mySimulationLoader = new SimulationLoader();
    mySimulationSaver = new SimulationSaver();
  }

  public SimulationPersistenceService(ConfigLoader configLoader, ConfigSaver configSaver,
      SimulationLoader simulationLoader, SimulationSaver simulationSaver) {
    myConfigLoader = configLoader;
    myConfigSaver = configSaver;
    mySimulationLoader = simulationLoader;
    mySimulationSaver = simulationSaver;
  }

  public SimulationConfig loadConfig(File xmlFile)
      throws ParserConfigurationException, IOException, SAXException {
    if (xmlFile == null) {
      throw new IllegalArgumentException("No file selected.");
    }
    return myConfigLoader.loadConfig(xmlFile);
  }

  public SimulationState loadState(File xmlFile)
      throws ParserConfigurationException, IOException, SAXException {
    if (xmlFile == null) {
      throw new IllegalArgumentException("No file selected.");
    }
    return mySimulationLoader.loadState(xmlFile);
  }

  public void saveConfigSnapshot(SimulationConfig baseConfig, ViewGrid grid, File saveDestination) {
    if (saveDestination == null) {
      throw new IllegalArgumentException("No Save Destination Selected.");
    }
    myConfigSaver.saveConfig(buildConfigSnapshot(baseConfig, grid), saveDestination);
  }

  public void saveState(List<SimulationConfig> snapshots, SimulationPreferences preferences,
      File saveDestination) {
    if (saveDestination == null) {
      throw new IllegalArgumentException("No Save Destination Selected.");
    }
    SimulationState state = new SimulationState(snapshots, preferences);
    mySimulationSaver.saveState(state, saveDestination);
  }

  public SimulationConfig buildConfigSnapshot(SimulationConfig baseConfig, ViewGrid grid) {
    if (baseConfig == null) {
      throw new IllegalArgumentException("Base configuration is required.");
    }
    if (grid == null) {
      throw new IllegalArgumentException("Grid snapshot is required.");
    }

    int[][] states = ViewGrid.produceIntArray(grid);
    return new SimulationConfig(
        baseConfig.getSimulationType(),
        baseConfig.getRuleClass(),
        baseConfig.getNeighborStrategyClass(),
        baseConfig.getTitle(),
        baseConfig.getAuthor(),
        baseConfig.getDescription(),
        grid.getRows(),
        grid.getCols(),
        states,
        baseConfig.getParameters()
    );
  }
}
