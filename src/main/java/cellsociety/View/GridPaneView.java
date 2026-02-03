package cellsociety.View;

import cellsociety.Model.ReadOnlyGrid;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GridPaneView implements GridView{
  private final GridPane myGrid = new GridPane();
  private final double myCellSize;
  private final Rectangle[][] myCells;
  private final int myRows;
  private final int myCols;
  private final static int ALIVE = 1;

  public GridPaneView(int rows, int cols, double cellSize){
    this.myCellSize = cellSize;
    this.myCols = cols;
    this.myRows = rows;
    this.myCells = new Rectangle[rows][cols];

    buildGrid();
  }

  @Override
  public Node getNode(){
    return myGrid;
  }

  private void buildGrid(){
    myGrid.setGridLinesVisible(true);

    for (int r = 0; r < myRows; r++){
      for (int c = 0; c < myCols; c++){
        Rectangle cell = new Rectangle(myCellSize,myCellSize);
        cell.setFill(Color.WHITE);
        cell.setStroke(Color.BLACK);

        myCells[r][c] = cell;
        myGrid.add(cell, c, r);
      }
    }
  }
  @Override
  public void clear(){
    for (int r = 0; r < myRows; r++){
      for (int c = 0; c < myCols; c++){
        myCells[r][c].setFill(Color.WHITE);
      }
    }
  }

  @Override
  public void render(ReadOnlyGrid grid, Map<String,String> stateColors){
    for (int r = 0; r < grid.getRows(); r++){
      for (int c = 0; c < grid.getCols(); c++){
        String stateString = "";
        int cellState = grid.getCellState(r, c);
        if (cellState == ALIVE) {
          stateString = "ALIVE";
        } else {
          stateString = "DEAD";
        }
        myCells[r][c].setFill(Paint.valueOf(stateColors.get(stateString)));
      }
    }
  }

  private Color getStateColor(int myState){
    if (myState == 1){
      return Color.LIGHTGREEN;
    }
    else {
      return Color.WHITE;
    }
  }



}
