package dk.sdu.mmmi.cbse.asteroid;

import java.util.Random;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

public class AsteroidPlugin implements IGamePluginService {

    @Override
    public void start(GameData gameData, World world) {
        Entity asteroid = createAsteroid(gameData);
        world.addEntity(asteroid);
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Remove entities
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            world.removeEntity(asteroid);
        }
    }

    public static Entity createAsteroid(GameData gameData) {
        Entity asteroid = new Asteroid();
        Random rnd = new Random();
        
        // Randomly determine size tier 0 (Small), 1 (Medium), 2 (Large)
        int tier = rnd.nextInt(3);
        int size = 16;
        int health = 4;
        
        if (tier == 1) {
            size = 8;
            health = 2;
        } else if (tier == 0) {
            size = 4;
            health = 1;
        }
        
        asteroid.setPolygonCoordinates(size, -size, -size, -size, -size, size, size, size);
        
        if (rnd.nextBoolean()) {
            // Spawn on Left or Right edge
            asteroid.setX(rnd.nextBoolean() ? 0 : gameData.getDisplayWidth());
            asteroid.setY(rnd.nextInt(gameData.getDisplayHeight()));
        } else {
            // Spawn on Top or Bottom edge
            asteroid.setX(rnd.nextInt(gameData.getDisplayWidth()));
            asteroid.setY(rnd.nextBoolean() ? 0 : gameData.getDisplayHeight());
        }
        
        asteroid.setRadius(size);
        asteroid.setRotation(rnd.nextInt(360));
        asteroid.setHealth(health);
        return asteroid;
    }
}
