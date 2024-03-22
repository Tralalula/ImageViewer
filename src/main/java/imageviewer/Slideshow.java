package imageviewer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.List;

public class Slideshow {
    private final ObservableList<Image> images = FXCollections.observableArrayList();
    private final ObjectProperty<Image> currentImage = new SimpleObjectProperty<>();
    private final BooleanProperty isActive = new SimpleBooleanProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final Timeline timeLine;

    private int currentIndex = 0;

    public Slideshow() {
        currentImage.set(null);
        isActive.set(false);
        progress.set(0);

        timeLine = new Timeline(startKeyFrame(), endKeyFrame());
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.currentTimeProperty().addListener((obs, ov, nv) -> progress.set(nv.toSeconds()));
    }

    private KeyFrame startKeyFrame() {
        return new KeyFrame(Duration.seconds(0), e -> progress.set(0));
    }

    private KeyFrame endKeyFrame() {
        return new KeyFrame(Duration.seconds(1.0), e -> {
            next();
            progress.set(1.0);
        });
    }

    public void load(List<String> imagePaths) {
        images.clear();

        for (String path : imagePaths) {
            images.add(new Image(path, true));
        }

        if (!images.isEmpty()) {
            currentImage.set(images.getFirst());
        }
    }

    public ObjectProperty<Image> currentImageProperty() {
        return currentImage;
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void next() {
        if (images.size() > 1) {
            currentIndex = (currentIndex + 1) % images.size();
            currentImage.set(images.get(currentIndex));
        }
    }

    public void previous() {
        if (images.size() > 1) {
            currentIndex = (currentIndex - 1 + images.size()) % images.size();
            currentImage.set(images.get(currentIndex));
        }
    }

    public void start() {
        if (!isActive.get() && !images.isEmpty()) {
            timeLine.play();
            isActive.set(true);
        }
    }

    public void stop() {
        if (isActive.get()) {
            timeLine.pause();
            isActive.set(false);
        }
    }
}
