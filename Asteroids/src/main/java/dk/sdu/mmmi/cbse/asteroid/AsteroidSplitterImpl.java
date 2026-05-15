package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.World;

public class AsteroidSplitterImpl implements IAsteroidSplitter {

    @Override
    public void createSplitAsteroid(Entity e, World world) {
        // Remove the original asteroid from the world
        world.removeEntity(e);
        
        Asteroid ast = (Asteroid) e;
        int maxHealth = ast.getMaxHealth();
        
        if (maxHealth <= 1) {
            return;
        }

        int newHealth = maxHealth / 2; // 4 -> 2, 2 -> 1
        float newRadius = e.getRadius() / 2;

        for (int i = 0; i < 2; i++) {
            Entity smallerAsteroid = new Asteroid();
            smallerAsteroid.setPolygonCoordinates(newRadius, -newRadius, -newRadius, -newRadius, -newRadius, newRadius, newRadius, newRadius);
            smallerAsteroid.setRadius(newRadius);
            smallerAsteroid.setHealth(newHealth);
            
            // Deflect the new asteroids by a random angle between 20 and 60 degrees in opposite directions
            double newRot = e.getRotation() + (i == 0 ? getRandomInRange(20, 60) : getRandomInRange(-60, -20));
            smallerAsteroid.setRotation(newRot);
            // Offset the spawn position so they dont spawn inside each other
            smallerAsteroid.setX(e.getX() + Math.cos(Math.toRadians(newRot)) * newRadius * 2);
            smallerAsteroid.setY(e.getY() + Math.sin(Math.toRadians(newRot)) * newRadius * 2);
            world.addEntity(smallerAsteroid);
        }
    }

    private int getRandomInRange(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

}
