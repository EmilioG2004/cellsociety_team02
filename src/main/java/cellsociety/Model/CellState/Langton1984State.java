package cellsociety.Model.CellState;

/**
 * Langton's 1984 Loop cell states.
 * 8-state cellular automaton using values 0?7.
 *
 * States are numeric because the rule table operates purely on integers.
 * @author Ixa Sigler
 */
public enum Langton1984State implements CellState {

  S0(0),  // empty
  S1(1),
  S2(2),
  S3(3),
  S4(4),
  S5(5),
  S6(6),
  S7(7);

  private static final int MIN_STATE = 0;
  private static final int MAX_STATE = 7;

  private static final String ERROR_MESSAGE_INVALID_STATE =
      "Invalid Langton1984 state: ";

  private final int stateValue;

  Langton1984State(int stateValue) {
    this.stateValue = stateValue;
  }

  @Override
  public int toInt() {
    return stateValue;
  }

  @Override
  public CellState getNextInCycle() {
    return fromInt((stateValue + 1) % 8);
  }

  public static Langton1984State fromInt(int value) {
    if (value < MIN_STATE || value > MAX_STATE) {
      throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
    }
    return values()[value];
  }

  public static int fromState(CellState state) {
    if (state instanceof Langton1984State ss) {
      return ss.stateValue;
    }
    throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
  }
}