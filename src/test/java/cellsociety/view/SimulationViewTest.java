package cellsociety.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.config.SimulationConfig;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.service.query.EmptyNodeQueryException;
import util.DukeApplicationTest;

public class SimulationViewTest extends DukeApplicationTest {

  private SimulationView myView;
  private Scene myScene;
  private ResourceBundle myEnglishBundle;

  @Override
  public void start(Stage stage) {
    myEnglishBundle = ResourceBundle.getBundle("cellsociety.Languages.English");
    myView = new SimulationView(myEnglishBundle);
    myScene = new Scene(myView.getRoot(), 1200, 750);
    stage.setScene(myScene);
    stage.show();
  }

  @Test
  void OpenSettings_WhenSettingsPreviouslyClosed_SettingsSplashBecomesVisibleAndManaged() {
    runAsJFXAction(() -> myView.closeSettings());

    runAsJFXAction(() -> myView.openSettings());

    assertTrue(myView.getMySettings().isVisible());
    assertTrue(myView.getMySettings().isManaged());
  }

  @Test
  void CloseSettings_WhenSettingsOpen_SettingsSplashBecomesHiddenAndUnmanaged() {
    runAsJFXAction(() -> myView.openSettings());

    runAsJFXAction(() -> myView.closeSettings());

    assertFalse(myView.getMySettings().isVisible());
    assertFalse(myView.getMySettings().isManaged());
  }

  @Test
  void ApplyTheme_WhenDarkThemeSelected_DarkStylesheetIsApplied() {
    runAsJFXAction(() -> myView.applyTheme(myScene, "Dark"));

    assertThemeApplied("dark.css");
  }

  @Test
  void ApplyTheme_WhenUnknownThemeSelected_LightStylesheetIsAppliedByDefault() {
    runAsJFXAction(() -> myView.applyTheme(myScene, "UnknownThemeChoice"));

    assertThemeApplied("light.css");
  }

  @Test
  void ApplyTheme_WhenSceneIsNull_IllegalArgumentExceptionThrown() {
    assertThrows(IllegalArgumentException.class, () -> myView.applyTheme(null, "Dark"));
  }

  @Test
  void ApplyLanguage_WhenSpanishBundleProvided_SettingsTextsUpdateToSpanish() {
    ResourceBundle spanishBundle = ResourceBundle.getBundle("cellsociety.Languages.Spanish");

    runAsJFXAction(() -> myView.applyLanguage(spanishBundle));

    assertEquals(
        spanishBundle.getString("button.applyChanges"),
        myView.getMySettings().getMyApplyChanges().getText()
    );
    assertEquals(
        spanishBundle.getString("button.start"),
        myView.getMyButtons().getStartButton().getText()
    );
  }
  @Test
  void ApplyLanguage_WhenPortugueseBundleProvided_ControlTextsUpdateToPortuguese() {
    ResourceBundle portugueseBundle = ResourceBundle.getBundle("cellsociety.Languages.Portuguese");

    runAsJFXAction(() -> myView.applyLanguage(portugueseBundle));

    assertEquals(
        portugueseBundle.getString("button.loadSimulation"),
        myView.getMySettings().getMyLoadButton().getText()
    );
    assertEquals(
        portugueseBundle.getString("button.pause"),
        myView.getMyButtons().getPauseButton().getText()
    );
  }

  @Test
  void ApplyLanguage_WhenBundleMissingRequiredKeys_MissingResourceExceptionThrown() {
    ResourceBundle missingKeysBundle = new ResourceBundle() {
      @Override
      protected Object handleGetObject(String key) {
        if ("settings.title".equals(key)) {
          return "Settings";
        }
        throw new MissingResourceException("Missing key", "TestBundle", key);
      }

      @Override
      public java.util.Enumeration<String> getKeys() {
        return java.util.Collections.enumeration(List.of("settings.title"));
      }
    };

    assertThrows(MissingResourceException.class, () -> myView.applyLanguage(missingKeysBundle));
  }

  @Test
  void ApplyLanguage_WhenBundleIsNull_IllegalArgumentExceptionThrown() {
    assertThrows(IllegalArgumentException.class, () -> myView.applyLanguage(null));
  }

