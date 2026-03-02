package cellsociety.controller;

import static cellsociety.view.constants.SimulationUiConstants.DEFAULT_SLOTS;
import static cellsociety.view.constants.SimulationUiConstants.MAX_SLOTS;

import cellsociety.config.ConfigSaver;
import cellsociety.view.SimulationPanel;
import cellsociety.Model.Simulation.Simulation;
import cellsociety.Model.Simulation.SimulationFactory;
import cellsociety.Model.Simulation.SimulationDriver;
import cellsociety.Model.Grid.GridViewData;
import cellsociety.view.SimulationView;
import cellsociety.config.ConfigLoader;
import cellsociety.config.SimulationConfig;
import cellsociety.config.SimulationState;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.TilePane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import cellsociety.view.constants.ResourcePaths;

/**
 * Primary controller that wires {@link SimulationView} to simulation logic.
 *
 * <p>Purpose: connects UI controls to all SimulationPresenters, handles configuration
 * loading/saving through the {@link cellsociety.config} package, and refreshes the view
 * after simulation steps.</p>
 *
 * <p>Assumptions: {@link SimulationView} is fully initialized before wiring; configuration
 * files are valid and compatible with {@link cellsociety.config.ConfigLoader}.</p>
 *
 * <p>Dependencies: {@link SimulationView}, {@link SimulationDriver}, configuration loaders/savers,
 * and JavaFX for UI thread updates.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SimulationView view = new SimulationView(bundle);
 * Scene scene = new Scene(view.getRoot());
 * SimulationController controller = new SimulationController(view, scene);
 * }</pre>
 *
 * @author Emilio Garcia
 */
public class SimulationController {

  private final SimulationView mySimView;
  private final Scene myScene;
  private final WiringManager myWires;

  private ResourceBundle myBundle;
  private final ConfigLoader myConfigLoader;
  private final ConfigSaver myConfigSaver;
  private final ErrorHandler myErrorHandler;

  private final List<SimulationSlot> mySlots;

  private TilePane myTile;
  private ChoiceBox<Integer> mySlotChoice;

  /**
   * Constructs a controller and wires all UI controls.
   *
   * <p>Purpose: initializes the driver, loaders/savers, and the multi-sim controller, then
   * registers callbacks to refresh the UI after each step.</p>
   *
   * <p>Assumptions: {@code simView} is non-null and already constructed; {@code scene} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param simView the view whose controls are connected to simulation behavior
   * @param scene the scene used for theme updates
   */
  public SimulationController(SimulationView simView, Scene scene, ResourceBundle bundle) {
    myBundle = bundle;
    myConfigLoader = new ConfigLoader();
    myConfigSaver = new ConfigSaver();
    mySimView = simView;
    myErrorHandler = new ErrorHandler(mySimView::getMyBundle);
    mySlots = new ArrayList<>();
    myScene = scene;
    myWires = new WiringManager(mySimView, myScene, this::setMyBundle, this::handleSaveAll);

    myTile = mySimView.getMultiSimTile();
    mySlotChoice = mySimView.getMultiSimSlotChoice();
    for (int i = 1; i <= MAX_SLOTS; i++) {
      mySlotChoice.getItems().add(i);
    }
    mySlotChoice.setValue(DEFAULT_SLOTS);
    mySlotChoice.setOnAction(e -> setSlotCount(mySlotChoice.getValue()));

    setSlotCount(DEFAULT_SLOTS);

    myWires.wireAll(mySlots);
  }

  /**
   * Sets how many slots exist (clamped to [1, MAX_SLOTS]).
   *
   * <p>Purpose: allows the user to choose how many panels to display.</p>
   *
   * <p>Assumptions: {@code desired} may be any integer; values outside the range are clamped.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param desired requested number of slots
   */
  public void setSlotCount(int desired) {
    int count = clamp(desired, 1, MAX_SLOTS);

    while (mySlots.size() > count) {
      SimulationSlot removed = mySlots.remove(mySlots.size() - 1);
      removed.pause();
    }

    while (mySlots.size() < count) {
      int index = mySlots.size();
      SimulationPanel panel = new SimulationPanel(myBundle);
      int slotIndex = index;
      panel.setOnLoad(() -> handleLoadForSlot(slotIndex));
      panel.setOnAbout(() -> handleAboutForSlot(slotIndex));
      panel.setOnSave(() -> handleSaveForSlot(slotIndex));
      SimulationPresenter presenter = new SimulationPresenter(panel);
      SimulationSlot slot = new SimulationSlot(index, panel, presenter);
      mySlots.add(slot);
    }
    rebuildTileChildren();
  }

