package cellsociety.config;

import java.util.List;

/**
 * Immutable snapshot of the overall application state, with active simulations and preferences
 */
public final class SimulationState {

  private final List<SimulationConfig> activeSimulations;
  private final SimulationPreferences preferences;

  public SimulationState(
      List<SimulationConfig> activeSimulations,
      SimulationPreferences preferences
  ) {
    if (activeSimulations == null) {
      throw new IllegalArgumentException("activeSimulations cannot be null");
    }
    if (preferences == null) {
      throw new IllegalArgumentException("preferences cannot be null");
    }

    this.activeSimulations = List.copyOf(activeSimulations);
    this.preferences = preferences;
  }

  public List<SimulationConfig> getActiveSimulations() {
    return activeSimulations;
  }
  public SimulationPreferences getPreferences() { return preferences; }
}
