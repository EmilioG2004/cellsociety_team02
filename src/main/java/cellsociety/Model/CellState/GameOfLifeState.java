package cellsociety.Model.CellState;

/** Game of Life cell states: DEAD, ALIVE. */
public enum GameOfLifeState implements CellState {
  DEAD("WHITE"),
  ALIVE("GREEN");

  private static final int STATE_VALUE_DEAD = 0;
  private static final int STATE_VALUE_ALIVE = 1;
  private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid GameOfLife state: ";

  private final String color;

  GameOfLifeState(String color) {
    this.color = color;
  }

  @Override
  public String getColor() {
    return color;
  }

  public static GameOfLifeState fromInt(int value) {
    return switch (value) {
      case STATE_VALUE_DEAD -> DEAD;
      case STATE_VALUE_ALIVE -> ALIVE;
      default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
    };
  }

  public static int fromState(CellState state){
    return switch (state){
      case DEAD -> STATE_VALUE_DEAD;
      case ALIVE -> STATE_VALUE_ALIVE;
      default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
    };
  }

  public static GameOfLifeState getNextStateInCycle(CellState current) {
    return switch (current) {
      case GameOfLifeState.DEAD -> GameOfLifeState.ALIVE;
      case GameOfLifeState.ALIVE -> GameOfLifeState.DEAD;
      default -> (GameOfLifeState) current;
    };
  }
}
