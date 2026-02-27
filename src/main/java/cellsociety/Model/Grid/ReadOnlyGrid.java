package cellsociety.Model.Grid;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import java.util.List;

/**
 * Read-only view of a grid of cell states. Allows consumers (e.g. the view) to read
 * dimensions and per-cell state without depending on the grid's internal representation
 * or receiving a full 2D array.
 */
public interface ReadOnlyGrid {

  int getRows();

  int getCols();

  CellState getCellState(int row, int col);

  boolean isInBounds(int row, int col);

  /** Neighbor strategy is "attached" to the grid (stored by the concrete implementation). */
  NeighborStrategy getNeighborStrategy();

  /**
   * Returns the state at (row + rowOffset, col + colOffset).
   * If out of bounds, returns null.
   */
  CellState getRelativeNeighbor(int row, int col, int rowOffset, int colOffset);

  /**
   * Returns all in-bounds neighbor states according to this grid's strategy.
   */
  List<CellState> getNeighbors(int row, int col);
}