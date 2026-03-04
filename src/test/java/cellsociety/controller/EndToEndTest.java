package cellsociety.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.controller.multiview.SimulationSlot;
import cellsociety.controller.services.GridEdgeFactory;
import cellsociety.controller.services.NeighborStrategyFactory;
import cellsociety.controller.services.SimulationLoadingService;
import cellsociety.view.SimulationView;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

public class EndToEndTest extends DukeApplicationTest {

  private static final String ENGLISH_BUNDLE_PATH = "cellsociety.Languages.English";
  private static final String LOAD_BUTTON_KEY = "button.loadsim";
  private static final String ERROR_INVALID_XML_KEY = "error.invalidXml";

  private StubbedSimulationView myView;
  private SimulationController myController;
  private ResourceBundle myBundle;

  @Override
  public void start(Stage stage) throws Exception {
    myBundle = ResourceBundle.getBundle(ENGLISH_BUNDLE_PATH);
    myView = new StubbedSimulationView(myBundle);
    Scene scene = new Scene(myView.getRoot(), 1400, 900);
    myController = new SimulationController(myView, scene, myBundle);
    stage.setScene(scene);
    stage.show();

    runAsJFXAction(myView::closeSettings);
    preloadThreeConfigurations();
  }

  @Test
  void UploadConfiguration_BadXmlFileChosen_ErrorAlertShownToUser() {
    myView.enqueueOpenConfig(file("data/configs/Test/test_exception_empty.xml"));
    clickLoadButtonForSlot(0);

    String dialogMessage = getDialogMessage(DukeApplicationTest.DEFAULT_SUBMIT_BUTTON_TEXT);
    assertEquals(myBundle.getString(ERROR_INVALID_XML_KEY), dialogMessage);
  }

  @Test
  void SpeedChangedAndSlotAdded_NewSimulationLoaded_NewSlotUsesCurrentSpeed() {
    clickOn(myView.getMyButtons().getSpeedUpButton());
    clickOn(myView.getMyButtons().getSpeedUpButton());
    clickOn(myView.getMyButtons().getPauseButton());

    double expectedSpeed = getSlots().get(0).getStepsPerSecond();

    setSlotCount(4);
    myView.enqueueOpenConfig(file("data/configs/SpreadingOfFire/fire_simple.xml"));
    clickLoadButtonForSlot(3);

    double actualNewSlotSpeed = getSlots().get(3).getStepsPerSecond();
    assertEquals(expectedSpeed, actualNewSlotSpeed, 0.00001);
  }

  @Test
  void SlotCountChangedAndSimulationLoaded_ExistingSlotsRemainLoadedAndConsistent() {
    String firstSlotTitleBefore = getSlots().get(0).getConfig().getTitle();
    setSlotCount(2);
    setSlotCount(3);

    myView.enqueueOpenConfig(file("data/configs/Percolotion/percolation_simple.xml"));
    clickLoadButtonForSlot(2);

    List<SimulationSlot> slots = getSlots();
    assertTrue(slots.get(0).hasSimulation());
    assertTrue(slots.get(1).hasSimulation());
    assertTrue(slots.get(2).hasSimulation());
    assertEquals(firstSlotTitleBefore, slots.get(0).getConfig().getTitle());
  }

  @Test
  void NeighborChoiceSelectedThenSlotAdded_NewlyLoadedSimulationUsesSelectedNeighborStrategy() {
    ChoiceBox<String> neighborChoices = myView.getMyButtons().getNeighborChoices();
    runAsJFXAction(() -> {
      neighborChoices.getSelectionModel().select("MooreRadius2");
      if (neighborChoices.getOnAction() != null) {
        neighborChoices.getOnAction().handle(new ActionEvent());
      }
    });

    setSlotCount(4);
    myView.enqueueOpenConfig(file("data/configs/GameOfLife/gol_blinker.xml"));
    clickLoadButtonForSlot(3);

    String strategyClassName = getSlots().get(3).getPresenter()
        .getDriver()
        .getSimulation()
        .getControllerGrid()
        .getNeighborStrategy()
        .getClass()
        .getSimpleName();

    assertEquals("MooreNeighborStrategyRadius2", strategyClassName);
  }

