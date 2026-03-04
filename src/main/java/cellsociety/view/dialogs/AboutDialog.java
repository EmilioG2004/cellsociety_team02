package cellsociety.view.dialogs;

import static cellsociety.view.constants.BundleKeys.*;

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
 *Purpose: provide an in-app "About" view for the currently loaded configuration.
 *
 *Assumptions: a {@link SimulationConfig} has been loaded before calling
 * {@link #generateInfo(SimulationConfig)}; language bundles include the required keys.
 *
 *Dependencies: JavaFX {@link StackPane}/{@link VBox} layout and a {@link ResourceBundle}
 * for localized strings.
 *
 *Example usage:
 * Example:
 * AboutDialog dialog = new AboutDialog();
 * dialog.applyLanguage(bundle);
 * dialog.generateInfo(config);
 *
 *
 * @author Emilio Garcia
 */
public class AboutDialog extends StackPane{
  private SimulationConfig myConfig;
  private ResourceBundle myBundle;
  private final Button myCloseButton;
  private final Text myTitle;
  private final Text myType;
  private final Text myNeighborStrategy;
  private final Text myAuthor;
  private final Text myDescription;
  private final Text myParameters;
  private String myCurrentNeighborStrategy;

  /**
   * Creates the dialog and its internal text nodes.
   *
   *Purpose: prepares the dialog for later population via {@link #generateInfo}.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  public AboutDialog(){
    setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    myCloseButton = new Button();
    myTitle = new Text();
    myType = new Text();
    myNeighborStrategy = new Text();
    myAuthor = new Text();
    myDescription = new Text();
    myParameters = new Text();
    setStyleClasses();

  }

  private void setStyleClasses() {
    myTitle.getStyleClass().add("text");
    myType.getStyleClass().add("text");
    myNeighborStrategy.getStyleClass().add("text");
    myAuthor.getStyleClass().add("text");
    myDescription.getStyleClass().add("text");
    myParameters.getStyleClass().add("text");
    getStyleClass().add("about");
  }

  /**
   * Populates the dialog with simulation metadata and shows it.
   *
   *Purpose: builds the dialog content whenever a configuration is available.
   *
   *Assumptions: {@code config} is non-null.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param config simulation configuration used to populate fields
   * @param neighborStrategy current neighbor strategy class name
   */
  public void generateInfo(SimulationConfig config, String neighborStrategy){
    myConfig = config;
    myCurrentNeighborStrategy = neighborStrategy;

    getChildren().clear();
    VBox infoPanel = new VBox(
        10,
        myTitle,
        myType,
        myNeighborStrategy,
        myAuthor,
        myDescription,
        myParameters,
        myCloseButton
    );
    infoPanel.setAlignment(Pos.TOP_CENTER);

    getChildren().add(infoPanel);

    if (myBundle != null) {
      applyLanguage(myBundle);
    }
  }

  /**
   * Removes the dialog from its parent container.
   *
   *Purpose: closes the overlay without disposing the object.
   *
   *Assumptions: the dialog is attached to a {@link Pane}.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  public void closeInfo() {
    if (getParent() instanceof Pane p) {
      p.getChildren().remove(this);
    }
  }

  /**
   * Returns the dialog's close button for controller wiring.
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return close button
   */
  public Button getMyCloseButton() { return myCloseButton; }

  /**
   * Applies localized text to all labels using the provided bundle.
   *
   *Purpose: updates visible strings when language changes.
   *
   *Assumptions: {@code bundle} contains the required keys.
   *
   *Exceptions: {@link java.util.MissingResourceException} if required keys are missing.
   *
   *Return value: none.
   *
   * @param bundle localization bundle for UI strings
   */
  public void applyLanguage(ResourceBundle bundle){
    myBundle = bundle;
    myTitle.setText(bundle.getString(ABOUT_TITLE));
    myCloseButton.setText(bundle.getString(BUTTON_CLOSE_PANEL));

    if (myConfig == null) return;

    myType.setText(MessageFormat.format(bundle.getString(ABOUT_SIMULATION_TYPE), myConfig.getSimulationType()));
    myNeighborStrategy.setText(MessageFormat.format(
        bundle.getString(ABOUT_NEIGHBOR_STRATEGY),
        myCurrentNeighborStrategy
    ));
    myDescription.setText(MessageFormat.format(bundle.getString(ABOUT_DESCRIPTION), myConfig.getDescription()));
    myAuthor.setText(MessageFormat.format(bundle.getString(ABOUT_AUTHOR), myConfig.getAuthor()));
    myParameters.setText(MessageFormat.format(bundle.getString(ABOUT_PARAMETERS), myConfig.getParameters()));
  }

}
