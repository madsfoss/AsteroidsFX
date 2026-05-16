package dk.sdu.mmmi.cbse.playersystem;

import java.util.Collection;
import java.util.ServiceLoader;
import static java.util.stream.Collectors.toList;

import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;


public class PlayerControlSystem implements IEntityProcessingService {

    private long lastFireTime = 0;

    @Override
    public void process(GameData gameData, World world) {
            
        for (Entity player : world.getEntities(Player.class)) {
            if (gameData.getKeys().isDown(GameKeys.LEFT)) {
                player.setRotation(player.getRotation() - 2);                
            }
            if (gameData.getKeys().isDown(GameKeys.RIGHT)) {
                player.setRotation(player.getRotation() + 2);                
            }
            if (gameData.getKeys().isDown(GameKeys.UP)) {
                double changeX = Math.cos(Math.toRadians(player.getRotation()));
                double changeY = Math.sin(Math.toRadians(player.getRotation()));
                player.setX(player.getX() + changeX);
                player.setY(player.getY() + changeY);
            }
            if (gameData.getKeys().isDown(GameKeys.SPACE)) {
                long currentTime = System.currentTimeMillis();
                int speedStacks = player.getEffectCount("FASTER_SHOOTING");
                long fireCooldown = 200 / (speedStacks + 1);
                
                if (currentTime - lastFireTime > fireCooldown) { // ms cooldown between shots
                    getBulletSPIs().stream().findFirst().ifPresent(
                            spi -> {
                                int bulletStacks = player.getEffectCount("MORE_BULLETS");
                                int sizeStacks = player.getEffectCount("LARGER_BULLETS");
                                int bulletCount = 1 + (bulletStacks * 2);
                                double originalRotation = player.getRotation();
                                
                                for (int i = 0; i < bulletCount; i++) {
                                    double offset = (i - bulletCount / 2) * 15;
                                    player.setRotation(originalRotation + offset);
                                    
                                    Entity bullet = spi.createBullet(player, gameData);
                                    
                                    if (sizeStacks > 0) {
                                        bullet.setRadius(bullet.getRadius() * (1 + sizeStacks));
                                        double[] newCoords = bullet.getPolygonCoordinates().clone();
                                        for (int j = 0; j < newCoords.length; j++) newCoords[j] *= (1 + sizeStacks);
                                        bullet.setPolygonCoordinates(newCoords);
                                    }
                                    bullet.setOwnerID(player.getID());
                                    world.addEntity(bullet);
                                }
                                player.setRotation(originalRotation);
                            }
                    );
                    lastFireTime = currentTime;
                }
            }
            
        if (player.getX() < 0) {
            player.setX(1);
        }

        if (player.getX() > gameData.getDisplayWidth()) {
            player.setX(gameData.getDisplayWidth()-1);
        }

        if (player.getY() < 0) {
            player.setY(1);
        }

        if (player.getY() > gameData.getDisplayHeight()) {
            player.setY(gameData.getDisplayHeight()-1);
        }

                                        
        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
