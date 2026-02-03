package cellsociety.Model;

public interface ReadOnlyGrid {
    int getRows();
    int getCols();
    String getCellStateColor(int row, int col, GameRule rules);
}
