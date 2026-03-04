package cellsociety.Model.GridEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps positions toroidally: moving off one edge reappears on the opposite edge.
 *
 * @author Michael Lei
 */
public class ToroidalEdge implements GridEdge {

  @Override
  public List<int[]> resolve(List<int[]> rawPositions, int rows, int cols) {
    List<int[]> result = new ArrayList<>();
    for (int[] pos : rawPositions) {
      int nr = Math.floorMod(pos[0], rows);
      int nc = Math.floorMod(pos[1], cols);
      result.add(new int[]{nr, nc});
    }
    return result;
  }
}
