package cellsociety.Model.NeighborStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorates any {@link NeighborStrategy} so that neighbor positions wrap toroidally:
 * moving off one edge reappears on the opposite edge.
 */
public class WrappingNeighborStrategy implements NeighborStrategy {

  private final NeighborStrategy base;

  public WrappingNeighborStrategy(NeighborStrategy base) {
    this.base = base;
  }

  @Override
  public List<int[]> getNeighborOffsets() {
    return base.getNeighborOffsets();
  }

  @Override
  public List<int[]> getNeighborPositions(int rows, int cols, int r, int c) {
    List<int[]> positions = new ArrayList<>();
    for (int[] offset : getNeighborOffsets()) {
      int nr = Math.floorMod(r + offset[0], rows);
      int nc = Math.floorMod(c + offset[1], cols);
      positions.add(new int[]{nr, nc});
    }
    return positions;
  }
}