  private void preloadThreeConfigurations() {
    ensureSlotCount(3);
    SimulationLoadingService loadingService = new SimulationLoadingService();
    NeighborStrategyFactory neighborFactory = new NeighborStrategyFactory();
    GridEdgeFactory edgeFactory = new GridEdgeFactory();
    List<SimulationSlot> slots = getSlots();
    try {
      loadingService.loadConfigIntoSlot(
          0,
          file("data/configs/GameOfLife/gol_glider.xml"),
          slots,
          Optional.empty(),
          neighborFactory,
          Optional.empty(),
          edgeFactory
      );
      loadingService.loadConfigIntoSlot(
          1,
          file("data/configs/Segregation/segregation_random_40.xml"),
          slots,
          Optional.empty(),
          neighborFactory,
          Optional.empty(),
          edgeFactory
      );
      loadingService.loadConfigIntoSlot(
          2,
          file("data/configs/WaTor/wator_simple.xml"),
          slots,
          Optional.empty(),
          neighborFactory,
          Optional.empty(),
          edgeFactory
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    assertTrue(slots.get(0).hasSimulation());
    assertTrue(slots.get(1).hasSimulation());
    assertTrue(slots.get(2).hasSimulation());
  }

  private void setSlotCount(int count) {
    runOnFxThread(() -> {
      ChoiceBox<Integer> slotChoice = myView.getMultiSimSlotChoice();
      slotChoice.setValue(count);
      if (slotChoice.getOnAction() != null) {
        slotChoice.getOnAction().handle(new ActionEvent());
      }
    });
  }

  private void ensureSlotCount(int expectedCount) {
    runOnFxThread(() -> {
      try {
        Method setSlotCount = SimulationController.class.getDeclaredMethod("setSlotCount", int.class);
        setSlotCount.setAccessible(true);
        setSlotCount.invoke(myController, expectedCount);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    });
    assertEquals(expectedCount, getSlots().size(),
        "Unable to resize slots to expected count " + expectedCount);
  }

  private void runOnFxThread(Runnable action) {
    if (Platform.isFxApplicationThread()) {
      action.run();
    } else {
      runAsJFXAction(action);
    }
  }

  private void clickLoadButtonForSlot(int slotIndex) {
    String loadText = myBundle.getString(LOAD_BUTTON_KEY);
    List<Node> slotNodes = new ArrayList<>(myView.getMultiSimTile().getChildren());
    assertTrue(slotIndex >= 0 && slotIndex < slotNodes.size(),
        "No slot node found for slot index " + slotIndex);
    List<Button> loadButtons = slotNodes.get(slotIndex)
        .lookupAll(".button")
        .stream()
        .filter(Button.class::isInstance)
        .map(Button.class::cast)
        .filter(button -> loadText.equals(button.getText()))
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    assertTrue(!loadButtons.isEmpty(), "No load button found inside slot " + slotIndex);
    clickOn(loadButtons.getFirst());
  }

  private File file(String relativePath) {
    File f = new File(relativePath);
    assertTrue(f.exists(), "Missing test file: " + relativePath);
    return f;
  }

  @SuppressWarnings("unchecked")
  private List<SimulationSlot> getSlots() {
    try {
      Field slotsField = SimulationController.class.getDeclaredField("mySlots");
      slotsField.setAccessible(true);
      return (List<SimulationSlot>) slotsField.get(myController);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  private static class StubbedSimulationView extends SimulationView {
    private final Deque<File> myOpenConfigSelections = new ArrayDeque<>();
    private final Deque<File> myOpenStateSelections = new ArrayDeque<>();
    private final Deque<File> mySaveSelections = new ArrayDeque<>();

    StubbedSimulationView(ResourceBundle bundle) {
      super(bundle);
    }

    void enqueueOpenConfig(File selectedFile) {
      myOpenConfigSelections.addLast(selectedFile);
    }

    @Override
    public File showOpenConfigDialog() {
      return myOpenConfigSelections.pollFirst();
    }

    @Override
    public File showOpenStateDialog() {
      return myOpenStateSelections.pollFirst();
    }

    @Override
    public File showSaveConfigDialog() {
      return mySaveSelections.pollFirst();
    }
  }
}
