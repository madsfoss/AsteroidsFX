package dk.sdu.mmmi.cbse.enemysystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;

public class Enemy extends Entity {

    public static Entity createEnemy(GameData gameData) {
        Entity enemy = new Enemy();
        enemy.setPolygonCoordinates(-5, -5, 10, 0, -5, 5);
        enemy.setX(Math.random() * gameData.getDisplayWidth());
        enemy.setY(Math.random() * gameData.getDisplayHeight());
        enemy.setRadius(8);
        enemy.setRotation(Math.random() * 360);
        enemy.setColor(1.0, 0.0, 0.0); // Set color to Red (R=1, G=0, B=0)
        return enemy;
    }
}