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
