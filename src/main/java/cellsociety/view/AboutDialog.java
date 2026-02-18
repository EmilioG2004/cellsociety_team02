package cellsociety.view;

import cellsociety.config.SimulationConfig;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Overlay dialog that displays simulation metadata (type, author, description, parameters).
 *
 * <p>Purpose: provide an in-app "About" view for the currently loaded configuration.</p>
 *
 * <p>Assumptions: a {@link SimulationConfig} has been loaded before calling
 * {@link #generateInfo(SimulationConfig)}; language bundles include the required keys.</p>
 *
 * <p>Dependencies: JavaFX {@link StackPane}/{@link VBox} layout and a {@link ResourceBundle}
 * for localized strings.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * AboutDialog dialog = new AboutDialog();
 * dialog.applyLanguage(bundle);
 * dialog.generateInfo(config);
 * }</pre>
 *
 * @author Emilio Garcia
 */
public class AboutDialog extends StackPane{
  private SimulationConfig myConfig;
  private ResourceBundle myBundle;
  private final Button myCloseButton;
  private final Text myTitle;
  private final Text myType;
  private final Text myAuthor;
  private final Text myDescription;
  private final Text myParameters;

  /**
   * Creates the dialog and its internal text nodes.
   *
   * <p>Purpose: prepares the dialog for later population via {@link #generateInfo}.</p>
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public AboutDialog(){
    setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    myCloseButton = new Button();
    myTitle = new Text();
    myType = new Text();
    myAuthor = new Text();
    myDescription = new Text();
    myParameters = new Text();
    setStyleClasses();

  }

  private void setStyleClasses() {
    myTitle.getStyleClass().add("text");
    myType.getStyleClass().add("text");
    myAuthor.getStyleClass().add("text");
    myDescription.getStyleClass().add("text");
    myParameters.getStyleClass().add("text");
    getStyleClass().add("about");
  }

  /**
   * Populates the dialog with simulation metadata and shows it.
   *
   * <p>Purpose: builds the dialog content whenever a configuration is available.</p>
   *
   * <p>Assumptions: {@code config} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param config simulation configuration used to populate fields
   */
  public void generateInfo(SimulationConfig config){
    myConfig = config;

    getChildren().clear();
    VBox infoPanel = new VBox(10, myTitle, myType, myAuthor, myDescription, myParameters, myCloseButton);
    infoPanel.setAlignment(Pos.TOP_CENTER);

    getChildren().add(infoPanel);

    if (myBundle != null) {
      applyLanguage(myBundle);
    }
  }

  /**
   * Removes the dialog from its parent container.
   *
   * <p>Purpose: closes the overlay without disposing the object.</p>
   *
   * <p>Assumptions: the dialog is attached to a {@link Pane}.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void closeInfo() {
    if (getParent() instanceof Pane p) {
      p.getChildren().remove(this);
    }
  }

  /**
   * Returns the dialog's close button for controller wiring.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return close button
   */
  public Button getMyCloseButton() { return myCloseButton; }

  /**
   * Applies localized text to all labels using the provided bundle.
   *
   * <p>Purpose: updates visible strings when language changes.</p>
   *
   * <p>Assumptions: {@code bundle} contains the required keys.</p>
   *
   * <p>Exceptions: {@link java.util.MissingResourceException} if required keys are missing.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param bundle localization bundle for UI strings
   */
  public void applyLanguage(ResourceBundle bundle){
    myBundle = bundle;
    myTitle.setText(bundle.getString("about.title"));
    myCloseButton.setText(bundle.getString("button.closePanel"));

    if (myConfig == null) return;

    myType.setText(MessageFormat.format(bundle.getString("about.simulationType"), myConfig.getSimulationType()));
    myDescription.setText(MessageFormat.format(bundle.getString("about.description"), myConfig.getDescription()));
    myAuthor.setText(MessageFormat.format(bundle.getString("about.author"), myConfig.getAuthor()));
    myParameters.setText(MessageFormat.format(bundle.getString("about.parameters"), myConfig.getParameters()));
  }

}
