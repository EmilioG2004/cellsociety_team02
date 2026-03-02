package cellsociety.Model.CellState;

/**
 * Marker for simulation cell states; provides integer representation and cycle toggle for the view.
 *
 * @author Michael Lei
 */
public interface CellState {

  int toInt();

  /** Returns the next state in the click-to-toggle cycle (e.g. DEAD → ALIVE → DEAD). */
  CellState getNextInCycle();
}
