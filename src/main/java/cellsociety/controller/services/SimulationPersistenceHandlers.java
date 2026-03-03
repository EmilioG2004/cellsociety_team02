package cellsociety.controller.services;

import static cellsociety.view.constants.SimulationUiConstants.MAX_SLOTS;
import static cellsociety.view.constants.ResourcePaths.BUNDLE_FOLDER;

import cellsociety.config.SimulationPreferences;
import cellsociety.controller.multiview.SimulationSlot;
import cellsociety.view.SimulationView;
import cellsociety.view.constants.ResourcePaths;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.IntConsumer;
import javafx.scene.Scene;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Handles load/save controller actions and delegates lower-level work to loading/saving services.
 */
public class SimulationPersistenceHandlers {

  private final SimulationView mySimView;
  private final Scene myScene;
  private final ErrorHandler myErrorHandler;
  private final NeighborStrategyFactory myNeighborStrategyFactory;
  private final SimulationLoadingService myLoadingService;
  private final SimulationSavingService mySavingService;
  private final Runnable myPopulationDataChangedCallback;
  private final IntConsumer mySetSlotCountCallback;

  public SimulationPersistenceHandlers(
      SimulationView simView,
      Scene scene,
      ErrorHandler errorHandler,
      NeighborStrategyFactory neighborStrategyFactory,
      Runnable populationDataChangedCallback,
      IntConsumer setSlotCountCallback) {
    mySimView = simView;
    myScene = scene;
    myErrorHandler = errorHandler;
    myNeighborStrategyFactory = neighborStrategyFactory;
    myLoadingService = new SimulationLoadingService();
    mySavingService = new SimulationSavingService();
    myPopulationDataChangedCallback = populationDataChangedCallback;
    mySetSlotCountCallback = setSlotCountCallback;
  }

  public void handleSplashLoad(List<SimulationSlot> slots, Optional<String> selectedNeighborChoice) {
    File xmlFile = mySimView.showOpenConfigDialog();
    if (xmlFile == null) {
      throw new IllegalArgumentException("No file selected.");
    }
    try {
      myLoadingService.loadConfigIntoSlot(
          0, xmlFile, slots, selectedNeighborChoice, myNeighborStrategyFactory);
      myPopulationDataChangedCallback.run();
    } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
      myErrorHandler.handle(ex);
    }
  }

  public void handleLoadState(List<SimulationSlot> slots, Optional<String> selectedNeighborChoice) {
    File xmlFile = mySimView.showOpenConfigDialog();
    if (xmlFile == null) {
      throw new IllegalArgumentException("No file selected.");
    }
    try {
      SimulationPreferences desiredPreferences = myLoadingService.loadStateIntoSlots(
          xmlFile,
          slots,
          mySetSlotCountCallback,
          value -> mySimView.getMultiSimSlotChoice().setValue(value),
          selectedNeighborChoice,
          myNeighborStrategyFactory
      );
      loadPreferences(slots, desiredPreferences);
      myPopulationDataChangedCallback.run();
    } catch (ParserConfigurationException | SAXException | IOException ex) {
      myErrorHandler.handle(ex);
    }
  }

  public void handleLoadForSlot(int slotIndex, List<SimulationSlot> slots,
      Optional<String> selectedNeighborChoice) {
    if (slotIndex < 0 || slotIndex >= MAX_SLOTS) {
      throw new IndexOutOfBoundsException("Invalid slot index: " + slotIndex);
    }
    File xmlFile = mySimView.showOpenConfigDialog();
    if (xmlFile == null) {
      throw new IllegalArgumentException("No file selected.");
    }
    try {
      myLoadingService.loadConfigIntoSlot(
          slotIndex, xmlFile, slots, selectedNeighborChoice, myNeighborStrategyFactory);
      myPopulationDataChangedCallback.run();
    } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
      myErrorHandler.handle(ex);
    }
  }

  public void handleSaveForSlot(int slotIndex, List<SimulationSlot> slots) {
    if (slotIndex < 0 || slotIndex >= MAX_SLOTS) {
      throw new IndexOutOfBoundsException("Invalid slot index: " + slotIndex);
    }
    try {
      File saveDestination = mySimView.showSaveConfigDialog();
      mySavingService.saveConfigForSlot(slotIndex, slots, saveDestination);
    } catch (RuntimeException ex) {
      myErrorHandler.handle(ex);
    }
  }

  public void handleSaveAll(List<SimulationSlot> slots) {
    try {
      File saveDestination = mySimView.showSaveConfigDialog();
      mySavingService.saveAllState(
          slots,
          mySimView.getMySettings().getMyThemes().getValue(),
          mySimView.getMySettings().getMyLanguages().getValue(),
          saveDestination
      );
    } catch (RuntimeException ex) {
      myErrorHandler.handle(ex);
    }
  }

  private void loadPreferences(List<SimulationSlot> slots, SimulationPreferences desiredPreferences) {
    int theme = desiredPreferences.getTheme();
    String themeChoice = mapThemeCodeToChoice(theme);
    String language = desiredPreferences.getLanguage();
    String languageBundle = BUNDLE_FOLDER + language;
    double speed = desiredPreferences.getSimulationSpeed();
    mySimView.applyTheme(myScene, themeChoice);
    mySimView.getMySettings().getMyThemes().setValue(themeChoice);
    mySimView.applyLanguage(ResourceBundle.getBundle(languageBundle));
    mySimView.getMySettings().getMyLanguages().setValue(language);
    for (SimulationSlot currentSlot : slots) {
      currentSlot.setSpeed(speed);
    }
  }

  private String mapThemeCodeToChoice(int themeCode) {
    if (themeCode == 1) return ResourcePaths.THEME_DARK;
    if (themeCode == 2) return ResourcePaths.THEME_DUKE;
    return ResourcePaths.THEME_LIGHT;
  }
}
