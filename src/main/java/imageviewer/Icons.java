package imageviewer;

import javafx.scene.paint.Color;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class Icons {
    public static FontIcon styledIcon(Ikon iconCode, String style) {
        FontIcon results = new FontIcon(iconCode);
        results.getStyleClass().add(style);
        return results;
    }
}
