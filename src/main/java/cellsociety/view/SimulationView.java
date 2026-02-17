package cellsociety.view;


import cellsociety.config.SimulationConfig;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import java.io.File;


public class SimulationView {
  public static final String DATA_FILE_FOLDER = System.getProperty("user.dir") + "/data";
  private static final String BASE_CSS =
      SimulationView.class.getResource("/cellsociety/stylesheets/base.css").toExternalForm();

  private static final String LIGHT_CSS =
      SimulationView.class.getResource("/cellsociety/stylesheets/light.css").toExternalForm();

  private static final String DARK_CSS =
      SimulationView.class.getResource("/cellsociety/stylesheets/dark.css").toExternalForm();

  private static final String DUKE_CSS =
      SimulationView.class.getResource("/cellsociety/stylesheets/duke.css").toExternalForm();



  private ResourceBundle myBundle;


  private final StackPane myRoot = new StackPane();
  private final BorderPane myMain = new BorderPane();
  private double mySpeedMultiplier = 1;

  private final Label myTitle;
  private final Button myUploadButton;
  private final Button myForwardButton;
  private final Button myBackButton;
  private final Button myStartButton;
  private final Button myPauseButton;
  private final Button mySpeedUpButton;
  private final Button mySlowDownButton;
  private final Button mySaveButton;
  private final Button myResetButton;
  private final Button myChangeDirectionButton;
  private final Label mySpeedLabel;
  private final Label myStateStackSizeLabel = new Label("Generation: 0");
  private final Button myAboutButton;
  private final Button myToggleOutlineButton;
  private final Button mySettingsButton;
  private final Button myRotateLeftButton;
  private final Button myRotateRightButton;


  private StackPane centerPane;
  private final AboutDialog myAboutDialog = new AboutDialog();
  private final SettingsSplash mySettings = new SettingsSplash();

  private GridView myGridView;

  public SimulationView(ResourceBundle bundle){
    this(bundle, false);
  }

  public SimulationView(ResourceBundle bundle, boolean showSplash){
    myBundle = bundle;
    myTitle = new Label();
    myUploadButton = new Button();
    myForwardButton = new Button();
    myBackButton = new Button();
    myStartButton = new Button();
    myPauseButton = new Button();
    mySpeedUpButton = new Button();
    mySlowDownButton = new Button();
    mySaveButton = new Button();
    myResetButton = new Button();
    myChangeDirectionButton = new Button();
    myAboutButton = new Button();
    myToggleOutlineButton = new Button();
    mySettingsButton = new Button();
    mySpeedLabel = new Label();
    myRoot.getStyleClass().add("app-root");
    myMain.getStyleClass().add("main-pane");
    myRotateLeftButton = new Button();
    myRotateRightButton = new Button();
    myForwardButton.setText(myBundle.getString("button.stepForward"));
    mySpeedMultiplier = 1.0;

    applyLanguage(myBundle);

    buildTop();


    myMain.setPadding(new Insets(12));

    myRoot.getChildren().addAll(myMain, mySettings);

    bindSplashToWindow();

    if (showSplash){
      openSettings();
    } else {
      closeSettings();
    }
  }

  private void bindSplashToWindow() {
    mySettings.prefWidthProperty().bind(myRoot.widthProperty());
    mySettings.prefHeightProperty().bind(myRoot.heightProperty());
    mySettings.maxWidthProperty().bind(myRoot.widthProperty());
    mySettings.maxHeightProperty().bind(myRoot.heightProperty());

    myAboutDialog.prefWidthProperty().bind(myRoot.widthProperty());
    myAboutDialog.prefHeightProperty().bind(myRoot.heightProperty());
    myAboutDialog.maxWidthProperty().bind(myRoot.widthProperty());
    myAboutDialog.maxHeightProperty().bind(myRoot.heightProperty());
  }

  private void buildTop(){
    myTitle.setFont(Font.font(28));
    VBox.setMargin(myTitle, new Insets(8,0,6,0));
    myTitle.setMaxWidth(Double.MAX_VALUE);
    myTitle.setAlignment(Pos.CENTER);

    HBox myUploadRow = new HBox(myUploadButton, mySaveButton, myAboutButton, mySettingsButton);
    myUploadRow.setAlignment(Pos.CENTER_LEFT);
    myUploadRow.setPadding(new Insets(6,10,10,10));
    myUploadRow.getStyleClass().add("upload-row");

    HBox myControlBar = buildControlBar();
    VBox myTopBox = new VBox(myTitle, myUploadRow, myControlBar);
    myTopBox.setSpacing(8);
    myMain.setTop(myTopBox);
  }

  private HBox buildControlBar(){
    HBox leftBox = new HBox(12, myStartButton, myPauseButton, mySpeedUpButton, mySlowDownButton, myResetButton, myForwardButton, myBackButton, myChangeDirectionButton, myToggleOutlineButton, mySpeedLabel, myStateStackSizeLabel);
    leftBox.setAlignment(Pos.CENTER_LEFT);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox bar = new HBox(12, leftBox, spacer);
    bar.setAlignment(Pos.CENTER_LEFT);
    bar.setPadding(new Insets(14));
    bar.setMinHeight(64);
    bar.getStyleClass().add("control-bar");


    return bar;
  }

