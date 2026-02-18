package cellsociety.controller;

import cellsociety.Model.Simulation;
import cellsociety.Model.SimulationFactory;
import cellsociety.config.ConfigLoader;
import cellsociety.config.SimulationConfig;
import cellsociety.view.SimulationPanel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.parsers.ParserConfigurationException;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.xml.sax.SAXException;

/**
 * Controller for a multi-simulation dashboard that displays multiple render-only panels.
 *
 * <p>Purpose: manages creation/removal of simulation slots, loads configurations
 * into individual slots, and provides bulk actions (start/pause/step) across slots.</p>
 *
 * <p>Assumptions: configuration files are valid; UI actions are executed on the JavaFX
 * application thread.</p>
 *
 * <p>Dependencies: {@link ConfigLoader}/{@link SimulationFactory} for building simulations,
 * {@link SimulationPanel} for rendering, and JavaFX layout controls.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * MultiSimController multi = new MultiSimController(bundle);
 * root.setCenter(multi.getNode());
 * }</pre>
 *
 * <p>Other details: this controller does not reuse {@link WiringManager} or the
 * single-sim control bar; it stays focused on multi-sim rendering.</p>
 *
 * @author Emilio Garcia
 */
public class MultiSimController {

  private static final int MAX_SLOTS = 3;
  private static final int DEFAULT_SLOTS = 1;
  private static final String DATA_FILE_FOLDER = System.getProperty("user.dir") + "/data";

  private final ResourceBundle myBundle;
  private final ConfigLoader myConfigLoader;

  private final List<MultiSimSlot> mySlots;

  private final TilePane myTile;
  private final ScrollPane myScroll;
  private final VBox myRoot;
  private final ChoiceBox<Integer> mySlotChoice;

  /**
   * Creates a multi-simulation controller with the default number of slots.
   *
   * <p>Purpose: initializes the dashboard UI and slot list.</p>
   *
   * <p>Assumptions: {@code bundle} contains {@code label.slots}.</p>
   *
   * <p>Exceptions: {@link java.util.MissingResourceException} if required keys are missing.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param bundle localization bundle for UI strings
   */
  public MultiSimController(ResourceBundle bundle) {
    myBundle = bundle;
    myConfigLoader = new ConfigLoader();
    mySlots = new ArrayList<>();

    myTile = new TilePane();
    myTile.setPadding(new Insets(12));
    myTile.setHgap(12);
    myTile.setVgap(12);
    myTile.setPrefColumns(2);
    myTile.setPrefTileWidth(420);
    myTile.setPrefTileHeight(420);

    myScroll = new ScrollPane(myTile);
    myScroll.setFitToWidth(true);
    myScroll.setFitToHeight(true);
    myScroll.getStyleClass().add("card-scroll");

    mySlotChoice = new ChoiceBox<>();
    for (int i = 1; i <= MAX_SLOTS; i++) {
      mySlotChoice.getItems().add(i);
    }
    mySlotChoice.setValue(DEFAULT_SLOTS);
    mySlotChoice.setOnAction(e -> setSlotCount(mySlotChoice.getValue()));

    Label slotLabel = new Label(myBundle.getString("label.slots"));
    HBox header = new HBox(8, slotLabel, mySlotChoice);
    header.setPadding(new Insets(4, 6, 8, 6));

    myRoot = new VBox(8, header, myScroll);
    myRoot.setPadding(new Insets(4));

    setSlotCount(DEFAULT_SLOTS);
  }

  /**
   * Returns the root node for embedding in the main view.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return dashboard root node
   */
  public Node getNode() {
    return myRoot;
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
      MultiSimSlot removed = mySlots.remove(mySlots.size() - 1);
      removed.pause();
    }

    while (mySlots.size() < count) {
      int index = mySlots.size();
      SimulationPanel panel = new SimulationPanel(myBundle);
      int slotIndex = index;
      panel.setOnLoad(() -> handleLoadForSlot(slotIndex));
      SimulationPresenter presenter = new SimulationPresenter(panel);
      MultiSimSlot slot = new MultiSimSlot(index, panel, presenter);
      mySlots.add(slot);
    }

