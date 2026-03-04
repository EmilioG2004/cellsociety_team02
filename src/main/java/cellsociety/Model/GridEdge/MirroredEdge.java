package cellsociety.Model.GridEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Treats grid edges as mirrors: out-of-bounds positions are reflected back
 * into the grid so that a full neighborhood is formed by "reflected" copies
 * of cells adjacent to the edge.
 *
 * @author Michael Lei
 */
public class MirroredEdge implements GridEdge {

  @Override
  public List<int[]> resolve(List<int[]> rawPositions, int rows, int cols) {
    List<int[]> result = new ArrayList<>();
    for (int[] pos : rawPositions) {
      int r = reflectIntoRange(pos[0], rows);
      int c = reflectIntoRange(pos[1], cols);
      result.add(new int[]{r, c});
    }
    return result;
  }

  /**
   * Reflects an index into the range [0, size) by treating 0 and size-1 as
   * mirrors; repeated reflections are applied until the index is in bounds.
   */
  private static int reflectIntoRange(int index, int size) {
    while (true) {
      if (index >= 0 && index < size) {
        return index;
      }
      if (index < 0) {
        index = -index;
      } else {
        index = 2 * (size - 1) - index;
      }
    }
  }
}
