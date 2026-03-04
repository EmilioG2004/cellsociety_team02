package cellsociety.Model.GridEdge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class MirroredEdgeTest {

  private final MirroredEdge edge = new MirroredEdge();

  @Test
  void resolve_inBounds_unchanged() {
    List<int[]> raw = List.of(new int[]{2, 3});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0)[0]);
    assertEquals(3, result.get(0)[1]);
  }

  @Test
  void resolve_negativeRow_reflectsToPositive() {
    List<int[]> raw = List.of(new int[]{-1, 2});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0)[0]);
    assertEquals(2, result.get(0)[1]);
  }

  @Test
  void resolve_negativeCol_reflectsToPositive() {
    List<int[]> raw = List.of(new int[]{2, -1});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0)[0]);
    assertEquals(1, result.get(0)[1]);
  }

  @Test
  void resolve_rowAtRows_reflectsBack() {
    List<int[]> raw = List.of(new int[]{5, 2});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(3, result.get(0)[0]);
    assertEquals(2, result.get(0)[1]);
  }

  @Test
  void resolve_colAtCols_reflectsBack() {
    List<int[]> raw = List.of(new int[]{2, 5});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0)[0]);
    assertEquals(3, result.get(0)[1]);
  }

  @Test
  void resolve_bothNegative_reflectsBoth() {
    List<int[]> raw = List.of(new int[]{-1, -1});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0)[0]);
    assertEquals(1, result.get(0)[1]);
  }

  @Test
  void resolve_largeNegative_multipleReflections() {
    // -6 -> 6 (out) -> 2*(4)-6 = 2
    List<int[]> raw = List.of(new int[]{-6, 2});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0)[0]);
    assertEquals(2, result.get(0)[1]);
  }

  @Test
  void resolve_largePositive_multipleReflections() {
    // 7 in 5 rows -> 2*(4)-7 = 1
    List<int[]> raw = List.of(new int[]{7, 2});
    List<int[]> result = edge.resolve(raw, 5, 5);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0)[0]);
    assertEquals(2, result.get(0)[1]);
  }

  @Test
  void resolve_alwaysReturnsSameCount_asToroidal() {
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

  @Test
  void resolve_singleCellGrid_reflectsIntoSameCell() {
    List<int[]> raw = List.of(new int[]{-1, 0}, new int[]{0, -1}, new int[]{0, 0});
    List<int[]> result = edge.resolve(raw, 1, 1);
    assertEquals(3, result.size());
    assertEquals(0, result.get(0)[0]);
    assertEquals(0, result.get(0)[1]);
    assertEquals(0, result.get(1)[0]);
    assertEquals(0, result.get(1)[1]);
    assertEquals(0, result.get(2)[0]);
    assertEquals(0, result.get(2)[1]);
  }
}
