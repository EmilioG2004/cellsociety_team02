package cellsociety.Model.CellState;

public enum FallingSandState implements CellState {
  EMPTY(0),
  METAL(1),
  SAND(2),
  WATER(3);

  /** Int encoding for config and fromInt. */
  public static final int EMPTY_INT = 0;
  public static final int METAL_INT = 1;
  public static final int SAND_INT = 2;
  public static final int WATER_INT = 3;

  private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid FallingSand state: ";

  private final int stateValue;

  FallingSandState(int stateValue) {
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

  public static FallingSandState getNextStateInCycle(CellState current) {
    return switch (current) {
      case FallingSandState.EMPTY -> FallingSandState.METAL;
      case FallingSandState.METAL -> FallingSandState.SAND;
      case FallingSandState.SAND -> FallingSandState.WATER;
      case FallingSandState.WATER -> FallingSandState.EMPTY;
      default -> (FallingSandState) current;
    };
  }

  public static FallingSandState fromInt(int value) {
    return switch (value) {
      case EMPTY_INT -> EMPTY;
      case METAL_INT -> METAL;
      case SAND_INT -> SAND;
      case WATER_INT -> WATER;
      default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
    };
  }

  public static int fromState(CellState state) {
    if (state instanceof FallingSandState fs) {
      return fs.stateValue;
    }
    throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
  }
}
