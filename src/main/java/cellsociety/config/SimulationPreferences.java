package cellsociety.config;

/**
 * Immutable snapshot of simulation preferences (e.g. theme, language, speed).
 * Produced by SimulationLoader.
 */
public final class SimulationPreferences {

  private final Integer theme;
  private final String language;
  private final double simulationSpeed;

  public SimulationPreferences(Integer theme, String language, double simulationSpeed) {
    this.theme = theme;
    this.language = language;
    this.simulationSpeed = simulationSpeed;
  }

  public Integer getTheme() { return theme; }
  public String getLanguage() { return language; }
  public double getSimulationSpeed() { return simulationSpeed; }
}