  @Test
  void ShowAboutSplash_WhenValidConfigAndNeighborProvided_AboutDialogIsAddedToCenterPane() {
    SimulationConfig validConfig = buildValidConfig();

    runAsJFXAction(() -> myView.showAboutSplash(validConfig, "MooreNeighborStrategy"));

    assertNotNull(myView.getMyAboutDialog().getParent());
    lookup("Neighbor Strategy: MooreNeighborStrategy").queryText();
  }

  @Test
  void ShowAboutSplash_WhenCalledTwice_AboutDialogContentIsUpdatedWithLatestNeighborStrategy() {
    SimulationConfig validConfig = buildValidConfig();

    runAsJFXAction(() -> myView.showAboutSplash(validConfig, "MooreNeighborStrategy"));
    runAsJFXAction(() -> myView.showAboutSplash(validConfig, "VonNeumannNeighborStrategy"));

    lookup("Neighbor Strategy: VonNeumannNeighborStrategy").queryText();
  }

  @Test
  void ShowAboutSplash_WhenConfigIsNull_IllegalArgumentExceptionThrown() {
    assertThrows(
        IllegalArgumentException.class,
        () -> myView.showAboutSplash(null, "MooreNeighborStrategy")
    );
  }

  @Test
  void ShowAboutSplash_WhenNeighborStrategyIsBlank_IllegalArgumentExceptionThrown() {
    SimulationConfig validConfig = buildValidConfig();

    assertThrows(
        IllegalArgumentException.class,
        () -> myView.showAboutSplash(validConfig, "")
    );
  }

  @Test
  void SetCenterContent_WhenNodeProvided_NodeIsDisplayedInCenterCard() {
    Label contentNode = new Label("CENTER_CONTENT_LABEL_ONE");

    runAsJFXAction(() -> myView.setCenterContent(contentNode));

    lookup("CENTER_CONTENT_LABEL_ONE").queryLabeled();
  }

  @Test
  void SetCenterContent_WhenCalledMultipleTimes_LatestNodeReplacesPreviousNode() {
    Label firstContentNode = new Label("CENTER_CONTENT_LABEL_FIRST");
    Label secondContentNode = new Label("CENTER_CONTENT_LABEL_SECOND");

    runAsJFXAction(() -> myView.setCenterContent(firstContentNode));
    runAsJFXAction(() -> myView.setCenterContent(secondContentNode));

    lookup("CENTER_CONTENT_LABEL_SECOND").queryLabeled();
    assertThrows(
        EmptyNodeQueryException.class,
        () -> lookup("CENTER_CONTENT_LABEL_FIRST").queryLabeled()
    );
  }

  @Test
  void SetCenterContent_WhenNodeIsNull_IllegalArgumentExceptionThrown() {
    assertThrows(IllegalArgumentException.class, () -> myView.setCenterContent(null));
  }

  @Test
  void SetSpeedLabel_WhenSpeedValueChanges_LabelDisplaysNewMultiplier() {
    runAsJFXAction(() -> myView.setSpeedLabel(2.0));

    String expectedSpeedLabel = MessageFormat.format(
        myEnglishBundle.getString("label.currentSpeed"),
        2.0
    );
    assertEquals(expectedSpeedLabel, myView.getMyButtons().getSpeedLabel().getText());
  }

  @Test
  void SetSpeedLabel_WhenCalledRepeatedly_LabelMatchesMostRecentValue() {
    runAsJFXAction(() -> myView.setSpeedLabel(0.5));
    runAsJFXAction(() -> myView.setSpeedLabel(3.0));

    String expectedSpeedLabel = MessageFormat.format(
        myEnglishBundle.getString("label.currentSpeed"),
        3.0
    );
    assertEquals(expectedSpeedLabel, myView.getMyButtons().getSpeedLabel().getText());
  }

  private void assertThemeApplied(String expectedCssFileSuffix) {
    assertTrue(myScene.getStylesheets().stream().anyMatch(css -> css.endsWith(expectedCssFileSuffix)));
  }

  private SimulationConfig buildValidConfig() {
    int[][] initialStates = new int[][]{{0, 1}, {1, 0}};
    Map<String, Double> parameters = new HashMap<>();
    parameters.put("exampleThreshold", 0.3);
    return new SimulationConfig(
        "TestSimulation",
        "cellsociety.Model.GameRules.ConwayGameOfLifeRule",
        "cellsociety.Model.NeighborStrategy.MooreNeighborStrategy",
        "Test Title",
        "Test Author",
        "Test Description",
        2,
        2,
        initialStates,
        parameters
    );
  }
}
