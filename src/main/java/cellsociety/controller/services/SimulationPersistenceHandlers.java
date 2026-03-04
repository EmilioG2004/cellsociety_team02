package cellsociety.controller.services;

import static cellsociety.view.constants.SimulationUiConstants.*;

import cellsociety.config.SimulationPreferences;
import cellsociety.controller.multiview.SimulationSlot;
import cellsociety.view.utils.LanguageApplier;
import cellsociety.view.SimulationView;
import cellsociety.view.utils.ThemeApplier;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
  private final GridEdgeFactory myGridEdgeFactory;
  private final SimulationLoadingService myLoadingService;
  private final SimulationSavingService mySavingService;
  private final LanguageApplier myLanguageApplier;
  private final ThemeApplier myThemeApplier;
  private final Runnable myPopulationDataChangedCallback;
  private final IntConsumer mySetSlotCountCallback;

  public SimulationPersistenceHandlers(
      SimulationView simView,
      Scene scene,
      ErrorHandler errorHandler,
      NeighborStrategyFactory neighborStrategyFactory,
      GridEdgeFactory gridEdgeFactory,
      Runnable populationDataChangedCallback,
      IntConsumer setSlotCountCallback) {
    mySimView = simView;
    myScene = scene;
    myErrorHandler = errorHandler;
    myNeighborStrategyFactory = neighborStrategyFactory;
    myGridEdgeFactory = gridEdgeFactory;
    myLoadingService = new SimulationLoadingService();
    mySavingService = new SimulationSavingService();
    myLanguageApplier = new LanguageApplier();
    myThemeApplier = new ThemeApplier();
    myPopulationDataChangedCallback = populationDataChangedCallback;
    mySetSlotCountCallback = setSlotCountCallback;
  }

  public void handleSplashLoad(
      List<SimulationSlot> slots,
      Optional<String> selectedNeighborChoice,
      Optional<String> selectedGridEdgeChoice) {
    File xmlFile = mySimView.showOpenConfigDialog();
    if (xmlFile == null) {
      throw new IllegalArgumentException(ERROR_MSG_NO_FILE_SELECTED);
    }
    try {
      myLoadingService.loadConfigIntoSlot(
          0,
          xmlFile,
          slots,
          selectedNeighborChoice,
          myNeighborStrategyFactory,
          selectedGridEdgeChoice,
          myGridEdgeFactory
      );
      myPopulationDataChangedCallback.run();
    } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
      myErrorHandler.handle(ex);
    }
  }

  public void handleLoadState(
      List<SimulationSlot> slots,
      Optional<String> selectedNeighborChoice,
      Optional<String> selectedGridEdgeChoice) {
    File xmlFile = mySimView.showOpenStateDialog();
    if (xmlFile == null) {
      throw new IllegalArgumentException(ERROR_MSG_NO_FILE_SELECTED);
    }
    try {
      SimulationPreferences desiredPreferences = myLoadingService.loadStateIntoSlots(
          xmlFile,
          slots,
          mySetSlotCountCallback,
          value -> mySimView.getMultiSimSlotChoice().setValue(value),
          selectedNeighborChoice,
          myNeighborStrategyFactory,
          selectedGridEdgeChoice,
          myGridEdgeFactory
      );
      loadPreferences(slots, desiredPreferences);
      myPopulationDataChangedCallback.run();
    } catch (ParserConfigurationException | SAXException | IOException ex) {
      myErrorHandler.handle(ex);
    }
  }

  public void handleLoadForSlot(int slotIndex, List<SimulationSlot> slots,
      Optional<String> selectedNeighborChoice, Optional<String> selectedGridEdgeChoice) {
    if (slotIndex < 0 || slotIndex >= MAX_SLOTS) {
      throw new IndexOutOfBoundsException(ERROR_MSG_INVALID_SLOT_INDEX_PREFIX + slotIndex);
    }
    File xmlFile = mySimView.showOpenConfigDialog();
    if (xmlFile == null) {
      throw new IllegalArgumentException(ERROR_MSG_NO_FILE_SELECTED);
    }
    try {
      myLoadingService.loadConfigIntoSlot(
          slotIndex,
          xmlFile,
          slots,
          selectedNeighborChoice,
          myNeighborStrategyFactory,
          selectedGridEdgeChoice,
          myGridEdgeFactory
      );
      myPopulationDataChangedCallback.run();
    } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
      myErrorHandler.handle(ex);
    }
  }

  public void handleSaveForSlot(int slotIndex, List<SimulationSlot> slots) {
    if (slotIndex < 0 || slotIndex >= MAX_SLOTS) {
      throw new IndexOutOfBoundsException(ERROR_MSG_INVALID_SLOT_INDEX_PREFIX + slotIndex);
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
    String themeChoice = myThemeApplier.toThemeChoice(theme);
    String language = desiredPreferences.getLanguage();
    double speed = desiredPreferences.getSimulationSpeed();
    myThemeApplier.apply(myScene, themeChoice);
    mySimView.getMySettings().getMyThemes().setValue(themeChoice);
    mySimView.applyLanguage(myLanguageApplier.resolveBundle(language));
    mySimView.getMySettings().getMyLanguages().setValue(language);
    for (SimulationSlot currentSlot : slots) {
      currentSlot.setSpeed(speed);
    }
  }

}
