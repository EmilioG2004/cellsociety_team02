package cellsociety.View;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GridHandler;
import java.util.Map;
import javafx.scene.Node;

public interface GridView {

  void render(GridHandler myGridHandler, Map<CellState, String> stateColors);

  void clear();

  Node getNode();
}
