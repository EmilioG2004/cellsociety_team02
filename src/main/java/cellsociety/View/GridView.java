package cellsociety.View;

import cellsociety.Model.ReadOnlyGrid;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

public interface GridView {

  void render(ReadOnlyGrid myGrid, Map<String,String> stateColors);

  void clear();

  Node getNode();

}
