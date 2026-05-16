package dk.sdu.mmmi.cbse.collisionsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

public class CollisionDetector implements IPostEntityProcessingService {

    public CollisionDetector() {
    }

    @Override
public void process(GameData gameData, World world) {
    // Put entities in an array/list
    Entity[] entities = world.getEntities().toArray(new Entity[0]);

    for (int i = 0; i < entities.length; i++) {
        Entity entity1 = entities[i];
        
        // Skip if entity1 was already removed in a previous collision this frame
        if (world.getEntity(entity1.getID()) == null) {
            continue;
        }

        // 2. Start 'j' at 'i + 1' to prevent double-checking entities against each other
        // and prevent checking an entity against itself!
        for (int j = i + 1; j < entities.length; j++) {
            Entity entity2 = entities[j];

            // Skip if entity2 was already removed
            if (world.getEntity(entity2.getID()) == null) {
                continue;
            }

            // Prevent an entity from colliding with its own projectiles
            if (entity1.getID().equals(entity2.getOwnerID()) || entity2.getID().equals(entity1.getOwnerID())) {
                continue;
            }

            // Prevent projectiles from the same owner from colliding with each othe
            if (entity1.getOwnerID() != null && entity1.getOwnerID().equals(entity2.getOwnerID())) {
                continue;
            }

            if (this.collides(entity1, entity2)) {
                
                // Upgrade collision logic
                if (entity1.getConsumableEffect() != null || entity2.getConsumableEffect() != null) {
                    Entity consumable = entity1.getConsumableEffect() != null ? entity1 : entity2;
                    Entity consumer = consumable == entity1 ? entity2 : entity1;

                    String consumerType = consumer.getClass().getSimpleName();
                    // Allow Player and Enemy to pick up upgrades
                    if (consumerType.equals("Player") || consumerType.equals("Enemy")) {
                        long duration = 30000; // Customizable duration (30 seconds)
                        consumer.addEffect(consumable.getConsumableEffect(), System.currentTimeMillis() + duration);
                        world.removeEntity(consumable);
                    } else if (consumerType.equals("Asteroid")) {
                        // Asteroids simply crush/destroy the upgrade upon collision
                        world.removeEntity(consumable);
                    }
                    continue; // Skip the health damage
                }

                entity1.setHealth(entity1.getHealth() - 1);
                entity2.setHealth(entity2.getHealth() - 1);
                
                if (entity1.getHealth() <= 0) {
                    world.removeEntity(entity1);
                }
                if (entity2.getHealth() <= 0) {
                    world.removeEntity(entity2);
                }
                
                // If Entity 1 is dead, break out of the inner loop so it stops 
                if (entity1.getHealth() <= 0) {
                    break; 
                }
            }
        }
    }
}

    public Boolean collides(Entity entity1, Entity entity2) {
        float dx = (float) entity1.getX() - (float) entity2.getX();
        float dy = (float) entity1.getY() - (float) entity2.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < (entity1.getRadius() + entity2.getRadius());
    }

}
