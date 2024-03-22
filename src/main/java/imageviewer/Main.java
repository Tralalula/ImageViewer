package imageviewer;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends Application {
    public static final String PATH = "/images/";

    private StringProperty currentImageName = new SimpleStringProperty();
    private Slideshow slideshow;
    private Stage stage;
    private HBox imagePreviews;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        var results = new BorderPane();
        results.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/css/style.css")).toExternalForm());

        results.setLeft(left());
        results.setCenter(center());
        results.setRight(right());
        results.setBottom(bottom());

        stage.setScene(new Scene(results, 1200, 960));
        stage.show();
    }

    public Region left() {
        var results = new VBox(8);

        var chooseImages = new Button("Choose...");
        chooseImages.setOnAction(e -> chooseImages());
        results.getChildren().add(chooseImages);

        return results;
    }

    private void chooseImages() {
        var chooser = new FileChooser();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        chooser.getExtensionFilters().add(filter);

        var files = chooser.showOpenMultipleDialog(stage);

        if (files != null) {
            var paths = files.stream().map(File::toURI).map(URI::toString).toList();
            slideshow.load(paths);
            refreshImageView();
            setupThumbnails();
        }
    }

    private void refreshImageView() {
        if (!slideshow.images().isEmpty()) {
            slideshow.select(0);
        }
    }

    public Region center() {
        var results = new VBox(8);

        var paths = List.of(
                PATH + "01.png", PATH + "02.png", PATH + "03.png", PATH + "04.png",
                PATH + "05.png", PATH + "06.png", PATH + "07.png", PATH + "08.png"
        );

        slideshow = new Slideshow();
        slideshow.setDuration(1.0);
        slideshow.load(paths);

        var imageView = new ImageView();
        imageView.setFitWidth(515);
        imageView.setFitHeight(343);
        imageView.imageProperty().bindBidirectional(slideshow.currentImageProperty());

        results.getChildren().addAll(imageView, controls());
        results.setAlignment(Pos.CENTER);

        return results;
    }

    public Region right() {
        var imageName = new Label("");
        imageName.textProperty().bind(slideshow.currentImageName());
        imageName.setMinWidth(200);
        imageName.setMaxWidth(200);

        var results = new VBox(8);
        results.getChildren().addAll(imageName);

        return results;
    }

    public Region controls() {
        var progressBar = new ProgressBar();
        progressBar.setPrefWidth(515);
        progressBar.progressProperty().bindBidirectional(slideshow.progressProperty());

        var playPause = Buttons.toggleableActionIconButton(
                Material2MZ.PLAY_ARROW,
                Material2MZ.PAUSE,
                "icon",
                slideshow.isActiveProperty(), e -> handlePlay()
        );

        var previous = Buttons.actionIconButton(Material2AL.ARROW_LEFT, "icon", e -> slideshow.previous());
        var next = Buttons.actionIconButton(Material2AL.ARROW_RIGHT, "icon", e -> slideshow.next());

        var controls = new HBox(8, previous, playPause, next);
        controls.setAlignment(Pos.CENTER);

        var progressBarContainer = new HBox(progressBar);
        progressBarContainer.setAlignment(Pos.CENTER);

        return new VBox(8, progressBarContainer, controls);
    }

    public void handlePlay() {
        if (slideshow.isActiveProperty().get()) slideshow.stop();
        else slideshow.start();
    }

    public Region bottom() {
        imagePreviews = new HBox(8);
        imagePreviews.setAlignment(Pos.CENTER);

        setupThumbnails();

        var scrollPane = new ScrollPane(imagePreviews);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMinHeight((150 * 0.67) + 8);
        scrollPane.setFitToHeight(true);

        var results = new HBox(scrollPane);
        results.setAlignment(Pos.CENTER);

        return results;
    }

    private void setupThumbnails() {
        imagePreviews.getChildren().clear();
        for (int i = 0; i < slideshow.images().size(); i++) {
            var img = slideshow.images().get(i);
            var view = new ImageView(img);
            view.setFitWidth(150);
            view.setFitHeight(150 * 0.67);
            view.setPreserveRatio(true);

            var container = new StackPane(view, border());
            container.setUserData(i);

            container.setOnMouseClicked(e -> {
                slideshow.select((int) container.getUserData());
            });

            imagePreviews.getChildren().add(container);
        }

        refreshSelectionIndicator();
        slideshow.currentImageProperty().addListener((obs, ov, nv) -> refreshSelectionIndicator());
    }

    private void refreshSelectionIndicator() {
        for (Node node : imagePreviews.getChildren()) {
            var container = (StackPane) node;
            var border = (Rectangle) container.getChildren().get(1); // index på border
            int index = (int) container.getUserData();

            border.setVisible(slideshow.images().indexOf(slideshow.currentImageProperty().get()) == index);
        }
    }

    private Rectangle border() {
        var results = new Rectangle(150, 150 * 0.67);
        results.setStroke(Color.FIREBRICK);
        results.setFill(Color.TRANSPARENT);
        results.setStrokeWidth(2);
        results.setVisible(false);
        return results;
    }
}