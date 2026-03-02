package cellsociety.Model.Grid;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only view of a grid of cell states. Extends GridViewData so it can be passed
 * to the view as the minimal interface. Rules and simulation use the full API (getCellState,
 * getNeighbors, etc.); the view uses only GridViewData (getRows, getCols, getCellStateInt).
 */
public interface ReadOnlyGrid extends GridViewData {

  CellState getCellState(int row, int col);

  @Override
  default int getCellStateInt(int row, int col) {
    return getCellState(row, col).toInt();
  }

  boolean isInBounds(int row, int col);

  /** Neighbor strategy is "attached" to the grid (stored by the concrete implementation). */
  NeighborStrategy getNeighborStrategy();

  /**
   * Returns all neighbor states at (row, col). Positions are determined by this grid's
   * NeighborStrategy (which handles bounds and wrapping); the grid only looks up cell states.
   */
  List<CellState> getNeighbors(int row, int col);

  /**
   * Returns a map from each neighbor's position to its cell state at (row, col).
   * Useful when a rule needs both the position and state of neighbors (e.g. movement, claiming).
   */
  default Map<Position, CellState> getNeighborStates(int row, int col) {
    Map<Position, CellState> map = new HashMap<>();
    for (int[] pos : getNeighborStrategy().getNeighborPositions(getRows(), getCols(), row, col)) {
      map.put(new Position(pos[0], pos[1]), getCellState(pos[0], pos[1]));
    }
    return map;
  }

  /**
   * Returns the number of neighbors at (row, col) whose state equals {@code target}.
   */
  default int countNeighborsWithState(int row, int col, CellState target) {
    return countNeighborsWithState(row, col, target.toInt());
  }

  /**
   * Returns the number of neighbors at (row, col) whose state's int value equals {@code targetStateInt}.
   */
  default int countNeighborsWithState(int row, int col, int targetStateInt) {
    int count = 0;
    for (CellState cell : getNeighbors(row, col)) {
      if (cell.toInt() == targetStateInt) count++;
    }
    return count;
  }
}