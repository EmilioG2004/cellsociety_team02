package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Utils.NeighborStrategy;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Abstract base class for cellular automaton transition rules. Each simulation type (e.g.,
 * Game of Life, Fire, Percolation) extends this class and defines its own state-transition
 * logic and neighbor semantics.
 */
public abstract class GameRule {

  /** Index of row component in neighbor offset arrays. */
  protected static final int OFFSET_ROW_INDEX = 0;
  /** Index of column component in neighbor offset arrays. */
  protected static final int OFFSET_COL_INDEX = 1;

  /** Defines which offsets from a cell's position count as neighbors (e.g., Moore, Von Neumann). */
  protected final NeighborStrategy neighborStrategy;
  /** Simulation-specific parameters (e.g., probCatch for Fire, threshold for Segregation). */
  protected Map<String, Double> parameters;

  /**
   * Constructs a GameRule with the given neighbor strategy and no parameters.
   *
   * @param neighborStrategy strategy defining which cells are neighbors
   */
  public GameRule(NeighborStrategy neighborStrategy) {
    this(neighborStrategy, Map.of());
  }

  /**
   * Constructs a GameRule with the given neighbor strategy and parameters.
   *
   * @param neighborStrategy strategy defining which cells are neighbors
   * @param parameters simulation-specific parameters (e.g., probCatch, threshold)
   */
  public GameRule(NeighborStrategy neighborStrategy, Map<String, Double> parameters) {
    this.neighborStrategy = neighborStrategy;
    this.parameters = parameters;
  }

  /**
   * Computes the next state for a cell given its current state and neighbor cells.
   *
   * @param currentState the cell's current state
   * @param neighbors list of neighboring cells (read-only)
   * @return the state the cell should transition to in the next generation
   */

  // public abstract CellState getNextState(CellState currentState, List<ReadOnlyCell> neighbors);
  public abstract CellState[][] getNextStates(CellState[][] currentStates);
  /**
   * Returns the neighbor strategy used by this rule. The grid calls this when building
   * the neighbor list for each cell during {@code updateAllCells}.
   *
   * @return the NeighborStrategy for this simulation type
   */
  public NeighborStrategy getNeighborStrategy() {
    return neighborStrategy;
  }
  public void setParameters(Map<String, Double> parameters) {
    this.parameters = parameters;
  }
  public void setParameter(String key, Double value) {
    parameters.put(key, value);
  }
  public Map<String, Double> getParameters() {
    return parameters;
  }
  /**
   * Returns a map from state display keys to CSS/hex colors for rendering the grid.
   *
   * @return map of state to color string
   */
  public abstract Map<CellState, String> getStateColors();

  /**
   * Converts an integer value (from XML config) to the appropriate CellState enum.
   *
   * @param value the integer state value from the config
   * @return the corresponding CellState
   */
  public abstract CellState fromInt(int value);

  /**
   * Counts how many neighbors have the specified state. Useful for rules like Game of Life
   * that depend on the number of live neighbors.
   *
   * @param neighbors list of neighboring cells
   * @param targetState the state to count
   * @return number of neighbors in the target state
   */
  protected List<CellState> getNeighbors(CellState[][] currentStates, int row, int col) {
    List<CellState> neighbors = new ArrayList<CellState>();
    for (int[] offset : neighborStrategy.getNeighborOffsets()) {
      int nr = row + offset[OFFSET_ROW_INDEX];
      int nc = col + offset[OFFSET_COL_INDEX];
      if (nr >= 0 && nr < currentStates.length && nc >= 0 && nc < currentStates[0].length) {
        neighbors.add(currentStates[nr][nc]);
      }
    }
    return neighbors;
  }
  protected static int countNeighborsWithState(List<CellState> neighbors, CellState targetState) {
    int count = 0;
    for (CellState cell : neighbors) {
      if (cell == targetState) {
        count++;
      }
    }
    return count;
  }
  public abstract CellState getNextStateInCycle(CellState current);
}
