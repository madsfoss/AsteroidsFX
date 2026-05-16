/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dk.sdu.mmmi.cbse.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class Game {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();
    private final List<IGamePluginService> gamePluginServices;
    private final List<IEntityProcessingService> entityProcessingServiceList;
    private final List<IPostEntityProcessingService> postEntityProcessingServices;
    private Image fullHeartImage;
    private Image halfHeartImage;
    private Image emptyHeartImage;
    private final List<ImageView> heartImageViews = new ArrayList<>();
    private boolean isGameOver = false;
    private VBox gameOverPane;
    private Text scoreText;
    private Text finalScoreText;
    private Text highScoreText;
    private final Map<String, ImagePattern> imagePatternCache = new ConcurrentHashMap<>();

    Game(List<IGamePluginService> gamePluginServices, List<IEntityProcessingService> entityProcessingServiceList, List<IPostEntityProcessingService> postEntityProcessingServices) {
        this.gamePluginServices = gamePluginServices;
        this.entityProcessingServiceList = entityProcessingServiceList;
        this.postEntityProcessingServices = postEntityProcessingServices;
    }

    public void start(Stage window) throws Exception {
        Pane rootNode = new Pane();
        Pane uiBar = new Pane();
        uiBar.setPrefSize(gameData.getDisplayWidth(), 40);
        uiBar.setStyle("-fx-background-color: #d3d3d3; -fx-border-color: black; -fx-border-width: 0 0 2 0;"); // Light grey bar with bottom border
        
        scoreText = new Text(10, 25, "Destroyed asteroids: 0");
        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        uiBar.getChildren().add(scoreText);

        try {
            fullHeartImage = new Image(getClass().getResourceAsStream("/health_display/full_heart.png"));
            halfHeartImage = new Image(getClass().getResourceAsStream("/health_display/half_heart.png"));
            emptyHeartImage = new Image(getClass().getResourceAsStream("/health_display/empty_heart.png"));

            int maxHearts = 3;
            for (int i = 0; i < maxHearts; i++) {
                ImageView heartView = new ImageView(emptyHeartImage);
                heartView.setFitWidth(32);
                heartView.setFitHeight(32);
                // Position from left to right, aligned to the right edge
                heartView.setX(gameData.getDisplayWidth() - 10 - (maxHearts * 36) + (i * 36));
                heartView.setY(4);
                heartImageViews.add(heartView);
                uiBar.getChildren().add(heartView);
            }
        } catch (Exception e) {
            System.err.println("Heart images not found. add them to Core/src/main/resources/health_display/");
        }

        gameOverPane = new VBox(20);
        gameOverPane.setAlignment(Pos.CENTER);
        gameOverPane.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight() + 40); // Cover entire game area and UI bar
        gameOverPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Semi-transparent black background
        
        Text gameOverText = new Text("GAME OVER");
        gameOverText.setStyle("-fx-fill: white; -fx-font-size: 40px; -fx-font-weight: bold;");
        
        Text restartText = new Text("Press SPACE to Restart");
        restartText.setStyle("-fx-fill: white; -fx-font-size: 20px;");
        
        finalScoreText = new Text("Score: 0");
        finalScoreText.setStyle("-fx-fill: white; -fx-font-size: 24px;");
        
        highScoreText = new Text("High Score: 0");
        highScoreText.setStyle("-fx-fill: white; -fx-font-size: 24px;");
        
        gameOverPane.getChildren().addAll(gameOverText, finalScoreText, highScoreText, restartText);
        gameOverPane.setVisible(false);

        gameWindow.setLayoutY(40);
        rootNode.getChildren().addAll(gameWindow, uiBar, gameOverPane);
        Scene scene = new Scene(rootNode);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) {
                gameData.getKeys().setKey(GameKeys.LEFT, true);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                gameData.getKeys().setKey(GameKeys.RIGHT, true);
            }
            if (event.getCode().equals(KeyCode.UP)) {
                gameData.getKeys().setKey(GameKeys.UP, true);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, true);
                if (isGameOver) {
                    restartGame();
                }
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) {
                gameData.getKeys().setKey(GameKeys.LEFT, false);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                gameData.getKeys().setKey(GameKeys.RIGHT, false);
            }
            if (event.getCode().equals(KeyCode.UP)) {
                gameData.getKeys().setKey(GameKeys.UP, false);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, false);
            }

        });

        // Lookup all Game Plugins using ServiceLoader
        for (IGamePluginService iGamePlugin : getGamePluginServices()) {
            iGamePlugin.start(gameData, world);
        }
        for (Entity entity : world.getEntities()) {
            Polygon polygon = new Polygon(entity.getPolygonCoordinates());
            if (entity.getImagePath() != null) {
                ImagePattern pattern = imagePatternCache.computeIfAbsent(entity.getImagePath(), path -> {
                    java.io.InputStream is = entity.getClass().getResourceAsStream(path);
                    if (is != null) return new ImagePattern(new Image(is));
                    return null;
                });
                if (pattern != null) {
                    polygon.setFill(pattern);
                } else {
                    double[] color = entity.getColor();
                    polygon.setFill(javafx.scene.paint.Color.color(color[0], color[1], color[2]));
                }
            } else {
                double[] color = entity.getColor();
                polygon.setFill(javafx.scene.paint.Color.color(color[0], color[1], color[2]));
            }
            polygons.put(entity, polygon);
            gameWindow.getChildren().add(polygon);
        }
        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();
    }

    public void render() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isGameOver) {
                    update();
                }
                draw();
                gameData.getKeys().update();
                
            }

        }.start();
    }

    private void update() {
        for (IEntityProcessingService entityProcessorService : getEntityProcessingServices()) {
            entityProcessorService.process(gameData, world);
        }
        for (IPostEntityProcessingService postEntityProcessorService : getPostEntityProcessingServices()) {
            postEntityProcessorService.process(gameData, world);
        }

    }

    private void draw() {
        // Remove polygons for entities that no longer exist in the world
        polygons.entrySet().removeIf(entry -> {
            if (world.getEntity(entry.getKey().getID()) == null) {
                gameWindow.getChildren().remove(entry.getValue());
                return true;
            }
            return false;
        });

        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity);
            if (polygon == null) {
                polygon = new Polygon(entity.getPolygonCoordinates());
                polygons.put(entity, polygon);
                gameWindow.getChildren().add(polygon);
            }
            polygon.setTranslateX(entity.getX());
            polygon.setTranslateY(entity.getY());
            polygon.setRotate(entity.getRotation());
            
            if (entity.getImagePath() != null) {
                ImagePattern pattern = imagePatternCache.computeIfAbsent(entity.getImagePath(), path -> {
                    java.io.InputStream is = entity.getClass().getResourceAsStream(path);
                    if (is != null) return new ImagePattern(new Image(is));
                    return null;
                });
                if (pattern != null) {
                    polygon.setFill(pattern);
                } else {
                    double[] color = entity.getColor();
                    polygon.setFill(javafx.scene.paint.Color.color(color[0], color[1], color[2]));
                }
            } else {
                double[] color = entity.getColor();
                polygon.setFill(javafx.scene.paint.Color.color(color[0], color[1], color[2]));
            }
        }

        // Update health display
        if (!heartImageViews.isEmpty()) {
            int playerHealth = 0;
            boolean playerExists = false;
            for (Entity entity : world.getEntities()) {
                if (entity.getClass().getSimpleName().equals("Player")) {
                    playerHealth = entity.getHealth();
                    playerExists = true;
                    break;
                }
            }

            if (!playerExists && !isGameOver) {
                isGameOver = true;
                
                if (gameData.getScore() > gameData.getHighScore()) {
                    gameData.setHighScore(gameData.getScore());
                }
                finalScoreText.setText("Score: " + gameData.getScore());
                highScoreText.setText("High Score: " + gameData.getHighScore());
                
                gameOverPane.setVisible(true);
            }

            for (int i = 0; i < heartImageViews.size(); i++) {
                ImageView heartView = heartImageViews.get(i);
                int fullThreshold = (heartImageViews.size() - i) * 2;
                int halfThreshold = fullThreshold - 1;

                if (playerHealth >= fullThreshold) {
                    heartView.setImage(fullHeartImage);
                } else if (playerHealth >= halfThreshold) {
                    heartView.setImage(halfHeartImage);
                } else {
                    heartView.setImage(emptyHeartImage);
                }
            }
        }

        scoreText.setText("Destroyed asteroids: " + gameData.getScore());

    }

    private void restartGame() {
        // Safely remove all entities from the world
        for (Entity entity : new ArrayList<>(world.getEntities())) {
            world.removeEntity(entity);
        }
        // Clear all JavaFX visual polygons
        gameWindow.getChildren().removeAll(polygons.values());
        polygons.clear();
        
        // Restart all plugins
        for (IGamePluginService iGamePlugin : getGamePluginServices()) {
            iGamePlugin.start(gameData, world);
        }
        
        gameData.setScore(0);
        
        isGameOver = false;
        gameOverPane.setVisible(false);
    }

    public List<IGamePluginService> getGamePluginServices() {
        return gamePluginServices;
    }

    public List<IEntityProcessingService> getEntityProcessingServices() {
        return entityProcessingServiceList;
    }

    public List<IPostEntityProcessingService> getPostEntityProcessingServices() {
        return postEntityProcessingServices;
    }

}
