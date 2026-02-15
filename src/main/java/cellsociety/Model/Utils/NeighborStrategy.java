package cellsociety.Model.Utils;

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
}
