package cellsociety.Model.GridEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Cylindrical boundary: wraps in the column dimension (toroidal) and clips
 * in the row dimension (finite). Moving off the left/right reappears on the
 * opposite side; moving off the top/bottom is out of bounds and discarded.
 *
 * @author Michael Lei
 */
public class CylindricalEdge implements GridEdge {

  @Override
  public List<int[]> resolve(List<int[]> rawPositions, int rows, int cols) {
    List<int[]> result = new ArrayList<>();
    for (int[] pos : rawPositions) {
      int r = pos[0];
      int nc = Math.floorMod(pos[1], cols);
      if (r >= 0 && r < rows) {
        result.add(new int[]{r, nc});
      }
    }
    return result;
  }
}
