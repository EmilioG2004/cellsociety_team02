package cellsociety.Model.NeighborStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Moore neighborhood extended to 2 cells away: a cell's neighbors are all adjacent cells
 *  and all cells adjacent to those.
 * So every cell in the 5×5 square centered on the cell, excluding the cell itself (24 neighbors).
 *
 * @author Michael Lei
 */
public class MooreNeighborStrategyRadius2 extends AbstractNeighborStrategy {

  private static final int RADIUS = 2;
  private static final List<int[]> OFFSETS = buildOffsets();

  private static List<int[]> buildOffsets() {
    List<int[]> list = new ArrayList<>();
    for (int dr = -RADIUS; dr <= RADIUS; dr++) {
      for (int dc = -RADIUS; dc <= RADIUS; dc++) {
        if (dr == 0 && dc == 0) continue;
        list.add(new int[]{dr, dc});
      }
    }
    return list;
  }

  @Override
  public List<int[]> getNeighborOffsets() {
    return OFFSETS;
  }

  /**
   * Radius-2 Moore has a fixed neighborhood; vision does not expand it further.
   * Returns the same 24 positions as the no-vision overload.
   */
  @Override
  public List<int[]> getNeighborPositions(int rows, int cols, int r, int c, int vision) {
    return getNeighborPositions(rows, cols, r, c);
  }
}
