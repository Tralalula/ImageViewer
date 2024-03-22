package imageviewer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {
    public static final String PATH = "/images/";

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        var stackPane = new StackPane();

        var paths = List.of(
                PATH + "01.png", PATH + "02.png", PATH + "03.png", PATH + "04.png",
                PATH + "05.png", PATH + "06.png", PATH + "07.png", PATH + "08.png"
        );

        var slideshow = new Slideshow();
        slideshow.load(paths);

        var imageView = new ImageView();
        imageView.imageProperty().bindBidirectional(slideshow.currentImageProperty());

        stackPane.getChildren().add(imageView);

        slideshow.start();

        var results = new BorderPane();
        results.setCenter(stackPane);

        stage.setScene(new Scene(results));
        stage.show();
    }

    public static ImageView scaledImage(String url, double fitWidth, double fitHeight) {
        ImageView results = new ImageView(new Image(url, true));

        results.setFitWidth(fitWidth);
        results.setFitHeight(fitHeight);
        return results;
    }
}