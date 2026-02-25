package cellsociety.Model.Creature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.CellState.WaTorTurnResult;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Fish}: breed threshold, afterTurn/afterBreeding, and act().
 */
class FishTest {

  @Test
  void canBreed_belowThreshold_returnsFalse() {
    Fish fish = new Fish(2);
    assertFalse(fish.canBreed());
  }

  @Test
  void canBreed_atThreshold_returnsTrue() {
    Fish fish = new Fish(4);
    assertTrue(fish.canBreed());
  }

  @Test
  void afterTurn_incrementsBreedCounter() {
    Fish fish = new Fish(1);
    Fish next = fish.afterTurn();
    assertEquals(2, next.getBreedCounter());
  }

  @Test
  void afterBreeding_resetsCounter() {
    Fish fish = new Fish(5);
    Fish next = fish.afterBreeding();
    assertEquals(0, next.getBreedCounter());
  }

  @Test
  void act_emptyNeighbors_staysInPlace() {
    Fish fish = new Fish(0);
    WaTorTurnResult result = fish.act(List.of(), new Random());
    assertFalse(result.hasDest());
    assertTrue(result.atCurrent().isFish());
  }

  @Test
  void act_oneOpenNeighbor_hasDest() {
    Fish fish = new Fish(0);
    WaTorTurnResult result = fish.act(List.of(new int[]{1, 0}), new Random(42));
    assertTrue(result.hasDest());
    assertEquals(1, result.destR());
    assertEquals(0, result.destC());
  }
}
