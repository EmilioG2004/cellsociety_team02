package cellsociety.controller;

import static cellsociety.view.constants.SimulationUiConstants.DEFAULT_SLOTS;
import static cellsociety.view.constants.SimulationUiConstants.MAX_SLOTS;
import cellsociety.controller.multiview.SimulationSlot;
import cellsociety.controller.multiview.SlotManager;
import cellsociety.controller.services.ErrorHandler;
import cellsociety.controller.services.NeighborStrategyFactory;
import cellsociety.controller.services.PopulationSyncService;
import cellsociety.controller.services.SimulationDialogService;
import cellsociety.controller.services.SimulationPersistenceHandlers;
import cellsociety.view.SimulationView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;

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
 * <p>Dependencies: {@link SimulationView}, configuration loaders/savers,
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
  private final ErrorHandler myErrorHandler;
  private final List<SimulationSlot> mySlots;
  private final SlotManager mySlotManager;
  private final NeighborStrategyFactory myNeighborStrategyFactory;
  private final PopulationSyncService myPopulationSyncService;
  private final SimulationDialogService myDialogService;
  private final SimulationPersistenceHandlers myPersistenceHandlers;
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
    mySimView = simView;
    myErrorHandler = new ErrorHandler(mySimView::getMyBundle);
    mySlots = new ArrayList<>();
    mySlotManager = new SlotManager(mySimView);
    myNeighborStrategyFactory = new NeighborStrategyFactory();
    myPopulationSyncService = new PopulationSyncService();
    myDialogService = new SimulationDialogService(mySimView);
    myScene = scene;
    myPersistenceHandlers = new SimulationPersistenceHandlers(mySimView, myScene, myErrorHandler, myNeighborStrategyFactory, this::handlePopulationDataChanged, this::setSlotCount
    );
    myWires = new WiringManager(mySimView, myScene, this::setMyBundle, this::handleSaveAll, this::handleLoadState, this::handlePopulationDataChanged);

    buildSlotChoices();
    setSlotCount(DEFAULT_SLOTS);

    myWires.wireAll(mySlots, this::handleSplashLoad);
    wireNeighborSelection();
  }

  private void buildSlotChoices() {
    ChoiceBox<Integer> mySlotChoice = mySimView.getMultiSimSlotChoice();
    for (int i = 1; i <= MAX_SLOTS; i++) {
      mySlotChoice.getItems().add(i);
    }
    mySlotChoice.setValue(DEFAULT_SLOTS);
    mySlotChoice.setOnAction(e -> setSlotCount(mySlotChoice.getValue()));
  }

  /**
   * Shows the file chooser and loads the selected config into slot 0.
   * Called when the user clicks "Load Simulation" in the settings/splash panel.
   */
  private void handleSplashLoad() {
    myPersistenceHandlers.handleSplashLoad(mySlots, mySelectedNeighborChoice);
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
    myPersistenceHandlers.handleLoadState(mySlots, mySelectedNeighborChoice);
  }

  private void handleLoadForSlot(int slotIndex) {
    myPersistenceHandlers.handleLoadForSlot(slotIndex, mySlots, mySelectedNeighborChoice);
  }

  private void handleSaveForSlot(int slotIndex) {
    myPersistenceHandlers.handleSaveForSlot(slotIndex, mySlots);
  }

  private void handleSaveAll() {
    myPersistenceHandlers.handleSaveAll(mySlots);
  }

  private void handleAboutForSlot(int slotIndex) {
    try {
      myDialogService.showAboutForSlot(mySlots, slotIndex);
    } catch (IllegalStateException | IndexOutOfBoundsException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private void handleViewGraphForSlot(int slotIndex) {
    try {
      myDialogService.showPopulationGraphForSlot(mySlots, slotIndex);
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
    myPopulationSyncService.syncLoadedSlots(mySlots);
  }

  public void setMyBundle(ResourceBundle bundle) { myBundle = bundle; }
}
