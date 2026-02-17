package cellsociety.view;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.ReadOnlyGrid;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * A concrete GridView implementation that renders a simulation grid using a JavaFX GridPane.
 * Each cell in the grid is represented visually by a Rectangle whose fill color corresponds
 * to the cell’s current state.
 *
 * Purpose:
 * This class provides a simple, rectangular, grid-based visualization for cellular automata.
 * It allows the simulation view layer to display cell states without exposing or depending
 * on the underlying grid data structure.
 *
 * Assumptions:
 * - The number of rows and columns is fixed for the lifetime of this view.
 * - The provided ReadOnlyGrid has dimensions that match the rows and columns used to
 *   construct this GridPaneView.
 * - The stateColors map contains a color entry for every possible cell state.
 *
 * Dependencies:
 * - ReadOnlyGrid for accessing simulation state (rows, cols, getCellState) without mutation.
 * - JavaFX GridPane and Rectangle for rendering.
 * - JavaFX Color and Paint for visual styling.
 *
 * Example usage:
 * GridView gridView = new GridPaneView(20, 20, 15);
 * Node gridNode = gridView.getNode();
 *
 * @author Emilio Garcia
 * @author Michael Lei
 */
public class GridPaneView implements GridView {

  private final GridPane myGridPane = new GridPane();
  private final double myCellSize;
  private final Rectangle[][] myCells;
  private final int myRows;
  private final int myCols;
  private java.util.function.BiConsumer<Integer, Integer> myOnCellClicked;
  private boolean isOutlined;

  /**
   * Constructs a GridPaneView with the specified dimensions and cell size.
   *
   * Purpose:
   * Initializes the visual grid and allocates rectangle objects for each cell.
   *
   * Assumptions:
   * - rows and cols are positive integers.
   * - cellSize is a positive value representing pixel dimensions.
   *
   * @param rows number of rows in the grid
   * @param cols number of columns in the grid
   * @param cellSize size (in pixels) of each cell’s width and height
   */
  public GridPaneView(int rows, int cols, double cellSize) {
    this.myCellSize = cellSize;
    this.myCols = cols;
    this.myRows = rows;
    this.myCells = new Rectangle[rows][cols];
    this.isOutlined = true;

    buildGrid();
  }

  /**
   * Returns the JavaFX node representing this grid view.
   *
   * Purpose:
   * Allows this view to be embedded in higher-level UI layouts without exposing
   * internal implementation details.
   *
   * @return Node containing the rendered grid
   */
  @Override
  public Node getNode() {
    return myGridPane;
  }

  /**
   * Builds the GridPane and initializes all cell rectangles.
   *
   * Purpose:
   * Creates the visual structure of the grid and stores references to each cell
   * so they can be efficiently updated during rendering.
   *
   * Assumptions:
   * - Grid dimensions are fixed and do not change after construction.
   */
  private void buildGrid() {
    myGridPane.setGridLinesVisible(false);

    for (int r = 0; r < myRows; r++) {
      for (int c = 0; c < myCols; c++) {
        Rectangle cell = new Rectangle(myCellSize, myCellSize);
        cell.setFill(Color.WHITE);
        cell.setStroke(Color.BLACK);

        myCells[r][c] = cell;
        final int row = r;
        final int col = c;
        cell.setOnMouseClicked(e -> { if (myOnCellClicked != null) myOnCellClicked.accept(row,col);
        });

        myGridPane.add(cell, c, r);
      }
    }
  }

  /**
   * Clears the grid by resetting all cells to the default background color.
   *
   * Purpose:
   * Provides a simple way to visually reset the grid without reconstructing it.
   *
   * Assumptions:
   * - The grid has already been built and all cell rectangles exist.
   */
  @Override
  public void clear() {
    for (int r = 0; r < myRows; r++) {
      for (int c = 0; c < myCols; c++) {
        myCells[r][c].setFill(Color.WHITE);
      }
    }
  }

  /**
   * Renders the current simulation state onto the grid.
   *
   * Purpose:
   * Updates each cell’s color based on the state values provided by the ReadOnlyGrid
   * and the corresponding color mapping.
   *
   * Assumptions:
   * - The dimensions of the ReadOnlyGrid match this view’s row and column counts.
   * - stateColors contains a valid color string for every state value returned by the grid.
   *
   * @param grid read-only view of the simulation grid (rows, cols, getCellState)
   * @param stateColors mapping from cell state values to color strings
   */
  @Override
  public void render(ReadOnlyGrid grid, Map<CellState, String> stateColors) {
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        CellState cellState = grid.getCellState(r, c);
        myCells[r][c].setFill(Paint.valueOf(stateColors.get(cellState)));

        if (isOutlined){
          myCells[r][c].setStroke(Color.BLACK);
        } else {
          myCells[r][c].setStroke(Paint.valueOf(stateColors.get(cellState)));
        }
      }
    }
  }
  @Override
  public void setOnCellClicked(java.util.function.BiConsumer<Integer, Integer> handler)
  {
    this.myOnCellClicked = handler;
  }

  @Override
  public void toggleOutline(ReadOnlyGrid grid, Map<CellState, String> stateColors){
    isOutlined = !isOutlined;
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        CellState cellState = grid.getCellState(r, c);
        if (isOutlined){
          myCells[r][c].setStroke(Color.BLACK);
        } else {
          myCells[r][c].setStroke(Paint.valueOf(stateColors.get(cellState)));
        }
      }
    }
  }

}
