package cellsociety.Model.Creature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.CellState.WaTor.Shark;
import cellsociety.Model.CellState.WaTor.WaTorTurnResult;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Shark}: energy, spendTurn, afterEating, tryEat, move.
 */
class SharkTest {


  @Test
  void spendTurn_decrementsEnergy() {
    Shark shark = new Shark(3);
    Shark next = shark.spendTurn();
    assertNotNull(next);
    assertEquals(2, next.getEnergy());
  }

  @Test
  void spendTurn_atOneEnergy_returnsNull() {
    Shark shark = new Shark(1);
    Shark next = shark.spendTurn();
    assertNull(next);
  }

  @Test
  void afterEating_increasesEnergyCapped() {
    Shark shark = new Shark(5);
    Shark next = shark.afterEating(4, 15);
    assertNotNull(next);
    assertEquals(8, next.getEnergy()); // 5-1+4
  }

  @Test
  void afterEating_respectsMaxEnergy() {
    Shark shark = new Shark(14);
    Shark next = shark.afterEating(4, 15);
    assertNotNull(next);
    assertEquals(15, next.getEnergy());
  }

  @Test
  void tryEat_emptyFishList_returnsNull() {
    Shark shark = new Shark(5);
    WaTorTurnResult result = shark.tryEat(List.of(), new Random(), 5, 4, 15);
    assertNull(result);
  }

  @Test
  void tryEat_withFish_returnsResultWithDest() {
    Shark shark = new Shark(5);
    List<int[]> fishSpots = List.of(new int[]{1, 1});
    WaTorTurnResult result = shark.tryEat(fishSpots, new Random(42), 5, 4, 15);
    assertNotNull(result);
    assertTrue(result.hasDest());
    assertEquals(1, result.destR());
    assertEquals(1, result.destC());
  }

  @Test
  void move_noOpenWater_returnsSingleCell() {
    Shark shark = new Shark(5);
    WaTorTurnResult result = shark.move(List.of(), new Random(), 5);
    assertNotNull(result);
    assertFalse(result.hasDest());
    assertTrue(result.atCurrent().isShark());
  }

  @Test
  void move_withOpenWater_returnsWithDest() {
    Shark shark = new Shark(5);
    WaTorTurnResult result = shark.move(List.of(new int[]{0, 1}), new Random(42), 5);
    assertNotNull(result);
    assertTrue(result.hasDest());
    assertEquals(0, result.destR());
    assertEquals(1, result.destC());
  }
}
