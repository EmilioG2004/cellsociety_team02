package cellsociety.View;

import cellsociety.Controller.SimulationController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewTest extends DukeApplicationTest {
  @Override
  public void start(Stage stage) {
    SimulationView view = new SimulationView();
    @SuppressWarnings("unused") // controller wires view buttons; needed for test setup
    SimulationController controller = new SimulationController(view);
    Scene scene = new Scene(view.getRoot(), 1200, 750);
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
