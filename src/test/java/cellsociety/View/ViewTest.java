package cellsociety.View;

import cellsociety.Controller.SimulationController;
import org.junit.jupiter.api.BeforeAll;
import util.DukeApplicationTest;
import org.junit.jupiter.api.Test;
public class ViewTest extends DukeApplicationTest {
  @Override
  public void start(Stage stage) {
  SimulationView view = new SimulationView();
  SimulationController controller = new SimulationController(view);
  Scene scene = new Scene(view.getRoot(), 1200, 750);
  stage.setScene(scene);
  stage.show();
}
  @Test
  void buttonClick_clickStartButton_nothingChanges () {
    Button button = lookup("Start Simulation").query();
    String expected = "Start Simulation";
    // GIVEN, app first starts up
    // WHEN, button is pressed to popup dialog box to enter input
    clickOn(button);
    writeInputsToDialog(expected);
    // THEN, check label text has been updated to match input
    assertLabelText(expected);
  }



}
