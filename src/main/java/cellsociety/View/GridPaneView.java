package cellsociety.View;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GridHandler;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GridPaneView implements GridView {

  private final GridPane myGrid = new GridPane();
  private final double myCellSize;
  private final Rectangle[][] myCells;
  private final int myRows;
  private final int myCols;

  public GridPaneView(int rows, int cols, double cellSize) {
    this.myCellSize = cellSize;
    this.myCols = cols;
    this.myRows = rows;
    this.myCells = new Rectangle[rows][cols];

    buildGrid();
  }

  @Override
  public Node getNode() {
    return myGrid;
  }

  private void buildGrid() {
    myGrid.setGridLinesVisible(true);

    for (int r = 0; r < myRows; r++) {
      for (int c = 0; c < myCols; c++) {
        Rectangle cell = new Rectangle(myCellSize, myCellSize);
        cell.setFill(Color.WHITE);
        cell.setStroke(Color.BLACK);

        myCells[r][c] = cell;
        myGrid.add(cell, c, r);
      }
    }
  }

  @Override
  public void clear() {
    for (int r = 0; r < myRows; r++) {
      for (int c = 0; c < myCols; c++) {
        myCells[r][c].setFill(Color.WHITE);
      }
    }
  }

  @Override
  public void render(GridHandler myGridHandler, Map<CellState, String> stateColors) {
    for (int r = 0; r < myGridHandler.getRows(); r++) {
      for (int c = 0; c < myGridHandler.getCols(); c++) {
        CellState cellState = myGridHandler.getCellState(r, c);
        myCells[r][c].setFill(Paint.valueOf(stateColors.get(cellState)));
      }
    }
  }
}
