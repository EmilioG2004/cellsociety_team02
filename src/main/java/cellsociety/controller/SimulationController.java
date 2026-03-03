package cellsociety.controller;

import static cellsociety.view.constants.SimulationUiConstants.DEFAULT_SLOTS;
import static cellsociety.view.constants.SimulationUiConstants.MAX_SLOTS;
import static cellsociety.view.constants.ResourcePaths.BUNDLE_FOLDER;

import cellsociety.config.SimulationPreferences;
import cellsociety.view.PopulationGraphDialog;
import cellsociety.Model.Simulation.driver.SimulationDriver;
import cellsociety.view.SimulationView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
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
  private final SimulationPersistenceService myPersistenceService;
  private final ErrorHandler myErrorHandler;
  private final List<SimulationSlot> mySlots;
  private final SlotManager mySlotManager;
  private final NeighborStrategyFactory myNeighborStrategyFactory;
  private final PopulationGraphDialog myPopulationGraphDialog;
  private Optional<String> mySelectedNeighborChoice = Optional.empty();


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
    myPersistenceService = new SimulationPersistenceService();
    mySimView = simView;
    myErrorHandler = new ErrorHandler(mySimView::getMyBundle);
    mySlots = new ArrayList<>();
    mySlotManager = new SlotManager(mySimView);
    myNeighborStrategyFactory = new NeighborStrategyFactory();
    myPopulationGraphDialog = new PopulationGraphDialog();
    myScene = scene;
    myWires = new WiringManager(
        mySimView,
        myScene,
        this::setMyBundle,
        this::handleSaveAll,
        this::handleLoadState,
        this::handlePopulationDataChanged
    );

    ChoiceBox<Integer> mySlotChoice = mySimView.getMultiSimSlotChoice();
    for (int i = 1; i <= MAX_SLOTS; i++) {
      mySlotChoice.getItems().add(i);
    }
    mySlotChoice.setValue(DEFAULT_SLOTS);
    mySlotChoice.setOnAction(e -> setSlotCount(mySlotChoice.getValue()));

    setSlotCount(DEFAULT_SLOTS);

    myWires.wireAll(mySlots, this::handleSplashLoad);
    wireNeighborSelection();
  }

  /**
   * Shows the file chooser and loads the selected config into slot 0.
   * Called when the user clicks "Load Simulation" in the settings/splash panel.
   */
  private void handleSplashLoad() {
    File xmlFile = mySimView.showOpenConfigDialog();
    if (xmlFile == null) return;
    try {
      myPersistenceService.loadConfigIntoSlot(
          0, xmlFile, mySlots, mySelectedNeighborChoice, myNeighborStrategyFactory);
      handlePopulationDataChanged();
    } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
      myErrorHandler.handle(ex);
    }
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
    mySlotManager.setSlotCount(
        mySlots,
        desired,
        1,
        MAX_SLOTS,
        myBundle,
        this::handleLoadForSlot,
        this::handleAboutForSlot,
        this::handleSaveForSlot,
        this::handleViewGraphForSlot
    );
  }

  private void handleLoadState(){
    File xmlFile = mySimView.showOpenConfigDialog();
    if (xmlFile == null) throw new IllegalArgumentException("No file selected.");

    try{
      SimulationPreferences desiredPreferences = myPersistenceService.loadStateIntoSlots(
          xmlFile,
          mySlots,
          this::setSlotCount,
          value -> mySimView.getMultiSimSlotChoice().setValue(value),
          mySelectedNeighborChoice,
          myNeighborStrategyFactory
      );
      loadPreferences(desiredPreferences);
      handlePopulationDataChanged();
    } catch (ParserConfigurationException | SAXException | IOException e) {
      myErrorHandler.handle(e);
    }
  }

  private void loadPreferences(SimulationPreferences desiredPreferences){
    int theme = desiredPreferences.getTheme();
    String themeChoice = mapThemeCodeToChoice(theme);
    String language = desiredPreferences.getLanguage();
    String languageBundle = BUNDLE_FOLDER + language;
    double speed = desiredPreferences.getSimulationSpeed();
    Map<Integer, String> colors = desiredPreferences.getColorMap();
    mySimView.applyTheme(myScene, themeChoice);
    mySimView.getMySettings().getMyThemes().setValue(themeChoice);
    mySimView.applyLanguage(ResourceBundle.getBundle(languageBundle));
    mySimView.getMySettings().getMyLanguages().setValue(language);
    for (SimulationSlot currentSlot : mySlots){
      currentSlot.setSpeed(speed);
    }
  }

  private void handleLoadForSlot(int slotIndex) {
    File xmlFile = mySimView.showOpenConfigDialog();
    if (xmlFile == null) throw new IllegalArgumentException("No file selected.");
    try {
      myPersistenceService.loadConfigIntoSlot(
          slotIndex, xmlFile, mySlots, mySelectedNeighborChoice, myNeighborStrategyFactory);
      handlePopulationDataChanged();
    } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private void handleSaveForSlot(int slotIndex) {
    try {
      File saveDestination = mySimView.showSaveConfigDialog();
      myPersistenceService.saveConfigForSlot(slotIndex, mySlots, saveDestination);
    } catch (RuntimeException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private void handleSaveAll() {
    try {
      File saveDestination = mySimView.showSaveConfigDialog();
      if (saveDestination == null) return;
      myPersistenceService.saveAllState(
          mySlots,
          mySimView.getMySettings().getMyThemes().getValue(),
          mySimView.getMySettings().getMyLanguages().getValue(),
          saveDestination
      );
    } catch (RuntimeException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private String mapThemeCodeToChoice(int themeCode) {
    if (themeCode == 1) return ResourcePaths.THEME_DARK;
    if (themeCode == 2) return ResourcePaths.THEME_DUKE;
    return ResourcePaths.THEME_LIGHT;
  }

  private void handleAboutForSlot(int slotIndex) {
    try {
      mySlotManager.checkIndex(mySlots, slotIndex);
      SimulationSlot slot = mySlots.get(slotIndex);
      if (!slot.hasSimulation()) {
        throw new IllegalStateException("No simulation loaded for slot " + slotIndex);
      }
      mySimView.showAboutSplash(slot.getConfig());
    } catch (IllegalStateException | IndexOutOfBoundsException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private void handleViewGraphForSlot(int slotIndex) {
    try {
      mySlotManager.checkIndex(mySlots, slotIndex);
      SimulationSlot slot = mySlots.get(slotIndex);
      if (!slot.hasSimulation()) {
        throw new IllegalStateException("No simulation loaded for slot " + slotIndex);
      }
      for (SimulationSlot current : mySlots) {
        current.pause();
      }
      myPopulationGraphDialog.showForSlot(slotIndex, slot.getPresenter().getPopulationHistory());
    } catch (RuntimeException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private void wireNeighborSelection() {
    ChoiceBox<String> neighborChoices = mySimView.getMyButtons().getNeighborChoices();
    neighborChoices.setOnAction(e -> {
      String selected = neighborChoices.getValue();
      if (selected == null) throw new IllegalArgumentException("No neighborhood strategy selected.");
      mySelectedNeighborChoice = Optional.of(selected);
      applyNeighborChoiceToLoadedSimulations(selected);
    });
  }

  private void applyNeighborChoiceToLoadedSimulations(String selectedChoice) {
    myNeighborStrategyFactory.applyToLoadedSimulations(mySlots, selectedChoice);
    handlePopulationDataChanged();
  }

  private void handlePopulationDataChanged() {
    for (SimulationSlot slot : mySlots) {
      if (!slot.hasSimulation()) continue;
      slot.getPresenter().syncPopulationAtCurrentGeneration();
    }
  }

  public void setMyBundle(ResourceBundle bundle) { myBundle = bundle; }
}
