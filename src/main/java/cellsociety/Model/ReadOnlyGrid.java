package cellsociety.Model;

public interface ReadOnlyGrid {
    int getRows();
    int getCols();
    int getCellState(int row, int col);
}
