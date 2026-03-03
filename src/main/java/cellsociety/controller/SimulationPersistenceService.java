package cellsociety.controller;

import cellsociety.Model.Grid.ViewGrid;
import cellsociety.Model.Simulation.factory.SimulationFactory;
import cellsociety.Model.Simulation.model.SimulationModel;
import cellsociety.config.ConfigLoader;
import cellsociety.config.ConfigSaver;
import cellsociety.config.SimulationConfig;
import cellsociety.config.SimulationLoader;
import cellsociety.config.SimulationPreferences;
import cellsociety.config.SimulationSaver;
import cellsociety.config.SimulationState;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import cellsociety.view.constants.ResourcePaths;

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

  /**
   * Loads one config file and installs it into the target slot.
   */
  public void loadConfigIntoSlot(int slotIndex, File xmlFile, List<SimulationSlot> slots,
      Optional<String> selectedNeighborChoice, NeighborStrategyFactory neighborStrategyFactory)
      throws ParserConfigurationException, IOException, SAXException {
    checkSlotIndex(slots, slotIndex);
    SimulationConfig config = loadConfig(xmlFile);
    SimulationModel sim = SimulationFactory.createSimulation(config);
    SimulationSlot slot = slots.get(slotIndex);
    slot.setSimulation(sim);
    slot.setConfig(config);
    selectedNeighborChoice.ifPresent(choice -> neighborStrategyFactory.applyToSlot(slot, choice));
  }

  /**
   * Loads full app state file and applies all contained simulations to slots.
   *
   * @return loaded simulation preferences from the state file
   */
  public SimulationPreferences loadStateIntoSlots(File xmlFile, List<SimulationSlot> slots,
      IntConsumer setSlotCount, Consumer<Integer> setSlotChoice, Optional<String> selectedNeighborChoice,
      NeighborStrategyFactory neighborStrategyFactory)
      throws ParserConfigurationException, IOException, SAXException {
    SimulationState desiredState = loadState(xmlFile);
    int activeSims = desiredState.getActiveSimulations().size();
    setSlotCount.accept(activeSims);
    setSlotChoice.accept(activeSims);

    for (int i = 0; i < slots.size(); i++) {
      SimulationSlot currentSlot = slots.get(i);
      SimulationConfig currentConfig = desiredState.getActiveSimulations().get(i);
      SimulationModel currentSim = SimulationFactory.createSimulation(currentConfig);
      currentSlot.setSimulation(currentSim);
      currentSlot.setConfig(currentConfig);
      selectedNeighborChoice.ifPresent(choice -> neighborStrategyFactory.applyToSlot(currentSlot, choice));
    }
    return desiredState.getPreferences();
  }

  /**
   * Saves one slot's current grid snapshot as a config file.
   */
  public void saveConfigForSlot(int slotIndex, List<SimulationSlot> slots, File saveDestination) {
    checkSlotIndex(slots, slotIndex);
    SimulationSlot slot = slots.get(slotIndex);
    if (!slot.hasSimulation()) {
      throw new IllegalStateException("No simulation loaded for slot " + slotIndex);
    }
    SimulationConfig baseConfig = slot.getConfig();
    ViewGrid grid = slot.getPresenter().getDriver().getSimulation().getViewGrid();
    saveConfigSnapshot(baseConfig, grid, saveDestination);
  }

  /**
   * Builds and saves the full application state for all loaded slots.
   */
  public void saveAllState(List<SimulationSlot> slots, String themeChoice, String languageChoice,
      File saveDestination) {
    List<SimulationConfig> snapshots = buildSimulationSnapshots(slots);
    SimulationPreferences preferences = buildPreferences(themeChoice, languageChoice, slots);
    saveState(snapshots, preferences, saveDestination);
  }

  public List<SimulationConfig> buildSimulationSnapshots(List<SimulationSlot> slots) {
    List<SimulationConfig> active = new ArrayList<>();
    for (SimulationSlot slot : slots) {
      if (!slot.hasSimulation()) continue;
      SimulationConfig base = slot.getConfig();
      ViewGrid grid = slot.getPresenter().getDriver().getSimulation().getViewGrid();
      active.add(buildConfigSnapshot(base, grid));
    }
    return active;
  }

  public SimulationPreferences buildPreferences(String themeChoice, String languageChoice,
      List<SimulationSlot> slots) {
    int theme = mapThemeToCode(themeChoice);
    double speed = getReferenceSpeed(slots);
    String language = languageChoice;
    if (language == null || language.isBlank()) {
      language = ResourcePaths.LANGUAGE_ENGLISH;
    }
    return new SimulationPreferences(theme, language, speed, new HashMap<Integer, String>());
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

  private void checkSlotIndex(List<SimulationSlot> slots, int slotIndex) {
    if (slotIndex < 0 || slotIndex >= slots.size()) {
      throw new IndexOutOfBoundsException("Invalid slot index: " + slotIndex);
    }
  }

  private int mapThemeToCode(String themeChoice) {
    if (ResourcePaths.THEME_DARK.equals(themeChoice)) return 1;
    if (ResourcePaths.THEME_DUKE.equals(themeChoice)) return 2;
    return 0;
  }

  private double getReferenceSpeed(List<SimulationSlot> slots) {
    if (!slots.isEmpty() && slots.get(0).hasSimulation()) {
      return slots.get(0).getStepsPerSecond();
    }
    for (SimulationSlot slot : slots) {
      if (slot.hasSimulation()) {
        return slot.getStepsPerSecond();
      }
    }
    return 1.0;
  }
}
