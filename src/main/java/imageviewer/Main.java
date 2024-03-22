package imageviewer;

import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import javax.swing.*;
import java.util.List;

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
                Styles.ACCENT,
                slideshow.isActiveProperty(), e -> handlePlay()
        );
        playPause.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border: none; -fx-font-size: 30;");

        return new HBox(playPause);
    }

    public void handlePlay() {
        if (slideshow.isActiveProperty().get()) slideshow.stop();
        else slideshow.start();
    }
}