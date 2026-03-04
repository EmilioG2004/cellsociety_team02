package cellsociety.Model.CellState;

/**
 * SDSR cell states
 * 9-state cellular automaton where value 8 is the dissolving cell
 *
 * States are numeric because the rule table operates purely on integers.
 * @author Ixa Sigler
 */
public enum LangtonSDSRState implements CellState {

  S0(0),  // empty
  S1(1),
  S2(2),
  S3(3),
  S4(4),
  S5(5),
  S6(6),
  S7(7),
  S8(8);

  private static final int MIN_STATE = 0;
  private static final int MAX_STATE = 8;

  private static final String ERROR_MESSAGE_INVALID_STATE =
      "Invalid LangtonSDSR state: ";

  private final int stateValue;

  LangtonSDSRState(int stateValue) {
    this.stateValue = stateValue;
  }

  @Override
  public int toInt() {
    return stateValue;
  }

  @Override
  public CellState getNextInCycle() {
    return fromInt((stateValue + 1) % 9);
  }

  public static LangtonSDSRState fromInt(int value) {
    if (value < MIN_STATE || value > MAX_STATE) {
      throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
    }
    return values()[value];
  }

  public static int fromState(CellState state) {
    if (state instanceof LangtonSDSRState ss) {
      return ss.stateValue;
    }
    throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
  }
}