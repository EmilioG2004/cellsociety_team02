package cellsociety.Model.CellState;

/** Spreading of Fire cell states: EMPTY, TREE, BURNING. */
public enum WaTorState implements CellState {
    SHARK("BLUE"),
    FISH("GREEN"),
    WATER("WHITE");

    private static final int STATE_VALUE_WATER = 0;
    private static final int STATE_VALUE_FISH = 1;
    private static final int STATE_VALUE_SHARK = 2;
    private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid WaTOR World state: ";

    private final String color;

    WaTorState(String color) {
        this.color = color;
    }
    public static WaTorState getNextStateInCycle(CellState current) {
        return switch (current) {
            case WaTorState.WATER -> WaTorState.FISH;
            case WaTorState.FISH -> WaTorState.SHARK;
            case WaTorState.SHARK -> WaTorState.WATER;
            default -> (WaTorState) current;
        };
    }
    @Override
    public String getColor() {
        return color;
    }

    public static WaTorState fromInt(int value) {
        return switch (value) {
            case STATE_VALUE_WATER -> WATER;
            case STATE_VALUE_FISH -> FISH;
            case STATE_VALUE_SHARK -> SHARK;
            default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
        };
    }

    public static int fromState(CellState state) {
        return switch (state) {
            case WATER -> STATE_VALUE_WATER;
            case FISH -> STATE_VALUE_FISH;
            case SHARK -> STATE_VALUE_SHARK;
            default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + state);
        };
    }
} 
    
