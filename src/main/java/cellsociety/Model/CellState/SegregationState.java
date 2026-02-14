package cellsociety.Model.CellState;

public enum SegregationState implements CellState {
    EMPTY("WHITE"),
    GROUP_A("BLUE"),
    GROUP_B("RED");

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
            case 0 -> EMPTY;
            case 1 -> GROUP_A;
            case 2 -> GROUP_B;
            default -> throw new IllegalArgumentException("Invalid Segregation state: " + value);
        };
    }
}
