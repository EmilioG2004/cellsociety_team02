package cellsociety.controller;

import static cellsociety.view.constants.BundleKeys.BUTTON_DIRECTION_BACKWARD;
import static cellsociety.view.constants.BundleKeys.BUTTON_DIRECTION_FORWARD;
import cellsociety.config.ConfigLoader;
import cellsociety.config.ConfigSaver;
import cellsociety.view.SimulationView;
import cellsociety.view.constants.ResourcePaths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.scene.Scene;

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
  private final Consumer<ResourceBundle> myBundleCallback;

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
  public WiringManager(SimulationView simView, Scene scene, Consumer<ResourceBundle> bundleCallback) {
    mySimView = simView;
    myScene = scene;
    myBundleCallback = bundleCallback;
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
  public void wireAll(List<SimulationSlot> mySlots) {
    wireAboutDialogClose();
    wireApplyChanges(mySlots);
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
  }

  private void wireAboutDialogClose() {
    mySimView.getMyAboutDialog().getMyCloseButton().setOnAction(c ->
        mySimView.getMyAboutDialog().closeInfo()
    );
  }

  private void wireForward(List<SimulationSlot> mySlots) {
    mySimView.getMyButtons().getForwardButton().setOnAction(e -> {
      for (SimulationSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.step();
        }
      }
    });
  }

  private void wireBackward(List<SimulationSlot> mySlots) {
    mySimView.getMyButtons().getBackButton().setOnAction(e -> {
      for (SimulationSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.stepBack();
        }
      }
    });
  }

  private void wireChangeDirection(List<SimulationSlot> mySlots) {
    var buttons = mySimView.getMyButtons();
    var dirButton = buttons.getChangeDirectionButton();
    var bundle = mySimView.getMyBundle();

    dirButton.setOnAction(e -> {
      Boolean forward = null;
      for (SimulationSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          forward = slot.isForwardDirection();
          break;
        }
      }

      boolean newDirection = (forward == null) ? true : !forward;

      for (SimulationSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.setDirection(newDirection);
        }
      }

      dirButton.setText(bundle.getString(
          newDirection ? BUTTON_DIRECTION_FORWARD : BUTTON_DIRECTION_BACKWARD
      ));
    });
  }

  private void wireStart(List<SimulationSlot> mySlots) {
    mySimView.getMyButtons().getStartButton().setOnAction(e -> {
      for (SimulationSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.start();
        }
      }    });
  }

  private void wirePause(List<SimulationSlot> mySlots) {
    mySimView.getMyButtons().getPauseButton().setOnAction(e -> {
      for (SimulationSlot slot : mySlots) {
        slot.pause();
      }
    });
  }

  private void wireSpeedUp(List<SimulationSlot> mySlots) {
    mySimView.getMyButtons().getSpeedUpButton().setOnAction(e -> {
      if (mySimView.getMySpeedMultiplier() >= 4) return;
      for (SimulationSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.setSpeed(slot.getStepsPerSecond() * 2.0);
        }
      }
      mySimView.doubleMySpeed();
      mySimView.updateSpeedLabel();
    });
  }

  private void wireSlowDown(List<SimulationSlot> mySlots) {
    mySimView.getMyButtons().getSlowDownButton().setOnAction(e -> {
      if (mySimView.getMySpeedMultiplier() <= 0.25) return;
      for (SimulationSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.setSpeed(slot.getStepsPerSecond() / 2.0);
        }
      }
      mySimView.halveMySpeed();
      mySimView.updateSpeedLabel();
    });
  }

  private void wireReset(List<SimulationSlot> mySlots) {
    mySimView.getMyButtons().getResetButton().setOnAction(e -> {
      for (SimulationSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.reset();
        }
      }
    });
  }

  private void wireToggleOutline(List<SimulationSlot> mySlots) {
    mySimView.getMyButtons().getToggleOutlineButton().setOnAction(e -> {
      for (SimulationSlot slot : mySlots) {
        if (slot.hasSimulation()) {
          slot.toggleOutline();
        }
      }
    });
  }

  private void wireApplyChanges(List<SimulationSlot> mySlots) {
    mySimView.getMySettings().getMyApplyChanges().setOnAction(e -> {
      String choice = mySimView.getMySettings().getMyLanguages().getValue();
      ResourceBundle chosenBundle = loadLanguageBundle(choice);
      mySimView.applyLanguage(chosenBundle);
      for (SimulationSlot slot: mySlots){
        slot.getMyPanel().applyLanguage(chosenBundle);
      }
      myBundleCallback.accept(chosenBundle);

      String themeChoice = mySimView.getMySettings().getMyThemes().getValue();
      mySimView.applyTheme(myScene, themeChoice);

      mySimView.closeSettings();
    });
  }

  private void wireSettings() {
    mySimView.getMyButtons().getSettingsButton().setOnAction(e -> mySimView.openSettings());
  }


  private ResourceBundle loadLanguageBundle(String choice) {
    return switch (choice) {
      case ResourcePaths.LANGUAGE_SPANISH -> ResourceBundle.getBundle(ResourcePaths.BUNDLE_SPANISH);
      case ResourcePaths.LANGUAGE_PORTUGUESE -> ResourceBundle.getBundle(ResourcePaths.BUNDLE_PORTUGUESE);
      default -> ResourceBundle.getBundle(ResourcePaths.BUNDLE_ENGLISH);
    };
  }
}
