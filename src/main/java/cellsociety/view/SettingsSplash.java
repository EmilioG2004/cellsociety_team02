package cellsociety.view;

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

public class SettingsSplash extends StackPane {
  private final Text myTitle;
  private final Text mySubtitle;
  private final Label myLanguage;
  private final Label myTheme;
  private final ChoiceBox<String> myLanguages = new ChoiceBox<>();
  private final ChoiceBox<String> myThemes = new ChoiceBox<>();
  private final Button myApplyChanges;
  private final Button myLoadButton;
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
    VBox myPane = new VBox(12, myTitle, mySubtitle, myLanguage, myLanguages, myTheme, myThemes, myLoadButton, myApplyChanges);
    myPane.setPadding(new Insets(20,20,20,20));
    myPane.setAlignment(Pos.CENTER);

    getChildren().add(myPane);
  }

  public void buildDropdowns(){
    myLanguages.getItems().addAll("English", "Spanish", "Portuguese");
    myThemes.getItems().addAll("Light", "Dark", "Duke");
  }

  public void applyLanguage(ResourceBundle bundle){
    myTitle.setText(bundle.getString("settings.dialog.title"));
    mySubtitle.setText(bundle.getString("settings.title"));
    myLanguage.setText(bundle.getString("settings.language"));
    myTheme.setText(bundle.getString("settings.theme"));
    myApplyChanges.setText(bundle.getString("button.applyChanges"));
    myLoadButton.setText(bundle.getString("button.loadSimulation"));

    myLanguages.setConverter(new StringConverter<>() {
      @Override
      public String toString(String value) {
        if (value == null) return "";
        return switch (value) {
          case "English" -> bundle.getString("language.english");
          case "Spanish" -> bundle.getString("language.spanish");
          case "Portuguese" -> bundle.getString("language.portuguese");
          default -> value;
        };
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
          case "Light" -> bundle.getString("theme.light");
          case "Dark" -> bundle.getString("theme.dark");
          case "Duke" -> bundle.getString("theme.duke");
          default -> value;
        };
      }

      @Override
      public String fromString(String string) {
        return string;
      }
    });
  }

  public Button getMyApplyChanges(){ return myApplyChanges; }

  public ChoiceBox<String> getMyLanguages() { return myLanguages; }

  public ChoiceBox<String> getMyThemes() { return myThemes; }

}
