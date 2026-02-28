package cellsociety.controller;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.ReadOnlyGrid;
import cellsociety.Model.Simulation;
import cellsociety.Model.SimulationFactory;
import cellsociety.config.ConfigLoader;
import cellsociety.config.ConfigSaver;
import cellsociety.config.SimulationConfig;
import cellsociety.view.SimulationView;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.scene.Scene;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Centralizes wiring of {@link SimulationView} controls to controller logic.
 *
 * <p>Purpose: keep {@link SimulationController} concise by encapsulating all event
 * handler registrations in one place.</p>
 *
 * <p>Assumptions: all UI controls in {@link SimulationView} are non-null and already
 * constructed; configuration loaders/savers are available.</p>
 *
 * <p>Dependencies: {@link SimulationView}
 * {@link ConfigLoader}, {@link ConfigSaver}, and {@link Scene} for theme changes.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * WiringManager wires = new WiringManager(view, driver, loader, saver, scene, ...);
 * wires.wireAll();
 * }</pre>
 *
 * @author Emilio Garcia
 */
public class WiringManager {

  private final SimulationView mySimView;
  private final Scene myScene;

  /**
   * Creates a wiring manager with callbacks for both single-sim and multi-sim modes.
   *
   * <p>Purpose: injects the dependencies and callbacks needed to attach event handlers.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param simView view whose controls will be wired
   * @param scene scene used for applying themes
   */
  public WiringManager(SimulationView simView, Scene scene) {
    mySimView = simView;
    myScene = scene;
  }

  /**
   * Wires every UI interaction for the main view.
   *
   * <p>Purpose: attaches event handlers for uploads, controls, settings, and multi-sim.</p>
   *
   * <p>Assumptions: controls are already constructed.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void wireAll(List<MultiSimSlot> mySlots) {
    wireApplyChanges();
    wireSettings();
    wireForward(mySlots);
    wireBackward(mySlots);
    wireChangeDirection(mySlots);
    wireStart(mySlots);
    wirePause(mySlots);
    wireSpeedUp(mySlots);
    wireSlowDown(mySlots);
    wireReset(mySlots);
    wireToggleOutline(mySlots);
    wireRotate();
  }

  private void wireForward(List<MultiSimSlot> mySlots) {
    mySimView.getMyButtons().getForwardButton().setOnAction(e -> {
      for (MultiSimSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.step();
        }
      }
    });
  }

  private void wireBackward(List<MultiSimSlot> mySlots) {
    mySimView.getMyButtons().getBackButton().setOnAction(e -> {
      for (MultiSimSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.stepBack();
        }
      }
    });
  }

  private void wireChangeDirection(List<MultiSimSlot> mySlots) {
    var buttons = mySimView.getMyButtons();
    var dirButton = buttons.getChangeDirectionButton();
    var bundle = mySimView.getMyBundle();

    dirButton.setOnAction(e -> {
      Boolean forward = null;
      for (MultiSimSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          forward = slot.isForwardDirection();
          break;
        }
      }

      boolean newDirection = (forward == null) ? true : !forward;

      for (MultiSimSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.setDirection(newDirection);
        }
      }

      dirButton.setText(bundle.getString(
          newDirection ? "button.direction.forward" : "button.direction.backward"
      ));
    });
  }

  private void wireStart(List<MultiSimSlot> mySlots) {
    mySimView.getMyButtons().getStartButton().setOnAction(e -> {
      for (MultiSimSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.start();
        }
      }    });
  }

  private void wirePause(List<MultiSimSlot> mySlots) {
    mySimView.getMyButtons().getPauseButton().setOnAction(e -> {
      for (MultiSimSlot slot : mySlots) {
        slot.pause();
      }
    });
  }

  private void wireSpeedUp(List<MultiSimSlot> mySlots) {
    mySimView.getMyButtons().getSpeedUpButton().setOnAction(e -> {
      if (mySimView.getMySpeedMultiplier() >= 4) return;
      for (MultiSimSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.setSpeed(slot.getStepsPerSecond() * 2.0);
        }
      }
      mySimView.doubleMySpeed();
      mySimView.updateSpeedLabel();
    });
  }

  private void wireSlowDown(List<MultiSimSlot> mySlots) {
    mySimView.getMyButtons().getSlowDownButton().setOnAction(e -> {
      if (mySimView.getMySpeedMultiplier() <= 0.25) return;
      for (MultiSimSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.setSpeed(slot.getStepsPerSecond() / 2.0);
        }
      }
      mySimView.halveMySpeed();
      mySimView.updateSpeedLabel();
    });
  }

  /**
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
   */

  private void wireReset(List<MultiSimSlot> mySlots) {
    mySimView.getMyButtons().getResetButton().setOnAction(e -> {
      for (MultiSimSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.reset();
        }
      }
    });
  }

  /**
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
   */

  private void wireToggleOutline(List<MultiSimSlot> mySlots) {
    mySimView.getMyButtons().getToggleOutlineButton().setOnAction(e -> {
      for (MultiSimSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.toggleOutline();
        }
      }
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

  private ResourceBundle loadLanguageBundle(String choice) {
    return switch (choice) {
      case "Spanish" -> ResourceBundle.getBundle("cellsociety.Languages.Spanish");
      case "Portuguese" -> ResourceBundle.getBundle("cellsociety.Languages.Portuguese");
      default -> ResourceBundle.getBundle("cellsociety.Languages.English");
    };
  }
}
