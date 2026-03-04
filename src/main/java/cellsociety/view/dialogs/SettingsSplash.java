package cellsociety.view.dialogs;

import static cellsociety.view.constants.BundleKeys.*;
import static cellsociety.view.constants.SimulationUiConstants.SPACING_MEDIUM;

import cellsociety.view.utils.LanguageApplier;
import cellsociety.view.constants.SimulationUiConstants;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import cellsociety.view.constants.ResourcePaths;

/**
 * Full-screen settings overlay for language/theme selection and initial upload.
 *
 * <p>Purpose: provides a startup and in-app settings UI without navigating away
 * from the main view.</p>
 *
 * <p>Assumptions: localization keys exist for all labels and buttons; controller
 * wires button actions.</p>
 *
 * <p>Dependencies: JavaFX controls and {@link ResourceBundle} for localization.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SettingsSplash splash = new SettingsSplash();
 * splash.applyLanguage(bundle);
 * splash.getMyApplyChanges().setOnAction(e -> applySettings());
 * }</pre>
 *
 * @author Emilio Garcia
 */
public class SettingsSplash extends StackPane {
  private final Text myTitle;
  private final Text mySubtitle;
  private final Label myLanguage;
  private final Label myTheme;
  private final ChoiceBox<String> myLanguages = new ChoiceBox<>();
  private final ChoiceBox<String> myThemes = new ChoiceBox<>();
  private final Button myApplyChanges;
  private final Button myLoadButton;
  private final LanguageApplier myLanguageApplier = new LanguageApplier();
  /**
   * Creates the settings splash and builds the dialog content.
   *
   * <p>Purpose: initializes controls and choice boxes for later wiring.</p>
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public SettingsSplash(){
    setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    getStyleClass().add("splash");
    myTitle = new Text();
    mySubtitle = new Text();
    setStyleClasses();
    myLanguage = new Label();
    myTheme = new Label();
    myApplyChanges = new Button();
    myLoadButton = new Button();
    buildDropdowns();
    buildDialog();
  }

  private void setStyleClasses() {
    myTitle.getStyleClass().add("text");
    mySubtitle.getStyleClass().add("text");
  }

  private void buildDialog(){
    getChildren().clear();
    VBox myPane = new VBox(SPACING_MEDIUM, myTitle, mySubtitle, myLanguage, myLanguages, myTheme, myThemes, myLoadButton, myApplyChanges);
    myPane.setPadding(new Insets(SimulationUiConstants.PADDING_SETTINGS, SimulationUiConstants.PADDING_SETTINGS, SimulationUiConstants.PADDING_SETTINGS,
        SimulationUiConstants.PADDING_SETTINGS));
    myPane.setAlignment(Pos.CENTER);

    getChildren().add(myPane);
  }

  /**
   * Populates the language and theme dropdown options.
   *
   * <p>Purpose: sets the internal canonical values used by the converters.</p>
   *
   * <p>Assumptions: dropdowns are empty or can accept duplicate entries.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  private void buildDropdowns(){
    myLanguages.getItems().addAll(myLanguageApplier.getLanguageChoices());
    myThemes.getItems().addAll(
        ResourcePaths.THEME_LIGHT,
        ResourcePaths.THEME_DARK,
        ResourcePaths.THEME_DUKE);
    myLanguages.setValue(myLanguageApplier.getDefaultLanguage());
    myThemes.setValue(ResourcePaths.THEME_LIGHT);
  }

  /**
   * Applies localized text and updates dropdown converters.
   *
   * <p>Purpose: display translated labels while preserving internal canonical values.</p>
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
    myTitle.setText(bundle.getString(SETTINGS_DIALOG_TITLE));
    mySubtitle.setText(bundle.getString(SETTINGS_TITLE));
    myLanguage.setText(bundle.getString(SETTINGS_LANGUAGE));
    myTheme.setText(bundle.getString(SETTINGS_THEME));
    myApplyChanges.setText(bundle.getString(BUTTON_APPLY_CHANGES));
    myLoadButton.setText(bundle.getString(BUTTON_LOAD_SIMULATION));

    myLanguages.setConverter(new StringConverter<>() {
      @Override
      public String toString(String value) {
        if (value == null) return "";
        return myLanguageApplier.toDisplayName(value, bundle);
      }

      @Override
      public String fromString(String string) {
        return string; // not used
      }
    });

    myThemes.setConverter(new StringConverter<>() {
      @Override
      public String toString(String value) {
        if (value == null) return "";
        return switch (value) {
          case ResourcePaths.THEME_LIGHT -> bundle.getString(THEME_LIGHT);
          case ResourcePaths.THEME_DARK -> bundle.getString(THEME_DARK);
          case ResourcePaths.THEME_DUKE -> bundle.getString(THEME_DUKE);
          default -> value;
        };
      }

      @Override
      public String fromString(String string) {
        return string;
      }
    });
  }

  /**
   * Returns the Apply Changes button for controller wiring.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return apply button
   */
  public Button getMyApplyChanges(){ return myApplyChanges; }

  /**
   * Returns the language selection dropdown.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return language choice box
   */
  public ChoiceBox<String> getMyLanguages() { return myLanguages; }

  /**
   * Returns the theme selection dropdown.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return theme choice box
   */
  public ChoiceBox<String> getMyThemes() { return myThemes; }

  /**
   * Returns the Load Simulation button for controller wiring.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return load simulation button
   */
  public Button getMyLoadButton() { return myLoadButton; }

}
