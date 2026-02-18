package cellsociety.view;

import cellsociety.config.SimulationConfig;
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

  private final ResourceBundle myBundle;
  private final ButtonManager myButtons;

  private final StackPane myRoot = new StackPane();
  private final BorderPane myMain = new BorderPane();

  //TODO: pull from backend
  private double mySpeedMultiplier;

  private StackPane centerPane;
  private final AboutDialog myAboutDialog = new AboutDialog();
  private final SettingsSplash mySettings = new SettingsSplash();

  private GridView myGridView;

  public SimulationView(ResourceBundle bundle){
    this(bundle, false);
  }

  public SimulationView(ResourceBundle bundle, boolean showSplash){
    myBundle = bundle;
    myButtons = new ButtonManager(myBundle);
    myRoot.getStyleClass().add("app-root");
    myMain.getStyleClass().add("main-pane");
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
    Label myTitle = buildTitle();
    HBox myUploadRow = buildUploadRow();
    HBox myControlBar = buildControlBar();

    VBox myTopBox = new VBox(myTitle, myUploadRow, myControlBar);
    myTopBox.setSpacing(8);
    myMain.setTop(myTopBox);
  }

  private Label buildTitle() {
    Label myTitle = myButtons.getTitleLabel();
    myTitle.setFont(Font.font(28));
    VBox.setMargin(myTitle, new Insets(8,0,6,0));
    myTitle.setMaxWidth(Double.MAX_VALUE);
    myTitle.setAlignment(Pos.CENTER);
    return myTitle;
  }

  private HBox buildUploadRow() {
    HBox myUploadRow = new HBox(12);
    myUploadRow.getChildren().addAll(myButtons.getUploadRowButtons());
    myUploadRow.setAlignment(Pos.CENTER_LEFT);
    myUploadRow.setPadding(new Insets(6,10,10,10));
    myUploadRow.getStyleClass().add("upload-row");
    return myUploadRow;
  }

  private HBox buildControlBar(){
    HBox leftBox = new HBox(12);
    leftBox.getChildren().addAll(myButtons.getControlBarLeftNodes());
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
    StackPane card = buildCenterCard();
    Node gridNode = myGridView.getNode();
    card.getChildren().setAll(gridNode);
    centerPane.getChildren().setAll(card);
    myGridView.bindToContainer(card);

  }

  private StackPane buildCenterCard() {
    StackPane card = new StackPane();
    card.setPadding(new Insets(18));
    card.prefWidthProperty().bind(centerPane.widthProperty());
    card.prefHeightProperty().bind(centerPane.heightProperty());

    card.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    card.getStyleClass().add("card");
    card.setAlignment(Pos.CENTER);
    return card;
  }

  private void buildBottom(){
    HBox bottomBox = new HBox();
    bottomBox.getChildren().addAll(myButtons.getRotateLeftButton(), myButtons.getRotateRightButton());
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

  public Parent getRoot(){ return myRoot; }

  public SettingsSplash getMySettings() { return mySettings; }

  public AboutDialog getMyAboutDialog() { return myAboutDialog; }

  public GridView getMyGridView() { return myGridView; }

  public void setGrid(int rows, int cols){
    this.myGridView = new GridPaneView(rows, cols);
    buildCenter();
    buildBottom();
  }

  public void doubleMySpeed() { this.mySpeedMultiplier *= 2; }

  public void halveMySpeed() { this.mySpeedMultiplier /= 2; }

  public void updateSpeedLabel() { myButtons.setSpeedLabel(mySpeedMultiplier); }

  public void applyLanguage(ResourceBundle bundle) {
    updateSpeedLabel();
    myButtons.applyLanguage(bundle);
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

  public ResourceBundle getMyBundle() { return myBundle; }

  public ButtonManager getMyButtons() { return myButtons; }

  public double getMySpeedMultiplier() { return mySpeedMultiplier; }

  public void setCenterContent(Node content) {
    if (centerPane == null) {
      centerPane = new StackPane();
      centerPane.setPadding(new Insets(18,0,18,0));
      myMain.setCenter(centerPane);
    }
    StackPane card = buildCenterCard();
    card.getChildren().setAll(content);
    centerPane.getChildren().setAll(card);
  }

}
