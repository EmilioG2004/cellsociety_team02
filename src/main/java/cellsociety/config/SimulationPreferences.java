package cellsociety.config;

import java.util.Map;

/**
 * Immutable snapshot of simulation preferences (e.g. theme, color mapping, language)
 * Produced by PreferencesLoader.
 */
public final class SimulationPreferences {

  private final Integer theme;
  private final String language;
  private final double simulationSpeed;
  private final Map<Integer, String> colorMap;

  /**
   * Constructs simulation preferences.
   */
  public SimulationPreferences(
      Integer theme,
      String language,
      double simulationSpeed,
      Map<Integer, String> colorMap
  ) {
    this.theme = theme;
    this.language = language;
    this.simulationSpeed = simulationSpeed;
    this.colorMap = colorMap;
  }

  // --- getters only (immutability) ---

  public Integer getTheme() { return theme; }
  public String getLanguage() { return language; }
  public double getSimulationSpeed() { return simulationSpeed; }
  public Map<Integer, String> getColorMap() { return colorMap; }

}
