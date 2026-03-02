package cellsociety.Model.NeighborStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines which grid positions count as neighbors for a given cell (e.g. Moore 8, Von Neumann 4).
 * Responsible for all neighbor logic: which directions and how bounds/wrapping are applied.
 * Callers (e.g. Grid, GameRule) use {@link #getNeighborPositions(int, int, int, int)} to get
 * neighbor positions, then look up cell states at those positions.
 *
 * @author Michael Lei
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
    return offsetsToPositions(getNeighborOffsets(), rows, cols, r, c);
  }

  default List<int[]> getExpandedOffsets(int rows, int cols, int vision) {
    List<int[]> baseOffsets = getNeighborOffsets();
    List<int[]> expandedOffsets = new ArrayList<>();
    for (int[] offset : baseOffsets) {
      expandedOffsets.add(offset);
      int row_inc = offset[0];
      int col_inc = offset[1];
      for (int i = 1; i <= vision; i++) {
        int new_row_inc = row_inc < 0 ? row_inc - i : row_inc + i;
        int new_col_inc = col_inc < 0 ? col_inc - i : col_inc + i;
        expandedOffsets.add(new int[]{new_row_inc, new_col_inc});
      }
    }
    return expandedOffsets;
  }

  default List<int[]> getNeighborPositions(int rows, int cols, int r, int c, int vision) {
    return offsetsToPositions(getExpandedOffsets(rows, cols, vision), rows, cols, r, c);
  }

  /** Converts a list of (dr, dc) offsets into in-bounds positions (r+dr, c+dc). */
  private List<int[]> offsetsToPositions(List<int[]> offsets, int rows, int cols, int r, int c) {
    List<int[]> positions = new ArrayList<>();
    for (int[] offset : offsets) {
      int nr = r + offset[0];
      int nc = c + offset[1];
      if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
        positions.add(new int[]{nr, nc});
      }
    }
    return positions;
  }
}
