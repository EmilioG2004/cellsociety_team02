package cellsociety.Model.NeighborStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines which grid positions count as neighbors for a given cell (e.g. Moore 8, Von Neumann 4).
 * Responsible for all neighbor logic: which directions and how bounds/wrapping are applied.
 * Callers (e.g. Grid, GameRule) use {@link #getNeighborPositions(int, int, int, int)} to get
 * neighbor positions, then look up cell states at those positions.
 */
public interface NeighborStrategy {

  /** Offset for previous row or column (e.g. north, west). */
  int OFFSET_PREV_ROW_OR_COL = -1;
  /** Offset for same row or column. */
  int OFFSET_SAME_ROW_OR_COL = 0;
  /** Offset for next row or column (e.g. south, east). */
  int OFFSET_NEXT_ROW_OR_COL = 1;

  /** Used by strategy implementations to compute neighbor positions. */
  List<int[]> getNeighborOffsets();

  /**
   * Returns the grid positions of all neighbors of (r, c) for a grid of size rows×cols.
   * Handles bounds (e.g. clip out-of-bounds or wrap). Callers use these positions to get cell states.
   */
  default List<int[]> getNeighborPositions(int rows, int cols, int r, int c) {
    List<int[]> positions = new ArrayList<>();
    for (int[] offset : getNeighborOffsets()) {
      int nr = r + offset[0];
      int nc = c + offset[1];
      if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
        positions.add(new int[]{nr, nc});
      }
    }
    return positions;
  }
}
