package cellsociety.Model.NeighborStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class DiagonalNeighborStrategyTest {

  @Test
  void getNeighborOffsets_returnsFourOffsets() {
    DiagonalNeighborStrategy strategy = new DiagonalNeighborStrategy();
    List<int[]> offsets = strategy.getNeighborOffsets();
    assertEquals(4, offsets.size());
  }

  @Test
  void getNeighborOffsets_containsOnlyDiagonals() {
    DiagonalNeighborStrategy strategy = new DiagonalNeighborStrategy();
    List<int[]> offsets = strategy.getNeighborOffsets();

    int[][] expected = {
        {-1, -1}, // NW
        {-1, 1},  // NE
        {1, -1},  // SW
        {1, 1}    // SE
    };
    assertEquals(expected.length, offsets.size());
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i][0], offsets.get(i)[0], "row offset at " + i);
      assertEquals(expected[i][1], offsets.get(i)[1], "col offset at " + i);
    }
  }

  @Test
  void getNeighborPositions_centerOfGrid_returnsFourPositions() {
    DiagonalNeighborStrategy strategy = new DiagonalNeighborStrategy();
    List<int[]> positions = strategy.getNeighborPositions(5, 5, 2, 2);
    assertEquals(4, positions.size());
  }

  @Test
  void getNeighborPositions_cornerCell_returnsOnePosition() {
    DiagonalNeighborStrategy strategy = new DiagonalNeighborStrategy();
    // (0,0): only SE (1,1) is in bounds
    List<int[]> positions = strategy.getNeighborPositions(5, 5, 0, 0);
    assertEquals(1, positions.size());
    assertEquals(1, positions.get(0)[0]);
    assertEquals(1, positions.get(0)[1]);
  }
}
