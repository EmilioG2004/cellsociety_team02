package cellsociety.Model.CellState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.CellState.WaTor.Fish;
import cellsociety.Model.CellState.WaTor.WaTorCellState;
import cellsociety.Model.CellState.WaTor.WaTorTurnResult;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link WaTorTurnResult}: single-cell and two-cell results.
 */
class WaTorTurnResultTest {

  @Test
  void single_hasNoDest() {
    WaTorTurnResult result = WaTorTurnResult.single(WaTorCellState.water());
    assertFalse(result.hasDest());
    assertEquals(WaTorCellState.water(), result.atCurrent());
    assertEquals(-1, result.destR());
  }

  @Test
  void withDest_hasDest() {
    WaTorCellState atCurrent = WaTorCellState.water();
    WaTorCellState atDest = new WaTorCellState(new Fish());
    WaTorTurnResult result = WaTorTurnResult.withDest(atCurrent, 1, 2, atDest);
    assertTrue(result.hasDest());
    assertEquals(atCurrent, result.atCurrent());
    assertEquals(1, result.destR());
    assertEquals(2, result.destC());
    assertEquals(atDest, result.atDest());
  }
}
