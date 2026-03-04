package cellsociety.controller.services;

import cellsociety.Model.Grid.ViewGrid;
import cellsociety.config.ConfigSaver;
import cellsociety.config.SimulationConfig;
import cellsociety.config.SimulationPreferences;
import cellsociety.config.SimulationSaver;
import cellsociety.config.SimulationState;
import cellsociety.controller.multiview.SimulationSlot;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cellsociety.view.constants.ResourcePaths;

/**
 * Service-layer class for simulation saving workflows.
 */
public class SimulationSavingService {

  private final ConfigSaver myConfigSaver;
  private final SimulationSaver mySimulationSaver;

  public SimulationSavingService() {
    myConfigSaver = new ConfigSaver();
    mySimulationSaver = new SimulationSaver();
  }

  public SimulationSavingService(ConfigSaver configSaver, SimulationSaver simulationSaver) {
    myConfigSaver = configSaver;
    mySimulationSaver = simulationSaver;
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
    return new SimulationPreferences(theme, language, speed);
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
