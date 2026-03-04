package cellsociety.Model.GridEdge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CylindricalEdgeTest {

  private final CylindricalEdge edge = new CylindricalEdge();

  @Test
  void resolve_allInBounds_returnsAllWithColUnchanged() {
    List<int[]> raw = List.of(new int[]{0, 0}, new int[]{1, 1}, new int[]{2, 3});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(3, result.size());
    assertEquals(0, result.get(0)[0]);
    assertEquals(0, result.get(0)[1]);
    assertEquals(2, result.get(2)[0]);
    assertEquals(3, result.get(2)[1]);
  }

  @Test
  void resolve_negativeRow_filtered() {
    List<int[]> raw = List.of(new int[]{-1, 0});
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
  void resolve_negativeCol_wrapsToRight() {
    List<int[]> raw = List.of(new int[]{2, -1});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0)[0]);
    assertEquals(4, result.get(0)[1]);
  }

  @Test
  void resolve_colOverflow_wrapsToLeft() {
    List<int[]> raw = List.of(new int[]{2, 5});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0)[0]);
    assertEquals(0, result.get(0)[1]);
  }

  @Test
  void resolve_rowOutOfBounds_colWraps_stillFiltered() {
    List<int[]> raw = List.of(new int[]{-1, -1});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertTrue(result.isEmpty());
  }

  @Test
  void resolve_rowInBounds_colWraps_included() {
    List<int[]> raw = List.of(new int[]{0, 10});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(0, result.get(0)[0]);
    assertEquals(0, result.get(0)[1]);
  }

  @Test
  void resolve_mixOfInOutAndWrapped() {
    List<int[]> raw = List.of(
        new int[]{-1, 0},   // row out -> filtered
        new int[]{0, 0},    // in bounds
        new int[]{4, -1},   // row in, col wraps -> (4, 4)
        new int[]{5, 2}     // row out -> filtered
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
}
