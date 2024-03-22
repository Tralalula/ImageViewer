package imageviewer;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.util.List;
import java.util.Objects;

public class Main extends Application {
    public static final String PATH = "/images/";

    private Slideshow slideshow;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        var results = new BorderPane();
        results.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/css/style.css")).toExternalForm());

        results.setCenter(center());
        results.setBottom(bottom());

        stage.setScene(new Scene(results, 1200, 960));
        stage.show();
    }

    public Region center() {
        var results = new VBox(8);

        var paths = List.of(
                PATH + "01.png", PATH + "02.png", PATH + "03.png", PATH + "04.png",
                PATH + "05.png", PATH + "06.png", PATH + "07.png", PATH + "08.png"
        );

        slideshow = new Slideshow();
        slideshow.load(paths);

        var imageView = new ImageView();
        imageView.setFitWidth(515);
        imageView.setFitHeight(343);
        imageView.imageProperty().bindBidirectional(slideshow.currentImageProperty());

        results.getChildren().addAll(imageView, controls());
        results.setAlignment(Pos.CENTER);

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
        var imagePreviews = new HBox(8);
        imagePreviews.setAlignment(Pos.CENTER);

        for (int i = 0; i < slideshow.images().size(); i++) {
            Image img = slideshow.images().get(i);
            ImageView view = new ImageView(img);
            view.setFitWidth(150);
            view.setFitHeight(150 * 0.67);
            view.setPreserveRatio(true);

            System.out.println(img.getHeight());
            System.out.println(view.getFitHeight());
            int tempI = i;
            view.setOnMouseClicked(e -> slideshow.select(tempI));
            imagePreviews.getChildren().add(view);
        }

        var scrollPane = new ScrollPane(imagePreviews);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMinHeight((150 * 0.67) + 8);
        scrollPane.setFitToHeight(true);

        var results = new HBox(scrollPane);
        results.setAlignment(Pos.CENTER);

        return results;
    }
}