package cellsociety.controller;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.ReadOnlyGrid;
import cellsociety.Model.Simulation;
import cellsociety.Model.SimulationFactory;
import cellsociety.Model.SimulationDriver;
import cellsociety.config.ConfigLoader;
import cellsociety.config.ConfigSaver;
import cellsociety.config.SimulationConfig;
import cellsociety.view.SimulationView;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.scene.Scene;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Centralizes wiring of SimulationView UI controls to controller/driver behavior.
 * Mirrors ButtonManager's goal: keep SimulationController small by moving wiring out.
 */
public class WiringManager {

  private final SimulationView mySimView;
  private final SimulationDriver myDriver;
  private final ConfigLoader myConfigLoader;
  private final ConfigSaver myConfigSaver;
  private final Scene myScene;

  private final java.util.function.Consumer<Simulation> setSimulation;
  private final java.util.function.Consumer<SimulationConfig> setInitialConfig;

  private final java.util.function.Supplier<SimulationConfig> getInitialConfig;
  private final Runnable myOpenMulti;
  private final Supplier<Boolean> isMultiMode;
  private final Runnable startAllLoaded;
  private final Runnable pauseAll;
  private final Runnable stepAllLoaded;
  private final Runnable stepBackAllLoaded;

  public WiringManager(SimulationView simView,
      SimulationDriver driver,
      ConfigLoader loader,
      ConfigSaver saver,
      Scene scene,
      Consumer<Simulation> setSimulationCallback,
      Consumer<SimulationConfig> setInitialConfigCallback,
      Supplier<SimulationConfig> getInitialConfigSupplier,
      Runnable multiSimCallback,
      Supplier<Boolean> isMultiModeSupplier,
      Runnable startAllLoadedCallback,
      Runnable pauseAllCallback,
      Runnable stepAllLoadedCallback,
      Runnable stepBackAllLoadedCallback) {
    mySimView = simView;
    myDriver = driver;
    myConfigLoader = loader;
    myConfigSaver = saver;
    myScene = scene;

    setSimulation = setSimulationCallback;
    setInitialConfig = setInitialConfigCallback;
    getInitialConfig = getInitialConfigSupplier;
    myOpenMulti = multiSimCallback;
    isMultiMode = isMultiModeSupplier;
    startAllLoaded = startAllLoadedCallback;
    pauseAll = pauseAllCallback;
    stepAllLoaded = stepAllLoadedCallback;
    stepBackAllLoaded = stepBackAllLoadedCallback;
  }

  /** Public entrypoint: wires every UI interaction. */
  public void wireAll() {
    wireApplyChanges();
    wireSettings();
    wireForward();
    wireBackward();
    wireChangeDirection();
    wireStart();
    wirePause();
    wireSave();
    wireUpload();
    wireSpeedUp();
    wireSlowDown();
    wireReset();
    wireAbout();
    wireToggleOutline();
    wireRotate();
    wireMultiSim();
  }

  private void wireForward() {
    mySimView.getMyButtons().getForwardButton().setOnAction(e -> {
      if (isMultiMode.get()) {
        stepAllLoaded.run();
        return;
      }
      if (myDriver.isRunning()) myDriver.pause();
      myDriver.step();
      if (myDriver.hasSimulation()) updateGenerationLabel();
    });
  }

  private void wireBackward() {
    mySimView.getMyButtons().getBackButton().setOnAction(e -> {
      if (isMultiMode.get()) {
        stepBackAllLoaded.run();
        return;
      }
      if (myDriver.isRunning()) myDriver.pause();
      myDriver.stepBack();
      if (myDriver.hasSimulation()) updateGenerationLabel();
    });
  }

  private void wireChangeDirection() {
    mySimView.getMyButtons().getChangeDirectionButton().setOnAction(e -> {
      myDriver.setDirection(!myDriver.isForwardDirection());
      if (myDriver.isForwardDirection()) {
        mySimView.getMyButtons().getChangeDirectionButton()
            .setText(mySimView.getMyBundle().getString("button.direction.forward"));
      } else {
        mySimView.getMyButtons().getChangeDirectionButton()
            .setText(mySimView.getMyBundle().getString("button.direction.backward"));
      }
    });
  }

  private void wireStart() {
    mySimView.getMyButtons().getStartButton().setOnAction(e -> {
      if (isMultiMode.get()) {
        startAllLoaded.run();
        return;
      }
      myDriver.start();
    });
  }

  private void wirePause() {
    mySimView.getMyButtons().getPauseButton().setOnAction(e -> {
      if (isMultiMode.get()) {
        pauseAll.run();
        return;
      }
      myDriver.pause();
    });
  }

  private void wireSpeedUp() {
    mySimView.getMyButtons().getSpeedUpButton().setOnAction(e -> {
      if (mySimView.getMySpeedMultiplier() >= 4) return;
      myDriver.setSpeed(myDriver.getStepsPerSecond() * 2);
      mySimView.doubleMySpeed();
      mySimView.updateSpeedLabel();
    });
  }

