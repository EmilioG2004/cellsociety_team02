package cellsociety.View;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

public interface GridView {

  void render(ReadOnlyGrid myGrid);

  void clear();

  Node getNode();

}
