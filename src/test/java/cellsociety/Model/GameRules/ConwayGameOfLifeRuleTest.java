package cellsociety.Model.GameRules;

import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.ReadOnlyCell;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * ConwayGameOfLifeRule tests
 */
class ConwayGameOfLifeRuleTest {

  private static final int DEAD = ConwayGameOfLifeRule.DEAD;
  private static final int ALIVE = ConwayGameOfLifeRule.ALIVE;

  private static class FakeReadOnlyCell implements ReadOnlyCell {
    private final int state;
    FakeReadOnlyCell(int state) { this.state = state; }
    @Override
    public int getState() { return state; }
  }

  private static List<ReadOnlyCell> neighborsAliveCount(int aliveCount) {
    int total = 8;
    if (aliveCount < 0 || aliveCount > total) throw new IllegalArgumentException("aliveCount out of range");

    List<ReadOnlyCell> neighbors = new ArrayList<>(total);
    for (int i = 0; i < aliveCount; i++) neighbors.add(new FakeReadOnlyCell(ALIVE));
    for (int i = aliveCount; i < total; i++) neighbors.add(new FakeReadOnlyCell(DEAD));
    return neighbors;
  }

  @Test
  void getNextState_AliveWithZeroNeighbors_Dies() {
    var rule = new ConwayGameOfLifeRule();

    int next = rule.getNextState(ALIVE, List.of());

    assertEquals(DEAD, next);
  }

  @Test
  void getNextState_DeadWithZeroNeighbors_StaysDead() {
    var rule = new ConwayGameOfLifeRule();

    int next = rule.getNextState(DEAD, List.of());

    assertEquals(DEAD, next);
  }

  @Test
  void getNextState_AliveWithOneAliveNeighbor_DiesByUnderpopulation() {
    var rule = new ConwayGameOfLifeRule();

    int next = rule.getNextState(ALIVE, neighborsAliveCount(1));

    assertEquals(DEAD, next);
  }

  @Test
  void getNextState_DeadWithOneAliveNeighbor_StaysDead() {
    var rule = new ConwayGameOfLifeRule();

    int next = rule.getNextState(DEAD, neighborsAliveCount(1));

    assertEquals(DEAD, next);
  }

  @Test
  void getNextState_AliveWithThreeAliveNeighbors_StaysAlive() {
    var rule = new ConwayGameOfLifeRule();

    int next = rule.getNextState(ALIVE, neighborsAliveCount(3));

    assertEquals(ALIVE, next);
  }

  @Test
  void getNextState_DeadWithThreeAliveNeighbors_BecomesAlive() {
    var rule = new ConwayGameOfLifeRule();

    int next = rule.getNextState(DEAD, neighborsAliveCount(3));

    assertEquals(ALIVE, next);
  }

  @Test
  void getNextState_AliveWithTwoAliveNeighbors_StaysAlive() {
    var rule = new ConwayGameOfLifeRule();

    int next = rule.getNextState(ALIVE, neighborsAliveCount(2));

    assertEquals(ALIVE, next);
  }

  @Test
  void getNextState_AliveWithFourAliveNeighbors_DiesByOverpopulation() {
    var rule = new ConwayGameOfLifeRule();

    int next = rule.getNextState(ALIVE, neighborsAliveCount(4));

    assertEquals(DEAD, next);
  }

  @Test
  void getNextState_DeadWithTwoAliveNeighbors_StaysDead() {
    var rule = new ConwayGameOfLifeRule();

    int next = rule.getNextState(DEAD, neighborsAliveCount(2));

    assertEquals(DEAD, next);
  }

  @Test
  void getNextState_NullNeighbors_ThrowsNullPointerException() {
    var rule = new ConwayGameOfLifeRule();

    assertThrows(NullPointerException.class, () -> rule.getNextState(ALIVE, null));
  }

  @Test
  void getNextState_NeighborsContainsNullCell_ThrowsNullPointerException() {
    var rule = new ConwayGameOfLifeRule();
    List<ReadOnlyCell> neighbors = new ArrayList<>();
    neighbors.add(null);

    assertThrows(NullPointerException.class, () -> rule.getNextState(ALIVE, neighbors));
  }

  @Test
  void getStateColors_Default_ReturnsExpectedColorsForAliveAndDead() {
    var rule = new ConwayGameOfLifeRule();

    Map<Integer, String> colors = rule.getStateColors();

    assertEquals("WHITE", colors.get(DEAD));
    assertEquals("GREEN", colors.get(ALIVE));
    assertEquals(2, colors.size());
  }

  @Test
  void getStateColors_ReturnedMap_IsUnmodifiable() {
    var rule = new ConwayGameOfLifeRule();

    Map<Integer, String> colors = rule.getStateColors();

    assertThrows(UnsupportedOperationException.class, () -> colors.put(999, "PURPLE"));
  }
}
