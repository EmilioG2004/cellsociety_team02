package cellsociety.Model.CellState;

public enum FallingSandState implements CellState {
    EMPTY("WHITE"),
    METAL("GRAY"),
    SAND("YELLOW"),
    WATER("BLUE");

    private static final int STATE_VALUE_EMPTY = 0;
    private static final int STATE_VALUE_METAL = 1;
    private static final int STATE_VALUE_SAND = 2;
    private static final int STATE_VALUE_WATER = 3;
    private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid FallingSand state: ";

    private final String color;

    FallingSandState(String color) {
        this.color = color;
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
    @Override
    public String getColor() {
        return color;
    }

    public static FallingSandState fromInt(int value) {
        return switch (value) {
            case STATE_VALUE_EMPTY -> EMPTY;
            case STATE_VALUE_METAL -> METAL;
            case STATE_VALUE_SAND -> SAND;
            case STATE_VALUE_WATER -> WATER;
            default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
        };
    }

    public static int fromState(CellState state){
        return switch (state){
            case EMPTY -> STATE_VALUE_EMPTY;
            case METAL -> STATE_VALUE_METAL;
            case SAND -> STATE_VALUE_SAND;
            case WATER -> STATE_VALUE_WATER;
            default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
        };
    }
}
