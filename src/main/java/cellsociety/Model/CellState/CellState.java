package cellsociety.Model.CellState;

/** Marker for simulation cell states; provides integer representation and cycle toggle for the view. */
public interface CellState {

  int toInt();

  /** Returns the next state in the click-to-toggle cycle (e.g. DEAD → ALIVE → DEAD). */
  CellState getNextInCycle();

  /** Factory for creating cell states from config integer codes. */
  @FunctionalInterface
  interface Factory {
    CellState fromInt(int value);
  }
}
