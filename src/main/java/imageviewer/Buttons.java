package imageviewer;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class Buttons {
    public static Button actionIconButton(Ikon iconCode, String style, EventHandler<ActionEvent> actionEventHandler) {
        Button results = actionButton("", actionEventHandler);
        FontIcon icon = Icons.styledIcon(iconCode, style);

        results.setGraphic(icon);
        results.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border: none;");
        results.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        results.setFocusTraversable(false);

        return results;
    }

    public static Button actionButton(String text, EventHandler<ActionEvent> actionEventHandler) {
        Button results = new Button(text);
        results.setOnAction(actionEventHandler);
        return results;
    }


    public static Button toggleableActionIconButton(Ikon iconCode1, Ikon iconCode2, String style, BooleanProperty toggleProperty, EventHandler<ActionEvent> actionEventHandler) {
        Button results = new Button("");
        ToggleableIcon toggleableIcon = new ToggleableIcon(iconCode1, iconCode2, style, toggleProperty);

        results.setGraphic(toggleableIcon);
        results.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border: none;");
        results.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        results.setFocusTraversable(false);
        results.setOnAction(actionEventHandler);

        return results;
    }
}
