package cellsociety.Model.NeighborStrategy;

import java.util.List;

/**
 * Only the four adjacent corners (diagonals): NW, NE, SW, SE.
 * Excludes the four edge-adjacent neighbors (N, S, E, W).
 *
 * @author Michael Lei
 */
public class DiagonalNeighborStrategy extends AbstractNeighborStrategy {

  private static final List<int[]> DIAGONAL_OFFSETS = List.of(
      new int[]{OFFSET_PREV_ROW_OR_COL, OFFSET_PREV_ROW_OR_COL},
      new int[]{OFFSET_PREV_ROW_OR_COL, OFFSET_NEXT_ROW_OR_COL},
      new int[]{OFFSET_NEXT_ROW_OR_COL, OFFSET_PREV_ROW_OR_COL},
      new int[]{OFFSET_NEXT_ROW_OR_COL, OFFSET_NEXT_ROW_OR_COL}
  );

  @Override
  public List<int[]> getNeighborOffsets() {
    return DIAGONAL_OFFSETS;
  }
}
