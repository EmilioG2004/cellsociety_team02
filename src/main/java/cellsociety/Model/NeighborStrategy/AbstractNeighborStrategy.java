package cellsociety.Model.NeighborStrategy;

import cellsociety.Model.GridEdge.FiniteEdge;
import cellsociety.Model.GridEdge.GridEdge;

/**
 * Base class for neighbor strategies that holds the {@link GridEdge} field.
 * Defaults to {@link FiniteEdge} (clip out-of-bounds positions).
 *
 * @author Michael Lei
 */
public abstract class AbstractNeighborStrategy implements NeighborStrategy {

  private GridEdge gridEdge;

  protected AbstractNeighborStrategy() {
    this.gridEdge = new FiniteEdge();
  }

  protected AbstractNeighborStrategy(GridEdge gridEdge) {
    this.gridEdge = gridEdge;
  }

  @Override
  public GridEdge getGridEdge() {
    return gridEdge;
  }

  @Override
  public void setGridEdge(GridEdge gridEdge) {
    this.gridEdge = gridEdge;
  }
}
