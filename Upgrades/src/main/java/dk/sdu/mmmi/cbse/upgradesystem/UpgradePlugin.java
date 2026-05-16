package dk.sdu.mmmi.cbse.upgradesystem;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

public class UpgradePlugin implements IGamePluginService {

    @Override
    public void start(GameData gameData, World world) {
        // We will add the logic to initially spawn an upgrade here later
    }

    @Override
    public void stop(GameData gameData, World world) {
        world.getEntities(Upgrade.class).forEach(world::removeEntity);
    }
}