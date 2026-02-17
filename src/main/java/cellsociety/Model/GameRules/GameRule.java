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
  /** simple helper method used by getNeighborPositions to return the new row or column given an offset and using wrap around */
  protected int getNewPosition(int total, int i, int offset)
  {
    int newPosition = i + offset;
    if(newPosition < 0)
    {
      newPosition = total - 1;
    }
    else if(newPosition >= total)
    {
      newPosition = 0;
    }
    return newPosition;
  }
  /** Returns positions of a given position's neighbors with wrap around*/
  protected List<int[]> getNeighborPositions(int rows, int cols, int r, int c)
  {
    List<int[]> neighborPositions= new ArrayList<>();
    for (int[] offset : neighborStrategy.getNeighborOffsets())
    {
      int nr = getNewPosition(rows, r, offset[OFFSET_ROW_INDEX]);
      int nc = getNewPosition(cols, c, offset[OFFSET_COL_INDEX]);
      neighborPositions.add(new int[] { nr, nc});
    }
    return neighborPositions;
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
  /** returns neighbor positions that equal targetState */
  protected List<int[]> getNeighborPositionsForState(List<int[]> neighborPositions, CellState targetState, CellState[][] currentStates)
  {
    List<int[]> targetPositions= new ArrayList<>();
    for (int[] neighborPos : neighborPositions)
    {
      if(currentStates[neighborPos[0]][neighborPos[1]]==targetState)
      {
        targetPositions.add(neighborPos);
      }
    }
    return targetPositions;
  }
  public abstract CellState getNextStateInCycle(CellState current);
}