    rebuildTileChildren();
  }

  /**
   * Returns the current slot count.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return number of slots managed by this controller
   */
  public int getSlotCount() {
    return mySlots.size();
  }

  /**
   * Returns the slot object for a given index.
   *
   * <p>Assumptions: {@code slotIndex} is within range.</p>
   *
   * <p>Exceptions: {@link IndexOutOfBoundsException} if the index is invalid.</p>
   *
   * @param slotIndex zero-based slot index
   * @return slot at the given index
   * @throws IndexOutOfBoundsException if the index is invalid
   */
  public MultiSimSlot getSlot(int slotIndex) {
    checkIndex(slotIndex);
    return mySlots.get(slotIndex);
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
    if (xmlFile == null) return;

    SimulationConfig config = myConfigLoader.loadConfig(xmlFile);
    Simulation sim = SimulationFactory.createSimulation(config);

    mySlots.get(slotIndex).setSimulation(sim);
    mySlots.get(slotIndex).setConfig(config);
  }

  private void handleLoadForSlot(int slotIndex) {
    File xmlFile = showOpenConfigDialog();
    if (xmlFile == null) return;

    try {
      loadConfigIntoSlot(slotIndex, xmlFile);
    } catch (ParserConfigurationException | SAXException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private File showOpenConfigDialog() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Open Simulation Configuration");
    chooser.setInitialDirectory(new File(DATA_FILE_FOLDER));
    chooser.getExtensionFilters().add(
        new ExtensionFilter("XML Files", "*.xml")
    );

    Window w = myRoot.getScene() == null ? null : myRoot.getScene().getWindow();
    return chooser.showOpenDialog(w);
  }

  /**
   * Starts the simulation in a specific slot.
   *
   * <p>Assumptions: {@code slotIndex} is valid.</p>
   *
   * <p>Exceptions: {@link IndexOutOfBoundsException} if the index is invalid.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param slotIndex zero-based slot index
   * @throws IndexOutOfBoundsException if the index is invalid
   */
  public void startSlot(int slotIndex) {
    checkIndex(slotIndex);
    mySlots.get(slotIndex).start();
  }

  /**
   * Pauses the simulation in a specific slot.
   *
   * <p>Assumptions: {@code slotIndex} is valid.</p>
   *
   * <p>Exceptions: {@link IndexOutOfBoundsException} if the index is invalid.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param slotIndex zero-based slot index
   * @throws IndexOutOfBoundsException if the index is invalid
   */
  public void pauseSlot(int slotIndex) {
    checkIndex(slotIndex);
    mySlots.get(slotIndex).pause();
  }

  /**
   * Steps the simulation forward once in a specific slot.
   *
   * <p>Assumptions: {@code slotIndex} is valid.</p>
   *
   * <p>Exceptions: {@link IndexOutOfBoundsException} if the index is invalid.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param slotIndex zero-based slot index
   * @throws IndexOutOfBoundsException if the index is invalid
   */
  public void stepSlot(int slotIndex) {
    checkIndex(slotIndex);
    mySlots.get(slotIndex).step();
  }

  /**
   * Steps the simulation backward once in a specific slot.
   *
   * <p>Assumptions: {@code slotIndex} is valid.</p>
   *
   * <p>Exceptions: {@link IndexOutOfBoundsException} if the index is invalid.</p>
   *
   * @param slotIndex zero-based slot index
   * @return true if the step-back succeeded, false if no history is available
   * @throws IndexOutOfBoundsException if the index is invalid
   */
  public boolean stepBackSlot(int slotIndex) {
    checkIndex(slotIndex);
    return mySlots.get(slotIndex).stepBack();
  }

  /**
   * Starts all slots that currently have simulations installed.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void startAllLoaded() {
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.start();
      }
    }
  }

  /**
   * Pauses all slots, regardless of whether they are running.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void pauseAll() {
    for (MultiSimSlot slot : mySlots) {
      slot.pause();
    }
  }

  /**
   * Resets all loaded slots to their initial configurations.
   *
   * <p>Assumptions: each slot has a stored initial configuration.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void resetAllLoaded() {
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.reset();
      }
    }
  }

  /**
   * Multiplies the speed of all loaded slots by a given factor.
   *
   * <p>Assumptions: {@code factor} is positive.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param factor multiplier applied to each slot's speed
   */
  public void speedUpAll(double factor) {
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.setSpeed(slot.getStepsPerSecond() * factor);
      }
    }
  }

  /**
   * Divides the speed of all loaded slots by a given factor.
   *
   * <p>Assumptions: {@code factor} is positive.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param factor divisor applied to each slot's speed
   */
  public void slowDownAll(double factor) {
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.setSpeed(slot.getStepsPerSecond() / factor);
      }
    }
  }

  /**
   * Toggles stepping direction for all loaded slots.
   *
   * <p>Purpose: keeps all simulations moving in the same direction.</p>
   *
   * <p>Assumptions: at least one slot is loaded to determine current direction.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return the new direction (true for forward, false for backward)
   */
  public boolean toggleDirectionAll() {
    Boolean forward = null;
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        forward = slot.isForwardDirection();
        break;
      }
    }
    boolean newDirection = (forward == null) || !forward;
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.setDirection(newDirection);
      }
    }
    return newDirection;
  }

  /**
   * Toggles grid outlines for all loaded slots.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void toggleOutlineAll() {
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.toggleOutline();
      }
    }
  }

  /**
   * Steps all loaded slots forward once.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void stepAllLoaded() {
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.step();
      }
    }
  }

  /**
   * Steps all loaded slots backward once.
   *
   * <p>Slots with no history will remain unchanged.</p>
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void stepBackAllLoaded() {
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.stepBack();
      }
    }
  }

  private void rebuildTileChildren() {
    myTile.getChildren().clear();
    for (MultiSimSlot slot : mySlots) {
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
}
