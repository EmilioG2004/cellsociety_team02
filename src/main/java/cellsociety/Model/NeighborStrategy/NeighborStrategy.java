package cellsociety.Model.NeighborStrategy;

import java.util.ArrayList;
import java.util.List;

/** Defines which grid offsets count as neighbors (e.g. Moore 8, Cardinal 4). */
public interface NeighborStrategy {

  /** Offset for previous row or column (e.g. north, west). */
  int OFFSET_PREV_ROW_OR_COL = -1;
  /** Offset for same row or column. */
  int OFFSET_SAME_ROW_OR_COL = 0;
  /** Offset for next row or column (e.g. south, east). */
  int OFFSET_NEXT_ROW_OR_COL = 1;

  List<int[]> getNeighborOffsets();

  /**
   * Returns the actual grid positions of all neighbors of (r, c).
   * The default implementation clips to grid bounds (no wrapping).
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
