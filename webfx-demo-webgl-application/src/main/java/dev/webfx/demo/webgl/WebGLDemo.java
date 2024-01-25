package dev.webfx.demo.webgl;

import dev.webfx.demo.webgl.cube.CubeScene;
import dev.webfx.demo.webgl.smoke.SmokeScene;
import dev.webfx.demo.webgl.text.TextScene;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.resource.Resource;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * @author Bruno Salmon
 */
public class WebGLDemo extends Application {

    private static final Font font = Font.loadFont(Resource.toUrl("Injekuta-Reg.otf", WebGLDemo.class), 14);

    private final Canvas canvas = new Canvas();
    private final CubeScene cubeScene = new CubeScene(canvas);
    private final SmokeScene smokeScene = new SmokeScene(canvas);
    private final TextScene textScene = new TextScene();
    private final Text fpsText = createWhiteText("");
    private final Circle selectedPenCircle = new Circle(13, Color.TRANSPARENT);
    private boolean generateSmoke;
    private double lastGeneratedSmokeTimeSeconds;

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(40);
        gridPane.setVgap(15);
        StackPane.setAlignment(gridPane, Pos.BOTTOM_LEFT);
        StackPane.setMargin(gridPane, new Insets(20));
        gridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        int rowIndex = 0;

        // FPS
        gridPane.addRow(rowIndex++, createWhiteText("FPS"), fpsText);
        // Pen heat
        gridPane.addRow(rowIndex++, createWhiteText("Pen Heat"), createHeatBox());
        // HDPI switch
        if (WebFxKitLauncher.getCanvasPixelDensity(canvas) > 1) // true on HDPI screens
            gridPane.addRow(rowIndex++, createWhiteText("HDPI"), createHdpiSwitch());
        // Rotate switch
        gridPane.addRow(rowIndex++, createWhiteText("Rotate"), createSwitch(cubeScene.autoRotateProperty()));
        // Tube switch
        gridPane.addRow(rowIndex++, createWhiteText("Tube"), createSwitch(cubeScene.tubeProperty()));
        // Videos switch
        gridPane.addRow(rowIndex++, createWhiteText("Videos"), createSwitch(cubeScene.playVideosProperty()));
        // Code switch
        gridPane.addRow(rowIndex++, createWhiteText("Code"), createSwitch(cubeScene.codeDoorShowingProperty()));
        // Keys legend
        gridPane.addRow(rowIndex, createWhiteText("Keys"), createWhiteText("↑ ↓ ← →"));

        new AnimationTimer() {
            final double fpsRefreshSeconds = 0.5;
            double lastFrameSeconds;
            int framesCount;

            @Override
            public void handle(long now) {
                framesCount++;
                final double nowSeconds = now / 1_000_000_000d;
                if (lastFrameSeconds == 0)
                    lastFrameSeconds = nowSeconds;
                else if (nowSeconds - lastFrameSeconds >= fpsRefreshSeconds) {
                    fpsText.setText(Integer.toString((int) (1d * framesCount / fpsRefreshSeconds)));
                    framesCount = 0;
                    lastFrameSeconds = nowSeconds;
                }
                cubeScene.onAnimationFrame(now);
                if (lastGeneratedSmokeTimeSeconds > 0 && nowSeconds < lastGeneratedSmokeTimeSeconds + 8) {
                    smokeScene.onAnimationFrame(now);
                }
                textScene.onAnimationFrame(now);
            }
        }.start();

        StackPane root = new StackPane(canvas, textScene.getContainer(), gridPane);
        Scene scene = new Scene(root, 800, 600, Color.web("#310E68"));
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Text createWhiteText(String text) {
        Text t = new Text(text);
        t.setFont(font);
        t.setFill(Color.WHITE);
        return t;
    }

    private static Switch createSwitch(BooleanProperty booleanProperty) {
        Switch s = new Switch();
        s.setCursor(Cursor.HAND);
        s.setSelected(booleanProperty.get());
        booleanProperty.bind(s.selectedProperty());
        return s;
    }

    private Switch createHdpiSwitch() {
        Switch hdpiSwitch = new Switch();
        hdpiSwitch.setCursor(Cursor.HAND);
        hdpiSwitch.setSelected(true);
        double pixelDensity = WebFxKitLauncher.getCanvasPixelDensity(canvas);
        FXProperties.runOnPropertiesChange(() -> {
            WebFxKitLauncher.setCanvasPixelDensity(canvas, hdpiSwitch.isSelected() ? pixelDensity : 1);
        }, hdpiSwitch.selectedProperty());
        return hdpiSwitch;
    }

    private HBox createHeatBox() {
        Color[] penColors = { Color.CYAN, Color.PINK, Color.ORANGE, Color.RED };
        Circle[] penCircles = Arrays.stream(penColors).map(this::createHeatCircle).toArray(Circle[]::new);
        HBox heatBox = new HBox(10, penCircles);
        heatBox.getChildren().add(selectedPenCircle);
        selectedPenCircle.setManaged(false);
        selectedPenCircle.setMouseTransparent(true);
        selectedPenCircle.strokeProperty().bind(textScene.penColorProperty());
        textScene.setPenColor(null);
        FXProperties.runOnPropertiesChange(() -> {
            if (textScene.getPenColor() != Color.CYAN) {
                Point2D penPosition = textScene.getPenPosition();
                if (penPosition != null) {
                    smokeScene.movePointer(penPosition.getX(), penPosition.getY(), generateSmoke);
                    lastGeneratedSmokeTimeSeconds = System.nanoTime() / 1_000_000_000d;
                }
                generateSmoke = penPosition != null;
            }
        }, textScene.penPositionProperty());
        return heatBox;
    }

    private Circle createHeatCircle(Color color) {
        Circle c = new Circle(10, color);
        c.setCursor(Cursor.HAND);
        c.setOnMouseClicked(e -> {
            textScene.setPenColor(color);
            selectedPenCircle.layoutXProperty().bind(c.layoutXProperty());
            selectedPenCircle.layoutYProperty().bind(c.layoutYProperty());
            textScene.restart();
            smokeScene.setHeightFactor(color == Color.PINK ? 0.25 : color == Color.ORANGE ? 0.4 : color == Color.RED ? 0.7 : 0);
            lastGeneratedSmokeTimeSeconds = 0;
        });
        return c;
    }
}