package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Utils.NeighborStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  public GameRule(NeighborStrategy neighborStrategy) {
    this(neighborStrategy, Map.of());
  }

  public GameRule(NeighborStrategy neighborStrategy, Map<String, Double> parameters) {
    this.neighborStrategy = neighborStrategy;
    this.parameters = parameters;
  }

  public abstract CellState[][] getNextStates(CellState[][] currentStates);
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
  public abstract Map<CellState, String> getStateColors();

  protected static Map<CellState, String> stateColorsFor(CellState... states) {
    Map<CellState, String> m = new HashMap<>();
    for (CellState s : states) {
      m.put(s, s.getColor());
    }
    return Map.copyOf(m);
  }

  public abstract CellState fromInt(int value);

  public abstract int fromState(CellState state);

  /** Returns in-bounds neighbor states at (row, col) using this rule's neighbor strategy. */
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

  /** Counts neighbors that equal targetState. */
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
