package dk.sdu.mmmi.cbse.common.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Entity implements Serializable {

    private final UUID ID = UUID.randomUUID();
    
    private double[] polygonCoordinates;
    private double x;
    private double y;
    private double rotation;
    private float radius;
    private double[] color = new double[]{0.0, 0.0, 0.0}; // Default to black (R, G, B)
    private int health = 1;
    private Map<String, List<Long>> activeEffects = new ConcurrentHashMap<>();
    private String consumableEffect = null;
    private String ownerID = null;
    private String imagePath = null;
            

    public String getID() {
        return ID.toString();
    }


    public void setPolygonCoordinates(double... coordinates ) {
        this.polygonCoordinates = coordinates;
    }

    public double[] getPolygonCoordinates() {
        return polygonCoordinates;
    }
       

    public void setX(double x) {
        this.x =x;
    }

    public double getX() {
        return x;
    }

    
    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
        
    public float getRadius() {
        return this.radius;
    }
    
    public void setColor(double r, double g, double b) {
        this.color = new double[]{r, g, b};
    }

    public double[] getColor() {
        return this.color;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return this.health;
    }

    public Map<String, List<Long>> getActiveEffects() {
        return activeEffects;
    }

    public void addEffect(String effect, long expirationTime) {
        activeEffects.computeIfAbsent(effect, k -> new CopyOnWriteArrayList<>()).add(expirationTime);
    }

    public void removeExpiredEffects(long currentTime) {
        activeEffects.entrySet().removeIf(entry -> {
            // Remove expired instances of the effect
            entry.getValue().removeIf(exp -> currentTime > exp);
            // Remove the effect completely if no stacks are left
            return entry.getValue().isEmpty();
        });
    }

    public int getEffectCount(String effect) {
        List<Long> expirations = activeEffects.get(effect);
        return expirations == null ? 0 : expirations.size();
    }

    public String getConsumableEffect() { return consumableEffect; }

    public void setConsumableEffect(String consumableEffect) { this.consumableEffect = consumableEffect; }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
