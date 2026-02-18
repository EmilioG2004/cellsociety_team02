package cellsociety.view;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Owns all SimulationView buttons/labels and centralizes bundle-based text updates.
 * Keeps SimulationView cleaner by moving getters and language application here.
 */
public class ButtonManager {

  private ResourceBundle myBundle;

  // Labels
  private final Label myTitle = new Label();
  private final Label mySpeedLabel = new Label();
  private final Label myStateStackSizeLabel = new Label();

  // Buttons
  private final Button myUploadButton = new Button();
  private final Button myForwardButton = new Button();
  private final Button myBackButton = new Button();
  private final Button myStartButton = new Button();
  private final Button myPauseButton = new Button();
  private final Button mySpeedUpButton = new Button();
  private final Button mySlowDownButton = new Button();
  private final Button mySaveButton = new Button();
  private final Button myResetButton = new Button();
  private final Button myChangeDirectionButton = new Button();
  private final Button myAboutButton = new Button();
  private final Button myToggleOutlineButton = new Button();
  private final Button mySettingsButton = new Button();
  private final Button myRotateLeftButton = new Button();
  private final Button myRotateRightButton = new Button();
  private final Button myMultiSimButton = new Button();

  public ButtonManager(ResourceBundle bundle) {
    myBundle = bundle;
    applyLanguage(bundle);
    myForwardButton.setText(myBundle.getString("button.stepForward"));

  }

  /**
   * Applies localized text to all controls owned by this manager.
   */
  public void applyLanguage(ResourceBundle bundle) {
    myBundle = bundle;

    myTitle.setText(myBundle.getString("app.title"));
    myUploadButton.setText(myBundle.getString("button.uploadConfig"));
    myForwardButton.setText(myBundle.getString("button.stepForward"));
    myBackButton.setText(myBundle.getString("button.stepBackward"));
    myStartButton.setText(myBundle.getString("button.start"));
    myPauseButton.setText(myBundle.getString("button.pause"));
    mySpeedUpButton.setText(myBundle.getString("button.speedUp"));
    mySlowDownButton.setText(myBundle.getString("button.slowDown"));
    mySaveButton.setText(myBundle.getString("button.saveState"));
    myResetButton.setText(myBundle.getString("button.resetGrid"));
    myAboutButton.setText(myBundle.getString("button.aboutConfig"));
    myToggleOutlineButton.setText(myBundle.getString("button.toggleOutlines"));
    mySettingsButton.setText(myBundle.getString("button.settings"));
    myRotateRightButton.setText(myBundle.getString("button.rotateRight"));
    myRotateLeftButton.setText(myBundle.getString("button.rotateLeft"));
    myChangeDirectionButton.setText(myBundle.getString("button.changeDirection"));
    myMultiSimButton.setText(myBundle.getString("button.multisim"));


    myStateStackSizeLabel.setText(myBundle.getString("label.generation.unset"));
  }

  /**
   * Formats and sets the current speed label using the bundle key "label.currentSpeed".
   */
  public void setSpeedLabel(double speedMultiplier) {
    String pattern = myBundle.getString("label.currentSpeed");
    mySpeedLabel.setText(MessageFormat.format(pattern, speedMultiplier));
  }

  /**
   * Convenience: controls used in the upload row (top-left row).
   */
  public List<Button> getUploadRowButtons() {
    return List.of(myUploadButton, mySaveButton, myAboutButton, mySettingsButton, myMultiSimButton);
  }

  /**
   * Convenience: controls used in the control bar (left side).
   * NOTE: includes the speed & generation labels.
   */
  public List<javafx.scene.Node> getControlBarLeftNodes() {
    return List.of(
        myStartButton,
        myPauseButton,
        mySpeedUpButton,
        mySlowDownButton,
        myResetButton,
        myForwardButton,
        myBackButton,
        myChangeDirectionButton,
        myToggleOutlineButton,
        mySpeedLabel,
        myStateStackSizeLabel
    );
  }

  // -----------------------
  // Getters for controller/view wiring
  // -----------------------

  public Label getTitleLabel() { return myTitle; }
  public Label getSpeedLabel() { return mySpeedLabel; }
  public Label getStateStackSizeLabel() { return myStateStackSizeLabel; }

  public Button getUploadButton() { return myUploadButton; }
  public Button getForwardButton() { return myForwardButton; }
  public Button getBackButton() { return myBackButton; }
  public Button getStartButton() { return myStartButton; }
  public Button getPauseButton() { return myPauseButton; }
  public Button getSpeedUpButton() { return mySpeedUpButton; }
  public Button getSlowDownButton() { return mySlowDownButton; }
  public Button getSaveButton() { return mySaveButton; }
  public Button getResetButton() { return myResetButton; }
  public Button getChangeDirectionButton() { return myChangeDirectionButton; }
  public Button getAboutButton() { return myAboutButton; }
  public Button getToggleOutlineButton() { return myToggleOutlineButton; }
  public Button getSettingsButton() { return mySettingsButton; }
  public Button getRotateLeftButton() { return myRotateLeftButton; }
  public Button getRotateRightButton() { return myRotateRightButton; }
  public Button getMyMultiSimButton() { return myMultiSimButton; }
}
