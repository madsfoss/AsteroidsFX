package dk.sdu.mmmi.cbse.common.asteroids;

import dk.sdu.mmmi.cbse.common.data.Entity;

public class Asteroid extends Entity {
    private boolean isHit = false;
    private int maxHealth = 0;

    @Override
    public void setHealth(int health) {
        if (maxHealth == 0) {
            maxHealth = health;
        }
        
        if (health <= 0) {
            isHit = true;
            super.setHealth(1); // Keep alive for one extra frame so AsteroidProcessor can split it
        } else {
            super.setHealth(health);
        }
    }

    public boolean isHit() {
        return isHit;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
}
