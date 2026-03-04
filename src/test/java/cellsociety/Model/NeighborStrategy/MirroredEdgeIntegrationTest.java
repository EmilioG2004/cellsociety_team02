package cellsociety.Model.NeighborStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.GridEdge.MirroredEdge;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Integration tests verifying that neighbor strategies work correctly with MirroredEdge:
 * edges act as mirrors so a full neighborhood is formed by reflected positions.
 */
class MirroredEdgeIntegrationTest {

  private static String key(int[] pos) {
    return pos[0] + "," + pos[1];
  }

  @Test
  void vonNeumann_mirrored_topLeftCorner_returnsFourNeighbors() {
    VonNeumannNeighborStrategy s = new VonNeumannNeighborStrategy();
    s.setGridEdge(new MirroredEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);
    assertEquals(4, positions.size());
  }

  @Test
  void vonNeumann_mirrored_topLeftCorner_reflectsCorrectly() {
    VonNeumannNeighborStrategy s = new VonNeumannNeighborStrategy();
    s.setGridEdge(new MirroredEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);

    Set<String> posSet = new HashSet<>();
    for (int[] p : positions) posSet.add(key(p));

    // N (-1,0) -> (1,0); W (0,-1) -> (0,1); S (1,0); E (0,1)
    assertTrue(posSet.contains("1,0"), "N reflects to row 1");
    assertTrue(posSet.contains("0,1"), "W reflects to col 1");
    assertTrue(posSet.contains("1,0"));
    assertTrue(posSet.contains("0,1"));
  }

  @Test
  void moore_mirrored_corner_returnsEightNeighbors() {
    MooreNeighborStrategy s = new MooreNeighborStrategy();
    s.setGridEdge(new MirroredEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);
    assertEquals(8, positions.size());
  }

  @Test
  void moore_mirrored_corner_reflectsAllDirections() {
    MooreNeighborStrategy s = new MooreNeighborStrategy();
    s.setGridEdge(new MirroredEdge());
    List<int[]> positions = s.getNeighborPositions(5, 5, 0, 0);

    Set<String> posSet = new HashSet<>();
    for (int[] p : positions) posSet.add(key(p));

    // (-1,-1)->(1,1), (-1,0)->(1,0), (-1,1)->(1,1), (0,-1)->(0,1), (0,1), (1,-1)->(1,1), (1,0), (1,1)
    assertTrue(posSet.contains("1,1"), "NW reflects to (1,1)");
    assertTrue(posSet.contains("1,0"), "N reflects to (1,0)");
    assertTrue(posSet.contains("0,1"), "W reflects to (0,1)");
    assertTrue(posSet.contains("1,0"));
    assertTrue(posSet.contains("0,1"));
    assertTrue(posSet.contains("1,1"));
  }

  @Test
  void finite_corner_clips_mirrored_corner_fullNeighborhood() {
    MooreNeighborStrategy finite = new MooreNeighborStrategy();
    MooreNeighborStrategy mirrored = new MooreNeighborStrategy();
    mirrored.setGridEdge(new MirroredEdge());

    List<int[]> finitePos = finite.getNeighborPositions(5, 5, 0, 0);
    List<int[]> mirroredPos = mirrored.getNeighborPositions(5, 5, 0, 0);

    assertEquals(3, finitePos.size(), "Finite clips to 3 at corner");
    assertEquals(8, mirroredPos.size(), "Mirrored reflects to full 8 at corner");
  }
}
