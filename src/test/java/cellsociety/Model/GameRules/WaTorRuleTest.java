package cellsociety.Model.GameRules;

import static cellsociety.Model.CellState.WaTorCellState.FISH;
import static cellsociety.Model.CellState.WaTorCellState.SHARK;
import static cellsociety.Model.CellState.WaTorCellState.WATER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.CellState.CellState;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WaTorRuleTest {

  private WaTorRule rule;

  @BeforeEach
  void setUp() {
    rule = new WaTorRule();
  }

  private static int count(CellState[][] grid, CellState state) {
    int n = 0;
    for (CellState[] row : grid) {
      for (CellState cell : row) {
        if (state.equals(cell)) n++;
      }
    }
    return n;
  }

  @Test
  void fromInt_zero_returnsWater() {
    assertEquals(WATER, rule.fromInt(0));
  }

  @Test
  void fromInt_one_returnsFish() {
    assertEquals(FISH, rule.fromInt(1));
  }

  @Test
  void fromInt_two_returnsShark() {
    assertEquals(SHARK, rule.fromInt(2));
  }

  @Test
  void fromInt_invalid_throws() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> rule.fromInt(5));
    assertTrue(thrown.getMessage().contains("Invalid"));
  }

  @Test
  void getStateColors_containsWaterFishShark() {
    Map<CellState, String> colors = rule.getStateColors();
    assertTrue(colors.containsKey(WATER));
    assertTrue(colors.containsKey(FISH));
    assertTrue(colors.containsKey(SHARK));
  }

  @Test
  void getNextStates_allWater_remainsAllWater() {
    CellState[][] grid = {
        {WATER, WATER, WATER},
        {WATER, WATER, WATER},
        {WATER, WATER, WATER}
    };
    CellState[][] next = rule.getNextStates(grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(WATER, next[r][c]);
      }
    }
  }

  @Test
  void getNextStates_sharkAdjacentToFish_fishGetsEaten() {
    // One shark, one fish adjacent. Fish has no WATER neighbors (so cannot move). Shark eats that fish (random choice of one).
    CellState[][] grid = {
        {SHARK, FISH, FISH},
        {FISH, FISH, WATER},
        {FISH, WATER, WATER}
    };
    int fishBefore  = count(grid, FISH);
    int sharkBefore = count(grid, SHARK);
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(sharkBefore, count(next, SHARK), "Shark count unchanged");
    assertEquals(fishBefore - 1, count(next, FISH), "One fish was eaten");
    assertEquals(WATER, next[0][0], "Shark left original cell");
  }

  @Test
  void getNextStates_fishWithOneEmptyNeighbor_movesThere() {
    // 2x1: only fish and one water; fish has single neighbor (wrap), so one choice. No sharks.
    CellState[][] grid = {
        {FISH},
        {WATER}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(1, count(next, FISH), "One fish moved");
    assertEquals(WATER, next[0][0], "Fish left original cell");
    assertEquals(FISH, next[1][0], "Fish moved to only empty neighbor");
  }

  @Test
  void getNextStates_sharkWithStartEnergyOne_diesAfterOneStep() {
    rule = new WaTorRule(Map.of("sharkStartEnergy", 1.0));
    CellState sharkWithLowEnergy = rule.fromInt(2); // Shark created with energy=1 from parameter
    CellState[][] grid = {
        {WATER, WATER, WATER},
        {WATER, sharkWithLowEnergy, WATER},
        {WATER, WATER, WATER}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(WATER, next[1][1], "Shark at 1 energy loses 1 and dies");
  }

  @Test
  void getNextStates_fishSurroundedBySharks_getsEaten() {
    CellState[][] grid = {
        {WATER, SHARK, WATER},
        {SHARK, FISH,  SHARK},
        {WATER, SHARK, WATER}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(SHARK, next[1][1], "Fish with no water neighbors does not move");
  }
}
