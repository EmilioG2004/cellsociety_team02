package cellsociety.view;

import cellsociety.controller.SimulationController;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import util.DukeApplicationTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewTest extends DukeApplicationTest {
  @Override
  public void start(Stage stage) {
    ResourceBundle bundle = ResourceBundle.getBundle("cellsociety.Languages.English");
    SimulationView view = new SimulationView(bundle);
    @SuppressWarnings("unused") // controller wires view buttons; needed for test setup
    Scene scene = new Scene(view.getRoot(), 1200, 750);
    SimulationController controller = new SimulationController(view, scene);

    stage.setScene(scene);
    stage.show();
  }

  @Test
  void buttonClick_clickStartButton_nothingChanges() {
    Button button = lookup("Start Simulation").query();
    String expected = "Start Simulation";
    // GIVEN: app first starts up (no simulation loaded)
    // WHEN: button is pressed
    clickOn(button);
    // THEN: button text is unchanged (nothing visible changes)
    assertEquals(expected, button.getText());
  }
}
