package cellsociety.Model.NeighborStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.GridEdge.ToroidalEdge;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Integration tests verifying that concrete strategies work correctly with ToroidalEdge,
 * including the vision-based overload that previously silently clipped on toroidal grids.
 */
class ToroidalIntegrationTest {

  private static String key(int[] pos) {
    return pos[0] + "," + pos[1];
  }

  @Test
  void vonNeumann_toroidal_topLeftCorner_returnsFourNeighbors() {
    VonNeumannNeighborStrategy s = new VonNeumannNeighborStrategy();
    s.setGridEdge(new ToroidalEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);
    assertEquals(4, positions.size());
  }

  @Test
  void vonNeumann_toroidal_topLeftCorner_wrapsCorrectly() {
    VonNeumannNeighborStrategy s = new VonNeumannNeighborStrategy();
    s.setGridEdge(new ToroidalEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);

    Set<String> posSet = new HashSet<>();
    for (int[] p : positions) posSet.add(key(p));

    assertTrue(posSet.contains("4,0"), "N wraps to bottom");   // row -1 → 4
    assertTrue(posSet.contains("0,4"), "W wraps to right");    // col -1 → 4
    assertTrue(posSet.contains("1,0"), "S stays");
    assertTrue(posSet.contains("0,1"), "E stays");
  }

  @Test
  void moore_toroidal_corner_returnsEightNeighbors() {
    MooreNeighborStrategy s = new MooreNeighborStrategy();
    s.setGridEdge(new ToroidalEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);
    assertEquals(8, positions.size());
  }

  @Test
  void moore_toroidal_corner_wrapsAllDirections() {
    MooreNeighborStrategy s = new MooreNeighborStrategy();
    s.setGridEdge(new ToroidalEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);

    Set<String> posSet = new HashSet<>();
    for (int[] p : positions) posSet.add(key(p));

    assertTrue(posSet.contains("4,4"), "NW wraps both");
    assertTrue(posSet.contains("4,0"), "N wraps row");
    assertTrue(posSet.contains("4,1"), "NE wraps row");
    assertTrue(posSet.contains("0,4"), "W wraps col");
    assertTrue(posSet.contains("0,1"), "E stays");
    assertTrue(posSet.contains("1,4"), "SW wraps col");
    assertTrue(posSet.contains("1,0"), "S stays");
    assertTrue(posSet.contains("1,1"), "SE stays");
  }

  @Test
  void diagonal_toroidal_corner_returnsFourNeighbors() {
    DiagonalNeighborStrategy s = new DiagonalNeighborStrategy();
    s.setGridEdge(new ToroidalEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);
    assertEquals(4, positions.size());
  }

  @Test
  void mooreRadius2_toroidal_corner_returns24Neighbors() {
    MooreNeighborStrategyRadius2 s = new MooreNeighborStrategyRadius2();
    s.setGridEdge(new ToroidalEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);
    assertEquals(24, positions.size());
  }

  @Test
  void mooreRadius2_toroidal_corner_allPositionsUnique() {
    MooreNeighborStrategyRadius2 s = new MooreNeighborStrategyRadius2();
    s.setGridEdge(new ToroidalEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);

    Set<String> posSet = new HashSet<>();
    for (int[] p : positions) posSet.add(key(p));
    assertEquals(24, posSet.size(), "All 24 positions should be unique");
  }

  @Test
  void vonNeumann_toroidal_vision_wrapsCorrectly() {
    VonNeumannNeighborStrategy s = new VonNeumannNeighborStrategy();
    s.setGridEdge(new ToroidalEdge());
    // vision=1 expands each cardinal direction by 1 more step
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0, 1);

    Set<String> posSet = new HashSet<>();
    for (int[] p : positions) posSet.add(key(p));

    // N direction: (-1,0)→(4,0), then (-2,0)→(3,0)
    assertTrue(posSet.contains("4,0"), "N wraps");
    assertTrue(posSet.contains("3,0"), "N+vision wraps");
  }

  @Test
  void moore_toroidal_vision_neverClips() {
    MooreNeighborStrategy s = new MooreNeighborStrategy();
    s.setGridEdge(new ToroidalEdge());
    // With vision=1, each of 8 directions gets 1 extra step = 16 positions total
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0, 1);
    assertEquals(16, positions.size(), "Toroidal should never clip, all 16 positions valid");
  }

  @Test
  void finite_corner_clips_toroidal_corner_wraps() {
    MooreNeighborStrategy finite = new MooreNeighborStrategy();
    MooreNeighborStrategy toroidal = new MooreNeighborStrategy();
    toroidal.setGridEdge(new ToroidalEdge());

    List<int[]> finitePos = finite.getNeighborPositions(5, 5, 0, 0);
    List<int[]> toroidalPos = toroidal.getNeighborPositions(5, 5, 0, 0);

    assertEquals(3, finitePos.size(), "Finite clips to 3 at corner");
    assertEquals(8, toroidalPos.size(), "Toroidal wraps to 8 at corner");
  }
}
