package cellsociety.Model.GridEdge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class FiniteEdgeTest {

  private final FiniteEdge edge = new FiniteEdge();

  @Test
  void resolve_allInBounds_returnsAll() {
    List<int[]> raw = List.of(new int[]{0, 0}, new int[]{1, 1}, new int[]{2, 3});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(3, result.size());
  }

  @Test
  void resolve_negativeRow_filtered() {
    List<int[]> raw = List.of(new int[]{-1, 0});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_negativeCol_filtered() {
    List<int[]> raw = List.of(new int[]{0, -1});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_rowEqualToRows_filtered() {
    List<int[]> raw = List.of(new int[]{5, 0});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_colEqualToCols_filtered() {
    List<int[]> raw = List.of(new int[]{0, 5});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_mixOfInAndOutOfBounds_filtersCorrectly() {
    List<int[]> raw = List.of(
        new int[]{-1, 0},  // out
        new int[]{0, 0},   // in
        new int[]{4, 4},   // in
        new int[]{5, 5}    // out
    );
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(2, result.size());
    assertEquals(0, result.get(0)[0]);
    assertEquals(0, result.get(0)[1]);
    assertEquals(4, result.get(1)[0]);
    assertEquals(4, result.get(1)[1]);
  }

  @Test
  void resolve_emptyInput_returnsEmpty() {
    List<int[]> result = edge.resolve(List.of(), 5, 5);
    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_boundaryPositions_kept() {
    List<int[]> raw = List.of(
        new int[]{0, 0},
        new int[]{0, 4},
        new int[]{4, 0},
        new int[]{4, 4}
    );
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(4, result.size());
  }
}
