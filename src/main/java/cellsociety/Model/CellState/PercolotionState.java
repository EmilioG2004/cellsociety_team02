package cellsociety.Model.CellState;

/**
 * Percolation cell states: CLOSED, OPEN, FULL.
 *
 * @author Michael Lei
 */
public enum PercolotionState implements CellState {
  CLOSED(0),
  OPEN(1),
  FULL(2);

  /** Int encoding for config and fromInt. */
  public static final int CLOSED_INT = 0;
  public static final int OPEN_INT = 1;
  public static final int FULL_INT = 2;

  private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid Percolotion state: ";

  private final int stateValue;

  PercolotionState(int stateValue) {
    this.stateValue = stateValue;
  }

  @Override
  public int toInt() {
    return stateValue;
  }

  @Override
  public CellState getNextInCycle() {
    return getNextStateInCycle(this);
  }

  public static PercolotionState fromInt(int value) {
    return switch (value) {
      case CLOSED_INT -> CLOSED;
      case OPEN_INT -> OPEN;
      case FULL_INT -> FULL;
      default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
    };
  }

  public static int fromState(CellState state) {
    if (state instanceof PercolotionState ps) {
      return ps.stateValue;
    }
    throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
  }

  public static PercolotionState getNextStateInCycle(CellState current) {
    return switch (current) {
      case PercolotionState.CLOSED -> PercolotionState.OPEN;
      case PercolotionState.OPEN -> PercolotionState.FULL;
      case PercolotionState.FULL -> PercolotionState.CLOSED;
      default -> (PercolotionState) current;
    };
  }
}
