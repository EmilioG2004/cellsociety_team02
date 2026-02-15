package cellsociety.Model.CellState;

public enum GameOfLifeState implements CellState {
  DEAD("WHITE"),
  ALIVE("GREEN");

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
      case 0 -> DEAD;
      case 1 -> ALIVE;
      default -> throw new IllegalArgumentException("Invalid GameOfLife state: " + value);
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
