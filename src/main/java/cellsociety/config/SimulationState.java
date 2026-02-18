package cellsociety.config;

import java.util.List;

/**
 * Immutable snapshot of the overall application state:
 * - Active simulations
 * - UI theme
 * - Simulation speed
 * - Current language
 */
public final class SimulationState {

  private final List<SimulationConfig> activeSimulations;
  private final int theme;
  private final double simulationSpeed;
  private final String language;

  public SimulationState(
      List<SimulationConfig> activeSimulations,
      int theme,
      double simulationSpeed,
      String language
  ) {
    if (activeSimulations == null) {
      throw new IllegalArgumentException("activeSimulations cannot be null");
    }
    if (simulationSpeed <= 0) {
      throw new IllegalArgumentException("simulationSpeed must be positive");
    }

    this.activeSimulations = List.copyOf(activeSimulations);
    this.theme = theme;
    this.simulationSpeed = simulationSpeed;
    this.language = language;
  }

  public List<SimulationConfig> getActiveSimulations() {
    return activeSimulations;
  }

  public int getTheme() {
    return theme;
  }

  public double getSimulationSpeed() {
    return simulationSpeed;
  }

  public String getLanguage() {
    return language;
  }
}
