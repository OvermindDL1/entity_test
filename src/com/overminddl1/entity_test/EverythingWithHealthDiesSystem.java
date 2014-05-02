package com.overminddl1.entity_test;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;

/**
 * Created by overminddl1 on 5/1/14.
 */
public class EverythingWithHealthDiesSystem extends IntervalEntityProcessingSystem {

    @Mapper
    ComponentMapper<Health> healths;

    public EverythingWithHealthDiesSystem() {
        super(Aspect.getAspectForAll(Health.class), 0.4f);
    }

    @Override
    protected void process(Entity e) {
        Health hp = healths.get(e);
        int h = hp.getHealth() - 1;
        if( h<= 0) {
            System.out.println(String.format("Entity %d has died!", e.getId()));
            e.deleteFromWorld();
        }
        else {
            hp.setHealth(h);
            System.out.println(String.format("Entity %d has remaining health of: %d", e.getId(), h));
        }
    }
}
