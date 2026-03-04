package cellsociety.controller.services;

import cellsociety.Model.Simulation.factory.SimulationFactory;
import cellsociety.Model.Simulation.model.SimulationModel;
import cellsociety.config.ColorMapProvider;
import cellsociety.config.ConfigLoader;
import cellsociety.config.SimulationConfig;
import cellsociety.config.SimulationLoader;
import cellsociety.config.SimulationPreferences;
import cellsociety.config.SimulationState;
import cellsociety.controller.multiview.SimulationSlot;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Service-layer class for simulation loading workflows.
 */
public class SimulationLoadingService {

  private final ConfigLoader myConfigLoader;
  private final SimulationLoader mySimulationLoader;

  public SimulationLoadingService() {
    myConfigLoader = new ConfigLoader();
    mySimulationLoader = new SimulationLoader();
  }

  public SimulationLoadingService(ConfigLoader configLoader, SimulationLoader simulationLoader) {
    myConfigLoader = configLoader;
    mySimulationLoader = simulationLoader;
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
    slot.applyStateColors(ColorMapProvider.getColorMap(config.getSimulationType()));
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

    int n = Math.min(slots.size(), desiredState.getActiveSimulations().size());
    for (int i = 0; i < n; i++) {
      SimulationSlot currentSlot = slots.get(i);
      SimulationConfig currentConfig = desiredState.getActiveSimulations().get(i);
      SimulationModel currentSim = SimulationFactory.createSimulation(currentConfig);
      currentSlot.setSimulation(currentSim);
      currentSlot.setConfig(currentConfig);
      currentSlot.applyStateColors(ColorMapProvider.getColorMap(currentConfig.getSimulationType()));
      selectedNeighborChoice.ifPresent(choice -> neighborStrategyFactory.applyToSlot(currentSlot, choice));
    }
    return desiredState.getPreferences();
  }

  private void checkSlotIndex(List<SimulationSlot> slots, int slotIndex) {
    if (slotIndex < 0 || slotIndex >= slots.size()) {
      throw new IndexOutOfBoundsException("Invalid slot index: " + slotIndex);
    }
  }
}