  private void wireSlowDown() {
    mySimView.getMyButtons().getSlowDownButton().setOnAction(e -> {
      if (mySimView.getMySpeedMultiplier() <= 0.25) return;
      myDriver.setSpeed(myDriver.getStepsPerSecond() / 2.0);
      mySimView.halveMySpeed();
      mySimView.updateSpeedLabel();
    });
  }

  private void wireSave() {
    mySimView.getMyButtons().getSaveButton().setOnAction(e -> {
      SimulationConfig initialConfig = getInitialConfig.get();
      if (initialConfig == null) return;
      if (!myDriver.hasSimulation()) return;
      if (myDriver.isRunning()) myDriver.pause();

      ReadOnlyGrid grid = myDriver.getSimulation().getReadOnlyGrid();
      CellState[][] stateSnapshot = ReadOnlyGrid.copyToArray(grid);
      int[][] intSnapshot = ReadOnlyGrid.produceIntArray(stateSnapshot, myDriver.getSimulation().getRule());

      SimulationConfig configSnapshot = new SimulationConfig(
          initialConfig.getSimulationType(),
          initialConfig.getRuleClass(),
          initialConfig.getTitle(),
          initialConfig.getAuthor(),
          initialConfig.getDescription(),
          grid.getRows(),
          grid.getCols(),
          intSnapshot,
          new HashMap<>(initialConfig.getParameters())
      );

      File saveDestination = mySimView.showSaveConfigDialog();
      if (saveDestination != null) {
        myConfigSaver.saveConfig(configSnapshot, saveDestination);
      }
    });
  }

  private void wireUpload() {
    mySimView.getMyButtons().getUploadButton().setOnAction(e -> handleUpload());
    mySimView.getMySettings().getMyUploadButton().setOnAction(e -> handleUpload());
  }

  private void handleUpload() {
    File myFile = mySimView.showOpenConfigDialog();
    if (myFile == null) return;

    try {
      SimulationConfig config = myConfigLoader.loadConfig(myFile);
      setInitialConfig.accept(config);

      Simulation sim = SimulationFactory.createSimulation(config);
      ReadOnlyGrid grid = sim.getReadOnlyGrid();

      mySimView.setGrid(grid.getRows(), grid.getCols());
      setSimulation.accept(sim);

      updateGenerationLabel();
    } catch (ParserConfigurationException | SAXException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void wireReset() {
    mySimView.getMyButtons().getResetButton().setOnAction(e -> {
      SimulationConfig initialConfig = getInitialConfig.get();
      if (initialConfig == null) return;

      Simulation sim = SimulationFactory.createSimulation(initialConfig);
      setSimulation.accept(sim);
      updateGenerationLabel();
    });
  }

  private void wireAbout() {
    mySimView.getMyAboutDialog().getMyCloseButton().setOnAction(c ->
        mySimView.getMyAboutDialog().closeInfo()
    );

    mySimView.getMyButtons().getAboutButton().setOnAction(e -> {
      SimulationConfig initialConfig = getInitialConfig.get();
      if (initialConfig == null) {
        throw new IllegalStateException("No configuration loaded");
      }
      mySimView.showAboutSplash(initialConfig);
    });
  }

  private void wireToggleOutline() {
    mySimView.getMyButtons().getToggleOutlineButton().setOnAction(e -> {
      if (!myDriver.hasSimulation()) return;
      Simulation sim = myDriver.getSimulation();
      mySimView.getMyGridView().toggleOutline(sim.getReadOnlyGrid(), sim.getRule().getStateColors());
    });
  }

  private void wireApplyChanges() {
    mySimView.getMySettings().getMyApplyChanges().setOnAction(e -> {
      String choice = mySimView.getMySettings().getMyLanguages().getValue();
      ResourceBundle chosenBundle = loadLanguageBundle(choice);
      mySimView.applyLanguage(chosenBundle);

      String themeChoice = mySimView.getMySettings().getMyThemes().getValue();
      mySimView.applyTheme(myScene, themeChoice);

      mySimView.closeSettings();
    });
  }

  private void wireSettings() {
    mySimView.getMyButtons().getSettingsButton().setOnAction(e -> mySimView.openSettings());
  }

  private void wireRotate() {
    mySimView.getMyButtons().getRotateLeftButton().setOnAction(e -> mySimView.getMyGridView().rotateLeft());
    mySimView.getMyButtons().getRotateRightButton().setOnAction(e -> mySimView.getMyGridView().rotateRight());
  }

  private void wireMultiSim() {
    mySimView.getMyButtons().getMyMultiSimButton().setOnAction(e -> myOpenMulti.run());
  }

  private void updateGenerationLabel() {
    Simulation sim = myDriver.getSimulation();
    if (sim == null) return;

    int generation = sim.getGeneration();
    mySimView.getMyButtons().getStateStackSizeLabel()
        .setText(MessageFormat.format(mySimView.getMyBundle().getString("label.generation"), generation));
  }

  private ResourceBundle loadLanguageBundle(String choice) {
    return switch (choice) {
      case "Spanish" -> ResourceBundle.getBundle("cellsociety.Languages.Spanish");
      case "Portuguese" -> ResourceBundle.getBundle("cellsociety.Languages.Portuguese");
      default -> ResourceBundle.getBundle("cellsociety.Languages.English");
    };
  }


}
