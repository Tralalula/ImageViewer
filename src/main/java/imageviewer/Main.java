package imageviewer;

import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import javax.swing.*;
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
        var results = new StackPane();

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

        results.getChildren().add(imageView);

        return results;
    }

    public Region bottom() {
        var playPause = Buttons.toggleableActionIconButton(
                Material2MZ.PLAY_ARROW,
                Material2MZ.PAUSE,
                "icon",
                slideshow.isActiveProperty(), e -> handlePlay()
        );

        var previous = Buttons.actionIconButton(Material2AL.ARROW_LEFT, "icon", e -> slideshow.previous());
        var next = Buttons.actionIconButton(Material2AL.ARROW_RIGHT, "icon", e -> slideshow.next());


        var results = new HBox(8, previous, playPause, next);
        results.setAlignment(Pos.CENTER);

        return results;
    }

    public void handlePlay() {
        if (slideshow.isActiveProperty().get()) slideshow.stop();
        else slideshow.start();
    }
}