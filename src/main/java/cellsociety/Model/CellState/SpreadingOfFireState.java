package cellsociety.Model.CellState;

/**
 * Spreading of Fire cell states: EMPTY, TREE, BURNING.
 *
 * @author Michael Lei
 */
public enum SpreadingOfFireState implements CellState {
  EMPTY(0),
  TREE(1),
  BURNING(2);

  /** Int encoding for config and fromInt. */
  public static final int EMPTY_INT = 0;
  public static final int TREE_INT = 1;
  public static final int BURNING_INT = 2;

  private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid SpreadingOfFire state: ";

  private final int stateValue;

  SpreadingOfFireState(int stateValue) {
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

  /** Returns the next state in the cycle: EMPTY -> TREE -> BURNING -> EMPTY. */
  public static SpreadingOfFireState getNextStateInCycle(CellState current) {
    return switch (current) {
      case SpreadingOfFireState.EMPTY -> SpreadingOfFireState.TREE;
      case SpreadingOfFireState.TREE -> SpreadingOfFireState.BURNING;
      case SpreadingOfFireState.BURNING -> SpreadingOfFireState.EMPTY;
      default -> (SpreadingOfFireState) current;
    };
  }

  /** Creates a state from its integer encoding. */
  public static SpreadingOfFireState fromInt(int value) {
    return switch (value) {
      case EMPTY_INT -> EMPTY;
      case TREE_INT -> TREE;
      case BURNING_INT -> BURNING;
      default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
    };
  }

  /** Returns the integer encoding for the given CellState. */
  public static int fromState(CellState state) {
    if (state instanceof SpreadingOfFireState fs) {
      return fs.stateValue;
    }
    throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
  }
}
