package cellsociety.Model.CellState;

/**
 * Segregation cell states: EMPTY, GROUP_A, GROUP_B.
 *
 * @author Ixa Sigler
 */
public enum SegregationState implements CellState {
  EMPTY(0),
  GROUP_A(1),
  GROUP_B(2);

  /** Int encoding for config and fromInt. */
  public static final int EMPTY_INT = 0;
  public static final int GROUP_A_INT = 1;
  public static final int GROUP_B_INT = 2;

  private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid Segregation state: ";

  private final int stateValue;

  SegregationState(int stateValue) {
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

  public static SegregationState fromInt(int value) {
    return switch (value) {
      case EMPTY_INT -> EMPTY;
      case GROUP_A_INT -> GROUP_A;
      case GROUP_B_INT -> GROUP_B;
      default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
    };
  }

  public static int fromState(CellState state) {
    if (state instanceof SegregationState ss) {
      return ss.stateValue;
    }
    throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
  }

  /** Returns the next state in the click-to-toggle cycle: EMPTY -> GROUP_A -> GROUP_B -> EMPTY. */
  public static SegregationState getNextStateInCycle(CellState current) {
    return switch (current) {
      case SegregationState.EMPTY -> SegregationState.GROUP_A;
      case SegregationState.GROUP_A -> SegregationState.GROUP_B;
      case SegregationState.GROUP_B -> SegregationState.EMPTY;
      default -> (SegregationState) current;
    };
  }
}
