package cellsociety.Model.GridEdge;

import java.util.List;

/**
 * Defines how positions that fall outside the grid boundary are handled.
 * Each implementation encodes one edge policy (e.g. clip, wrap).
 *
 * @author Michael Lei
 */
public interface GridEdge {

  /**
   * Resolves raw (possibly out-of-bounds) positions into valid grid positions
   * according to this edge policy.
   *
   * @param rawPositions list of [row, col] positions (may be out of bounds)
   * @param rows         number of rows in the grid
   * @param cols         number of columns in the grid
   * @return list of valid [row, col] positions after applying the edge policy
   */
  List<int[]> resolve(List<int[]> rawPositions, int rows, int cols);
}