  private void buildCenter(){
    if (myGridView == null) return;
    if (centerPane == null) {
      centerPane = new StackPane();
      centerPane.setPadding(new Insets(18,0,18,0));
      myMain.setCenter(centerPane);
    }

    StackPane card = new StackPane();
    card.setPadding(new Insets(18));
    card.setPrefSize(760,520);
    card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    card.getStyleClass().add("card");
    card.setAlignment(Pos.CENTER);

    Node gridNode = myGridView.getNode();
    card.getChildren().setAll(gridNode);
    centerPane.getChildren().setAll(card);
  }

  private void buildBottom(){
    HBox bottomBox = new HBox(myRotateLeftButton, myRotateRightButton);
    bottomBox.setAlignment(Pos.BOTTOM_CENTER);
    myMain.setBottom(bottomBox);
  }

  public File showOpenConfigDialog() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Open Simulation Configuration");
    chooser.setInitialDirectory(new File(DATA_FILE_FOLDER));
    chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("XML Files", "*.xml")
    );

    Window w = myMain.getScene() == null ? null : myMain.getScene().getWindow();
    return chooser.showOpenDialog(w);
  }

  public File showSaveConfigDialog(){
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Open Simulation Configuration");
    chooser.getExtensionFilters().add(
        new ExtensionFilter("XML Files", "*.xml")
    );

    Window w = myMain.getScene() == null ? null : myMain.getScene().getWindow();
    return chooser.showSaveDialog(w);
  }

  public void showAboutSplash(SimulationConfig config){
    if (centerPane == null){
      centerPane = new StackPane();
      centerPane.setPadding(new Insets(18,0,18,0));
      myMain.setCenter(centerPane);
    }
    myAboutDialog.generateInfo(config);

    centerPane.getChildren().add(myAboutDialog);

    StackPane.setAlignment(myAboutDialog, Pos.CENTER);
  }

  public void closeSettings(){
    mySettings.setVisible(false);
    mySettings.setManaged(false);
  }

  public void openSettings(){
    mySettings.setVisible(true);
    mySettings.setManaged(true);
  }

  public Parent getRoot(){
    return myRoot;
  }

  public Button getMyUploadButton(){
    return myUploadButton;
  }

  public Button getMyStartButton(){
    return myStartButton;
  }

  public Button getMyPauseButton(){
    return myPauseButton;
  }

  public Button getMySpeedUpButton(){
    return mySpeedUpButton;
  }

  public Button getMySlowDownButton(){
    return mySlowDownButton;
  }

  public Button getMyForwardButton(){
    return myForwardButton;
  }

  public Button getMyBackButton(){
    return myBackButton;
  }

  public Button getMyChangeDirectionButton(){
    return myChangeDirectionButton;
  }

  public Button getMySaveButton(){
    return mySaveButton;
  }

  public Button getMyAboutButton(){
    return myAboutButton;
  }

  public Button getMyToggleOutlineButton() { return myToggleOutlineButton; }

  public Button getMySettingsButton() { return mySettingsButton; }

  public Label getMyStateStackSizeLabel(){
    return myStateStackSizeLabel;
  }

  public SettingsSplash getMySettings() { return mySettings; }

  public AboutDialog getMyAboutDialog() { return myAboutDialog; }

  public GridView getMyGridView() { return myGridView; }

  public void setGrid(int rows, int cols, double cellSize){
    this.myGridView = new GridPaneView(rows, cols, cellSize);
    buildCenter();
    buildBottom();
  }


  /**
   * Provides access to the Reset button so a controller can attach behavior.
   *
   * @return the button used to clear and reset the grid to its initial configuration
   */
  public Button getMyResetButton() {
    return myResetButton;
  }

  public double getMySpeedMultiplier(){
    return mySpeedMultiplier;
  }

  public void doubleMySpeed() { this.mySpeedMultiplier *= 2; }

  public void halveMySpeed() { this.mySpeedMultiplier /= 2; }

  public void updateSpeedLabel(){
    String pattern = myBundle.getString("label.currentSpeed");
    mySpeedLabel.setText(MessageFormat.format(pattern, mySpeedMultiplier));
  }

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
    updateSpeedLabel();
    mySettings.applyLanguage(bundle);
    myAboutDialog.applyLanguage(bundle);
  }

  public void applyTheme(Scene scene, String themeChoice) {
    String themeCss = switch (themeChoice) {
      case "Dark" -> DARK_CSS;
      case "Duke" -> DUKE_CSS;
      default -> LIGHT_CSS;
    };
    scene.getStylesheets().setAll(BASE_CSS, themeCss);
  }

  public Button getMyRotateLeftButton(){ return myRotateLeftButton; }

  public Button getMyRotateRightButton(){ return myRotateRightButton; }

  public ResourceBundle getMyBundle() { return myBundle; }
}
