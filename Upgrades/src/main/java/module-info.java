import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

module Upgrades {
    requires Common;
    requires CommonBullet;
    
    uses dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
    
    provides IGamePluginService with dk.sdu.mmmi.cbse.upgradesystem.UpgradePlugin;
    
}
