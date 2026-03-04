package cellsociety.Model.NeighborStrategy;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import cellsociety.Model.GridEdge.FiniteEdge;
import cellsociety.Model.GridEdge.GridEdge;
import cellsociety.Model.GridEdge.MirroredEdge;
import cellsociety.Model.GridEdge.ToroidalEdge;
import java.util.List;
import org.junit.jupiter.api.Test;

class AbstractNeighborStrategyTest {

  /** Minimal concrete subclass for testing the abstract base. */
  private static class TestStrategy extends AbstractNeighborStrategy {
    TestStrategy() { super(); }
    TestStrategy(GridEdge edge) { super(edge); }

    @Override
    public List<int[]> getNeighborOffsets() {
      return List.of(new int[]{0, 1});
    }
  }

  @Test
  void defaultConstructor_usesFiniteEdge() {
    TestStrategy s = new TestStrategy();
    assertInstanceOf(FiniteEdge.class, s.getGridEdge());
  }

  @Test
  void constructor_withGridEdge_usesProvidedEdge() {
    ToroidalEdge toroidal = new ToroidalEdge();
    TestStrategy s = new TestStrategy(toroidal);
    assertSame(toroidal, s.getGridEdge());
  }

  @Test
  void setGridEdge_changesEdge() {
    TestStrategy s = new TestStrategy();
    ToroidalEdge toroidal = new ToroidalEdge();
    s.setGridEdge(toroidal);
    assertSame(toroidal, s.getGridEdge());
  }

  @Test
  void setGridEdge_backToFinite() {
    TestStrategy s = new TestStrategy(new ToroidalEdge());
    FiniteEdge finite = new FiniteEdge();
    s.setGridEdge(finite);
    assertSame(finite, s.getGridEdge());
  }

  @Test
  void setGridEdge_toMirrored_usesMirroredEdge() {
    TestStrategy s = new TestStrategy();
    MirroredEdge mirrored = new MirroredEdge();
    s.setGridEdge(mirrored);
    assertSame(mirrored, s.getGridEdge());
  }
}
