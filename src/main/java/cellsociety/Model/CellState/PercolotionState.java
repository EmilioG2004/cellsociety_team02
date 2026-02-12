package cellsociety.Model.CellState;

public enum PercolotionState implements CellState {
    CLOSED("BLACK"),
    OPEN("WHITE"),
    FULL("BLUE");

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
            case 0 -> CLOSED;
            case 1 -> OPEN;
            case 2 -> FULL;
            default -> throw new IllegalArgumentException("Invalid Percolotion state: " + value);
        };
    }
}
