package com.overminddl1.entity_test;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;

import java.util.Random;

/**
 * Created by overminddl1 on 5/1/14.
 */
// Just to give some random motion to entities to show that they are moving
public class PhysicsRandomDebugSystem extends EntityProcessingSystem {

    private Random r;

    @Mapper
    private ComponentMapper<Physics> physics;

    public PhysicsRandomDebugSystem(long seed) {
        super(Aspect.getAspectForAll(Physics.class));
        r = new Random(seed);
    }

    @Override
    protected void process(Entity e) {
        Physics f = physics.get(e);
        if (r.nextFloat() < 0.25f) {
            f.setForceX((r.nextFloat() - 0.5f) * 10.0F);
            f.setForceY((r.nextFloat() - 0.5f) * 10.0F);
        }
    }
}
