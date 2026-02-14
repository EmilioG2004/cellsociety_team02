package cellsociety.Model.CellState;

public enum SpreadingOfFireState implements CellState {
    EMPTY("WHITE"),
    TREE("GREEN"),
    BURNING("RED");

    private final String color;

    SpreadingOfFireState(String color) {
        this.color = color;
    }
    public static SpreadingOfFireState getNextStateInCycle(CellState current) {
        return switch (current) {
            case SpreadingOfFireState.EMPTY -> SpreadingOfFireState.TREE;
            case SpreadingOfFireState.TREE -> SpreadingOfFireState.BURNING;
            case SpreadingOfFireState.BURNING -> SpreadingOfFireState.EMPTY;
            case SpreadingOfFireState.TREE -> SpreadingOfFireState.BURNING;
            default -> (SpreadingOfFireState) current;
        };
    }
    @Override
    public String getColor() {
        return color;
    }

    public static SpreadingOfFireState fromInt(int value) {
        return switch (value) {
            case 0 -> EMPTY;
            case 1 -> TREE;
            case 2 -> BURNING;
            default -> throw new IllegalArgumentException("Invalid SpreadingOfFire state: " + value);
        };
    }
}
