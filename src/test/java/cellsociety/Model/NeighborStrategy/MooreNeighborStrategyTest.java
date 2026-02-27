package cellsociety.Model.NeighborStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class MooreNeighborStrategyTest {

  @Test
  void getNeighborOffsets_returnsEightOffsets() {
    MooreNeighborStrategy strategy = new MooreNeighborStrategy();
    List<int[]> offsets = strategy.getNeighborOffsets();
    assertEquals(8, offsets.size());
  }

  @Test
  void getNeighborOffsets_containsAllMooreDirections() {
    MooreNeighborStrategy strategy = new MooreNeighborStrategy();
    List<int[]> offsets = strategy.getNeighborOffsets();

    int[][] expected = {
      {-1, -1}, {-1, 0}, {-1, 1},
      {0, -1},           {0, 1},
      {1, -1},  {1, 0},  {1, 1}
    };

    assertEquals(expected.length, offsets.size());
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i][0], offsets.get(i)[0], "row offset at " + i);
      assertEquals(expected[i][1], offsets.get(i)[1], "col offset at " + i);
    }
  }
}
