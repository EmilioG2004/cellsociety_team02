package cellsociety.Model.CellState;

/** Percolation cell states: CLOSED, OPEN, FULL. */
public enum PercolotionState implements CellState {
    CLOSED("BLACK"),
    OPEN("WHITE"),
    FULL("BLUE");

    private static final int STATE_VALUE_CLOSED = 0;
    private static final int STATE_VALUE_OPEN = 1;
    private static final int STATE_VALUE_FULL = 2;
    private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid Percolotion state: ";

    private final String color;

    PercolotionState(String color) {
        this.color = color;
    }

    @Override
    public String getColor() {
        return color;
    }

    public static PercolotionState fromInt(int value) {
        return switch (value) {
            case STATE_VALUE_CLOSED -> CLOSED;
            case STATE_VALUE_OPEN -> OPEN;
            case STATE_VALUE_FULL -> FULL;
            default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
        };
    }

    public static int fromState(CellState state){
        return switch (state){
            case CLOSED -> STATE_VALUE_CLOSED;
            case OPEN -> STATE_VALUE_OPEN;
            case FULL -> STATE_VALUE_FULL;
            default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
        };
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
