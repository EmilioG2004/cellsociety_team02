package cellsociety.View;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.ReadOnlyGrid;
import java.util.Map;
import javafx.scene.Node;

public interface GridView {

  void render(ReadOnlyGrid myGrid, Map<CellState, String> stateColors);

  void clear();

  Node getNode();
}
