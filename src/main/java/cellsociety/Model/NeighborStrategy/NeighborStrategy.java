package cellsociety.Model.NeighborStrategy;

import cellsociety.Model.GridEdge.GridEdge;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines which grid positions count as neighbors for a given cell (e.g. Moore 8, Von Neumann 4).
 * Neighbor shape is defined by {@link #getNeighborOffsets()}; edge behavior (clip, wrap, etc.)
 * is delegated to a {@link GridEdge} via {@link #getGridEdge()}.
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

  /** Returns the edge policy used by this strategy. */
  GridEdge getGridEdge();

  /** Sets the edge policy used by this strategy. */
  void setGridEdge(GridEdge gridEdge);

  /**
   * Returns the grid positions of all neighbors of (r, c) for a grid of size rows x cols.
   * Edge handling is delegated to {@link GridEdge#resolve}.
   */
  default List<int[]> getNeighborPositions(int rows, int cols, int r, int c) {
    return getGridEdge().resolve(applyOffsets(getNeighborOffsets(), r, c), rows, cols);
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
    return getGridEdge().resolve(applyOffsets(getExpandedOffsets(rows, cols, vision), r, c), rows, cols);
  }

  /** Adds each (dr, dc) offset to the position (r, c), returning raw (possibly out-of-bounds) positions. */
  private List<int[]> applyOffsets(List<int[]> offsets, int r, int c) {
    List<int[]> positions = new ArrayList<>();
    for (int[] offset : offsets) {
      positions.add(new int[]{r + offset[0], c + offset[1]});
    }
    return positions;
  }
}
