package cellsociety.Model.CellState;

/**
 * Result of a fish or shark taking one turn: what to write at the current cell
 * and optionally at a destination cell (when moving or eating).
 *
 * @param atCurrent state to write at the creature's current position
 * @param destR     row of destination cell
 * @param destC     column of destination cell 
 * @param atDest    state to write at destination 
 */
public record WaTorTurnResult(WaTorCellState atCurrent, int destR, int destC, WaTorCellState atDest) {

  public boolean hasDest() {
    return destR >= 0;
  }

  public static WaTorTurnResult single(WaTorCellState atCurrent) {
    return new WaTorTurnResult(atCurrent, -1, -1, null);
  }

  public static WaTorTurnResult withDest(WaTorCellState atCurrent, int destR, int destC, WaTorCellState atDest) {
    return new WaTorTurnResult(atCurrent, destR, destC, atDest);
  }
}