  /**
   * Loads an XML config file into the given slot index.
   *
   * <p>Purpose: instantiate and display a simulation for the selected slot.</p>
   *
   * <p>Assumptions: {@code xmlFile} is non-null and readable.</p>
   *
   * <p>Exceptions: may throw parsing/IO exceptions if loading fails.</p>
   *
   * @param slotIndex zero-based slot index
   * @param xmlFile configuration file to load
   * @throws ParserConfigurationException if the XML parser cannot be configured
   * @throws IOException if the file cannot be read
   * @throws SAXException if the XML is malformed
   * @throws IllegalArgumentException if the configuration is invalid
   */
  public void loadConfigIntoSlot(int slotIndex, File xmlFile)
      throws ParserConfigurationException, IOException, SAXException, IllegalArgumentException {

    checkIndex(slotIndex);
    if (xmlFile == null) throw new IllegalArgumentException("No file selected.");

    SimulationConfig config = myConfigLoader.loadConfig(xmlFile);
    Simulation sim = SimulationFactory.createSimulation(config);

    mySlots.get(slotIndex).setSimulation(sim);
    mySlots.get(slotIndex).setConfig(config);
  }

  private void handleLoadForSlot(int slotIndex) {
    File xmlFile = mySimView.showOpenConfigDialog();
    if (xmlFile == null) return;

    try {
      loadConfigIntoSlot(slotIndex, xmlFile);
    } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private void saveConfigInSlot(int slotIndex) {
    checkIndex(slotIndex);
    SimulationSlot slot = mySlots.get(slotIndex);

    if (!slot.hasSimulation()) {
      throw new IllegalStateException("No simulation loaded for slot " + slotIndex);
    }

    SimulationConfig initialConfig = slot.getConfig();
    GridViewData grid = slot.getPresenter().getDriver().getSimulation().getGridViewData();
    int[][] intSnapshot = GridViewData.produceIntArray(grid);

    SimulationConfig configSnapshot = new SimulationConfig(
        initialConfig.getSimulationType(),
        initialConfig.getRuleClass(),
        initialConfig.getNeighborStrategyClass(),
        initialConfig.getTitle(),
        initialConfig.getAuthor(),
        initialConfig.getDescription(),
        grid.getRows(),
        grid.getCols(),
        intSnapshot,
        initialConfig.getParameters()
    );

    File saveDestination = mySimView.showSaveConfigDialog();
    if (saveDestination == null) return;

    myConfigSaver.saveConfig(configSnapshot, saveDestination);
  }

  private void handleSaveForSlot(int slotIndex) {
    try {
      saveConfigInSlot(slotIndex);
    } catch (RuntimeException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private void handleSaveAll() {
    try {
      SimulationState stateSnapshot = buildSimulationStateSnapshot();
      File saveDestination = mySimView.showSaveConfigDialog();
      if (saveDestination == null) return;
      myConfigSaver.saveState(stateSnapshot, saveDestination);
    } catch (RuntimeException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private SimulationState buildSimulationStateSnapshot() {
    List<SimulationConfig> active = new ArrayList<>();
    for (SimulationSlot slot : mySlots) {
      if (!slot.hasSimulation()) continue;
      SimulationConfig base = slot.getConfig();
      GridViewData grid = slot.getPresenter().getDriver().getSimulation().getGridViewData();
      int[][] states = GridViewData.produceIntArray(grid);
      SimulationConfig snapshot = new SimulationConfig(
          base.getSimulationType(),
          base.getRuleClass(),
          base.getNeighborStrategyClass(),
          base.getTitle(),
          base.getAuthor(),
          base.getDescription(),
          grid.getRows(),
          grid.getCols(),
          states,
          base.getParameters()
      );
      active.add(snapshot);
    }

    int theme = mapThemeToCode(mySimView.getMySettings().getMyThemes().getValue());
    double speed = mySimView.getMySpeedMultiplier();
    String language = mySimView.getMySettings().getMyLanguages().getValue();
    if (language == null || language.isBlank()) {
      language = ResourcePaths.LANGUAGE_ENGLISH;
    }

    return new SimulationState(active, theme, speed, language);
  }

  private int mapThemeToCode(String themeChoice) {
    if (ResourcePaths.THEME_DARK.equals(themeChoice)) return 1;
    if (ResourcePaths.THEME_DUKE.equals(themeChoice)) return 2;
    return 0;
  }

  private void handleAboutForSlot(int slotIndex) {
    try {
      checkIndex(slotIndex);
      SimulationSlot slot = mySlots.get(slotIndex);
      if (!slot.hasSimulation()) {
        throw new IllegalStateException("No simulation loaded for slot " + slotIndex);
      }
      mySimView.showAboutSplash(slot.getConfig());
    } catch (IllegalStateException | IndexOutOfBoundsException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private void rebuildTileChildren() {
    myTile.getChildren().clear();
    for (SimulationSlot slot : mySlots) {
      myTile.getChildren().add(slot.getNode());
    }
  }

  private void checkIndex(int slotIndex) {
    if (slotIndex < 0 || slotIndex >= mySlots.size()) {
      throw new IndexOutOfBoundsException("Invalid slot index: " + slotIndex);
    }
  }

  private int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  public void setMyBundle(ResourceBundle bundle){ myBundle = bundle; }
}
