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
 * Controller for a "multi-simulation dashboard" that displays 1-4 render-only simulation panels.
 *
 * Purpose:
 * - Create/remove simulation slots (panels)
 * - Load a config into a slot (build Simulation via ConfigLoader + SimulationFactory)
 * - Start/pause/step per slot
 *
 * This controller intentionally does NOT reuse WiringManager or SimulationView controls.
 * It keeps multi-sim logic separate from the main interactive SimulationController.
 *
 * @author Emilio Garcia
 */
public class MultiSimController {

  private static final int MAX_SLOTS = 4;
  private static final int DEFAULT_SLOTS = 1;
  private static final String DATA_FILE_FOLDER = System.getProperty("user.dir") + "/data";

  private final ResourceBundle myBundle;
  private final ConfigLoader myConfigLoader;

  private final List<MultiSimSlot> mySlots;

  private final TilePane myTile;
  private final ScrollPane myScroll;
  private final VBox myRoot;
  private final ChoiceBox<Integer> mySlotChoice;

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
   * @return a Node you can place in a Scene or in SimulationView's center.
   */
  public Node getNode() {
    return myRoot;
  }

  /**
   * Sets how many slots exist (clamped to [1, MAX_SLOTS]).
   * Removing a slot pauses its driver.
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

  public int getSlotCount() {
    return mySlots.size();
  }

  public MultiSimSlot getSlot(int slotIndex) {
    checkIndex(slotIndex);
    return mySlots.get(slotIndex);
  }

  /**
   * Loads an XML config file into the given slot index.
   */
  public void loadConfigIntoSlot(int slotIndex, File xmlFile)
      throws ParserConfigurationException, IOException, SAXException, IllegalArgumentException {

    checkIndex(slotIndex);
    if (xmlFile == null) return;

    SimulationConfig config = myConfigLoader.loadConfig(xmlFile);
    Simulation sim = SimulationFactory.createSimulation(config);

    mySlots.get(slotIndex).setSimulation(sim);
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

  public void startSlot(int slotIndex) {
    checkIndex(slotIndex);
    mySlots.get(slotIndex).start();
  }

  public void pauseSlot(int slotIndex) {
    checkIndex(slotIndex);
    mySlots.get(slotIndex).pause();
  }

  public void stepSlot(int slotIndex) {
    checkIndex(slotIndex);
    mySlots.get(slotIndex).step();
  }

  public boolean stepBackSlot(int slotIndex) {
    checkIndex(slotIndex);
    return mySlots.get(slotIndex).stepBack();
  }

  /**
   * Manage all slots that currently have simulations installed.
   */
  public void startAllLoaded() {
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.start();
      }
    }
  }

  public void pauseAll() {
    for (MultiSimSlot slot : mySlots) {
      slot.pause();
    }
  }

  public void stepAllLoaded() {
    for (MultiSimSlot slot : mySlots) {
      if (slot.hasSimulation()) {
        slot.step();
      }
    }
  }

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
