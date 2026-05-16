package dk.sdu.mmmi.cbse.collisionsystem;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dk.sdu.mmmi.cbse.common.data.Entity;

public class CollisionDetectorTest {
    
    private CollisionDetector collisionDetector;
    private Entity entity1;
    private Entity entity2;

    @BeforeEach
    public void setUp() {
        collisionDetector = new CollisionDetector();
        entity1 = new Entity();
        entity2 = new Entity();
    }

    @Test
    public void testEntitiesCollide() {
        entity1.setX(10);
        entity1.setY(10);
        entity1.setRadius(5);

        entity2.setX(12);
        entity2.setY(12);
        entity2.setRadius(5);

        // Distance between (10,10) and (12,12) is ~2.82, which is less than 5+5
        assertTrue(collisionDetector.collides(entity1, entity2), "Entities should collide when overlapping.");
    }

    @Test
    public void testEntitiesDoNotCollide() {
        entity1.setX(10);
        entity1.setY(10);
        entity1.setRadius(5);

        entity2.setX(100);
        entity2.setY(100);
        entity2.setRadius(5);

        // Distance is ~127, which is much greater than 10
        assertFalse(collisionDetector.collides(entity1, entity2), "Entities should not collide when far apart.");
    }
}
