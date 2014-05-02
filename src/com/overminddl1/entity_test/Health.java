package com.overminddl1.entity_test;

import com.artemis.Component;

/**
 * Created by overminddl1 on 5/1/14.
 */
public class Health extends Component {

    private int health;

    public Health() {
    }

    public Health(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

}
