package cellsociety.view;

import static cellsociety.view.constants.BundleKeys.*;

import cellsociety.view.constants.ResourcePaths;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 * Owns the SimulationView's controls and centralizes bundle-based text updates.
 *
 *Purpose: keeps {@link SimulationView} focused on layout by isolating control creation,
 * language application, and accessors in one place.
 *
 *Assumptions: the provided {@link ResourceBundle} contains all required keys; callers
 * update the bundle via {@link #applyLanguage(ResourceBundle)} when the language changes.
 *
 *Dependencies: JavaFX {@link Button} and {@link Label} controls and a
 * {@link ResourceBundle} for localization.
 *
 *Example usage:
 * Example:
 * ButtonManager buttons = new ButtonManager(bundle);
 * HBox uploadRow = new HBox(buttons.getUploadRowNodes());
 *
 *
 *Other details: this class does not wire event handlers; controllers do that.
 *
 * @author Emilio Garcia
 */
public class ButtonManager {

  private ResourceBundle myBundle;

  // Labels
  private final Label myTitle = new Label();
  private final Label mySpeedLabel = new Label();
  private final Label myNeighborStrategy = new Label();
  private final Label myGridEdge = new Label();

  // Buttons
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
  private final ChoiceBox<String> myNeighborChoices = new ChoiceBox<>();
  private final ChoiceBox<String> myGridEdgeChoices = new ChoiceBox<>();

  /**
   * Creates a button manager and applies the initial language.
   *
   *Purpose: initializes all labels and buttons with localized text.
   *
   *Assumptions: {@code bundle} is non-null and contains the expected keys.
   *
   *Exceptions: {@link java.util.MissingResourceException} if required keys are missing.
   *
   *Return value: none.
   *
   * @param bundle localization bundle for UI text
   */
  public ButtonManager(ResourceBundle bundle) {
    myBundle = bundle;
    applyLanguage(bundle);
    myForwardButton.setText(myBundle.getString(BUTTON_STEP_FORWARD));
    buildNeighborChoices();
    buildGridEdgeChoices();

  }

  /**
   * Applies localized text to all controls owned by this manager.
   *
   *Purpose: updates labels and button text when the language changes.
   *
   *Assumptions: {@code bundle} contains all required keys.
   *
   *Exceptions: {@link java.util.MissingResourceException} if required keys are missing.
   *
   *Return value: none.
   *
   * @param bundle localization bundle for UI text
   */
  public void applyLanguage(ResourceBundle bundle) {
    myBundle = bundle;

    myTitle.setText(myBundle.getString(APP_TITLE));
    myForwardButton.setText(myBundle.getString(BUTTON_STEP_FORWARD));
    myBackButton.setText(myBundle.getString(BUTTON_STEP_BACKWARD));
    myStartButton.setText(myBundle.getString(BUTTON_START));
    myPauseButton.setText(myBundle.getString(BUTTON_PAUSE));
    mySpeedUpButton.setText(myBundle.getString(BUTTON_SPEED_UP));
    mySlowDownButton.setText(myBundle.getString(BUTTON_SLOW_DOWN));
    mySaveButton.setText(myBundle.getString(BUTTON_SAVE_STATE));
    myResetButton.setText(myBundle.getString(BUTTON_RESET_GRID));
    myToggleOutlineButton.setText(myBundle.getString(BUTTON_TOGGLE_OUTLINES));
    mySettingsButton.setText(myBundle.getString(BUTTON_SETTINGS));
    myRotateRightButton.setText(myBundle.getString(BUTTON_ROTATE_RIGHT));
    myRotateLeftButton.setText(myBundle.getString(BUTTON_ROTATE_LEFT));
    myChangeDirectionButton.setText(myBundle.getString(BUTTON_CHANGE_DIRECTION));
    myNeighborStrategy.setText(myBundle.getString(LABEL_NEIGHBOR));
    myGridEdge.setText(myBundle.getString(LABEL_EDGE));
  }

  /**
   * Formats and sets the current speed label using the bundle key {@code label.currentSpeed}.
   *
   *Purpose: keeps the displayed speed multiplier in sync with controller state.
   *
   *Assumptions: {@code speedMultiplier} is positive.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param speedMultiplier current speed multiplier (e.g., 0.5, 1.0, 2.0)
   */
  public void setSpeedLabel(double speedMultiplier) {
    String pattern = myBundle.getString(LABEL_CURRENT_SPEED);
    mySpeedLabel.setText(MessageFormat.format(pattern, speedMultiplier));
  }

  /**
   * Returns the buttons shown in the upload row.
   *
   *Purpose: enables {@link SimulationView} to assemble its upload row layout.
   *
   *Assumptions: button order is meaningful to the UI layout.
   *
   *Exceptions: none.
   *
   * @return ordered list of upload-row buttons
   */
  public List<Node> getUploadRowNodes() {
    return List.of(
        mySaveButton,
        mySettingsButton,
        myNeighborStrategy,
        myNeighborChoices,
        myGridEdge,
        myGridEdgeChoices
    );
  }

  private void buildNeighborChoices(){
    myNeighborChoices.getItems().addAll(
        ResourcePaths.VON_NEUMANN,
        ResourcePaths.MOORE,
        ResourcePaths.MOORE_RADIUS_2,
        ResourcePaths.DIAGONAL
    );
  }

  private void buildGridEdgeChoices() {
    myGridEdgeChoices.getItems().addAll(
        ResourcePaths.FINITE,
        ResourcePaths.TOROIDAL,
        ResourcePaths.CYLINDRICAL,
        ResourcePaths.MIRRORED
    );
  }

  /**
   * Returns the nodes shown in the control bar.
   *
   *Purpose: exposes the control bar elements in a consistent order.
   *
   *Assumptions: the calling view is responsible for layout spacing and alignment.
   *
   *Exceptions: none.
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
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return label used for the app title
   */
  public Label getTitleLabel() { return myTitle; }
  /**
   * Returns the speed label.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return label displaying the speed multiplier
   */
  public Label getSpeedLabel() { return mySpeedLabel; }
  /**
   * Returns the step-forward button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that advances one step
   */
  public Button getForwardButton() { return myForwardButton; }
  /**
   * Returns the step-backward button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that steps backward
   */
  public Button getBackButton() { return myBackButton; }
  /**
   * Returns the start button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that starts the simulation
   */
  public Button getStartButton() { return myStartButton; }
  /**
   * Returns the pause button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that pauses the simulation
   */
  public Button getPauseButton() { return myPauseButton; }
  /**
   * Returns the speed-up button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that increases speed
   */
  public Button getSpeedUpButton() { return mySpeedUpButton; }
  /**
   * Returns the slow-down button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that decreases speed
   */
  public Button getSlowDownButton() { return mySlowDownButton; }
  /**
   * Returns the save button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that saves current state to XML
   */
  public Button getSaveButton() { return mySaveButton; }
  /**
   * Returns the reset button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that resets the simulation
   */
  public Button getResetButton() { return myResetButton; }
  /**
   * Returns the direction toggle button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that toggles forward/backward stepping
   */
  public Button getChangeDirectionButton() { return myChangeDirectionButton; }
  /**
   * Returns the outline toggle button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that toggles grid outlines
   */
  public Button getToggleOutlineButton() { return myToggleOutlineButton; }
  /**
   * Returns the settings button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that opens the settings splash
   */
  public Button getSettingsButton() { return mySettingsButton; }
  /**
   * Returns the rotate-left button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that rotates grid left
   */
  public Button getRotateLeftButton() { return myRotateLeftButton; }
  /**
   * Returns the rotate-right button.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return button that rotates grid right
   */
  public Button getRotateRightButton() { return myRotateRightButton; }

  public ChoiceBox<String> getNeighborChoices() { return myNeighborChoices; }

  public ChoiceBox<String> getGridEdgeChoices() { return myGridEdgeChoices; }
}
