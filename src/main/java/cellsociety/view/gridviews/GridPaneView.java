package cellsociety.view.gridviews;

import cellsociety.Model.Grid.ViewGrid;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * GridPane-based {@link GridView} implementation that renders cells as rectangles.
 *
 *Purpose: provide a straightforward grid visualization for cellular automata
 * while remaining compatible with the {@link GridView} interface.
 *
 *Assumptions: grid dimensions are fixed for the lifetime of the view; the
 * provided {@link ViewGrid} matches those dimensions.
 *
 *Dependencies: JavaFX {@link GridPane}, {@link Rectangle}, and binding APIs.
 *
 *Example usage:
 * Example:
 * GridView gridView = new GridPaneView(20, 20);
 * gridView.render(sim.getViewGrid());
 *
 *
 *Other details: rotation is implemented by rotating the underlying GridPane
 * and exposing a layout-friendly wrapper node.
 *
 * @author Emilio Garcia
 * @author Michael Lei
 */
public class GridPaneView implements GridView {

  private final GridPane myGridPane = new GridPane();
  private Rectangle[][] myCells;
  private int myRows;
  private int myCols;
  private java.util.function.BiConsumer<Integer, Integer> myOnCellClicked;
  private boolean isOutlined;
  private final IntegerProperty myRotationSteps = new SimpleIntegerProperty(0);
  private Map<Integer, Color> myStateColors = new HashMap<>();

  private final Group myRotator;
  private final StackPane myWrapper = new StackPane();


  /**
   * Constructs a GridPaneView with the specified dimensions.
   *
   *Purpose: initializes the visual grid and allocates rectangles for each cell.
   *
   *Assumptions: {@code rows} and {@code cols} are positive integers.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param rows number of rows in the grid
   * @param cols number of columns in the grid
   */
  public GridPaneView(int rows, int cols) {
    myCols = cols;
    myRows = rows;
    myCells = new Rectangle[rows][cols];
    isOutlined = true;
    myRotator = new Group(myGridPane);
    bindWrapper();
    buildGrid();

  }

  private void bindWrapper() {
    myWrapper.setAlignment(Pos.CENTER);
    myWrapper.getChildren().add(myRotator);

    myWrapper.prefWidthProperty().bind(Bindings.createDoubleBinding(
        () -> myRotator.getBoundsInParent().getWidth(),
        myRotator.boundsInParentProperty()
    ));
    myWrapper.prefHeightProperty().bind(Bindings.createDoubleBinding(
        () -> myRotator.getBoundsInParent().getHeight(),
        myRotator.boundsInParentProperty()
    ));
  }

  /**
   * {@inheritDoc}
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return node used to display the grid
   */
  @Override
  public Node getNode() {
    return myWrapper;
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
        Rectangle cell = new Rectangle();
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
   * {@inheritDoc}
   *
   *Assumptions: the grid has been constructed.
   *
   *Exceptions: none.
   *
   *Return value: none.
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
   * {@inheritDoc}
   *
   *Assumptions: {@code grid} dimensions match the view's dimensions.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  @Override
  public void render(ViewGrid grid) {
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        myCells[r][c].setFill(stateToColor(grid.getCellStateInt(r, c)));

        if (isOutlined){
          myCells[r][c].setStroke(Color.BLACK);
        } else {
          myCells[r][c].setStroke(stateToColor(grid.getCellStateInt(r, c)));
        }
      }
    }
  }
  /**
   * {@inheritDoc}
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  @Override
  public void setOnCellClicked(java.util.function.BiConsumer<Integer, Integer> handler) {
    this.myOnCellClicked = handler;
  }

  /**
   * {@inheritDoc}
   *
   *Assumptions: {@code grid} dimensions match the view's dimensions.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  @Override
  public void toggleOutline(ViewGrid grid){
    isOutlined = !isOutlined;
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        if (isOutlined){
          myCells[r][c].setStroke(Color.BLACK);
        } else {
          myCells[r][c].setStroke(stateToColor(grid.getCellStateInt(r, c)));
        }
      }
    }
  }

  private Color stateToColor(int stateValue) {
    Color mappedColor = myStateColors.get(stateValue);
    if (mappedColor != null) {
      return mappedColor;
    }
    return switch (stateValue) {
      case 0 -> Color.WHITE;
      case 1 -> Color.BLACK;
      case 2 -> Color.DARKORANGE;
      case 3 -> Color.DODGERBLUE;
      case 4 -> Color.FORESTGREEN;
      default -> Color.GRAY;
    };
  }

  @Override
  public void setStateColors(Map<Integer, String> stateColors) {
    myStateColors.clear();
    for (Map.Entry<Integer, String> entry : stateColors.entrySet()) {
      myStateColors.put(entry.getKey(), Color.web(entry.getValue()));
    }
  }

  /**
   * {@inheritDoc}
   *
   *Assumptions: rotation state is maintained internally.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  public void rotateRight() {
    myRotationSteps.set((myRotationSteps.get() + 1) % 4);
    myRotator.setRotate(myRotationSteps.get() * 90.0);
  }

  /**
   * {@inheritDoc}
   *
   *Assumptions: rotation state is maintained internally.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  public void rotateLeft() {
    myRotationSteps.set((myRotationSteps.get() + 3) % 4);
    myRotator.setRotate(myRotationSteps.get() * 90.0);
  }

  /**
   * {@inheritDoc}
   *
   *Assumptions: {@code container} is a Region that participates in layout.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  @Override
  public void bindToContainer(Region container){
    DoubleBinding cellSize = Bindings.createDoubleBinding(() -> computeCellSize(container), container.widthProperty(), container.heightProperty(), myRotationSteps);

    for (int r = 0; r < myRows; r++) {
      for (int c = 0; c < myCols; c++) {
        Rectangle rect = myCells[r][c];
        rect.widthProperty().bind(cellSize);
        rect.heightProperty().bind(cellSize);
      }
    }
  }

  private double computeCellSize(Region container) {
    double width = container.getWidth();
    double height = container.getHeight();

    double padWidth = 0;
    double padHeight = 0;
    if (container.getPadding() != null) {
      padWidth = container.getPadding().getLeft() + container.getPadding().getRight();
      padHeight = container.getPadding().getTop() + container.getPadding().getBottom();
    }

    double availWidth = Math.max(1, width - padWidth);
    double availHeight = Math.max(1, height - padHeight);

    boolean rotated90 = (myRotationSteps.get() % 2 != 0);

    int effectiveCols = rotated90 ? myRows : myCols;
    int effectiveRows = rotated90 ? myCols : myRows;

    double raw = Math.min(availWidth / effectiveCols, availHeight / effectiveRows);

    return Math.max(1, Math.floor(raw - 1));
  }

}
