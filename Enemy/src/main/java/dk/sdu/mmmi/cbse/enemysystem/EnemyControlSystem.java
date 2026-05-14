package dk.sdu.mmmi.cbse.enemysystem;

import java.util.Collection;
import java.util.ServiceLoader;
import static java.util.stream.Collectors.toList;

import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class EnemyControlSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {
        
        // Randomly spawn a new enemy if there are less than a max amount
        if (Math.random() < 0.005 && world.getEntities(Enemy.class).size() < 3) {
            world.addEntity(Enemy.createEnemy(gameData));
        }

        for (Entity enemy : world.getEntities(Enemy.class)) {
            // Randomly steer left or right
            double randomTurn = Math.random();
            if (randomTurn < 0.2) {
                enemy.setRotation(enemy.getRotation() - 5);
            } else if (randomTurn > 0.8) {
                enemy.setRotation(enemy.getRotation() + 5);
            }
            
            // Constantly move forward based on current rotation
            double changeX = Math.cos(Math.toRadians(enemy.getRotation()));
            double changeY = Math.sin(Math.toRadians(enemy.getRotation()));
            enemy.setX(enemy.getX() + changeX * 1);
            enemy.setY(enemy.getY() + changeY * 1);

            // Randomly fire a bullet
            if (Math.random() < 0.02) {
                getBulletSPIs().stream().findFirst().ifPresent(
                        spi -> { world.addEntity(spi.createBullet(enemy, gameData)); }
                );
            }
            
            // Screen boundaries wrapping
            if (enemy.getX() < 0) {
                enemy.setX(gameData.getDisplayWidth());
            }
            if (enemy.getX() > gameData.getDisplayWidth()) {
                enemy.setX(0);
            }
            if (enemy.getY() < 0) {
                enemy.setY(gameData.getDisplayHeight());
            }
            if (enemy.getY() > gameData.getDisplayHeight()) {
                enemy.setY(0);
            }
        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
