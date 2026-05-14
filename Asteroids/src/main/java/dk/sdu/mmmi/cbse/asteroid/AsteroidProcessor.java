package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class AsteroidProcessor implements IEntityProcessingService {

    private IAsteroidSplitter asteroidSplitter = new AsteroidSplitterImpl();

    @Override
    public void process(GameData gameData, World world) {

        // Continuously spawn new asteroids if there are fewer than 4 in the world
        if (Math.random() < 0.005 && world.getEntities(Asteroid.class).size() < 4) {
            world.addEntity(AsteroidPlugin.createAsteroid(gameData));
        }

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            Asteroid ast = (Asteroid) asteroid;
            if (ast.isHit()) {
                if (ast.getMaxHealth() <= 1) {
                    gameData.setScore(gameData.getScore() + 1);
                }
                asteroidSplitter.createSplitAsteroid(asteroid, world);
                continue; // Skip moving the original as it is now removed/split
            }

            double changeX = Math.cos(Math.toRadians(asteroid.getRotation()));
            double changeY = Math.sin(Math.toRadians(asteroid.getRotation()));

            asteroid.setX(asteroid.getX() + changeX * 0.5);
            asteroid.setY(asteroid.getY() + changeY * 0.5);

            // Wrap around Left to Right
                if (asteroid.getX() < 0) {
                asteroid.setX(asteroid.getX() + gameData.getDisplayWidth());
            }
            // Wrap around Right to Left
            if (asteroid.getX() > gameData.getDisplayWidth()) {
                asteroid.setX(asteroid.getX() % gameData.getDisplayWidth());
            }
            // Wrap around Top to Bottom
            if (asteroid.getY() < 0) {
                asteroid.setY(asteroid.getY() + gameData.getDisplayHeight());
            }
            // Wrap around Bottom to Top
            if (asteroid.getY() > gameData.getDisplayHeight()) {
                asteroid.setY(asteroid.getY() % gameData.getDisplayHeight());
            }

        }

    }


    public void setAsteroidSplitter(IAsteroidSplitter asteroidSplitter) {
        this.asteroidSplitter = asteroidSplitter;
    }

    public void removeAsteroidSplitter(IAsteroidSplitter asteroidSplitter) {
        this.asteroidSplitter = null;
    }


}
