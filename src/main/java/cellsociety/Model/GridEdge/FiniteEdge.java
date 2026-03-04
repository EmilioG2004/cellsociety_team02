package cellsociety.Model.GridEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Clips positions that fall outside the grid boundary (discards out-of-bounds neighbors).
 *
 * @author Michael Lei
 */
public class FiniteEdge implements GridEdge {

  @Override
  public List<int[]> resolve(List<int[]> rawPositions, int rows, int cols) {
    List<int[]> result = new ArrayList<>();
    for (int[] pos : rawPositions) {
      int nr = pos[0];
      int nc = pos[1];
      if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
        result.add(new int[]{nr, nc});
      }
    }
    return result;
  }
}
