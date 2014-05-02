package com.overminddl1.entity_test;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;

/**
 * Created by overminddl1 on 5/1/14.
 */
// Using IntervalEntitySystem instead of IntervalEntityProcessingSystem as I want to handle them all in a loop anyway as
// I could then do more complex things like collision detection if I increase this example to do that as well.
public class PhysicsSimulationSystem extends IntervalEntitySystem {

    @Mapper
    ComponentMapper<Position> positions;
    @Mapper
    ComponentMapper<Physics> physics;

    public PhysicsSimulationSystem() {
        super(Aspect.getAspectForAll(Position.class, Physics.class), 1.0f / 20.0f);
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        float delta = world.getDelta();
        // This could be done with an IntervalEntityProcessingSystem but I might add collision detection here later...
        for (int i = 0, s = entities.size(); s > i; i++) {
            Entity e = entities.get(i);
            Position p = positions.get(e);
            Physics f = physics.get(e);

            f.moveForcesToVelocity();

            p.deltaX(f.getVelocityX() * delta);
            p.deltaY(f.getVelocityY() * delta);

            // eat away at the velocity to slow things down over time
            f.dampenVelocity(0.1f * delta);
        }
    }

}
