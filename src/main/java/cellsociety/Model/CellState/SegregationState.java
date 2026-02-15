package cellsociety.Model.CellState;

public enum SegregationState implements CellState {
    EMPTY("WHITE"),
    GROUP_A("BLUE"),
    GROUP_B("RED");

    private static final int STATE_VALUE_EMPTY = 0;
    private static final int STATE_VALUE_GROUP_A = 1;
    private static final int STATE_VALUE_GROUP_B = 2;
    private static final String ERROR_MESSAGE_INVALID_STATE = "Invalid Segregation state: ";

    private final String color;

    SegregationState(String color) {
        this.color = color;
    }
    
    @Override
    public String getColor() {
        return color;
    }

    public static SegregationState fromInt(int value) {
        return switch (value) {
            case STATE_VALUE_EMPTY -> EMPTY;
            case STATE_VALUE_GROUP_A -> GROUP_A;
            case STATE_VALUE_GROUP_B -> GROUP_B;
            default -> throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_STATE + value);
        };
    }
}
