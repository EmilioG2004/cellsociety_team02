package cellsociety.Model.GridEdge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ToroidalEdgeTest {

  private final ToroidalEdge edge = new ToroidalEdge();

  @Test
  void resolve_inBounds_unchanged() {
    List<int[]> raw = List.of(new int[]{2, 3});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0)[0]);
    assertEquals(3, result.get(0)[1]);
  }

  @Test
  void resolve_negativeRow_wrapsToBottom() {
    List<int[]> raw = List.of(new int[]{-1, 2});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(4, result.get(0)[0]);
    assertEquals(2, result.get(0)[1]);
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
  void resolve_rowOverflow_wrapsToTop() {
    List<int[]> raw = List.of(new int[]{5, 2});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(0, result.get(0)[0]);
    assertEquals(2, result.get(0)[1]);
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
  void resolve_bothNegative_wrapsBoth() {
    List<int[]> raw = List.of(new int[]{-1, -1});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(4, result.get(0)[0]);
    assertEquals(4, result.get(0)[1]);
  }

  @Test
  void resolve_largeNegative_wrapsCorrectly() {
    List<int[]> raw = List.of(new int[]{-6, -7});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(4, result.get(0)[0]);  // floorMod(-6, 5) = 4
    assertEquals(3, result.get(0)[1]);  // floorMod(-7, 5) = 3
  }

  @Test
  void resolve_neverFilters_alwaysReturnsSameCount() {
    List<int[]> raw = List.of(
        new int[]{-1, -1},
        new int[]{0, 0},
        new int[]{5, 5},
        new int[]{10, 10}
    );
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(4, result.size());
  }

  @Test
  void resolve_emptyInput_returnsEmpty() {
    List<int[]> result = edge.resolve(List.of(), 5, 5);
    assertEquals(0, result.size());
  }
}
