package dk.sdu.mmmi.cbse.upgradesystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class UpgradeControlSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {
        long currentTime = System.currentTimeMillis();
        
        // Clean up expired upgrades across all entities
        for (Entity entity : world.getEntities()) {
            entity.removeExpiredEffects(currentTime);
        }

        // Spawn up to 2 upgrades randomly
        if (Math.random() < 0.002 && world.getEntities(Upgrade.class).size() < 2) {
            world.addEntity(createUpgrade(gameData));
        }
    }

    private Entity createUpgrade(GameData gameData) {
        Entity upgrade = new Upgrade();
        String[] effects = {"FASTER_SHOOTING", "MORE_BULLETS", "LARGER_BULLETS"};
        String chosenEffect = effects[(int) (Math.random() * effects.length)];
        
        upgrade.setConsumableEffect(chosenEffect);
        upgrade.setPolygonCoordinates(-9, -9, 9, -9, 9, 9, -9, 9); // Square shape
        upgrade.setX(Math.random() * gameData.getDisplayWidth());
        upgrade.setY(Math.random() * gameData.getDisplayHeight());
        upgrade.setRadius(9);
        
        if (chosenEffect.equals("FASTER_SHOOTING")) upgrade.setImagePath("/faster_bullets.png");
        else if (chosenEffect.equals("MORE_BULLETS")) upgrade.setImagePath("/more_bullets.png");
        else upgrade.setImagePath("/bigger_bullets.png");
        
        return upgrade;
    }

}