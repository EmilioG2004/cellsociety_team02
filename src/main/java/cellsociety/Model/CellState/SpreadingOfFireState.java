package cellsociety.Model.CellState;

/** Spreading of Fire cell states: EMPTY, TREE, BURNING. */
public enum SpreadingOfFireState implements CellState {
    EMPTY("WHITE"),
    TREE("GREEN"),
    BURNING("RED");

    private static final int STATE_VALUE_EMPTY = 0;
    private static final int STATE_VALUE_TREE = 1;
    private static final int STATE_VALUE_BURNING = 2;
    private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid SpreadingOfFire state: ";

    private final String color;

    SpreadingOfFireState(String color) {
        this.color = color;
    }
    public static SpreadingOfFireState getNextStateInCycle(CellState current) {
        return switch (current) {
            case SpreadingOfFireState.EMPTY -> SpreadingOfFireState.TREE;
            case SpreadingOfFireState.TREE -> SpreadingOfFireState.BURNING;
            case SpreadingOfFireState.BURNING -> SpreadingOfFireState.EMPTY;
            default -> (SpreadingOfFireState) current;
        };
    }
    @Override
    public String getColor() {
        return color;
    }

    public static SpreadingOfFireState fromInt(int value) {
        return switch (value) {
            case STATE_VALUE_EMPTY -> EMPTY;
            case STATE_VALUE_TREE -> TREE;
            case STATE_VALUE_BURNING -> BURNING;
            default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
        };
    }

    public static int fromState(CellState state){
        return switch (state){
            case EMPTY -> STATE_VALUE_EMPTY;
            case TREE -> STATE_VALUE_TREE;
            case BURNING -> STATE_VALUE_BURNING;
            default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
        };
    }
}
