package cellsociety.view;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Owns the SimulationView's controls and centralizes bundle-based text updates.
 *
 * <p>Purpose: keeps {@link SimulationView} focused on layout by isolating control creation,
 * language application, and accessors in one place.</p>
 *
 * <p>Assumptions: the provided {@link ResourceBundle} contains all required keys; callers
 * update the bundle via {@link #applyLanguage(ResourceBundle)} when the language changes.</p>
 *
 * <p>Dependencies: JavaFX {@link Button} and {@link Label} controls and a
 * {@link ResourceBundle} for localization.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ButtonManager buttons = new ButtonManager(bundle);
 * HBox uploadRow = new HBox(buttons.getUploadRowButtons());
 * }</pre>
 *
 * <p>Other details: this class does not wire event handlers; controllers do that.</p>
 *
 * @author Emilio Garcia
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
  private final Button myToggleOutlineButton = new Button();
  private final Button mySettingsButton = new Button();
  private final Button myRotateLeftButton = new Button();
  private final Button myRotateRightButton = new Button();
  private final Button myMultiSimButton = new Button();

  /**
   * Creates a button manager and applies the initial language.
   *
   * <p>Purpose: initializes all labels and buttons with localized text.</p>
   *
   * <p>Assumptions: {@code bundle} is non-null and contains the expected keys.</p>
   *
   * <p>Exceptions: {@link java.util.MissingResourceException} if required keys are missing.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param bundle localization bundle for UI text
   */
  public ButtonManager(ResourceBundle bundle) {
    myBundle = bundle;
    applyLanguage(bundle);
    myForwardButton.setText(myBundle.getString("button.stepForward"));

  }

  /**
   * Applies localized text to all controls owned by this manager.
   *
   * <p>Purpose: updates labels and button text when the language changes.</p>
   *
   * <p>Assumptions: {@code bundle} contains all required keys.</p>
   *
   * <p>Exceptions: {@link java.util.MissingResourceException} if required keys are missing.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param bundle localization bundle for UI text
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
    myToggleOutlineButton.setText(myBundle.getString("button.toggleOutlines"));
    mySettingsButton.setText(myBundle.getString("button.settings"));
    myRotateRightButton.setText(myBundle.getString("button.rotateRight"));
    myRotateLeftButton.setText(myBundle.getString("button.rotateLeft"));
    myChangeDirectionButton.setText(myBundle.getString("button.changeDirection"));
    myMultiSimButton.setText(myBundle.getString("button.multisim"));


    myStateStackSizeLabel.setText(myBundle.getString("label.generation.unset"));
  }

  /**
   * Formats and sets the current speed label using the bundle key {@code label.currentSpeed}.
   *
   * <p>Purpose: keeps the displayed speed multiplier in sync with controller state.</p>
   *
   * <p>Assumptions: {@code speedMultiplier} is positive.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param speedMultiplier current speed multiplier (e.g., 0.5, 1.0, 2.0)
   */
  public void setSpeedLabel(double speedMultiplier) {
    String pattern = myBundle.getString("label.currentSpeed");
    mySpeedLabel.setText(MessageFormat.format(pattern, speedMultiplier));
  }

  /**
   * Returns the buttons shown in the upload row.
   *
   * <p>Purpose: enables {@link SimulationView} to assemble its upload row layout.</p>
   *
   * <p>Assumptions: button order is meaningful to the UI layout.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return ordered list of upload-row buttons
   */
  public List<Button> getUploadRowButtons() {
    return List.of(mySaveButton, mySettingsButton);
  }

  /**
   * Returns the nodes shown in the control bar.
   *
   * <p>Purpose: exposes the control bar elements in a consistent order.</p>
   *
   * <p>Assumptions: the calling view is responsible for layout spacing and alignment.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return ordered list of control-bar nodes
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
        mySpeedLabel
    );
  }

  // -----------------------
  // Getters for controller/view wiring
  // -----------------------

  /**
   * Returns the title label.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return label used for the app title
   */
  public Label getTitleLabel() { return myTitle; }
  /**
   * Returns the speed label.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return label displaying the speed multiplier
   */
  public Label getSpeedLabel() { return mySpeedLabel; }
  /**
   * Returns the generation label.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return label displaying the current generation/stack size
   */
  public Label getStateStackSizeLabel() { return myStateStackSizeLabel; }

  /**
   * Returns the upload button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that opens the config chooser
   */
  public Button getUploadButton() { return myUploadButton; }
  /**
   * Returns the step-forward button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that advances one step
   */
  public Button getForwardButton() { return myForwardButton; }
  /**
   * Returns the step-backward button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that steps backward
   */
  public Button getBackButton() { return myBackButton; }
  /**
   * Returns the start button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that starts the simulation
   */
  public Button getStartButton() { return myStartButton; }
  /**
   * Returns the pause button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that pauses the simulation
   */
  public Button getPauseButton() { return myPauseButton; }
  /**
   * Returns the speed-up button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that increases speed
   */
  public Button getSpeedUpButton() { return mySpeedUpButton; }
  /**
   * Returns the slow-down button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that decreases speed
   */
  public Button getSlowDownButton() { return mySlowDownButton; }
  /**
   * Returns the save button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that saves current state to XML
   */
  public Button getSaveButton() { return mySaveButton; }
  /**
   * Returns the reset button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that resets the simulation
   */
  public Button getResetButton() { return myResetButton; }
  /**
   * Returns the direction toggle button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that toggles forward/backward stepping
   */
  public Button getChangeDirectionButton() { return myChangeDirectionButton; }
  /**
   * Returns the outline toggle button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that toggles grid outlines
   */
  public Button getToggleOutlineButton() { return myToggleOutlineButton; }
  /**
   * Returns the settings button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that opens the settings splash
   */
  public Button getSettingsButton() { return mySettingsButton; }
  /**
   * Returns the rotate-left button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that rotates grid left
   */
  public Button getRotateLeftButton() { return myRotateLeftButton; }
  /**
   * Returns the rotate-right button.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button that rotates grid right
   */
  public Button getRotateRightButton() { return myRotateRightButton; }
}
