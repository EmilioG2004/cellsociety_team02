package cellsociety.Model;

import cellsociety.Model.Utils.NeighborStrategy;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for cellular automaton transition rules. Each simulation type (e.g.,
 * Game of Life, Fire, Percolation) extends this class and defines its own state-transition
 * logic and neighbor semantics.
 */
public abstract class GameRule {
  /** Defines which offsets from a cell's position count as neighbors (e.g., Moore, Von Neumann). */
  protected final NeighborStrategy neighborStrategy;
  /** Simulation-specific parameters (e.g., probCatch for Fire, threshold for Segregation). */
  protected final Map<String, String> parameters;

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
  public GameRule(NeighborStrategy neighborStrategy, Map<String, String> parameters) {
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
  public abstract int getNextState(int currentState, List<ReadOnlyCell> neighbors);

  /**
   * Returns the neighbor strategy used by this rule. The grid calls this when building
   * the neighbor list for each cell during {@code updateAllCells}.
   *
   * @return the NeighborStrategy for this simulation type
   */
  public NeighborStrategy getNeighborStrategy() {
    return neighborStrategy;
  }
  /**
   * Returns a map from state display keys to CSS/hex colors for rendering the grid.
   *
   * @return map of state name to color string
   */
  public abstract Map<Integer, String> getStateColors();

  /**
   * Counts how many neighbors have the specified state. Useful for rules like Game of Life
   * that depend on the number of live neighbors.
   *
   * @param neighbors list of neighboring cells
   * @param targetState the state to count
   * @return number of neighbors in the target state
   */
  protected int countNeighborsWithState(List<ReadOnlyCell> neighbors, int targetState) {
    int count = 0;
    for (ReadOnlyCell cell : neighbors) {
      if (cell.getState()==targetState) {
        count++;
      }
    }
    return count;
  }
}
