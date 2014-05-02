package com.overminddl1.entity_test;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;

/**
 * Created by overminddl1 on 5/1/14.
 */
public class EntityDebugPrinterSystem extends IntervalEntityProcessingSystem {

    @Mapper
    ComponentMapper<Position> positions;
    @Mapper
    ComponentMapper<Physics> physics;

    public EntityDebugPrinterSystem() {
        super(Aspect.getAspectForAll(Position.class), 1.0f);
    }

    // Entities also have a getUUID method that returns a normal java UUID if you need a universally unique ID, like if
    // you are saving an entity to disk and still want to be able to reference somehow.
    @Override
    protected void process(Entity e) {
        Position p = positions.get(e);
        Physics f = physics.getSafe(e);
        if (f == null) {
            System.out.println(String.format("Entity %d at (%f, %f) is static",
                    e.getId(),
                    p.getX(), p.getY()
            ));
        } else {
            System.out.println(String.format("Entity %d at (%f, %f) is going (%f, %f)",
                    e.getId(),
                    p.getX(), p.getY(),
                    f.getVelocityX(), f.getVelocityY()
            ));
        }
    }
}
