package cellsociety.view;

import cellsociety.Model.Grid.GridViewData;
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
 * <p>Purpose: provide a straightforward grid visualization for cellular automata
 * while remaining compatible with the {@link GridView} interface.</p>
 *
 * <p>Assumptions: grid dimensions are fixed for the lifetime of the view; the
 * provided {@link GridViewData} matches those dimensions.</p>
 *
 * <p>Dependencies: JavaFX {@link GridPane}, {@link Rectangle}, and binding APIs.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * GridView gridView = new GridPaneView(20, 20);
 * gridView.render(sim.getGridViewData());
 * }</pre>
 *
 * <p>Other details: rotation is implemented by rotating the underlying GridPane
 * and exposing a layout-friendly wrapper node.</p>
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

  private final Group myRotator;
  private final StackPane myWrapper = new StackPane();


  /**
   * Constructs a GridPaneView with the specified dimensions.
   *
   * <p>Purpose: initializes the visual grid and allocates rectangles for each cell.</p>
   *
   * <p>Assumptions: {@code rows} and {@code cols} are positive integers.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
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
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
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
   * <p>Assumptions: the grid has been constructed.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
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
   * <p>Assumptions: {@code grid} dimensions match the view's dimensions.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  @Override
  public void render(GridViewData grid) {
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
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  @Override
  public void setOnCellClicked(java.util.function.BiConsumer<Integer, Integer> handler) {
    this.myOnCellClicked = handler;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Assumptions: {@code grid} dimensions match the view's dimensions.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  @Override
  public void toggleOutline(GridViewData grid){
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
    return switch (stateValue) {
      case 0 -> Color.WHITE;
      case 1 -> Color.BLACK;
      case 2 -> Color.DARKORANGE;
      case 3 -> Color.DODGERBLUE;
      case 4 -> Color.FORESTGREEN;
      default -> Color.GRAY;
    };
  }

  /**
   * {@inheritDoc}
   *
   * <p>Assumptions: rotation state is maintained internally.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void rotateRight() {
    myRotationSteps.set((myRotationSteps.get() + 1) % 4);
    myRotator.setRotate(myRotationSteps.get() * 90.0);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Assumptions: rotation state is maintained internally.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void rotateLeft() {
    myRotationSteps.set((myRotationSteps.get() + 3) % 4);
    myRotator.setRotate(myRotationSteps.get() * 90.0);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Assumptions: {@code container} is a Region that participates in layout.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
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
