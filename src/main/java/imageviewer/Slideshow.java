package imageviewer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Slideshow {
    private final ObservableList<Image> images = FXCollections.observableArrayList();
    private final ObjectProperty<Image> currentImage = new SimpleObjectProperty<>();
    private final BooleanProperty isActive = new SimpleBooleanProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final DoubleProperty duration = new SimpleDoubleProperty(1.0);
    private final StringProperty currentImageName = new SimpleStringProperty();
    private final IntegerProperty pixels = new SimpleIntegerProperty();
    private final IntegerProperty redPixels = new SimpleIntegerProperty();
    private final IntegerProperty greenPixels = new SimpleIntegerProperty();
    private final IntegerProperty bluePixels = new SimpleIntegerProperty();
    private final IntegerProperty mixedPixels = new SimpleIntegerProperty();

    private Timeline timeLine;
    private int currentIndex = 0;
    private List<String> imageNames = new ArrayList<>();

    public Slideshow() {
        currentImage.set(null);
        isActive.set(false);
        progress.set(0);

        timeLine = new Timeline(startKeyFrame(), endKeyFrame());
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.currentTimeProperty().addListener((obs, ov, nv) -> progress.set(nv.toSeconds()));

        currentImage.addListener((obs, ov, nv) -> {
            updateCurrentImageName();
            performBackgroundTask(this::readPixels, this::setPixels);
        });
    }

    private void updateCurrentImageName() {
        if (currentIndex >= 0 && currentIndex < imageNames.size()) {
            currentImageName.set(imageNames.get(currentIndex));
        } else {
            currentImageName.set("");
        }
    }

    public StringProperty currentImageName() {
        return currentImageName;
    }

    public IntegerProperty pixelsProperty() {
        return pixels;
    }

    public IntegerProperty redPixelsProperty() {
        return redPixels;
    }

    public IntegerProperty greenPixelsProperty() {
        return greenPixels;
    }

    public IntegerProperty bluePixelsProperty() {
        return bluePixels;
    }

    public IntegerProperty mixedPixelsProperty() {
        return mixedPixels;
    }

    private KeyFrame startKeyFrame() {
        return new KeyFrame(Duration.seconds(0), e -> progress.set(0));
    }

    private KeyFrame endKeyFrame() {
        return new KeyFrame(Duration.seconds(duration.get()), e -> {
            next(false);
            progress.set(1.0);
        });
    }

    public void load(List<String> imagePaths) {
        images.clear();
        toImageNames(imagePaths);

        for (String path : imagePaths) {
            images.add(new Image(path, true));
        }

        if (!images.isEmpty()) {
            var first = images.getFirst();
            currentImage.set(first);

            first.progressProperty().addListener((obs, ov, nv) -> {
                if (nv.doubleValue() == 1.0) { // 100% loaded from bg thread
                    performBackgroundTask(this::readPixels, this::setPixels);
                }
            });
        }
    }

    private void toImageNames(List<String> imagePaths) {
        imageNames.clear();
        for (String fullPath : imagePaths) {
            var path = fullPath.replaceFirst("^file:/", "");
            path = path.replaceFirst("^/", "").replace("/", "\\");
            var fileName = new File(path).getName();
            imageNames.add(fileName);
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
        next(true);
    }

    private void next(boolean userInitiated) {
        if (images.size() > 1) {
            currentIndex = (currentIndex + 1) % images.size();
            currentImage.set(images.get(currentIndex));
            if (userInitiated && isActive.get()) stop();
        }
    }

    public void previous() {
        previous(true);
    }

    private void previous(boolean userInitiated) {
        if (images.size() > 1) {
            currentIndex = (currentIndex - 1 + images.size()) % images.size();
            currentImage.set(images.get(currentIndex));
            if (userInitiated && isActive.get()) stop();
        }
    }

    public void start() {
        if (!isActive.get() && !images.isEmpty()) {
            timeLine = new Timeline(startKeyFrame(), endKeyFrame());
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.currentTimeProperty().addListener((obs, ov, nv) -> {
                progress.set(nv.toSeconds() / duration.get());
            });

            timeLine.play();
            isActive.set(true);
        }
    }

    public void stop() {
        if (isActive.get()) {
            timeLine.stop();
            timeLine.jumpTo(Duration.ZERO);
            progress.set(0.0);
            isActive.set(false);
        }
    }

    public void select(int index) {
        if (index >= 0 && index < images.size()) {
            currentIndex = index;
            currentImage.set(images.get(currentIndex));
            stop();
        }
    }

    public ObservableList<Image> images() {
        return FXCollections.unmodifiableObservableList(images);
    }

    public void setDuration(double newDuration) {
        duration.set(newDuration);
    }

    private ImageData readPixels() {
        var currentImg = currentImage.get();
        if (currentImg == null) return new ImageData(0, 0, 0, 0, 0);

        var reader = currentImg.getPixelReader();
        if (reader == null) return new ImageData(0, 0, 0, 0, 0);

        var width = (int) currentImg.getWidth();
        var height = (int) currentImg.getHeight();
        var pixels = width * height;
        int redPixels = 0, greenPixels = 0, bluePixels = 0, mixedPixels = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var color = reader.getColor(x, y);
                var red = color.getRed();
                var green = color.getGreen();
                var blue = color.getBlue();

                var maxComponent = Math.max(Math.max(red, green), blue);

                if (maxComponent == red && red > green && red > blue) {
                    redPixels++;
                } else if (maxComponent == green && green > red && green > blue) {
                    greenPixels++;
                } else if (maxComponent == blue && blue > red && blue > green) {
                    bluePixels++;
                } else {
                    mixedPixels++;
                }
            }
        }

        return new ImageData(pixels, redPixels, greenPixels, bluePixels, mixedPixels);
    }


    private void setPixels(ImageData imageData) {
        this.pixels.set(imageData.pixels());
        this.redPixels.set(imageData.redPixels());
        this.greenPixels.set(imageData.greenPixels());
        this.bluePixels.set(imageData.bluePixels());
        this.mixedPixels.set(imageData.bluePixels());
    }

    private <T> void performBackgroundTask(Callable<T> task, Consumer<T> onSuccess) {
        Task<T> backgroundTask = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return task.call();
            }
        };

        backgroundTask.setOnSucceeded(event -> {
            T result = backgroundTask.getValue();
            onSuccess.accept(result);
        });

        new Thread(backgroundTask).start();
    }
}
