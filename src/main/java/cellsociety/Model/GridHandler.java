package cellsociety.Model;
import cellsociety.Model.CellState.CellState;
public class GridHandler {
    private Grid myGrid;

    public GridHandler(Grid grid) {
        this.myGrid = grid;
    }
    public int getRows() {
        return myGrid.getRows();
    }
    public int getCols() {
        return myGrid.getCols();
    }
    public CellState[][] getCellStates() {
        return myGrid.getCellStates();
    }
    public CellState getCellState(int row, int col) {
        return myGrid.getCellState(row, col);
    }
}
