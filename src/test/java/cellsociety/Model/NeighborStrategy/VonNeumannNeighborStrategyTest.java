package cellsociety.Model.NeighborStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class VonNeumannNeighborStrategyTest {

  @Test
  void getNeighborOffsets_returnsFourOffsets() {
    VonNeumannNeighborStrategy strategy = new VonNeumannNeighborStrategy();
    List<int[]> offsets = strategy.getNeighborOffsets();
    assertEquals(4, offsets.size());
  }

  @Test
  void getNeighborOffsets_containsCardinalDirectionsOnly() {
    VonNeumannNeighborStrategy strategy = new VonNeumannNeighborStrategy();
    List<int[]> offsets = strategy.getNeighborOffsets();

    int[][] expected = {
        {-1, 0},  // N
        {0, 1},   // E
        {1, 0},   // S
        {0, -1}   // W
    };

    assertEquals(expected.length, offsets.size());
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i][0], offsets.get(i)[0], "row offset at " + i);
      assertEquals(expected[i][1], offsets.get(i)[1], "col offset at " + i);
    }
  }

  @Test
  void getNeighborPositions_centerOfGrid_returnsFourPositions() {
    VonNeumannNeighborStrategy strategy = new VonNeumannNeighborStrategy();
    List<int[]> positions = strategy.getNeighborPositions(5, 5, 2, 2);
    assertEquals(4, positions.size());
  }

  @Test
  void getNeighborPositions_topLeftCorner_returnsTwoPositions() {
    VonNeumannNeighborStrategy strategy = new VonNeumannNeighborStrategy();
    List<int[]> positions = strategy.getNeighborPositions(5, 5, 0, 0);
    assertEquals(2, positions.size());
  }

  @Test
  void getNeighborPositions_topEdge_returnsThreePositions() {
    VonNeumannNeighborStrategy strategy = new VonNeumannNeighborStrategy();
    List<int[]> positions = strategy.getNeighborPositions(5, 5, 0, 2);
    assertEquals(3, positions.size());
  }
}
