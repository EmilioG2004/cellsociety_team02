package cellsociety.view.utils;

import static cellsociety.view.constants.ResourcePaths.CSS_BASE;
import static cellsociety.view.constants.ResourcePaths.CSS_DARK;
import static cellsociety.view.constants.ResourcePaths.CSS_DUKE;
import static cellsociety.view.constants.ResourcePaths.CSS_LIGHT;
import static cellsociety.view.constants.ResourcePaths.THEME_DARK;
import static cellsociety.view.constants.ResourcePaths.THEME_DUKE;
import static cellsociety.view.constants.ResourcePaths.THEME_LIGHT;

import cellsociety.view.SimulationView;
import java.util.Map;
import javafx.scene.Scene;

/**
 * Centralized theme mapping and stylesheet application.
 */
public class ThemeApplier {

  private final String myBaseCss;
  private final Map<String, String> myThemeCssByChoice;
  private final Map<Integer, String> myThemeChoiceByCode;
  private final Map<String, Integer> myThemeCodeByChoice;

  public ThemeApplier() {
    myBaseCss = SimulationView.class.getResource(CSS_BASE).toExternalForm();

    String lightCss = SimulationView.class.getResource(CSS_LIGHT).toExternalForm();
    String darkCss = SimulationView.class.getResource(CSS_DARK).toExternalForm();
    String dukeCss = SimulationView.class.getResource(CSS_DUKE).toExternalForm();

    myThemeCssByChoice = Map.of(
        THEME_LIGHT, lightCss,
        THEME_DARK, darkCss,
        THEME_DUKE, dukeCss
    );

    myThemeChoiceByCode = Map.of(
        0, THEME_LIGHT,
        1, THEME_DARK,
        2, THEME_DUKE
    );

    myThemeCodeByChoice = Map.of(
        THEME_LIGHT, 0,
        THEME_DARK, 1,
        THEME_DUKE, 2
    );
  }

  public void apply(Scene scene, String themeChoice) {
    scene.getStylesheets().setAll(myBaseCss, resolveThemeCss(themeChoice));
  }

  public String toThemeChoice(int themeCode) {
    return myThemeChoiceByCode.getOrDefault(themeCode, THEME_LIGHT);
  }

  public int toThemeCode(String themeChoice) {
    return myThemeCodeByChoice.getOrDefault(themeChoice, 0);
  }

  private String resolveThemeCss(String themeChoice) {
    return myThemeCssByChoice.getOrDefault(themeChoice, myThemeCssByChoice.get(THEME_LIGHT));
  }
}
