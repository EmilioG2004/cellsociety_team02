package cellsociety.Model.NeighborStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MooreNeighborStrategyRadius2Test {

  @Test
  void getNeighborOffsets_returnsTwentyFourOffsets() {
    MooreNeighborStrategyRadius2 strategy = new MooreNeighborStrategyRadius2();
    List<int[]> offsets = strategy.getNeighborOffsets();
    assertEquals(24, offsets.size());
  }

  @Test
  void getNeighborOffsets_containsAllPositionsIn5x5ExceptCenter() {
    MooreNeighborStrategyRadius2 strategy = new MooreNeighborStrategyRadius2();
    List<int[]> offsets = strategy.getNeighborOffsets();

    Set<String> seen = new HashSet<>();
    for (int[] off : offsets) {
      assertEquals(2, off.length);
      int dr = off[0], dc = off[1];
      assertTrue(Math.abs(dr) <= 2 && Math.abs(dc) <= 2, "offset in 5x5: (" + dr + "," + dc + ")");
      assertTrue(dr != 0 || dc != 0, "center (0,0) excluded");
      seen.add(dr + "," + dc);
    }
    assertEquals(24, seen.size());
  }

  @Test
  void getNeighborPositions_centerOfLargeGrid_returns24Positions() {
    MooreNeighborStrategyRadius2 strategy = new MooreNeighborStrategyRadius2();
    List<int[]> positions = strategy.getNeighborPositions(10, 10, 5, 5);
    assertEquals(24, positions.size());
  }

  @Test
  void getNeighborPositions_withVision_returnsSameAsWithoutVision() {
    MooreNeighborStrategyRadius2 strategy = new MooreNeighborStrategyRadius2();
    List<int[]> noVision = strategy.getNeighborPositions(10, 10, 4, 4);
    List<int[]> withVision = strategy.getNeighborPositions(10, 10, 4, 4, 1);
    assertEquals(noVision.size(), withVision.size());
    assertEquals(24, noVision.size());
  }

  @Test
  void getNeighborPositions_nearEdge_clipsToBounds() {
    MooreNeighborStrategyRadius2 strategy = new MooreNeighborStrategyRadius2();
    // Cell (0,0): full 5x5 would go to (-2,-2)..(2,2); only (0,0)..(2,2) are in bounds, minus center = 8 neighbors
    List<int[]> positions = strategy.getNeighborPositions(5, 5, 0, 0);
    assertEquals(8, positions.size());
  }
}
