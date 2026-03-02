package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.Map;

/**
 * Abstract base for cellular automaton rules. All neighbor logic lives in the grid;
 * the rule only takes the current grid and returns the next grid, plus parameters and cell-state factory.
 *
 * @author Michael Lei
 */
public abstract class GameRule implements CellFactory {

  /** Simulation-specific parameters (e.g., probCatch for Fire, threshold for Segregation). */
  protected Map<String, Double> parameters;

  public GameRule(Map<String, Double> parameters) {
    this.parameters = parameters != null ? parameters : Map.of();
  }

  /**
   * Advances the simulation one step. Uses only the grid API (e.g. getCellState, getNeighbors);
   * neighbor semantics are entirely defined by the grid.
   */
  public abstract Grid getNextGrid(Grid currentGrid);

  /**
   * Default neighbor strategy for this rule type. Used by the factory when building the grid
   * if config does not specify a strategy. Rules do not use this for transition logic.
   */
  public abstract NeighborStrategy getDefaultNeighborStrategy();

  public void setParameters(Map<String, Double> parameters) {
    this.parameters = parameters != null ? parameters : Map.of();
  }

  public void setParameter(String key, Double value) {
    parameters.put(key, value);
  }

  public Map<String, Double> getParameters() {
    return parameters;
  }

  /** Creates a cell state from a config integer code. */
  public abstract CellState fromInt(int value);
}
