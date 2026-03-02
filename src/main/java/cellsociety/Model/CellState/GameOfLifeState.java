package cellsociety.Model.CellState;

/** Game of Life cell states: DEAD, ALIVE. */
public enum GameOfLifeState implements CellState {
  DEAD(0),
  ALIVE(1);

  /** Int encoding for config and fromInt. */
  public static final int DEAD_INT = 0;
  public static final int ALIVE_INT = 1;

  private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid GameOfLife state: ";

  private final int stateValue;

  GameOfLifeState(int stateValue) {
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

  public static GameOfLifeState fromInt(int value) {
    return switch (value) {
      case DEAD_INT -> DEAD;
      case ALIVE_INT -> ALIVE;
      default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
    };
  }

  public static int fromState(CellState state) {
    if (state instanceof GameOfLifeState gs) {
      return gs.stateValue;
    }
    throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
  }

  public static GameOfLifeState getNextStateInCycle(CellState current) {
    return switch (current) {
      case GameOfLifeState.DEAD -> GameOfLifeState.ALIVE;
      case GameOfLifeState.ALIVE -> GameOfLifeState.DEAD;
      default -> (GameOfLifeState) current;
    };
  }
}
